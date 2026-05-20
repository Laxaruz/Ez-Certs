package com.ezcerts.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.service.UsuarioService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LoginControllerTest {

    @Test
    void redirectsToSetupWhenNoUsersExist() {
        UsuarioService usuarioService = Mockito.mock(UsuarioService.class);
        when(usuarioService.listar()).thenReturn(Collections.emptyList());

        LoginController controller = new LoginController(usuarioService);

        assertEquals("redirect:/setup/crear-usuario", controller.login());
    }

    @Test
    void showsLoginWhenUsersAlreadyExist() {
        UsuarioService usuarioService = Mockito.mock(UsuarioService.class);
        when(usuarioService.listar()).thenReturn(List.of(new Usuario()));

        LoginController controller = new LoginController(usuarioService);

        assertEquals("login", controller.login());
    }
}
