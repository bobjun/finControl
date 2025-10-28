package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import br.com.meuGasto.finControl.entity.PlanejamentoCategoria;
import br.com.meuGasto.finControl.service.PlanejamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Controller
@RequestMapping("/planejamentos")
public class PlanejamentoController {

    @Autowired
    private PlanejamentoService planejamentoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("planejamentos", planejamentoService.listarTodos());
        return "planejamentos/listar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("planejamento", new PlanejamentoMensal());
        return "planejamentos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute PlanejamentoMensal planejamento) {
        if (planejamento.getId() == null) {
            planejamentoService.criar(planejamento);
        } else {
            planejamentoService.atualizar(planejamento.getId(), planejamento);
        }
        return "redirect:/planejamentos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        planejamentoService.buscarPorId(id)
            .ifPresent(p -> model.addAttribute("planejamento", p));
        return "planejamentos/form";
    }

    @GetMapping("/{id}/resumo")
    public String resumo(@PathVariable Long id, Model model) {
        planejamentoService.buscarPorId(id)
            .ifPresent(p -> {
                model.addAttribute("planejamento", p);
                // Adicionar mais informações para o resumo posteriormente
            });
        return "planejamentos/resumo";
    }

    @GetMapping("/{id}/categorias/nova")
    public String novaCategoria(@PathVariable Long id, Model model) {
        model.addAttribute("planejamentoId", id);
        model.addAttribute("categoria", new PlanejamentoCategoria());
        return "planejamentos/categoria-form";
    }

    @PostMapping("/{id}/categorias/salvar")
    public String salvarCategoria(@PathVariable Long id, @ModelAttribute PlanejamentoCategoria categoria) {
        planejamentoService.adicionarCategoria(id, categoria);
        return "redirect:/planejamentos/" + id + "/resumo";
    }

    @PostMapping("/{planejamentoId}/categorias/{categoriaId}/remover")
    public String removerCategoria(@PathVariable Long planejamentoId, @PathVariable Long categoriaId) {
        planejamentoService.removerCategoria(planejamentoId, categoriaId);
        return "redirect:/planejamentos/" + planejamentoId + "/resumo";
    }
}
