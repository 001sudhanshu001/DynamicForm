package com.learn.repository.custom;

import com.learn.dto.dynamicfilter.AbstractFieldFilter;
import com.learn.dto.dynamicfilter.AppliedDynamicFilter;
import com.learn.dto.response.FilledHtmlFormResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilledHtmlFormRepositoryImpl implements CustomFilledHtmlFormRepository{

    private final EntityManager entityManager;

    @Override
    public List<FilledHtmlFormResponse> filterFilledForms(AppliedDynamicFilter appliedDynamicFilter) {
        appliedDynamicFilter.resolveWhereClausesWithQueryParams();

        // TODO -> Apply HTML Form_id or By the userName of the Created BY
        String baseQuery = "SELECT f.id, f.form_field_values from filled_html_forms f where '1' = '1'";
        StringBuilder queryTemplate = new StringBuilder(baseQuery);

        List<String> whereClauses = appliedDynamicFilter.getWhereClauses();
        for(String condition : whereClauses) {
            queryTemplate
                    .append(" and ")
                    .append(condition.replace(AbstractFieldFilter.COLUMN_NAME_INDICATOR, "f.form_field_values"));
        }

        String query = queryTemplate.toString();
        Query nativeQuery = entityManager.createNativeQuery(query, FilledHtmlFormResponse.class);
        List<Object> queryParameters = appliedDynamicFilter.getQueryParameters();

        for(int i = 0;i < queryParameters.size(); i++) {
            nativeQuery.setParameter(i + 1, queryParameters.get(i));
        }

        return nativeQuery.getResultList();
    }
}
