package com.leijendary.spring.iamtemplate.data.response.v1;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CredentialResponseV1 {

    private String username;
    private String type;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastUsedDate;
}
