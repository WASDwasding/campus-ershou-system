package com.campus.ershou.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class SecretGuard implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SecretGuard.class);

    private final AppProperties props;
    private final Environment env;

    public SecretGuard(AppProperties props, Environment env) {
        this.props = props;
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        byte[] jwt = props.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        byte[] hmac = props.getStudentNo().getHmacKey().getBytes(StandardCharsets.UTF_8);
        if (jwt.length < 32) {
            throw new IllegalStateException("app.jwt.secret 至少 32 字节");
        }
        if (hmac.length < 16) {
            throw new IllegalStateException("app.student-no.hmac-key 至少 16 字节");
        }
        if (props.getFile().getSignKey().getBytes(StandardCharsets.UTF_8).length < 16) {
            throw new IllegalStateException("app.file.sign-key 至少 16 字节");
        }
        if (props.getPay().getMock().getSecret().getBytes(StandardCharsets.UTF_8).length < 16) {
            throw new IllegalStateException("app.pay.mock.secret 至少 16 字节");
        }
        boolean local = Arrays.asList(env.getActiveProfiles()).contains("local")
                || (env.getActiveProfiles().length == 0 && "local".equals(env.getProperty("spring.profiles.default")));
        if (!local && (props.getJwt().getSecret().startsWith("local-only")
                || props.getStudentNo().getHmacKey().startsWith("local-only")
                || props.getJwt().getSecret().contains("PleaseChange")
                || props.getStudentNo().getHmacKey().contains("PleaseChange")
                || props.getFile().getSignKey().startsWith("local-only")
                || props.getPay().getMock().getSecret().startsWith("local-only"))) {
            throw new IllegalStateException("非 local 环境必须覆盖 JWT 与学号 HMAC 密钥");
        }
        if (props.getMail().isEnabled() && props.getMail().isExposeCode()) {
            throw new IllegalStateException("启用真实邮件时禁止 expose-code");
        }
        log.info("密钥检查通过 local={} mailEnabled={}", local, props.getMail().isEnabled());
    }
}
