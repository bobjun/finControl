package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Gasto;
import br.com.meuGasto.finControl.entity.GastoPlanejamento;
import br.com.meuGasto.finControl.repository.GastoPlanejamentoRepository;
import br.com.meuGasto.finControl.repository.GastoRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor


public class GastoService {
    private static final Logger log = LoggerFactory.getLogger(GastoService.class);

    private final GastoRepository gastoRepository;
    private final GastoPlanejamentoRepository gastoPlanejamentoRepository;
    private final NotificacaoService notificacaoService;
    private final UsuarioService usuarioService;

    // Operações CRUD básicas

    
    /**
     * Salva um novo gasto
     */
    public Gasto salvar(Gasto gasto) {
        if (gasto.getDataGasto() == null) {
            gasto.setDataGasto(LocalDateTime.now());
        }
        Gasto gastoSalvo = gastoRepository.save(gasto);

        // Também criar/atualizar registro em GastoPlanejamento para contabilizar no planejamento mensal
        try {
            GastoPlanejamento gp = gastoPlanejamentoRepository.findByGastoId(gastoSalvo.getId()).orElseGet(GastoPlanejamento::new);
            gp.setDescricao(gastoSalvo.getDescricao());
            gp.setCategoria(gastoSalvo.getCategoria());
            gp.setValor(gastoSalvo.getValor());
            gp.setGastoId(gastoSalvo.getId());
            if (gastoSalvo.getDataGasto() != null) {
                gp.setData(gastoSalvo.getDataGasto().toLocalDate());
            } else {
                gp.setData(LocalDate.now());
            }
            gastoPlanejamentoRepository.save(gp);
        } catch (Exception e) {
            log.error("Erro ao sincronizar GastoPlanejamento para gasto id={}: {}", gastoSalvo.getId(), e.getMessage(), e);
        }

        // Envia notificação se necessário
        String emailUsuario = usuarioService.getEmailUsuarioLogado();
        if (emailUsuario != null) {
            notificacaoService.notificarGastoAlto(gastoSalvo, emailUsuario);
        }

        return gastoSalvo;
    }
    
    /**
     * Busca todos os gastos
     */
    @Transactional(readOnly = true)
    public List<Gasto> listarTodos() {
        return gastoRepository.findAll();
    }
    
    /**
     * Busca um gasto por ID
     */
    @Transactional(readOnly = true)
    public Optional<Gasto> buscarPorId(Long id) {
        return gastoRepository.findById(id);
    }

    /**
     * Calcula o total de gastos
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalGastos() {
        return gastoRepository.findAll().stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Conta o número total de gastos
     */
    @Transactional(readOnly = true)
    public long contarGastos() {
        return gastoRepository.count();
    }

    /**
     * Calcula o total de gastos do mês atual
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalGastosMes() {
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime fimMes = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay();

        return gastoRepository.findAll().stream()
                .filter(gasto -> {
                    LocalDateTime dataGasto = gasto.getDataGasto();
                    return dataGasto != null &&
                           dataGasto.isAfter(inicioMes) &&
                           dataGasto.isBefore(fimMes);
                })
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Conta o número de categorias distintas
     */
    @Transactional(readOnly = true)
    public long contarCategorias() {
        return gastoRepository.findAll().stream()
                .map(Gasto::getCategoria)
                .distinct()
                .count();
    }

    /**
     * Atualiza um gasto existente
     */
    public Gasto atualizar(Long id, Gasto gastoAtualizado) {
        return gastoRepository.findById(id)
                .map(gastoExistente -> {
                    gastoExistente.setDescricao(gastoAtualizado.getDescricao());
                    gastoExistente.setValor(gastoAtualizado.getValor());
                    gastoExistente.setCategoria(gastoAtualizado.getCategoria());
                    gastoExistente.setDataGasto(gastoAtualizado.getDataGasto());
                    gastoExistente.setObservacoes(gastoAtualizado.getObservacoes());
                    gastoExistente.setDataAtualizacao(LocalDateTime.now());
                    Gasto salvo = gastoRepository.save(gastoExistente);

                    // sincronizar GastoPlanejamento vinculado
                    try {
                        GastoPlanejamento gp = gastoPlanejamentoRepository.findByGastoId(salvo.getId()).orElseGet(GastoPlanejamento::new);
                        gp.setDescricao(salvo.getDescricao());
                        gp.setCategoria(salvo.getCategoria());
                        gp.setValor(salvo.getValor());
                        gp.setGastoId(salvo.getId());
                        if (salvo.getDataGasto() != null) gp.setData(salvo.getDataGasto().toLocalDate());
                        else gp.setData(LocalDate.now());
                        gastoPlanejamentoRepository.save(gp);
                    } catch (Exception e) {
                        log.error("Erro ao sincronizar atualização de GastoPlanejamento para gasto id={}: {}", salvo.getId(), e.getMessage(), e);
                    }

                    return salvo;
                })
                .orElseThrow(() -> new RuntimeException("Gasto não encontrado com ID: " + id));
    }
    
