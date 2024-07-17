package com.learn.repository;

import com.learn.entity.FilledHtmlForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilledHtmlFormRepository extends JpaRepository<FilledHtmlForm, Long> {
}