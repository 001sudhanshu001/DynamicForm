package com.learn.repository;

import com.learn.constants.FormFieldStatus;
import com.learn.entity.HtmlForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HtmlFormRepository extends JpaRepository<HtmlForm, Long> {

    @Query("""
        select hf
        from HtmlForm hf
        join fetch hf.htmlFormFields hff
        where hf.id = :formId and hff.formFieldStatus = :status
    """)
    Optional<HtmlForm> getHtmlFormWithActiveFormFields(@Param("formId") Long formId,
                                                        @Param("status") FormFieldStatus status);
}
