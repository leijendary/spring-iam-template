package com.leijendary.spring.iamtemplate.controller.v1;

import com.leijendary.spring.iamtemplate.controller.AbstractController;
import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.UserQueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.PermissionRequestV1;
import com.leijendary.spring.iamtemplate.data.response.DataResponse;
import com.leijendary.spring.iamtemplate.data.response.v1.PermissionResponseV1;
import com.leijendary.spring.iamtemplate.service.IamUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.leijendary.spring.iamtemplate.controller.AbstractController.BASE_API_PATH;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping(BASE_API_PATH + "/v1/users")
@RequiredArgsConstructor
@Api("User resource API. All users that are not connected to an account are shown here")
public class UserControllerV1 extends AbstractController {

    private final IamUserService iamUserService;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_urn:user:list:v1')")
    @ApiOperation("Get the paginated list of users without an account")
    public CompletableFuture<DataResponse<List<PermissionResponseV1>>> list(
            final QueryRequest queryRequest, final UserQueryRequest userQueryRequest, final Pageable pageable) {
        final var page = iamUserService.list(queryRequest, userQueryRequest, pageable);
        final var response = DataResponse.<List<PermissionResponseV1>>builder()
                .data(page.getContent())
                .meta(page)
                .links(page)
                .object(PermissionResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_urn:user:create:v1')")
    @ResponseStatus(CREATED)
    @ApiOperation("Saves a permission into the database. The permission should be unique")
    public CompletableFuture<DataResponse<PermissionResponseV1>> create(
            @Valid @RequestBody final PermissionRequestV1 request, final HttpServletResponse httpServletResponse) {
        final var permissionResponse = iamUserService.create(request);
        final var response = DataResponse.<PermissionResponseV1>builder()
                .data(permissionResponse)
                .status(CREATED)
                .object(PermissionResponseV1.class)
                .build();

        locationHeader(httpServletResponse, permissionResponse.getId());

        return completedFuture(response);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:user:get:v1')")
    @ApiOperation("Retrieves the permission from the database")
    public CompletableFuture<DataResponse<PermissionResponseV1>> get(@PathVariable final long id) {
        final var permissionResponse = iamUserService.get(id);
        final var response = DataResponse.<PermissionResponseV1>builder()
                .data(permissionResponse)
                .object(PermissionResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:user:update:v1')")
    @ApiOperation("Updates the permission record into the database. The permission should be unique")
    public CompletableFuture<DataResponse<PermissionResponseV1>> update(
            @PathVariable final long id, @Valid @RequestBody final PermissionRequestV1 request) {
        final var permissionResponse = iamUserService.update(id, request);
        final var response = DataResponse.<PermissionResponseV1>builder()
                .data(permissionResponse)
                .object(PermissionRequestV1.class)
                .build();

        return completedFuture(response);
    }

    @DeleteMapping ("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:user:delete:v1')")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation("Removes the permission record from the database")
    public CompletableFuture<Void> delete(@PathVariable final long id) {
        iamUserService.delete(id);

        return completedFuture(null);
    }
}
