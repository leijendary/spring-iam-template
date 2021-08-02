package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class NominatePasswordRequestV1 extends VerifyRequestV1 {

    @NotBlank(message = "validation.required")
    @Digits(message = "validation.digits.invalid", integer = 9999, fraction = 0)
    @Length(message = "validation.digits.exact", min = 4, max = 4)
    private String password;
}
