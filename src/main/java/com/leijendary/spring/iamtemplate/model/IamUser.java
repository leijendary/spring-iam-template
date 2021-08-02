package com.leijendary.spring.iamtemplate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;

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

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = EAGER)
    private Set<IamUserCredential> credentials = new HashSet<>();

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

    public boolean isIncomplete() {
        return firstName == null || lastName == null;
    }
}
