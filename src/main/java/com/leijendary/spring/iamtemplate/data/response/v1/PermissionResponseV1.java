package com.leijendary.spring.iamtemplate.data.response.v1;

import lombok.Data;

import java.io.Serializable;

@Data
public class PermissionResponseV1 implements Serializable {

    private long id;
    private String permission;
}
