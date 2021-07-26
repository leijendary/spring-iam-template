package com.leijendary.spring.iamtemplate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class IamRolePermission extends IdentityIdModel {

    @OneToOne
    @JoinColumn(name = "iam_role_id")
    private IamRole role;

    @OneToOne
    @JoinColumn(name = "iam_permission_id")
    private IamPermission permission;
}
