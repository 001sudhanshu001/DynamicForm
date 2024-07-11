package com.learn.repository;

import com.learn.entity.HtmlFormField;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HtmlFormFieldRepository extends JpaRepository<HtmlFormField, Long> {
}
