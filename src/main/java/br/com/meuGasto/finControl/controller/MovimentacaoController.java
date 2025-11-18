package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.dto.*;
import br.com.meuGasto.finControl.service.MovimentacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequiredArgsConstructor
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    /**
     * GET /api/movimentacoes - Listar todas as movimentações com paginação
     * @param page número da página (padrão: 0)
     * @param size tamanho da página (padrão: 10)
     * @return MovimentacaoResponse com lista paginada
     */
    @GetMapping
    public ResponseEntity<MovimentacaoResponseDTO> listarMovimentacoes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            MovimentacaoResponseDTO response = movimentacaoService.listarMovimentacoes(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/movimentacoes/{id} - Buscar uma movimentação por ID
     * @param id ID da movimentação
     * @return Dados da movimentação
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarMovimentacao(@PathVariable Long id) {
        try {
            MovimentacaoDTO movimentacao = movimentacaoService.buscarMovimentacao(id);
            return ResponseEntity.ok(movimentacao);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Movimentação não encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/movimentacoes - Criar uma nova movimentação
     * @param dto Dados da movimentação
     * @return Movimentação criada
     */
    @PostMapping
    public ResponseEntity<?> criarMovimentacao(@Valid @RequestBody MovimentacaoDTO dto) {
        try {
            MovimentacaoDTO created = movimentacaoService.criarMovimentacao(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar movimentação: " + e.getMessage());
        }
    }

    /**
     * PUT /api/movimentacoes/{id} - Atualizar uma movimentação existente
     * @param id ID da movimentação
     * @param dto Dados atualizados
     * @return Movimentação atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarMovimentacao(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoDTO dto) {
        try {
            MovimentacaoDTO updated = movimentacaoService.atualizarMovimentacao(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Movimentação não encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar movimentação: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/movimentacoes/{id} - Excluir uma movimentação
     * @param id ID da movimentação
     * @return Status 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirMovimentacao(@PathVariable Long id) {
        try {
            movimentacaoService.excluirMovimentacao(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Movimentação não encontrada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir movimentação: " + e.getMessage());
        }
    }

    /**
     * GET /api/movimentacoes/resumo - Obter resumo financeiro
     * @param dias Número de dias a considerar (padrão: 30)
     * @return ResumoFinanceiro com totalReceitas, totalDespesas e saldoAtual
     */
    @GetMapping("/resumo")
    public ResponseEntity<?> obterResumoFinanceiro(
            @RequestParam(defaultValue = "30") int dias) {
        try {
            ResumoFinanceiroDTO resumo = movimentacaoService.obterResumoFinanceiro(dias);
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter resumo financeiro: " + e.getMessage());
        }
    }

    /**
     * GET /api/movimentacoes/evolucao - Obter evolução financeira
     * @param dias Número de dias a considerar (padrão: 30)
     * @return Lista de EvolucaoFinanceira com dados diários
     */
    @GetMapping("/evolucao")
    public ResponseEntity<?> obterEvolucaoFinanceira(
            @RequestParam(defaultValue = "30") int dias) {
        try {
            List<EvolucaoFinanceiraDTO> evolucao = movimentacaoService.obterEvolucaoFinanceira(dias);
            return ResponseEntity.ok(evolucao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter evolução financeira: " + e.getMessage());
        }
    }

    /**
     * GET /api/movimentacoes/despesas/categorias - Obter despesas por categoria
     * @param dias Número de dias a considerar (padrão: 30)
     * @return Lista de DespesaCategoria com total e percentual
     */
    @GetMapping("/despesas/categorias")
    public ResponseEntity<?> obterDespesasPorCategoria(
            @RequestParam(defaultValue = "30") int dias) {
        try {
            List<DespesaCategoriaDTO> despesas = movimentacaoService.obterDespesasPorCategoria(dias);
            return ResponseEntity.ok(despesas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter despesas por categoria: " + e.getMessage());
        }
    }

    /**
     * GET /api/movimentacoes/previsao - Obter previsão financeira
     * @return PrevisaoFinanceira com previsões do mês
     */
    @GetMapping("/previsao")
    public ResponseEntity<?> obterPrevisaoFinanceira() {
        try {
            PrevisaoFinanceiraDTO previsao = movimentacaoService.obterPrevisaoFinanceira();
            return ResponseEntity.ok(previsao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter previsão financeira: " + e.getMessage());
        }
    }

    /**
     * GET /api/movimentacoes/export - Exportar movimentações em CSV
     * @return Arquivo CSV com dados das movimentações
     */
    @GetMapping("/export")
    public ResponseEntity<?> exportarMovimentacoes() {
        try {
            byte[] csvData = movimentacaoService.exportarMovimentacoes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movimentacoes.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao exportar movimentações: " + e.getMessage());
        }
    }

    /**
     * OPTIONS para suporte CORS
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> options() {
        return ResponseEntity.ok().build();
    }
}

