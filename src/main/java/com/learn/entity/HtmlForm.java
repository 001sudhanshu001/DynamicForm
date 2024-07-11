package com.learn.entity;

import com.learn.constants.FormStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

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

}
