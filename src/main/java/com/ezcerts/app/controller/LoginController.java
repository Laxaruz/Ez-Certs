package com.ezcerts.app.controller;

import com.ezcerts.app.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        if (usuarioService.listar().isEmpty()) {
            return "redirect:/setup/crear-usuario";
        }
        return "login";
    }
}
