package com.ezcerts.app.controller;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.ezcerts.app.service.UsuarioService;
import com.ezcerts.app.service.EmpleadoService;
import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.model.Empleado;
import com.ezcerts.app.model.Rol;
import com.ezcerts.app.dto.UsuarioCrearDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.math.BigDecimal;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private EmpleadoService empleadoService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @GetMapping("/dashboard/usuarios-activos")
    public String usuariosActivos(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios-activos";
    }

    @GetMapping("/dashboard/usuarios-activos/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuarioDto", new UsuarioCrearDto());
        return "crear-usuario";
    }

    @PostMapping("/dashboard/usuarios-activos/crear")
    public String procesarFormularioCrear(@ModelAttribute("usuarioDto") UsuarioCrearDto dto, Model model) {
        if (!dto.getContrasena().equals(dto.getConfirmarContrasena())) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "crear-usuario";
        }
        
        try {
            Usuario nuevoUsuario = new Usuario();
            String username = dto.getNombre().split(" ")[0]; // basic username generation
            nuevoUsuario.setUsername(username); 
            nuevoUsuario.setCorreo(dto.getCorreo());
            nuevoUsuario.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
            nuevoUsuario.setRol(Rol.valueOf(dto.getRol().toUpperCase()));
            nuevoUsuario.setActivo(dto.isActivo());
            
            Usuario usuarioGuardado = usuarioService.crearUsuario(nuevoUsuario);
            
            Empleado nuevoEmpleado = new Empleado();
            nuevoEmpleado.setUsuario(usuarioGuardado);
            nuevoEmpleado.setNombreCompleto(dto.getNombre());
            nuevoEmpleado.setNumeroDocumento(dto.getCedula());
            nuevoEmpleado.setDepartamento(dto.getArea());
            nuevoEmpleado.setCargo("N/A"); // Default value
            nuevoEmpleado.setFechaIngreso(LocalDate.now()); // Default value
            nuevoEmpleado.setSalario(BigDecimal.ZERO); // Default value
            
            empleadoService.crearEmpleado(nuevoEmpleado);
            
            return "redirect:/dashboard/usuarios-activos?exito";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el usuario. Es posible que el correo o cédula ya existan.");
            return "crear-usuario";
        }
    }
}
