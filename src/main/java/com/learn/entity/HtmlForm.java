package com.learn.entity;

import com.learn.constants.FormStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Getter @Setter
@Table(
        name = "html_forms",
        indexes = {
                @Index(name = "html_form_name_unique_idx", columnList = "name", unique = true)
        }
)
public class HtmlForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "remark", nullable = false)
    private String remark;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Version
    private Long version;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "form_status", nullable = false)
    private FormStatus formStatus;

    @Column(name = "auto_disable_submission_after_date")
    private LocalDateTime autoDisableSubmissionAfterDate;

    @OneToMany(
            mappedBy = "htmlForm",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private Set<HtmlFormField> htmlFormFields = new LinkedHashSet<>();

    public boolean isActive() {
        return FormStatus.ACTIVE.equals(formStatus);
    }

    public void addHtmlFormField(HtmlFormField htmlFormField) {
        htmlFormFields.add(htmlFormField);
        htmlFormField.setHtmlForm(this);
    }

    public Optional<HtmlFormField> htmlFormFieldHavingName(String formFieldName) {
        return htmlFormFields.stream()
                .filter(htmlFormField -> StringUtils.equals(htmlFormField.getName(), formFieldName))
                .findFirst();
    }

    @Override
    public String toString() {
        return "HtmlForm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", version=" + version +
                ", formStatus=" + formStatus +
                ", autoDisableSubmissionAfterDate=" + autoDisableSubmissionAfterDate +
                '}';
    }
}
