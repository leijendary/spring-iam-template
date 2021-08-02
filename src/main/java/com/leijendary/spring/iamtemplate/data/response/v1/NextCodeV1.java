package com.leijendary.spring.iamtemplate.data.response.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextCodeV1 {

    private String next;
    private String code;
}
