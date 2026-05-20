package com.ezcerts.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.ezcerts.app.model.Rol;
import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class CustomAuthenticationProviderTest {

    @Test
    void authenticatesExistingBcryptPasswords() {
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(usuarioRepository, passwordEncoder);

        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setCorreo("admin@ezcerts.com");
        usuario.setActivo(true);
        usuario.setRol(Rol.ADMIN);
        usuario.setContrasenaHash(passwordEncoder.encode("secreto123"));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        Authentication authentication = provider.authenticate(
                new UsernamePasswordAuthenticationToken("admin", "secreto123")
        );

        assertNotNull(authentication);
        assertEquals("admin", authentication.getName());
        assertEquals(1, authentication.getAuthorities().size());
    }

    @Test
    void keepsSupportingLegacyPlaintextPasswords() {
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        CustomAuthenticationProvider provider =
                new CustomAuthenticationProvider(usuarioRepository, new BCryptPasswordEncoder());

        Usuario usuario = new Usuario();
        usuario.setUsername("legacy");
        usuario.setCorreo("legacy@ezcerts.com");
        usuario.setActivo(true);
        usuario.setRol(Rol.EMPLEADO);
        usuario.setContrasenaHash("clave-plana");

        when(usuarioRepository.findByUsername("legacy")).thenReturn(Optional.of(usuario));

        Authentication authentication = provider.authenticate(
                new UsernamePasswordAuthenticationToken("legacy", "clave-plana")
        );

        assertNotNull(authentication);
        assertEquals("legacy", authentication.getName());
    }
}
