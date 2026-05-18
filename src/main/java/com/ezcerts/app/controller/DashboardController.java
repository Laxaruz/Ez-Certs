package com.ezcerts.app.controller;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (roles.contains("ROLE_EMPLEADO")) {
            return "redirect:/dashboard/empleado";
        }

        if (roles.contains("ROLE_RRHH") || roles.contains("ROLE_ADMIN")) {
            return "redirect:/dashboard/admin";
        }

        return "redirect:/login";
    }

    @GetMapping("/dashboard/empleado")
    public String dashboardEmpleado() {
        return "dashboard-empleado";
    }

    @GetMapping("/dashboard/admin")
    public String dashboardAdmin() {
        return "dashboard-admin";
    }
}

