package com.campus.ershou.goods;

import com.campus.ershou.common.util.RedisKeys;
import com.campus.ershou.common.util.StudentNoHasher;
import com.campus.ershou.user.UserEntity;
import com.campus.ershou.user.UserMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class CatalogFlowIT {

    private static final String SELLER_NO = "91001101";
    private static final String BUYER_NO = "91001102";
    private static final Path FILE_DIR = Path.of(System.getProperty("java.io.tmpdir"), "campus-ershou-it-files");

    @DynamicPropertySource
    static void fileDir(DynamicPropertyRegistry registry) {
        registry.add("app.file.dir", FILE_DIR::toString);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StudentNoHasher hasher;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate redis;

    private long sellerId;
    private long buyerId;

    @BeforeEach
    void setUp() throws Exception {
        cleanup();
        sellerId = insertUser(SELLER_NO, "seller_it", "seller-it@example.com", "VERIFIED");
        buyerId = insertUser(BUYER_NO, "buyer_it", "buyer-it@example.com", "UNVERIFIED");
    }

    @AfterEach
    void tearDown() throws Exception {
        cleanup();
    }

    @Test
    void profileVerificationAndGoods() throws Exception {
        String sellerToken = login(SELLER_NO);
        String buyerToken = login(BUYER_NO);

        mockMvc.perform(get("/api/me/stats").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.onSaleCount").value(0))
                .andExpect(jsonPath("$.data.pendingOrderCount").value(0));
        mockMvc.perform(get("/api/me/badges").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unreadMessages").value(0));

        String avatarId = upload(buyerToken, "AVATAR");
        mockMvc.perform(put("/api/me/profile")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nickname":"买家昵称","avatarFileId":"%s","bio":"你好","dormBuildingId":1}
                                """.formatted(avatarId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("买家昵称"))
                .andExpect(jsonPath("$.data.avatarUrl").isNotEmpty())
                .andExpect(jsonPath("$.data.dormBuilding.name").value("学上公寓1"));

        mockMvc.perform(multipart("/api/files")
                        .file(jpeg())
                        .param("purpose", "GOODS")
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("NOT_VERIFIED"));

        String cardId = upload(buyerToken, "VERIFICATION");
        mockMvc.perform(post("/api/auth/campus-verification")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dormBuildingId":1,"cardImageFileId":"%s"}
                                """.formatted(cardId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"));
        mockMvc.perform(get("/api/auth/campus-verification").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.dormBuilding.id").value(1));

        mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(goodsJson("unused")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("NOT_VERIFIED"));

        String imageId = upload(sellerToken, "GOODS");
        MvcResult created = mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(goodsJson(imageId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ON_SALE"))
                .andExpect(jsonPath("$.data.price").value(1500))
                .andExpect(jsonPath("$.data.viewer.isMine").value(true))
                .andReturn();
        int goodsId = JsonPath.read(created.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/api/goods").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("高数教材"));

        mockMvc.perform(get("/api/goods/" + goodsId).header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.viewer.canBuy").value(false))
                .andExpect(jsonPath("$.data.viewer.favorited").value(false));

        mockMvc.perform(put("/api/goods/" + goodsId + "/favorite").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.favoriteCount").value(1));
        mockMvc.perform(get("/api/me/favorites").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1));
        mockMvc.perform(get("/api/me/history").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].id").value(goodsId));

        mockMvc.perform(get("/api/users/" + sellerId).header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verified").value(true))
                .andExpect(jsonPath("$.data.onSaleCount").value(1))
                .andExpect(jsonPath("$.data.studentNoMasked").doesNotExist());

        mockMvc.perform(post("/api/goods/" + goodsId + "/off-shelf").header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("OFF_SHELF"));
        mockMvc.perform(get("/api/goods").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
        mockMvc.perform(get("/api/goods/" + goodsId).header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/me/goods").header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].status").value("OFF_SHELF"));

        assertThat(redis.opsForList().size(RedisKeys.history(buyerId))).isEqualTo(1);
    }

    private String login(String studentNo) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentNo":"%s","password":"FlowTest-1"}
                                """.formatted(studentNo)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token");
    }

    private String upload(String token, String purpose) throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/files")
                        .file(jpeg())
                        .param("purpose", purpose)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.fileId");
    }

    private static String goodsJson(String imageId) {
        return """
                {"title":"高数教材","description":"九成新","categoryId":1,"condition":"GOOD","price":1500,
                 "imageFileIds":["%s"],"meetupBuildingId":1,"tradeNote":"宿舍楼下"}
                """.formatted(imageId);
    }

    private static MockMultipartFile jpeg() throws Exception {
        BufferedImage image = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.BLUE.getRGB());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);
        return new MockMultipartFile("file", "photo.jpg", "image/jpeg", out.toByteArray());
    }

    private long insertUser(String studentNo, String username, String email, String verifyStatus) {
        UserEntity user = new UserEntity();
        user.setStudentNoHash(hasher.hash(studentNo));
        user.setStudentNoMasked(hasher.mask(studentNo));
        user.setUsername(username);
        user.setNickname(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("FlowTest-1"));
        user.setStatus("ACTIVE");
        user.setVerifyStatus(verifyStatus);
        user.setTokenVersion(1);
        userMapper.insert(user);
        return user.getId();
    }

    private void cleanup() throws Exception {
        jdbc.update("""
                DELETE f FROM favorites f
                INNER JOIN users u ON u.id = f.user_id
                WHERE u.email IN ('seller-it@example.com', 'buyer-it@example.com')
                """);
        jdbc.update("""
                DELETE gi FROM goods_images gi
                INNER JOIN goods g ON g.id = gi.goods_id
                INNER JOIN users u ON u.id = g.seller_id
                WHERE u.email IN ('seller-it@example.com', 'buyer-it@example.com')
                """);
        jdbc.update("""
                DELETE g FROM goods g
                INNER JOIN users u ON u.id = g.seller_id
                WHERE u.email IN ('seller-it@example.com', 'buyer-it@example.com')
                """);
        jdbc.update("""
                DELETE v FROM campus_verifications v
                INNER JOIN users u ON u.id = v.user_id
                WHERE u.email IN ('seller-it@example.com', 'buyer-it@example.com')
                """);
        jdbc.update("""
                DELETE sf FROM stored_files sf
                INNER JOIN users u ON u.id = sf.owner_id
                WHERE u.email IN ('seller-it@example.com', 'buyer-it@example.com')
                """);
        jdbc.update("DELETE FROM users WHERE email IN ('seller-it@example.com', 'buyer-it@example.com')");
        if (buyerId > 0) {
            redis.delete(RedisKeys.history(buyerId));
        }
        if (Files.isDirectory(FILE_DIR)) {
            try (var paths = Files.walk(FILE_DIR)) {
                paths.sorted((a, b) -> b.compareTo(a)).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                        // 测试目录清不掉不影响断言
                    }
                });
            }
        }
    }
}