    /**
     * Exclui um gasto por ID
     */
    public void excluir(Long id) {
        if (!gastoRepository.existsById(id)) {
            throw new RuntimeException("Gasto não encontrado com ID: " + id);
        }
        // primeiro remover vinculo no planejamento
        try {
            gastoPlanejamentoRepository.deleteByGastoId(id);
        } catch (Exception e) {
            log.warn("Falha ao remover GastoPlanejamento vinculado para gasto id={}: {}", id, e.getMessage());
        }
        gastoRepository.deleteById(id);
    }
    
    // Operações de busca específicas
    
    /**
     * Busca gastos por categoria
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarPorCategoria(String categoria) {
        return gastoRepository.findByCategoria(categoria);
    }
    
    /**
     * Busca gastos por descrição
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarPorDescricao(String descricao) {
        return gastoRepository.findByDescricaoContainingIgnoreCase(descricao);
    }
    
    /**
     * Busca gastos por valor maior que
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarPorValorMaiorQue(BigDecimal valor) {
        return gastoRepository.findByValorGreaterThan(valor);
    }
    
    /**
     * Busca gastos por valor menor que
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarPorValorMenorQue(BigDecimal valor) {
        return gastoRepository.findByValorLessThan(valor);
    }
    
    /**
     * Busca gastos por faixa de valor
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarPorFaixaValor(BigDecimal valorMin, BigDecimal valorMax) {
        return gastoRepository.findByValorBetween(valorMin, valorMax);
    }
    
    /**
     * Busca gastos por período
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return gastoRepository.findByDataGastoBetween(dataInicio, dataFim);
    }
    
    /**
     * Busca gastos por categoria e valor
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarPorCategoriaEValor(String categoria, BigDecimal valor) {
        return gastoRepository.findByCategoriaAndValorGreaterThan(categoria, valor);
    }
    
    /**
     * Busca gastos ordenados por data (mais recentes primeiro)
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarOrdenadosPorData() {
        return gastoRepository.findByOrderByDataGastoDesc();
    }
    
    /**
     * Busca gastos ordenados por valor (maior primeiro)
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarOrdenadosPorValor() {
        return gastoRepository.findByOrderByValorDesc();
    }
    
    /**
     * Busca gastos ordenados por categoria
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarOrdenadosPorCategoria() {
        return gastoRepository.findByOrderByCategoriaAsc();
    }
    
    /**
     * Busca gastos por palavra-chave
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarPorPalavraChave(String palavraChave) {
        return gastoRepository.findByPalavraChave(palavraChave);
    }
    
    // Operações estatísticas
    
    /**
     * Conta gastos por categoria
     */
    @Transactional(readOnly = true)
    public long contarPorCategoria(String categoria) {
        return gastoRepository.countByCategoria(categoria);
    }
    
    /**
     * Soma valores por categoria
     */
    @Transactional(readOnly = true)
    public BigDecimal somarValorPorCategoria(String categoria) {
        BigDecimal resultado = gastoRepository.sumValorByCategoria(categoria);
        return resultado != null ? resultado : BigDecimal.ZERO;
    }
    
    /**
     * Soma total de todos os gastos
     */
    @Transactional(readOnly = true)
    public BigDecimal somarTotalValor() {
        BigDecimal resultado = gastoRepository.sumTotalValor();
        return resultado != null ? resultado : BigDecimal.ZERO;
    }
    
    /**
     * Busca gastos acima da média
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarGastosAcimaDaMedia() {
        return gastoRepository.findGastosAcimaDaMedia();
    }
    
    /**
     * Busca top 5 gastos mais caros
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarTop5GastosMaisCaros() {
        return gastoRepository.findTop5GastosMaisCaros();
    }
    
    /**
     * Busca gastos por categoria e período
     */
    @Transactional(readOnly = true)
    public List<Gasto> buscarPorCategoriaEPeriodo(String categoria, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return gastoRepository.findByCategoriaAndDataGastoBetween(categoria, dataInicio, dataFim);
    }
    
    // Validações de negócio
    
    /**
     * Valida se o gasto pode ser salvo
     */
    public boolean validarGasto(Gasto gasto) {
        if (gasto.getDescricao() == null || gasto.getDescricao().trim().isEmpty()) {
            return true;
        }
        if (gasto.getValor() == null || gasto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        if (gasto.getCategoria() == null || gasto.getCategoria().trim().isEmpty()) {
            return true;
        }
        return false;
    }
    
    /**
     * Verifica se existe gasto com o ID
     */
    public boolean existeGasto(Long id) {
        return gastoRepository.existsById(id);
    }
}
