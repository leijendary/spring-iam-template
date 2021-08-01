package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class IdRequestV1 {

    @Min(message = "validation.id.invalid", value = 1)
    private long id;
}
