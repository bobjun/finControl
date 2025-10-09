package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.entity.Gasto;
import br.com.meuGasto.finControl.service.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/gastos")
public class WebGastoController {

    @Autowired
    private GastoService gastoService;

    @GetMapping("/listar")
    public String listarGastos(Model model) {
        List<Gasto> gastos = gastoService.listarTodos();

        // Calculando estatísticas
        BigDecimal totalGastos = gastos.stream()
            .map(Gasto::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal gastosMes = gastos.stream()
            .filter(gasto -> {
                LocalDate dataGasto = gasto.getDataGasto().toLocalDate();
                LocalDate hoje = LocalDate.now();
                return dataGasto.getMonth() == hoje.getMonth() &&
                       dataGasto.getYear() == hoje.getYear();
            })
            .map(Gasto::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<String> categorias = gastos.stream()
            .map(Gasto::getCategoria)
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        // Adicionando atributos ao modelo
        model.addAttribute("gastos", gastos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalGastos", totalGastos);
        model.addAttribute("gastosMes", gastosMes);
        model.addAttribute("quantidadeGastos", gastos.size());
        model.addAttribute("quantidadeCategorias", categorias.size());

        return "listarGastos";
    }

    @GetMapping("/editar/{id}")
    public String editarGasto(@PathVariable Long id, Model model) {
        Gasto gasto = gastoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Gasto não encontrado"));
        model.addAttribute("gasto", gasto);
        return "editarGasto";
    }

    @PostMapping("/editar/{id}")
    public String atualizarGasto(@PathVariable Long id,
                                @Valid @ModelAttribute Gasto gasto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "editarGasto";
        }

        try {
            gastoService.atualizar(id, gasto);
            redirectAttributes.addFlashAttribute("mensagem", "Gasto atualizado com sucesso!");
            return "redirect:/gastos/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar gasto: " + e.getMessage());
            return "redirect:/gastos/editar/" + id;
        }
    }

    @DeleteMapping("/deletar/{id}")
    public String deletarGasto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            gastoService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagem", "Gasto excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir gasto: " + e.getMessage());
        }
        return "redirect:/gastos/listar";
    }

    @PostMapping("/deletar/{id}")
    public String deletarGastoPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return deletarGasto(id, redirectAttributes);
    }

    @GetMapping("/adicionar")
    public String mostrarFormularioAdicionarGasto(Model model) {
        if (!model.containsAttribute("gasto")) {
            model.addAttribute("gasto", new Gasto());
        }
        return "adicionarGasto";
    }

    @PostMapping("/adicionar")
    public String adicionarGasto(@Valid @ModelAttribute Gasto gasto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.gasto", result);
            redirectAttributes.addFlashAttribute("gasto", gasto);
            return "redirect:/gastos/adicionar";
        }

        if (gasto.getDataGasto() == null) {
            gasto.setDataGasto(LocalDateTime.now());
        }

        try {
            gastoService.salvar(gasto);
            redirectAttributes.addFlashAttribute("mensagem", "Gasto adicionado com sucesso!");
            return "redirect:/gastos/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao adicionar gasto: " + e.getMessage());
            return "redirect:/gastos/adicionar";
        }
    }
}
