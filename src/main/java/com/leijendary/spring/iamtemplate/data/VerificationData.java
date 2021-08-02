package com.leijendary.spring.iamtemplate.data;

import lombok.Data;

@Data
public class VerificationData {

    private String deviceId;
    private String field;
    private String type;
    private String code;
}
