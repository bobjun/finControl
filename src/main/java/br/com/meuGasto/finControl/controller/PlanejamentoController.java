package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import br.com.meuGasto.finControl.repository.PlanejamentoMensalRepository;
import br.com.meuGasto.finControl.service.PlanejamentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/planejamentos")
public class PlanejamentoController {

    private final PlanejamentoMensalRepository repo;
    private final PlanejamentoService service;


    public PlanejamentoController(PlanejamentoMensalRepository repo, PlanejamentoService service) {
        this.repo = repo;
        this.service = service;
    }


    @GetMapping
    public String listar(Model model) {
        model.addAttribute("planejamentos", repo.findAll());
        return "planejamentos/listar";
    }

    @GetMapping("/novo")
    public String novoPlanejamento(Model model) {
        model.addAttribute("planejamento", new PlanejamentoMensal());
        return "planejamentos/form"; // templates/planejamentos/form.html
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute PlanejamentoMensal planejamento) {
        repo.save(planejamento);
        return "redirect:/planejamentos";
    }

    @GetMapping("/{id}/resumo")
    public String resumo(@PathVariable Long id, Model model) {
        var planejamento = repo.findById(id).orElseThrow();
        var resumo = service.getResumo(planejamento.getMesAno());
        model.addAttribute("resumo", resumo);
        return "planejamentos/resumo";
    }
}
