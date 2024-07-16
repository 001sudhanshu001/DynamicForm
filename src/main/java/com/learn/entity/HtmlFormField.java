package com.learn.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import com.learn.constants.FormFieldStatus;
import com.learn.constants.FormFieldValidationRule;
import com.learn.constants.InputType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "html_form_fields")
@Getter
@Setter
public class HtmlFormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Comment("type denotes input type in html, example: radio, checkbox, text etc")
    @Column(name = "type", nullable = false, updatable = false)
    private InputType type;

    @Comment("label to show on html form, above the input field")
    @Column(name = "label", nullable = false, length = 500)
    private String label;

    @Comment("validations to apply before saving/updating the form field")
    @Type(JsonType.class)
    @Column(name = "validation_rules", columnDefinition = "json", nullable = false)
    private Map<FormFieldValidationRule, String> validationRules;

    @Comment("sorting_order determine the form field order to show on frontend")
    @Column(name = "sorting_order", nullable = false)
    private Integer sortingOrder;

    @Comment("form field belong to the form")
    @ManyToOne(optional = false)
    @JoinColumn(name = "belong_to_html_form", nullable = false, updatable = false)
    private HtmlForm htmlForm;

    @Comment("determine that field is eligible to show on frontend or not")
    @Enumerated(EnumType.STRING)
    @Column(name = "form_field_status", nullable = false)
    private FormFieldStatus formFieldStatus;

    @Column(name = "remarks", nullable = false)
    private String remarks;

    @Column(name = "place_holder")
    private String placeHolder;

    @Column(name = "help_description")
    private String helpDescription;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Version
    private Long version;

    @Transient
    private Long formId;

    public boolean isActive() {
        return FormFieldStatus.ACTIVE.equals(formFieldStatus);
    }

    @Override
    public String toString() {
        return "HtmlFormField{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", label='" + label + '\'' +
                ", validationRules=" + validationRules +
                ", sortingOrder=" + sortingOrder +
                ", formFieldStatus=" + formFieldStatus +
                ", remarks='" + remarks + '\'' +
                ", placeHolder='" + placeHolder + '\'' +
                ", helpDescription='" + helpDescription + '\'' +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", version=" + version +
                ", formId=" + formId +
                '}';
    }
}
