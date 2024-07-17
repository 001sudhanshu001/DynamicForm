package com.learn.entity;

import com.learn.constants.FormStatus;
import com.learn.dto.internal.AddFormFieldResult;
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
import java.util.function.Predicate;

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

    @OneToMany(mappedBy = "htmlForm", fetch = FetchType.LAZY)
    private Set<FilledHtmlForm> filledHtmlForms;

    public boolean isActive() {
        return FormStatus.ACTIVE.equals(formStatus);
    }

    public AddFormFieldResult addHtmlFormField(HtmlFormField htmlFormField) {
        // Checking if the new Field added in the form is Unique or not
        String addingNewFieldWithName = htmlFormField.getName();

        Optional<HtmlFormField> probableFormFieldWithSameNameAsCurrentOne = htmlFormFields.stream()
                .filter(alreadyAddedFormField -> alreadyAddedFormField.fieldNameEqualsTo(addingNewFieldWithName))
                .findFirst();

        if (probableFormFieldWithSameNameAsCurrentOne.isEmpty()) {
            htmlFormFields.add(htmlFormField);
            htmlFormField.setHtmlForm(this);
            return AddFormFieldResult.successResult();
        }

        // If probableFormFieldWithSameNameAsCurrentOne is not Empty means there is already a filed with the same name
        String template = "Form Field With Name %s Already Exists In Form";
        return AddFormFieldResult.failResult(template.formatted(addingNewFieldWithName));
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
