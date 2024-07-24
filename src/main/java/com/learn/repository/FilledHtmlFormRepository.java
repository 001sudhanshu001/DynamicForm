package com.learn.repository;

import com.learn.entity.FilledHtmlForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FilledHtmlFormRepository extends JpaRepository<FilledHtmlForm, Long> {
    @Query("select f from FilledHtmlForm f where f.htmlForm.id = :formId and f.user.id = :userId")
    Optional<FilledHtmlForm> findByFormIdAndUserId(@Param("formId") Long formId, @Param("userId") Long userId);
}