package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVerificationResponseV1 {

    private String verificationCode;
}
