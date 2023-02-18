package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.AuditingUUIDEntity
import com.leijendary.spring.template.iam.core.entity.SoftDeleteEntity
import com.leijendary.spring.template.iam.core.extension.reflectGet
import com.leijendary.spring.template.iam.util.Status
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.FetchType.EAGER
import org.hibernate.annotations.Where
import java.time.OffsetDateTime

@Entity
@Where(clause = "deleted_at is null")
class User : AuditingUUIDEntity(), SoftDeleteEntity {
    var firstName: String? = null
    var middleName: String? = null
    var lastName: String? = null
    var email: String? = null
    var emailVerified: Boolean = false
    var countryCode: String? = null
    var phone: String? = null
    var phoneVerified: Boolean = false
    var status = Status.ACTIVE

    @ManyToOne(cascade = [ALL])
    @JoinColumn
    var account: Account? = null

    @OneToOne
    @JoinColumn
    var role: Role? = null

    @OneToMany(mappedBy = "user", cascade = [ALL], fetch = EAGER)
    val credentials: MutableSet<UserCredential> = HashSet()

    override var deletedAt: OffsetDateTime? = null
    override var deletedBy: String? = null

    val fullName: String
        get() = if (isIncomplete) "" else "$firstName $lastName"

    val isIncomplete: Boolean
        get() = firstName.isNullOrBlank() or firstName.isNullOrBlank()

    fun getUsername(field: String) = this.reflectGet(field) as String
}
