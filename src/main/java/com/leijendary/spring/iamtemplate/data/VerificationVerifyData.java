package com.leijendary.spring.iamtemplate.data;

import lombok.Data;

@Data
public class VerificationVerifyData {

    private long verificationId;
    private String deviceId;
    private String code;
    private String type;
}
