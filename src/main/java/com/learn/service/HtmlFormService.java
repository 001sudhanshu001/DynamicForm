package com.learn.service;

import com.learn.entity.HtmlForm;
import com.learn.repository.HtmlFormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class HtmlFormService {
    private final HtmlFormRepository htmlFormRepository;

    @Transactional
    public HtmlForm save(HtmlForm htmlForm) {
        return htmlFormRepository.save(htmlForm);
    }
}
