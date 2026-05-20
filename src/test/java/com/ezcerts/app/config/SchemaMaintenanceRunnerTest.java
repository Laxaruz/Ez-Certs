package com.ezcerts.app.config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

class SchemaMaintenanceRunnerTest {

    @Test
    void altersTipoColumnWhenCurrentLengthIsTooShort() {
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        when(jdbcTemplate.query(anyString(), org.mockito.ArgumentMatchers.<ResultSetExtractor<Integer>>any())).thenReturn(10);

        SchemaMaintenanceRunner runner = new SchemaMaintenanceRunner(jdbcTemplate);

        runner.ensureCertificadoTipoColumnLength();

        verify(jdbcTemplate).execute("ALTER TABLE certificados MODIFY COLUMN tipo VARCHAR(30) NOT NULL");
    }

    @Test
    void skipsAlterWhenColumnAlreadyHasExpectedLength() {
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        when(jdbcTemplate.query(anyString(), org.mockito.ArgumentMatchers.<ResultSetExtractor<Integer>>any())).thenReturn(30);

        SchemaMaintenanceRunner runner = new SchemaMaintenanceRunner(jdbcTemplate);

        runner.ensureCertificadoTipoColumnLength();

        verify(jdbcTemplate, never()).execute(anyString());
    }
}
