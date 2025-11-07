package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.dto.PlanejamentoResumoDTO;
import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import br.com.meuGasto.finControl.repository.GastoPlanejamentoRepository;
import br.com.meuGasto.finControl.repository.PlanejamentoMensalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
public class PlanejamentoService {

    private static final Logger log = LoggerFactory.getLogger(PlanejamentoService.class);

    private final PlanejamentoMensalRepository planejamentoRepo;
    private final GastoPlanejamentoRepository gastoRepo;

    public PlanejamentoService(PlanejamentoMensalRepository planejamentoRepo, GastoPlanejamentoRepository gastoRepo) {
        this.planejamentoRepo = planejamentoRepo;
        this.gastoRepo = gastoRepo;
    }

    public PlanejamentoResumoDTO getResumo(YearMonth mesAno) {
        PlanejamentoMensal p = planejamentoRepo.findByMesAno(mesAno)
                .orElseThrow(() -> new RuntimeException("Planejamento não encontrado"));

        LocalDate inicio = mesAno.atDay(1);
        LocalDate fim = mesAno.atEndOfMonth();

        BigDecimal gastoTotal = gastoRepo.sumByPeriodo(inicio, fim);
        return new PlanejamentoResumoDTO(p, gastoTotal);
    }

    // Novo: retorna resumo para o mes solicitado ou, se não existir, tenta o planejamento mais recente como fallback
    public PlanejamentoResumoDTO getResumoOuUltimo(YearMonth mesAno) {
        // primeiro tente obter todos os planejamentos para o mesAno — evita NonUniqueResultException
        List<PlanejamentoMensal> encontrados = planejamentoRepo.findAllByMesAno(mesAno);

        YearMonth periodoParaCalculo;
        PlanejamentoMensal pSelecionado;

        if (encontrados != null && !encontrados.isEmpty()) {
            if (encontrados.size() > 1) {
                log.warn("Foram encontrados {} planejamentos para {}. Usando o mais recente (por id).", encontrados.size(), mesAno);
            }
            // escolher o mais recente por id (assume-se id crescente com inserção)
            pSelecionado = encontrados.stream()
                    .max(Comparator.comparingLong(pl -> pl.getId() == null ? 0L : pl.getId()))
                    .orElseThrow(() -> new RuntimeException("Erro ao selecionar planejamento mais recente para " + mesAno));
            periodoParaCalculo = mesAno;
        } else {
            // fallback: pega ultimo planejamento cadastrado
            var ult = planejamentoRepo.findTopByOrderByMesAnoDesc();
            if (ult.isPresent()) {
                pSelecionado = ult.get();
                periodoParaCalculo = pSelecionado.getMesAno();
            } else {
                throw new RuntimeException("Nenhum planejamento cadastrado");
            }
        }

        LocalDate inicio = periodoParaCalculo.atDay(1);
        LocalDate fim = periodoParaCalculo.atEndOfMonth();

        BigDecimal gastoTotal = gastoRepo.sumByPeriodo(inicio, fim);
        return new PlanejamentoResumoDTO(pSelecionado, gastoTotal);
    }

    // Novo método: cria resumo diretamente a partir de um PlanejamentoMensal passado (evita consultar repo por mesAno)
    public PlanejamentoResumoDTO getResumo(PlanejamentoMensal planejamento) {
        if (planejamento == null) {
            throw new IllegalArgumentException("Planejamento não pode ser nulo");
        }
        YearMonth mesAno = planejamento.getMesAno();
        if (mesAno == null) {
            throw new IllegalArgumentException("Mes/Ano do planejamento é nulo");
        }

        LocalDate inicio = mesAno.atDay(1);
        LocalDate fim = mesAno.atEndOfMonth();

        BigDecimal gastoTotal = gastoRepo.sumByPeriodo(inicio, fim);
        return new PlanejamentoResumoDTO(planejamento, gastoTotal);
    }

}
