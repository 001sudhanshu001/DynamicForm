package com.learn.repository;

import com.learn.entity.FilledHtmlForm;
import com.learn.repository.custom.CustomFilledHtmlFormRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FilledHtmlFormRepository extends
        JpaRepository<FilledHtmlForm, Long>, CustomFilledHtmlFormRepository {

    @Query("select f from FilledHtmlForm f where f.htmlForm.id = :formId and f.appUser.id = :userId")
    Optional<FilledHtmlForm> findByFormIdAndUserId(@Param("formId") Long formId,
                                                   @Param("userId") Long userId);

    @Query("select f from FilledHtmlForm f where f.htmlForm.id = :formId and f.appUser.email = :userName")
    Optional<FilledHtmlForm> findByFilledFormIdAndUserName(@Param("formId") Long formId,
                                                   @Param("userName") String userName);
}