package com.campus.ershou.common.file;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.api.FieldErrorItem;
import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.util.Randoms;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class FileService {

    public static final Set<String> PURPOSES = Set.of("GOODS", "AVATAR", "IM", "VERIFICATION", "EVIDENCE");
    private static final int MAX_BYTES = 5_242_880;
    private static final int FULL_EDGE = 1600;
    private static final int THUMB_EDGE = 400;

    private final StoredFileMapper fileMapper;
    private final UserMapper userMapper;
    private final FileUrls fileUrls;
    private final Path root;

    public FileService(StoredFileMapper fileMapper, UserMapper userMapper, FileUrls fileUrls, AppProperties props) {
        this.fileMapper = fileMapper;
        this.userMapper = userMapper;
        this.fileUrls = fileUrls;
        this.root = Path.of(props.getFile().getDir()).toAbsolutePath().normalize();
    }

    public Map<String, Object> upload(long userId, MultipartFile multipart, String purpose) {
        String normalized = purpose == null ? "" : purpose.trim().toUpperCase(Locale.ROOT);
        if (!PURPOSES.contains(normalized)) {
            throw ApiException.validation(List.of(new FieldErrorItem("purpose", "用途不合法")));
        }
        UserEntity user = requireUser(userId);
        assertCanUpload(user, normalized);
        if (multipart == null || multipart.isEmpty()) {
            throw ApiException.of(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
        byte[] bytes;
        try {
            bytes = multipart.getBytes();
        } catch (IOException e) {
            throw ApiException.of(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
        if (bytes.length > MAX_BYTES) {
            throw ApiException.of(ErrorCode.FILE_TOO_LARGE);
        }
        if (!isAllowedImage(bytes)) {
            throw ApiException.of(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
        BufferedImage image = readImage(bytes);
        String fileId = Randoms.hex(16);
        try {
            Files.createDirectories(root);
            writeJpeg(scale(image, FULL_EDGE), path(fileId, false));
            writeJpeg(scale(image, THUMB_EDGE), path(fileId, true));
        } catch (IOException e) {
            throw new IllegalStateException("无法保存图片", e);
        }
        StoredFile stored = new StoredFile();
        stored.setId(fileId);
        stored.setOwnerId(userId);
        stored.setPurpose(normalized);
        stored.setPublicAccess(isPublicPurpose(normalized));
        fileMapper.insert(stored);
        return describe(stored, true);
    }

    public StoredFile requireUsable(String fileId, long ownerId, String purpose) {
        StoredFile file = fileId == null ? null : fileMapper.findById(fileId);
        if (file == null || file.getOwnerId() == null || file.getOwnerId() != ownerId || !purpose.equals(file.getPurpose())) {
            throw ApiException.of(ErrorCode.FILE_NOT_USABLE);
        }
        return file;
    }

    public Map<String, Object> urlFor(long userId, String fileId) {
        StoredFile file = fileMapper.findById(fileId);
        if (file == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        if (!file.isPublic() && (file.getOwnerId() == null || file.getOwnerId() != userId)) {
            throw ApiException.of(ErrorCode.FORBIDDEN);
        }
        return describe(file, false);
    }

    public void writeContent(String fileId, String size, OutputStream out) throws IOException {
        StoredFile file = fileMapper.findById(fileId);
        if (file == null) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        Path path = path(fileId, "thumb".equals(size));
        if (!Files.isRegularFile(path)) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        Files.copy(path, out);
    }

    public StoredFile find(String fileId) {
        return fileMapper.findById(fileId);
    }

    public Map<String, Object> describe(StoredFile file, boolean includePurpose) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("fileId", file.getId());
        if (includePurpose) {
            data.put("purpose", file.getPurpose());
        }
        if (file.isPublic()) {
            data.put("url", fileUrls.publicUrl(file.getId()));
            data.put("thumbUrl", fileUrls.thumbUrl(file.getId()));
        } else {
            long exp = fileUrls.privateExpiryEpoch();
            data.put("url", fileUrls.privateUrl(file.getId(), "", exp));
            data.put("thumbUrl", fileUrls.privateUrl(file.getId(), "thumb", exp));
            data.put("expiresInSeconds", propsSeconds());
        }
        return data;
    }

    public String publicUrlIfOwned(String fileId) {
        if (fileId == null || fileId.isBlank()) {
            return null;
        }
        StoredFile file = fileMapper.findById(fileId);
        if (file == null || !file.isPublic()) {
            return null;
        }
        return fileUrls.publicUrl(fileId);
    }

    private int propsSeconds() {
        return (int) fileUrlsPrivateTtl();
    }

    private long fileUrlsPrivateTtl() {
        return Math.max(1, fileUrls.privateExpiryEpoch() - java.time.Instant.now().getEpochSecond());
    }

    private UserEntity requireUser(long userId) {
        UserEntity user = userMapper.findById(userId);
        if (user == null) {
            throw ApiException.of(ErrorCode.UNAUTHORIZED);
        }
        return user;
    }

    private static void assertCanUpload(UserEntity user, String purpose) {
        boolean verified = "VERIFIED".equals(user.getVerifyStatus());
        boolean open = "UNVERIFIED".equals(user.getVerifyStatus()) || "REJECTED".equals(user.getVerifyStatus());
        boolean allowed = switch (purpose) {
            case "AVATAR" -> true;
            case "VERIFICATION" -> open;
            case "GOODS", "IM", "EVIDENCE" -> verified;
            default -> false;
        };
        if (!allowed) {
            if ("VERIFICATION".equals(purpose) && "PENDING".equals(user.getVerifyStatus())) {
                throw ApiException.of(ErrorCode.VERIFICATION_PENDING);
            }
            if ("VERIFICATION".equals(purpose) && verified) {
                throw ApiException.of(ErrorCode.ALREADY_VERIFIED);
            }
            throw ApiException.of(ErrorCode.NOT_VERIFIED);
        }
    }

    private static boolean isPublicPurpose(String purpose) {
        return "GOODS".equals(purpose) || "AVATAR".equals(purpose);
    }

    private static boolean isAllowedImage(byte[] bytes) {
        if (bytes.length >= 3 && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff) {
            return true;
        }
        if (bytes.length >= 8
                && (bytes[0] & 0xff) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4e
                && bytes[3] == 0x47) {
            return true;
        }
        return bytes.length >= 12
                && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P';
    }

    private static BufferedImage readImage(byte[] bytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                throw ApiException.of(ErrorCode.FILE_TYPE_NOT_ALLOWED);
            }
            return image;
        } catch (IOException e) {
            throw ApiException.of(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
    }

    private static BufferedImage scale(BufferedImage src, int maxEdge) {
        double ratio = Math.min(1d, maxEdge / (double) Math.max(src.getWidth(), src.getHeight()));
        int width = Math.max(1, (int) Math.round(src.getWidth() * ratio));
        int height = Math.max(1, (int) Math.round(src.getHeight() * ratio));
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = out.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(src, 0, 0, width, height, null);
        graphics.dispose();
        return out;
    }

    private static void writeJpeg(BufferedImage image, Path path) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("没有 JPEG 编码器");
        }
        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.85f);
        try (ImageOutputStream output = ImageIO.createImageOutputStream(path.toFile())) {
            writer.setOutput(output);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    private Path path(String fileId, boolean thumb) {
        String name = thumb ? fileId + "_thumb.jpg" : fileId + ".jpg";
        Path resolved = root.resolve(name).normalize();
        if (!resolved.startsWith(root)) {
            throw ApiException.of(ErrorCode.NOT_FOUND);
        }
        return resolved;
    }
}
