package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class IdRequestV1 {

    @Min(value = 1, message = "validation.id.invalid")
    private long id;
}
