package com.ezcerts.app.service.impl;

import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.model.Empleado;
import com.ezcerts.app.model.Rol;
import com.ezcerts.app.dto.UsuarioCrearDto;
import com.ezcerts.app.repository.UsuarioRepository;
import com.ezcerts.app.repository.EmpleadoRepository;
import com.ezcerts.app.service.UsuarioService;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, EmpleadoRepository empleadoRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registrarUsuarioYEmpleado(UsuarioCrearDto dto) throws Exception {
        // 1. Crear e instanciar la entidad Usuario
        Usuario nuevoUsuario = new Usuario();
        String username = dto.getNombre().split(" ")[0]; // Generación básica del username
        nuevoUsuario.setUsername(username); 
        nuevoUsuario.setCorreo(dto.getCorreo());
        nuevoUsuario.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        nuevoUsuario.setRol(Rol.valueOf(dto.getRol().toUpperCase()));
        nuevoUsuario.setActivo(dto.isActivo());
        
        // Guardar el usuario primero
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 2. Si el rol es EMPLEADO, instanciar y asociar el Empleado
        if ("EMPLEADO".equalsIgnoreCase(usuarioGuardado.getRol().name())) {
            Empleado nuevoEmpleado = new Empleado();
            nuevoEmpleado.setNombreCompleto(dto.getNombre());
            nuevoEmpleado.setNumeroDocumento(dto.getCedula());
            
            // Asignar parámetros con validaciones de nulos para que no lance SQLException
            nuevoEmpleado.setDepartamento(dto.getArea() != null && !dto.getArea().isEmpty() ? dto.getArea() : "General");
            nuevoEmpleado.setCargo(dto.getCargo() != null && !dto.getCargo().isEmpty() ? dto.getCargo() : "Analista");
            nuevoEmpleado.setFechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now());
            nuevoEmpleado.setSalario(dto.getSalario() != null ? dto.getSalario() : BigDecimal.ZERO);
            
            // REGLA CRÍTICA: Vincular el objeto usuario guardado (relación foreign key)
            nuevoEmpleado.setUsuario(usuarioGuardado);
            
            // Guardar el empleado en la tabla
            empleadoRepository.save(nuevoEmpleado);
        }
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public void inactivarUsuario(Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        });
    }
}
