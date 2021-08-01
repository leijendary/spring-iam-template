package com.leijendary.spring.iamtemplate.model;

import com.leijendary.spring.iamtemplate.model.listener.IamVerificationListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;

import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.now;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@EntityListeners({ AuditingEntityListener.class, IamVerificationListener.class })
public class IamVerification extends SnowflakeIdModel {

    @ManyToOne
    @JoinColumn(name = "iam_user_id")
    private IamUser user;

    private String code;
    private OffsetDateTime expiry;
    private String deviceId;
    private String field;
    private String type;

    @CreatedDate
    private OffsetDateTime createdDate;

    public IamVerification(final IamUser user, final String code, final int expiry, final String deviceId,
                           final String field, final String type) {
        this.user = user;
        this.code = code;
        this.expiry = now().plusMinutes(expiry);
        this.deviceId = deviceId;
        this.field = field;
        this.type = type;
    }
}
