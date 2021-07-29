package com.leijendary.spring.iamtemplate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class IamAccount extends SnowflakeIdModel {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "account")
    private Set<IamUser> users;

    private String type;
    private String status;

    @CreatedDate
    private OffsetDateTime createdDate;

    @CreatedBy
    private String createdBy;

    private OffsetDateTime deactivatedDate;
    private String deactivatedBy;
}
