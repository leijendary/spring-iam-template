package com.leijendary.spring.iamtemplate.data.response.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVerifyResponseV1 {

    private long verificationId;
    private String code;
}
