package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.dto.PlanejamentoResumoDTO;
import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import br.com.meuGasto.finControl.repository.GastoPlanejamentoRepository;
import br.com.meuGasto.finControl.repository.PlanejamentoMensalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class PlanejamentoService {

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

}

