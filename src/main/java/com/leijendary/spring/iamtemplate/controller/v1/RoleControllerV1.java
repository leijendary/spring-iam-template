package com.leijendary.spring.iamtemplate.controller.v1;

import com.leijendary.spring.iamtemplate.controller.AbstractController;
import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.SampleRequestV1;
import com.leijendary.spring.iamtemplate.data.response.DataResponse;
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
        final var response = DataResponse.<List<SampleResponseV1>>builder()
                .data(page.getContent())
                .meta(page)
                .links(page)
                .object(SampleResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_urn:role:create:v1')")
    @ResponseStatus(CREATED)
    @ApiOperation("Saves a sample record into the database")
    public CompletableFuture<DataResponse<SampleResponseV1>> create(
            @Valid @RequestBody final SampleRequestV1 request, final HttpServletResponse httpServletResponse) {
        final var sampleResponse = iamRoleService.create(request);
        final var response = DataResponse.<SampleResponseV1>builder()
                .data(sampleResponse)
                .status(CREATED)
                .object(SampleResponseV1.class)
                .build();

        locationHeader(httpServletResponse, sampleResponse.getId());

        return completedFuture(response);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:role:get:v1')")
    @ApiOperation("Retrieves the sample record from the database")
    public CompletableFuture<DataResponse<SampleResponseV1>> get(@PathVariable final long id) {
        final var sampleResponse = iamRoleService.get(id);
        final var response = DataResponse.<SampleResponseV1>builder()
                .data(sampleResponse)
                .object(SampleResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:role:update:v1')")
    @ApiOperation("Updates the sample record into the database")
    public CompletableFuture<DataResponse<SampleResponseV1>> update(
            @PathVariable final long id, @Valid @RequestBody final SampleRequestV1 request) {
        final var sampleResponse = iamRoleService.update(id, request);
        final var response = DataResponse.<SampleResponseV1>builder()
                .data(sampleResponse)
                .object(SampleResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @DeleteMapping ("{id}")
    @PreAuthorize("hasAuthority('SCOPE_urn:role:delete:v1')")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation("Removes the sample record from the database")
    public CompletableFuture<Void> delete(@PathVariable final long id) {
        iamRoleService.delete(id);

        return completedFuture(null);
    }
}
