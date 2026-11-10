package com.ezcerts.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardViewsTest {
    @Autowired
    private MockMvc mvc;

    private MockHttpSession session(String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                "visual-check", "unused", List.of(new SimpleGrantedAuthority("ROLE_" + role))));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        return session;
    }

    @Test
    void rendersAdministratorViews() throws Exception {
        MockHttpSession admin = session("ADMIN");
        mvc.perform(get("/dashboard/admin").session(admin))
                .andExpect(status().isOk()).andExpect(view().name("dashboard-admin"))
                .andExpect(content().string(containsString("metrics-grid")));
        mvc.perform(get("/dashboard/admin/certificados-generados").session(admin))
                .andExpect(status().isOk()).andExpect(view().name("certificados-generados"))
                .andExpect(content().string(containsString("certificate-search")));
        mvc.perform(get("/dashboard/usuarios-activos").session(admin))
                .andExpect(status().isOk()).andExpect(view().name("usuarios-activos"));
        mvc.perform(get("/dashboard/usuarios-activos/crear").session(admin))
                .andExpect(status().isOk()).andExpect(view().name("crear-usuario"));
    }

    @Test
    void rendersEmployeeViewsAndRestrictsAdmin() throws Exception {
        MockHttpSession employee = session("EMPLEADO");
        mvc.perform(get("/dashboard/empleado").session(employee))
                .andExpect(status().isOk()).andExpect(view().name("dashboard-empleado"))
                .andExpect(content().string(containsString("employee-overview")));
        mvc.perform(get("/dashboard/empleado/solicitar-certificado").session(employee))
                .andExpect(status().isOk()).andExpect(view().name("solicitar-certificado"));
        mvc.perform(get("/dashboard/admin").session(employee))
                .andExpect(status().isForbidden());
    }
}

