package com.ezcerts.app.service;

import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.repository.UsuarioRepository;
import java.util.Collections;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrCorreo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(usernameOrCorreo)
                .or(() -> usuarioRepository.findByCorreo(usernameOrCorreo))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new DisabledException("Usuario inactivo");
        }

        return User.withUsername(usuario.getUsername())
                .password(usuario.getContrasenaHash())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())))
                .build();
    }
}

