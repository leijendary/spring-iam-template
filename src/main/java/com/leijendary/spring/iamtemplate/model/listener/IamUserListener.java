package com.leijendary.spring.iamtemplate.model.listener;

import com.leijendary.spring.iamtemplate.event.producer.UserProducer;
import com.leijendary.spring.iamtemplate.model.IamUser;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import static com.leijendary.spring.iamtemplate.factory.IamUserFactory.toSchema;
import static com.leijendary.spring.iamtemplate.util.SpringContext.getBean;

public class IamUserListener {

    @PostPersist
    public void onSave(final IamUser iamUser) {
        final var authProducer = getBean(UserProducer.class);
        final var authSchema = toSchema(iamUser);

        authProducer.create(authSchema);
    }

    @PostUpdate
    public void onUpdate(final IamUser iamUser) {
        final var authProducer = getBean(UserProducer.class);
        final var authSchema = toSchema(iamUser);

        if (iamUser.getDeactivatedDate() != null) {
            authProducer.update(authSchema);
        } else {
            // Deactivation is a part of update
            authProducer.deactivate(authSchema);
        }
    }
}
