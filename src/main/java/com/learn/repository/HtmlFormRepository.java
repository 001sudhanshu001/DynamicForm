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


    @Query("SELECT COUNT(hf.id) FROM HtmlForm hf WHERE hf.id = :formId AND hf.appUser.email = :userName")
    Long countByUserNameAndFormId(@Param("userName") String userName, @Param("formId") Long formId);
//    @Query("SELECT COUNT(hff.id) FROM HtmlFormField hff WHERE hff.id = :formFieldId AND hff.htmlForm.id = :formId AND hff.htmlForm.appUser.email = : userName")
//    Long countFormAndFormId(@Param("userName") String userName, @Param("formId") Long formId, @Param("formFieldId") Long formFieldId);

    @Query("""
       SELECT COUNT(hff.id)
       FROM HtmlFormField hff
       JOIN hff.htmlForm hf
       JOIN hf.appUser u
       WHERE hff.id = :formFieldId
       AND hf.id = :formId
       AND u.email = :userName
       """)
    Long countByUserNameFormIdAndFormFieldId(
            @Param("userName") String userName,
            @Param("formId") Long formId,
            @Param("formFieldId") Long formFieldId
    );

    @Query("SELECT hf FROM HtmlForm hf WHERE hf.id = :formId AND hf.appUser.email = :userName")
    Optional<HtmlForm> findByIdAndUserName(@Param("userName") String userName, @Param("formId") Long formId);


}
