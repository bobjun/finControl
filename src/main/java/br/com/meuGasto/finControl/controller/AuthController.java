package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.entity.Usuario;
import br.com.meuGasto.finControl.service.UsuarioService;
import br.com.meuGasto.finControl.service.GastoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class AuthController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GastoService gastoService;

    /**
     * Página inicial - redireciona para login
     */
    @GetMapping
    public String home() {
        return "redirect:/login";
    }
    
    /**
     * Página de login
     */
    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null) {
            model.addAttribute("error", "Email ou senha inválidos!");
        }
        if (logout != null) {
            model.addAttribute("message", "Logout realizado com sucesso!");
        }
        return "login";
    }
    
    /**
     * Página de registro
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }
    
    /**
     * Processar registro de usuário
     */
    @PostMapping("/register")
    public String processRegister(@Valid Usuario usuario, BindingResult result, 
                                 Model model, RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "register";
        }
        
        // Verificar se email já existe
        if (usuarioService.existeUsuarioComEmail(usuario.getEmail())) {
            model.addAttribute("error", "Email já está em uso!");
            return "register";
        }
        
        // Validar dados do usuário
        if (!usuarioService.validarUsuario(usuario)) {
            model.addAttribute("error", "Dados inválidos!");
            return "register";
        }
        
        try {
            // Criptografa a senha antes de salvar
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            usuarioService.salvar(usuario);
            redirectAttributes.addFlashAttribute("message", "Usuário registrado com sucesso! Faça login para continuar.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao registrar usuário: " + e.getMessage());
            return "register";
        }
    }
    
    /**
     * Dashboard após login
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalGastos", gastoService.calcularTotalGastos());
        model.addAttribute("quantidadeGastos", gastoService.contarGastos());
        model.addAttribute("quantidadeCategorias", gastoService.contarCategorias());
        model.addAttribute("gastosMes", gastoService.calcularTotalGastosMes());
        return "dashboard";
    }
}
