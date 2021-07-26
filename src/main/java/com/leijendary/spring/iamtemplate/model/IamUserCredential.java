package com.leijendary.spring.iamtemplate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class IamUserCredential extends IdentityIdModel {

    @ManyToOne
    @JoinColumn(name = "iam_user_id")
    private IamUser user;

    private String username;
    private String password;

    @CreatedDate
    private OffsetDateTime createdDate;

    private OffsetDateTime lastUsedDate;
}
