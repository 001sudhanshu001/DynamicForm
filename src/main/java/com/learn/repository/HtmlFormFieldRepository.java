package com.learn.repository;

import com.learn.constants.FormFieldStatus;
import com.learn.entity.HtmlFormField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface HtmlFormFieldRepository extends JpaRepository<HtmlFormField, Long> {

    @Modifying
    @Query("UPDATE HtmlFormField f SET f.formFieldStatus = ?2 WHERE f.id = ?1")
    void updateActiveStatus(Long formFieldId, FormFieldStatus status);
}
