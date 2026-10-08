package com.campus.ershou.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @NestedConfigurationProperty
    private final Jwt jwt = new Jwt();
    @NestedConfigurationProperty
    private final StudentNo studentNo = new StudentNo();
    @NestedConfigurationProperty
    private final Mail mail = new Mail();
    @NestedConfigurationProperty
    private final Register register = new Register();
    @NestedConfigurationProperty
    private final Login login = new Login();
    @NestedConfigurationProperty
    private final FileStorage file = new FileStorage();

    public Jwt getJwt() {
        return jwt;
    }

    public StudentNo getStudentNo() {
        return studentNo;
    }

    public Mail getMail() {
        return mail;
    }

    public Register getRegister() {
        return register;
    }

    public Login getLogin() {
        return login;
    }

    public FileStorage getFile() {
        return file;
    }

    public static class Jwt {
        private String secret = "";
        private Duration userTtl = Duration.ofDays(7);
        private Duration adminTtl = Duration.ofHours(12);

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Duration getUserTtl() {
            return userTtl;
        }

        public void setUserTtl(Duration userTtl) {
            this.userTtl = userTtl;
        }

        public Duration getAdminTtl() {
            return adminTtl;
        }

        public void setAdminTtl(Duration adminTtl) {
            this.adminTtl = adminTtl;
        }
    }

    public static class StudentNo {
        private String hmacKey = "";

        public String getHmacKey() {
            return hmacKey;
        }

        public void setHmacKey(String hmacKey) {
            this.hmacKey = hmacKey;
        }
    }

    public static class Mail {
        private boolean enabled = false;
        private boolean exposeCode = true;
        private String from = "campus-ershou@localhost";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isExposeCode() {
            return exposeCode;
        }

        public void setExposeCode(boolean exposeCode) {
            this.exposeCode = exposeCode;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }
    }

    public static class Register {
        private Duration codeTtl = Duration.ofMinutes(10);
        private Duration setupTokenTtl = Duration.ofMinutes(15);
        private Duration sendCooldown = Duration.ofSeconds(60);
        private int codeAttempts = 5;
        private int ipSendLimitPerMinute = 10;

        public Duration getCodeTtl() {
            return codeTtl;
        }

        public void setCodeTtl(Duration codeTtl) {
            this.codeTtl = codeTtl;
        }

        public Duration getSetupTokenTtl() {
            return setupTokenTtl;
        }

        public void setSetupTokenTtl(Duration setupTokenTtl) {
            this.setupTokenTtl = setupTokenTtl;
        }

        public Duration getSendCooldown() {
            return sendCooldown;
        }

        public void setSendCooldown(Duration sendCooldown) {
            this.sendCooldown = sendCooldown;
        }

        public int getCodeAttempts() {
            return codeAttempts;
        }

        public void setCodeAttempts(int codeAttempts) {
            this.codeAttempts = codeAttempts;
        }

        public int getIpSendLimitPerMinute() {
            return ipSendLimitPerMinute;
        }

        public void setIpSendLimitPerMinute(int ipSendLimitPerMinute) {
            this.ipSendLimitPerMinute = ipSendLimitPerMinute;
        }
    }

    public static class Login {
        private Duration failWindow = Duration.ofMinutes(15);
        private int failLimit = 5;
        private Duration lockTtl = Duration.ofMinutes(15);

        public Duration getFailWindow() {
            return failWindow;
        }

        public void setFailWindow(Duration failWindow) {
            this.failWindow = failWindow;
        }

        public int getFailLimit() {
            return failLimit;
        }

        public void setFailLimit(int failLimit) {
            this.failLimit = failLimit;
        }

        public Duration getLockTtl() {
            return lockTtl;
        }

        public void setLockTtl(Duration lockTtl) {
            this.lockTtl = lockTtl;
        }
    }

    public static class FileStorage {
        private String dir = ".data/files";
        private String signKey = "";
        private String publicBaseUrl = "http://127.0.0.1:8080";
        private Duration privateUrlTtl = Duration.ofMinutes(10);

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public String getSignKey() {
            return signKey;
        }

        public void setSignKey(String signKey) {
            this.signKey = signKey;
        }

        public String getPublicBaseUrl() {
            return publicBaseUrl;
        }

        public void setPublicBaseUrl(String publicBaseUrl) {
            this.publicBaseUrl = publicBaseUrl;
        }

        public Duration getPrivateUrlTtl() {
            return privateUrlTtl;
        }

        public void setPrivateUrlTtl(Duration privateUrlTtl) {
            this.privateUrlTtl = privateUrlTtl;
        }
    }
}
