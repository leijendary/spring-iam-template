package com.leijendary.spring.iamtemplate.data.response.v1;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AccountResponseV1 {

    private long id;
    private String type;
    private String status;
    private OffsetDateTime createdDate;
    private String createdBy;
    private OffsetDateTime deactivatedDate;
    private String deactivatedBy;
}
