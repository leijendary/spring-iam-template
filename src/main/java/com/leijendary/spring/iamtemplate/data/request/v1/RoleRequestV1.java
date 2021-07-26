package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class RoleRequestV1 {

    @NotBlank(message = "validation.required")
    private String name;

    @NotBlank(message = "validation.required")
    private String description;

    @Valid
    @Min(message = "validation.atLeast", value = 1)
    private Set<PermissionRequestV1> permissions;
}
