package com.experimentation.filestorage.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

/**
 * AuditModel is used for providing created_at and updated_at fields for auditing purposes.
 * This class will not have a table for itself.
 * Its subclasses will inherit the auditing fields and can have tables.
 * For createdAt and updatedAt, only getters are allowed for Jackson, it will be treated as read-only.
 *
 * References:
 *  https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/domain/support/
 *      AuditingEntityListener.html
 *  https://docs.oracle.com/javaee/5/api/javax/persistence/MappedSuperclass.html
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
public abstract class AuditModel implements Serializable {

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
