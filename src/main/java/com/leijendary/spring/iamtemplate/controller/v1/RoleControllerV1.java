package com.leijendary.spring.iamtemplate.controller.v1;

import com.leijendary.spring.iamtemplate.controller.AbstractController;
import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.RolePermissionRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RoleRequestV1;
import com.leijendary.spring.iamtemplate.data.response.DataResponse;
import com.leijendary.spring.iamtemplate.data.response.v1.PermissionResponseV1;
import com.leijendary.spring.iamtemplate.data.response.v1.RoleResponseV1;
import com.leijendary.spring.iamtemplate.service.IamRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.leijendary.spring.iamtemplate.controller.AbstractController.BASE_API_PATH;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping(BASE_API_PATH + "/v1/roles")
@RequiredArgsConstructor
@Api("Role resource API. Roles are a group of permissions that can be assigned to a user")
public class RoleControllerV1 extends AbstractController {

    private final IamRoleService iamRoleService;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_urn:role:list:v1')")
    @ApiOperation("Get the paginated list of roles")
    public CompletableFuture<DataResponse<List<RoleResponseV1>>> list(
            final QueryRequest queryRequest, final Pageable pageable) {
        final var page = iamRoleService.list(queryRequest, pageable);
        final var response = DataResponse.<List<RoleResponseV1>>builder()
                .data(page.getContent())
                .meta(page)
                .links(page)
                .object(RoleResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_urn:role:create:v1')")
    @ResponseStatus(CREATED)
    @ApiOperation("Saves a role into the database. The name must be unique and at least one (1) permission")
    public CompletableFuture<DataResponse<RoleResponseV1>> create(
            @Valid @RequestBody final RoleRequestV1 request, final HttpServletResponse httpServletResponse) {
        final var roleResponse = iamRoleService.create(request);
        final var response = DataResponse.<RoleResponseV1>builder()
                .data(roleResponse)
                .status(CREATED)
                .object(RoleResponseV1.class)
                .build();

        locationHeader(httpServletResponse, roleResponse.getId());

        return completedFuture(response);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:role:get:v1')")
    @ApiOperation("Retrieves the role from the database")
    public CompletableFuture<DataResponse<RoleResponseV1>> get(@PathVariable final long id) {
        final var roleResponse = iamRoleService.get(id);
        final var response = DataResponse.<RoleResponseV1>builder()
                .data(roleResponse)
                .object(RoleResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:role:update:v1')")
    @ApiOperation("Updates the role into the database. The name should be unique and at least one (1) permission")
    public CompletableFuture<DataResponse<RoleResponseV1>> update(
            @PathVariable final long id, @Valid @RequestBody final RoleRequestV1 request) {
        final var roleResponse = iamRoleService.update(id, request);
        final var response = DataResponse.<RoleResponseV1>builder()
                .data(roleResponse)
                .object(RoleResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @DeleteMapping ("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:role:delete:v1')")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation("Removes the role from the database")
    public CompletableFuture<Void> delete(@PathVariable final long id) {
        iamRoleService.delete(id);

        return completedFuture(null);
    }

    @GetMapping("{id}/permissions")
    @PreAuthorize("hasAuthority('SCOPE_urn:role:permission:list:v1')")
    @ApiOperation("Retrieves the permissions of the role from the database")
    public CompletableFuture<DataResponse<Set<PermissionResponseV1>>> getPermissions(@PathVariable final long id) {
        final var permissions = iamRoleService.getPermissions(id);
        final var response = DataResponse.<Set<PermissionResponseV1>>builder()
                .data(permissions)
                .object(PermissionResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @PostMapping("{id}/permissions")
    @PreAuthorize("hasAuthority('SCOPE_urn:role:permission:add:v1')")
    @ApiOperation("Add add permission into the database")
    public CompletableFuture<DataResponse<Set<PermissionResponseV1>>> addPermissions(
            @PathVariable final long id, @Valid @RequestBody RolePermissionRequestV1 request) {
        final var permissions = iamRoleService.addPermissions(id, request);
        final var response = DataResponse.<Set<PermissionResponseV1>>builder()
                .data(permissions)
                .object(PermissionResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @DeleteMapping("{id}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('SCOPE_urn:role:permission:delete:v1')")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation("Removes the specific permission from the role")
    public CompletableFuture<Void> removePermission(
            @PathVariable final long id, @PathVariable final long permissionId) {
        iamRoleService.removePermission(id, permissionId);

        return completedFuture(null);
    }
}
