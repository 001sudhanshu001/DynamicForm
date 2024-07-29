package com.learn.entity;

import com.learn.constants.TableNames;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Table(
        name = TableNames.FILLED_HTML_FORMS,
        indexes = {
                @Index(
                        name = "user_filled_html_form_combine_unique_idx",
                        unique = true,
                        columnList = "html_form_id, filled_by_user_id"
                ) // This makes sure that one student is allowed to fill form only once
        }
)
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class FilledHtmlForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "html_form_id", nullable = false, updatable = false)
    private HtmlForm htmlForm;

    @ManyToOne
    @JoinColumn(name = "filled_by_user_id", nullable = false, updatable = false)
    private AppUser appUser;

    @Type(JsonType.class)
    @Column(name = "form_field_values", columnDefinition = "json", nullable = false)
    private Map<String, Object> formFieldValues = new LinkedHashMap<>();

    @Version
    private Long version;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public void putValueInFormField(String fieldName, Object value) {
        formFieldValues.put(fieldName, value);
    }

}
