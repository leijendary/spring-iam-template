package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.Data;

@Data
public class VerifyRequestV1 {

    private String verificationId;
    private String code;
}
