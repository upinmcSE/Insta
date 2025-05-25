package init.upinmcse.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "created_at", length = 255)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at", length = 255)
    @UpdateTimestamp
    private Date updatedAt;
}
