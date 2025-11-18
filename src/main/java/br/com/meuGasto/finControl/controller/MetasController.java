package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.entity.Meta;
import br.com.meuGasto.finControl.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/metas")
@CrossOrigin(origins = "*")
public class MetasController {

    @Autowired
    private MetaService metaService;

    @PostMapping
    public ResponseEntity<?> criarMeta(@RequestBody Meta meta) {
        try {
            if (metaService.validarMeta(meta)) {
                return ResponseEntity.badRequest().body("Dados da meta inválidos. Verifique descrição e valor objetivo.");
            }
            Meta salvo = metaService.salvar(meta);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar meta: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Meta>> listarTodos() {
        List<Meta> metas = metaService.listarTodos();
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Meta> meta = metaService.buscarPorId(id);
        return meta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Meta meta) {
        try {
            if (metaService.validarMeta(meta)) {
                return ResponseEntity.badRequest().body("Dados da meta inválidos. Verifique descrição e valor objetivo.");
            }
            Meta atualizado = metaService.atualizar(id, meta);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar meta: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            metaService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir meta: " + e.getMessage());
        }
    }

    @GetMapping("/validar")
    public ResponseEntity<?> validarMeta(@RequestParam String descricao, @RequestParam String valorObjetivo) {
        try {
            Meta meta = new Meta();
            meta.setDescricao(descricao);
            meta.setValorObjetivo(new java.math.BigDecimal(valorObjetivo));
            boolean isValid = !metaService.validarMeta(meta);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao validar meta: " + e.getMessage());
        }
    }
}

