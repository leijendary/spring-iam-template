package com.leijendary.spring.iamtemplate.data.request.v1;

import com.leijendary.spring.iamtemplate.validator.annotation.v1.AccountTypeV1;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AccountRequestV1 {

    @NotEmpty(message = "validation.required")
    @AccountTypeV1
    private String type;
}