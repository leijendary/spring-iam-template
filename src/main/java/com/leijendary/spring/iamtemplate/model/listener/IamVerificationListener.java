package com.leijendary.spring.iamtemplate.model.listener;

import com.leijendary.spring.iamtemplate.config.properties.VerificationProperties;
import com.leijendary.spring.iamtemplate.event.producer.NotificationProducer;
import com.leijendary.spring.iamtemplate.event.schema.NotificationSchema;
import com.leijendary.spring.iamtemplate.factory.UsernameFieldFactory;
import com.leijendary.spring.iamtemplate.generator.HtmlGenerator;
import com.leijendary.spring.iamtemplate.model.IamVerification;
import org.springframework.context.MessageSource;

import javax.persistence.PostPersist;
import java.util.HashMap;
import java.util.Map;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.data.VerificationType.REGISTRATION;
import static com.leijendary.spring.iamtemplate.data.VerificationType.RESET_PASSWORD;
import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.getLocale;
import static com.leijendary.spring.iamtemplate.util.SpringContext.getBean;
import static com.leijendary.spring.iamtemplate.util.UsernameUtil.getUsername;

public class IamVerificationListener {

    @PostPersist
    public void onSave(final IamVerification iamVerification) {
        final var field = iamVerification.getField();
        final var type = iamVerification.getType();
        final var iamUser = iamVerification.getUser();
        final var code = iamVerification.getCode();
        final var usernameField = UsernameFieldFactory.of(iamUser);
        final var to = getUsername(usernameField, field);

        // Registration SMS
        if (type.equals(REGISTRATION) && field.equals(MOBILE_NUMBER)) {
            final var content = registrationSms(code);

            // Sent the message to the SMS
            sendViaSms(to, field, content);

            return;
        }

        // Reset Password SMS
        if (type.equals(RESET_PASSWORD) && field.equals(MOBILE_NUMBER)) {
            final var content = resetPasswordSms(code);

            // Sent the message to the SMS
            sendViaSms(to, field, content);

            return;
        }

        // Registration Email
        if (type.equals(REGISTRATION) && field.equals(EMAIL_ADDRESS)) {
            final var name = iamUser.getFullName();
            final var content = registrationEmail(name, code);

            sendViaEmail(to, name, field, content);

            return;
        }

        // Reset Password Email
        if (type.equals(RESET_PASSWORD) && field.equals(EMAIL_ADDRESS)) {
            final var name = iamUser.getFullName();
            final var content = resetPasswordEmail(name, code);

            sendViaEmail(to, name, field, content);
        }
    }

    private String registrationSms(final String code) {
        final var messageSource = getBean(MessageSource.class);

        return messageSource.getMessage("notification.verification.sms", new Object[] { code }, getLocale());
    }

    private String resetPasswordSms(final String code) {
        final var messageSource = getBean(MessageSource.class);

        return messageSource.getMessage("notification.resetPassword.sms", new Object[] { code }, getLocale());
    }

    private String registrationEmail(final String name, final String code) {
        final var htmlGenerator = getBean(HtmlGenerator.class);
        final var verification = getBean(VerificationProperties.class);
        final var url = verification.getRegister().getUrl();
        final var parameters = buildParameters(name, code, url);

        return htmlGenerator.parse("register.verify", parameters);
    }

    private String resetPasswordEmail(final String name, final String code) {
        final var htmlGenerator = getBean(HtmlGenerator.class);
        final var verification = getBean(VerificationProperties.class);
        final var url = verification.getResetPassword().getUrl();
        final var parameters = buildParameters(name, code, url);

        return htmlGenerator.parse("reset-password.verify", parameters);
    }

    private void sendViaSms(final String to, final String field, final String content) {
        final var notificationProducer = getBean(NotificationProducer.class);
        final var notificationSchema = new NotificationSchema();
        notificationSchema.setTo(to);
        notificationSchema.setType(field);
        notificationSchema.setContent(content);

        // Send the notification schema to the notification sms topic
        notificationProducer.sms(notificationSchema);
    }

    private void sendViaEmail(final String to, final String name, final String field, final String content) {
        final var notificationProducer = getBean(NotificationProducer.class);
        final var notificationSchema = new NotificationSchema();
        notificationSchema.setTo(to);
        notificationSchema.setName(name);
        notificationSchema.setType(field);
        notificationSchema.setContent(content);

        // Send the notification schema to the notification email topic
        notificationProducer.email(notificationSchema);
    }

    private Map<String, Object> buildParameters(final String name, final String code, String url) {
        url = url.replace("{code}", code);

        final var parameters = new HashMap<String, Object>();
        parameters.put("name", name);
        parameters.put("url", url);

        return parameters;
    }
}
