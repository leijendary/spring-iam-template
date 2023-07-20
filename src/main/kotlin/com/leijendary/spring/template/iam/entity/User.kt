package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.AuditingUUIDEntity
import com.leijendary.spring.template.iam.core.entity.SoftDeleteEntity
import com.leijendary.spring.template.iam.core.extension.reflectSet
import com.leijendary.spring.template.iam.core.projection.PhoneProjection
import com.leijendary.spring.template.iam.entity.UserCredential.Type.EMAIL
import com.leijendary.spring.template.iam.entity.UserCredential.Type.PHONE
import com.leijendary.spring.template.iam.model.Status.ACTIVE
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.EAGER
import org.hibernate.annotations.Where
import java.time.OffsetDateTime

@Entity
@Where(clause = "deleted_at is null")
class User : AuditingUUIDEntity(), PhoneProjection, SoftDeleteEntity {
    lateinit var firstName: String
    var middleName: String? = null
    lateinit var lastName: String
    lateinit var email: String
    var emailVerified: Boolean = false
    override var countryCode: String? = null
    override var phone: String? = null
    var phoneVerified: Boolean = false
    var image: String? = null

    @Enumerated(STRING)
    var status = ACTIVE

    @ManyToOne(cascade = [ALL])
    var account: Account? = null

    @OneToOne
    var role: Role? = null

    @OneToMany(mappedBy = "user", cascade = [ALL], fetch = EAGER)
    val credentials: MutableSet<UserCredential> = HashSet()

    @OneToMany(mappedBy = "user", cascade = [ALL])
    val socials: MutableSet<UserSocial> = HashSet()

    @OneToMany(mappedBy = "user", cascade = [ALL])
    val addresses: MutableSet<UserAddress> = HashSet()

    override var deletedAt: OffsetDateTime? = null
    override var deletedBy: String? = null
    var deletedReason: String? = null

    fun getUsername(field: UserCredential.Type) = when (field) {
        EMAIL -> this.email
        PHONE -> this.fullPhone
    }

    fun setVerified(field: UserCredential.Type, value: Boolean = true) {
        this.reflectSet("${field.value}Verified", value)
    }
}
