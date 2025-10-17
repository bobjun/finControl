package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.entity.Gasto;
import br.com.meuGasto.finControl.service.GastoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gastos")
@CrossOrigin(origins = "*")
public class GastoController {
    
    @Autowired
    private GastoService gastoService;
    
    // Endpoints CRUD básicos
    
    /**
     * POST /api/gastos - Criar um novo gasto
     */
    @PostMapping
    public ResponseEntity<?> criarGasto(@RequestBody Gasto gasto) {
        try {
            if (gastoService.validarGasto(gasto)) {
                return ResponseEntity.badRequest()
                        .body("Dados do gasto inválidos. Verifique descrição, valor e categoria.");
            }
            
            Gasto gastoSalvo = gastoService.salvar(gasto);
            return ResponseEntity.status(HttpStatus.CREATED).body(gastoSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar gasto: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/gastos - Listar todos os gastos
     */
    @GetMapping
    public ResponseEntity<List<Gasto>> listarTodos() {
        List<Gasto> gastos = gastoService.listarTodos();
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/{id} - Buscar gasto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Gasto> gasto = gastoService.buscarPorId(id);
        if (gasto.isPresent()) {
            return ResponseEntity.ok(gasto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * PUT /api/gastos/{id} - Atualizar gasto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarGasto(@PathVariable Long id, @Valid @RequestBody Gasto gasto) {
        try {
            if (!gastoService.existeGasto(id)) {
                return ResponseEntity.notFound().build();
            }
            
            if (gastoService.validarGasto(gasto)) {
                return ResponseEntity.badRequest()
                        .body("Dados do gasto inválidos. Verifique descrição, valor e categoria.");
            }
            
            Gasto gastoAtualizado = gastoService.atualizar(id, gasto);
            return ResponseEntity.ok(gastoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar gasto: " + e.getMessage());
        }
    }
    
    /**
     * DELETE /api/gastos/{id} - Excluir gasto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirGasto(@PathVariable Long id) {
        try {
            if (!gastoService.existeGasto(id)) {
                return ResponseEntity.notFound().build();
            }
            
            gastoService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir gasto: " + e.getMessage());
        }
    }
    
    // Endpoints de busca específica
    
    /**
     * GET /api/gastos/categoria/{categoria} - Buscar por categoria
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Gasto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Gasto> gastos = gastoService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/descricao?q={descricao} - Buscar por descrição
     */
    @GetMapping("/descricao")
    public ResponseEntity<List<Gasto>> buscarPorDescricao(@RequestParam String q) {
        List<Gasto> gastos = gastoService.buscarPorDescricao(q);
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/valor-maior?valor={valor} - Buscar por valor maior que
     */
    @GetMapping("/valor-maior")
    public ResponseEntity<List<Gasto>> buscarPorValorMaiorQue(@RequestParam BigDecimal valor) {
        List<Gasto> gastos = gastoService.buscarPorValorMaiorQue(valor);
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/valor-menor?valor={valor} - Buscar por valor menor que
     */
    @GetMapping("/valor-menor")
    public ResponseEntity<List<Gasto>> buscarPorValorMenorQue(@RequestParam BigDecimal valor) {
        List<Gasto> gastos = gastoService.buscarPorValorMenorQue(valor);
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/valor-faixa?min={min}&max={max} - Buscar por faixa de valor
     */
    @GetMapping("/valor-faixa")
    public ResponseEntity<List<Gasto>> buscarPorFaixaValor(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        List<Gasto> gastos = gastoService.buscarPorFaixaValor(min, max);
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/periodo?inicio={inicio}&fim={fim} - Buscar por período
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<Gasto>> buscarPorPeriodo(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        List<Gasto> gastos = gastoService.buscarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/palavra-chave?q={palavra} - Buscar por palavra-chave
     */
    @GetMapping("/palavra-chave")
    public ResponseEntity<List<Gasto>> buscarPorPalavraChave(@RequestParam String q) {
        List<Gasto> gastos = gastoService.buscarPorPalavraChave(q);
        return ResponseEntity.ok(gastos);
    }
    
    // Endpoints de ordenação
    
    /**
     * GET /api/gastos/ordenados/data - Buscar ordenados por data
     */
    @GetMapping("/ordenados/data")
    public ResponseEntity<List<Gasto>> buscarOrdenadosPorData() {
        List<Gasto> gastos = gastoService.buscarOrdenadosPorData();
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/ordenados/valor - Buscar ordenados por valor
     */
    @GetMapping("/ordenados/valor")
    public ResponseEntity<List<Gasto>> buscarOrdenadosPorValor() {
        List<Gasto> gastos = gastoService.buscarOrdenadosPorValor();
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/ordenados/categoria - Buscar ordenados por categoria
     */
    @GetMapping("/ordenados/categoria")
    public ResponseEntity<List<Gasto>> buscarOrdenadosPorCategoria() {
        List<Gasto> gastos = gastoService.buscarOrdenadosPorCategoria();
        return ResponseEntity.ok(gastos);
    }
    
    // Endpoints estatísticos
    
    /**
     * GET /api/gastos/estatisticas/total - Soma total de todos os gastos
     */
    @GetMapping("/estatisticas/total")
    public ResponseEntity<BigDecimal> somarTotalValor() {
        BigDecimal total = gastoService.somarTotalValor();
        return ResponseEntity.ok(total);
    }
    
    /**
     * GET /api/gastos/estatisticas/categoria/{categoria}/total - Soma por categoria
     */
    @GetMapping("/estatisticas/categoria/{categoria}/total")
    public ResponseEntity<BigDecimal> somarValorPorCategoria(@PathVariable String categoria) {
        BigDecimal total = gastoService.somarValorPorCategoria(categoria);
        return ResponseEntity.ok(total);
    }
    
    /**
     * GET /api/gastos/estatisticas/categoria/{categoria}/contagem - Contar por categoria
     */
    @GetMapping("/estatisticas/categoria/{categoria}/contagem")
    public ResponseEntity<Long> contarPorCategoria(@PathVariable String categoria) {
        long contagem = gastoService.contarPorCategoria(categoria);
        return ResponseEntity.ok(contagem);
    }
    
    /**
     * GET /api/gastos/estatisticas/acima-media - Gastos acima da média
     */
    @GetMapping("/estatisticas/acima-media")
    public ResponseEntity<List<Gasto>> buscarGastosAcimaDaMedia() {
        List<Gasto> gastos = gastoService.buscarGastosAcimaDaMedia();
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/estatisticas/top5 - Top 5 gastos mais caros
     */
    @GetMapping("/estatisticas/top5")
    public ResponseEntity<List<Gasto>> buscarTop5GastosMaisCaros() {
        List<Gasto> gastos = gastoService.buscarTop5GastosMaisCaros();
        return ResponseEntity.ok(gastos);
    }
    
    // Endpoints combinados
    
    /**
     * GET /api/gastos/categoria/{categoria}/periodo?inicio={inicio}&fim={fim} - Buscar por categoria e período
     */
    @GetMapping("/categoria/{categoria}/periodo")
    public ResponseEntity<List<Gasto>> buscarPorCategoriaEPeriodo(
            @PathVariable String categoria,
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fim) {
        List<Gasto> gastos = gastoService.buscarPorCategoriaEPeriodo(categoria, inicio, fim);
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * GET /api/gastos/categoria/{categoria}/valor?valor={valor} - Buscar por categoria e valor
     */
    @GetMapping("/categoria/{categoria}/valor")
    public ResponseEntity<List<Gasto>> buscarPorCategoriaEValor(
            @PathVariable String categoria,
            @RequestParam BigDecimal valor) {
        List<Gasto> gastos = gastoService.buscarPorCategoriaEValor(categoria, valor);
        return ResponseEntity.ok(gastos);
    }
}
