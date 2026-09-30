package com.campus.ershou.common.mail;

import com.campus.ershou.common.api.ApiException;
import com.campus.ershou.common.api.ErrorCode;
import com.campus.ershou.common.config.AppProperties;
import com.campus.ershou.common.util.Masking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final AppProperties props;
    private final ObjectProvider<JavaMailSender> mailSender;

    public MailService(AppProperties props, ObjectProvider<JavaMailSender> mailSender) {
        this.props = props;
        this.mailSender = mailSender;
    }

    public void sendVerifyCode(String to, String code) {
        if (!props.getMail().isEnabled()) {
            log.info("邮件未启用，跳过发送 recipient={}", Masking.email(to));
            return;
        }
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            throw ApiException.of(ErrorCode.UPSTREAM_UNAVAILABLE, "邮件服务不可用");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(props.getMail().getFrom());
        message.setTo(to);
        message.setSubject("校园二手交易平台验证码");
        message.setText("你的注册验证码是 " + code + "，10 分钟内有效。如非本人操作请忽略。");
        try {
            sender.send(message);
        } catch (MailException ex) {
            log.warn("发送验证码邮件失败 recipient={}", Masking.email(to));
            throw ApiException.of(ErrorCode.UPSTREAM_UNAVAILABLE, "邮件服务不可用");
        }
    }
}
