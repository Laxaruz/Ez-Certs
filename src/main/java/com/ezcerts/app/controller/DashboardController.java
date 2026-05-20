package com.ezcerts.app.controller;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
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
import com.ezcerts.app.repository.CertificadoRepository;
import com.ezcerts.app.model.Certificado;
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

    @Autowired
    private CertificadoRepository certificadoRepository;

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
    public String dashboardEmpleado(Authentication authentication, Model model) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username).orElse(null);
        if (usuario != null) {
            Empleado empleado = empleadoService.buscarPorUsuarioId(usuario.getId()).orElse(null);
            if (empleado != null) {
                List<Certificado> actividades = certificadoRepository.findByEmpleadoIdOrderByFechaGeneracionDesc(empleado.getId());
                model.addAttribute("actividades", actividades);
                model.addAttribute("cantidadCertificados", actividades.size());
            }
        }
        return "dashboard-empleado";
    }

    @GetMapping("/dashboard/empleado/solicitar-certificado")
    public String solicitarCertificado() {
        return "solicitar-certificado";
    }

    @GetMapping("/dashboard/admin")
    public String dashboardAdmin() {
        return "dashboard-admin";
    }

    @GetMapping("/dashboard/admin/certificados-generados")
    public String certificadosGenerados(Model model) {
        List<Certificado> certificados = certificadoRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fechaGeneracion"));
        model.addAttribute("certificados", certificados);
        
        long total = certificados.size();
        long esteMes = certificados.stream().filter(c -> c.getFechaGeneracion().getMonth() == LocalDate.now().getMonth() && c.getFechaGeneracion().getYear() == LocalDate.now().getYear()).count();
        long hoy = certificados.stream().filter(c -> c.getFechaGeneracion().toLocalDate().isEqual(LocalDate.now())).count();
        int anio = LocalDate.now().getYear();

        model.addAttribute("total", total);
        model.addAttribute("esteMes", esteMes);
        model.addAttribute("hoy", hoy);
        model.addAttribute("anio", anio);

        return "certificados-generados";
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
            // Delega TODA la lógica de inserción transaccional al Service
            usuarioService.registrarUsuarioYEmpleado(dto);
            
            return "redirect:/dashboard/usuarios-activos?exito";
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            System.err.println("Database error: " + ex.getMessage());
            model.addAttribute("error", "Error crítico: El correo o la cédula que intenta registrar ya existen. Por favor verifique los datos.");
            return "crear-usuario";
        } catch (Exception e) {
            e.printStackTrace(); // Imprime la traza en la terminal para identificar mapeos fallidos
            model.addAttribute("error", "Error interno al crear el usuario. Por favor revise su consola/log: " + e.getMessage());
            return "crear-usuario";
        }
    }
}
