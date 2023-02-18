package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.RolePermission
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RolePermissionRepository : JpaRepository<RolePermission, Long> {
    fun deleteAllByRoleIdAndPermissionId(roleId: UUID, permissionId: Long)

    fun deleteAllByPermissionId(permissionId: Long)

    fun findAllByRoleId(roleId: UUID): List<RolePermission>
}
