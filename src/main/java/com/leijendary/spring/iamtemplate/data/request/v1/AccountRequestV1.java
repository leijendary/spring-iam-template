package com.leijendary.spring.iamtemplate.data.request.v1;

import com.leijendary.spring.iamtemplate.validator.annotation.v1.AccountTypeV1;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestV1 {

    @NotNull(message = "validation.required")
    @AccountTypeV1
    private String type;
}
