package com.learn.repository;

import com.learn.entity.HtmlForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HtmlFormRepository extends JpaRepository<HtmlForm, Long> {
}
