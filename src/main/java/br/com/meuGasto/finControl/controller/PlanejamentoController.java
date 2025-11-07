package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import br.com.meuGasto.finControl.repository.PlanejamentoMensalRepository;
import br.com.meuGasto.finControl.service.PlanejamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        // carregamos o planejamento por id (único) e calculamos o resumo a partir do objeto carregado
        var planejamento = repo.findById(id).orElseThrow();
        var resumo = service.getResumo(planejamento);
        model.addAttribute("resumo", resumo);
        return "planejamentos/resumo";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Planejamento não encontrado");
        }
    }

    @GetMapping("/{id}/editar")
    public String editarPlanejamento(@PathVariable Long id, Model model) {
        PlanejamentoMensal planejamento = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Planejamento não encontrado"));
        model.addAttribute("planejamento", planejamento);
        return "planejamentos/form"; // Reutiliza o formulário para edição
    }
}
