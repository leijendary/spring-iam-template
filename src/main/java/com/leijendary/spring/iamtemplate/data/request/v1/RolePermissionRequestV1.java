package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class RolePermissionRequestV1 {

    @Valid
    @Size(min = 1, message = "validation.atLeast")
    private Set<IdRequestV1> permissions;
}
