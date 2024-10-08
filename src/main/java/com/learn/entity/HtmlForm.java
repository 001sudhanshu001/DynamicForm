package com.learn.entity;

import com.learn.constants.FormFieldStatus;
import com.learn.constants.FormStatus;
import com.learn.dto.internal.AddFormFieldResult;
import com.learn.dto.internal.FieldStatusChangeResult;
import com.learn.dto.internal.FieldValidationResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;

import javax.sound.midi.Soundbank;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

@Entity
@Getter @Setter
@Table(name = "html_forms")
@EntityListeners(AuditingEntityListener.class)
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

    private LocalDateTime lastDateUpToWhichFormCanBeFilled;

    @Version
    private Long version;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "form_status", nullable = false)
    private FormStatus formStatus;

    @Column(name = "auto_disable_submission_after_date")
    private LocalDateTime autoDisableSubmissionAfterDate;

    @ManyToOne
    @JoinColumn(name = "form_belongs_to_user_id")
    private AppUser appUser;

    @OneToMany(
            mappedBy = "htmlForm",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private Set<HtmlFormField> htmlFormFields = new HashSet<>();

    @OneToMany(mappedBy = "htmlForm", fetch = FetchType.LAZY)
    private Set<FilledHtmlForm> filledHtmlForms = new HashSet<>();

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

    public Optional<HtmlFormField> htmlFormFieldHavingName(String formFieldName,
                                                           Predicate<HtmlFormField> predicate) {
        return htmlFormFields.stream()
                .filter(htmlFormField -> StringUtils.equals(htmlFormField.getName(), formFieldName))
                .filter(predicate)
                .findFirst();
    }

    public FieldStatusChangeResult changeFormFieldStatus(Long formFieldId, boolean requestToMakeActive) {
        Optional<HtmlFormField> optionalHtmlFormField = htmlFormFields.stream()
                .filter(htmlFormField -> htmlFormField.getId().equals(formFieldId))
                .findFirst();

        String msg = requestToMakeActive ? "Activate" : "InActive";
        if (optionalHtmlFormField.isEmpty()) {
            String notFoundMessage = "Can't " + msg + " HtmlFormField, Not Found With Given Id:: " + formFieldId;
            return FieldStatusChangeResult.failResult(HttpStatus.NOT_FOUND, notFoundMessage);
        }

        HtmlFormField htmlFormField = optionalHtmlFormField.get();
        htmlFormField.setFormFieldStatus(FormFieldStatus.ACTIVE);

        if (formStatus.equals(FormStatus.NO_ACTIVE_FORM_FIELD)) {
            formStatus = FormStatus.ACTIVE;
        }

        return FieldStatusChangeResult.successResult();
    }

    public boolean changeDisplayName(String fieldNameToChangeDisplayName,
                                     String newDisplayNameForField) {

         return htmlFormFieldHavingName(fieldNameToChangeDisplayName)
                .map(htmlFormField -> {
                    htmlFormField.setDisplayName(newDisplayNameForField);
                    return htmlFormField;
                })
                .isPresent();
    }

    public boolean setFieldDisplayOrder(String fieldName, int displayOrder) {
        return htmlFormFieldHavingName(fieldName)
                .map(htmlFormField -> {
                    htmlFormField.setDisplayOrder(displayOrder);
                    return htmlFormField.getId();
                })
                .isPresent();
    }


     /* -------------------- Validation ------------------ */

    public Set<String> missingRequiredFields(Set<String> providedFields) {
        Set<String> missingFields = new HashSet<>();
        for (HtmlFormField field : htmlFormFields) {
            if (field.isActive() && field.isMandatoryField() && !providedFields.contains(field.getName())) {
                missingFields.add(field.getName());
            }
        }
        return missingFields;
    }

    public FieldValidationResult validateFieldValue(String formFieldName, Object formFieldValue) {
        return htmlFormFields.stream()
                .filter(HtmlFormField::isActive)
                .filter(htmlFormField -> StringUtils.equals(htmlFormField.getName(), formFieldName)) // selecting a field whose name is as in the payload
                .map(htmlFormField -> htmlFormField.validateValue(formFieldValue)) // for each filed checking the value is valid or not
                .toList() // called method of the field itself
                .getFirst();
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
