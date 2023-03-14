package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.AuditingUUIDEntity
import com.leijendary.spring.template.iam.core.entity.SoftDeleteEntity
import com.leijendary.spring.template.iam.core.extension.reflectSet
import com.leijendary.spring.template.iam.core.projection.PhoneProjection
import com.leijendary.spring.template.iam.entity.UserCredential.Type.EMAIL
import com.leijendary.spring.template.iam.entity.UserCredential.Type.PHONE
import com.leijendary.spring.template.iam.util.Status
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.FetchType.EAGER
import org.hibernate.annotations.Where
import java.time.OffsetDateTime

@Entity
@Where(clause = "deleted_at is null")
class User : AuditingUUIDEntity(), PhoneProjection, SoftDeleteEntity {
    var firstName: String? = null
    var middleName: String? = null
    var lastName: String? = null
    var email: String? = null
    var emailVerified: Boolean = false
    override var countryCode: String? = null
    override var phone: String? = null
    var phoneVerified: Boolean = false
    var image: String? = null
    var status = Status.ACTIVE

    @ManyToOne(cascade = [ALL])
    @JoinColumn
    var account: Account? = null

    @OneToOne
    @JoinColumn
    var role: Role? = null

    @OneToMany(mappedBy = "user", cascade = [ALL], fetch = EAGER)
    val credentials: MutableSet<UserCredential> = HashSet()

    @OneToMany(mappedBy = "user", cascade = [ALL])
    val devices: MutableSet<UserDevice> = HashSet()

    @OneToMany(mappedBy = "user", cascade = [ALL])
    val socials: MutableSet<UserSocial> = HashSet()

    @OneToMany(mappedBy = "user", cascade = [ALL])
    val addresses: MutableSet<UserAddress> = HashSet()

    override var deletedAt: OffsetDateTime? = null
    override var deletedBy: String? = null

    fun getUsername(field: String) = when (UserCredential.Type.from(field)) {
        EMAIL -> this.email!!
        PHONE -> this.fullPhone
    }

    fun setVerified(field: String, value: Boolean = true) = this.reflectSet("${field}Verified", value)
}
