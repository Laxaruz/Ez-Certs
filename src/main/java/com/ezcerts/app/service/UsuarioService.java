package com.ezcerts.app.service;

import com.ezcerts.app.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario crearUsuario(Usuario usuario);

    Usuario actualizarUsuario(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsername(String username);

    Optional<Usuario> buscarPorCorreo(String correo);

    List<Usuario> listar();

    void inactivarUsuario(Long id);
}

