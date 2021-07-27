package com.leijendary.spring.iamtemplate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class IamUser extends SnowflakeIdModel {

    @ManyToOne
    @JoinColumn(name = "iam_account_id")
    private IamAccount account;

    @OneToOne
    @JoinColumn(name = "iam_role_id")
    private IamRole role;

    @OneToMany(mappedBy = "user")
    private Set<IamUserCredential> credentials;

    private String firstName;
    private String middleName;
    private String lastName;
    private String emailAddress;
    private String countryCode;
    private String mobileNumber;
    private String status;

    @CreatedDate
    private OffsetDateTime createdDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private OffsetDateTime lastModifiedDate;

    @LastModifiedBy
    private String lastModifiedBy;

    private OffsetDateTime deactivatedDate;
    private String deactivatedBy;
}
