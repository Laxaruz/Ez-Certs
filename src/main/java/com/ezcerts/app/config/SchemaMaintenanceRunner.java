package com.ezcerts.app.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaMaintenanceRunner {
    private static final int TIPO_CERTIFICADO_LENGTH = 30;

    private final JdbcTemplate jdbcTemplate;

    public SchemaMaintenanceRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureCertificadoTipoColumnLength() {
        Integer currentLength = jdbcTemplate.query(
                """
                SELECT CHARACTER_MAXIMUM_LENGTH
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'certificados'
                  AND column_name = 'tipo'
                """,
                rs -> rs.next() ? rs.getInt(1) : null
        );

        if (currentLength != null && currentLength < TIPO_CERTIFICADO_LENGTH) {
            jdbcTemplate.execute("ALTER TABLE certificados MODIFY COLUMN tipo VARCHAR(30) NOT NULL");
        }
    }
}
