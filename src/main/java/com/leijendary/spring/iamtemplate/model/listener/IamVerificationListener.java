package com.leijendary.spring.iamtemplate.model.listener;

import com.leijendary.spring.iamtemplate.event.producer.NotificationProducer;
import com.leijendary.spring.iamtemplate.event.schema.NotificationSchema;
import com.leijendary.spring.iamtemplate.factory.UsernameFieldFactory;
import com.leijendary.spring.iamtemplate.model.IamVerification;
import org.springframework.context.MessageSource;

import javax.persistence.PostPersist;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.data.VerificationType.REGISTRATION;
import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.getLocale;
import static com.leijendary.spring.iamtemplate.util.SpringContext.getBean;
import static com.leijendary.spring.iamtemplate.util.UsernameUtil.getUsername;
import static java.lang.String.join;

public class IamVerificationListener {

    @PostPersist
    public void onSave(final IamVerification iamVerification) {
        final var field = iamVerification.getField();
        final var type = iamVerification.getType();
        final var iamUser = iamVerification.getUser();
        final var code = iamVerification.getCode();
        final var usernameField = UsernameFieldFactory.of(iamUser);
        final var to = getUsername(usernameField, field);

        if (type.equals(REGISTRATION) && field.equals(MOBILE_NUMBER)) {
            registrationSms(to, code, field);

            return;
        }

        if (type.equals(REGISTRATION) && field.equals(EMAIL_ADDRESS)) {
            final var name = join(" ", iamUser.getFirstName(), iamUser.getLastName());

            registrationEmail(to, name, code, field);
        }
    }

    private void registrationSms(final String to, final String code, final String field) {
        final var notificationProducer = getBean(NotificationProducer.class);
        final var messageSource = getBean(MessageSource.class);
        final var content = messageSource.getMessage("notification.verification.sms",
                new Object[] { code }, getLocale());

        final var notificationSchema = new NotificationSchema();
        notificationSchema.setTo(to);
        notificationSchema.setType(field);
        notificationSchema.setContent(content);

        // Send the notification schema to the notification sms topic
        notificationProducer.sms(notificationSchema);
    }

    private void registrationEmail(final String to, final String name, final String code, final String field) {

    }
}
