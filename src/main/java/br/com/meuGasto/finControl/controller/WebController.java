package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.entity.Usuario;
import br.com.meuGasto.finControl.service.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @Autowired
    private GastoService gastoService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

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

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalGastos", gastoService.calcularTotalGastos());
        model.addAttribute("quantidadeGastos", gastoService.contarGastos());
        model.addAttribute("quantidadeCategorias", gastoService.contarCategorias());
        model.addAttribute("gastosMes", gastoService.calcularTotalGastosMes());
        return "dashboard";
    }
}
