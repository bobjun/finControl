package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Gasto;
import br.com.meuGasto.finControl.repository.GastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final GastoRepository gastoRepository;

    @Cacheable("estatisticasPorCategoria")
    public Map<String, BigDecimal> getGastosPorCategoria(LocalDateTime inicio, LocalDateTime fim) {
        List<Gasto> gastos = gastoRepository.findByDataGastoBetween(inicio, fim);
        return gastos.stream()
                .collect(Collectors.groupingBy(
                        Gasto::getCategoria,
                        Collectors.reducing(BigDecimal.ZERO,
                                Gasto::getValor,
                                BigDecimal::add)));
    }

    @Cacheable("estatisticasMensais")
    public Map<String, Object> getEstatisticasMensais(LocalDateTime mes) {
        LocalDateTime inicioDeMes = mes.withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime fimDeMes = mes.withDayOfMonth(mes.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59);

        List<Gasto> gastosDoMes = gastoRepository.findByDataGastoBetween(inicioDeMes, fimDeMes);

        BigDecimal totalGasto = gastosDoMes.stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> gastoPorCategoria = getGastosPorCategoria(inicioDeMes, fimDeMes);

        String categoriaMaisCara = gastoPorCategoria.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Nenhuma");

        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalGasto", totalGasto);
        estatisticas.put("gastoPorCategoria", gastoPorCategoria);
        estatisticas.put("categoriaMaisCara", categoriaMaisCara);
        estatisticas.put("quantidadeGastos", gastosDoMes.size());

        return estatisticas;
    }

    @Cacheable("tendencias")
    public Map<String, Object> analisarTendencias(int mesesAnteriores) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicio = agora.minusMonths(mesesAnteriores);

        List<Gasto> gastos = gastoRepository.findByDataGastoBetween(inicio, agora);

        // Análise de tendência mensal
        Map<String, BigDecimal> gastosPorMes = gastos.stream()
                .collect(Collectors.groupingBy(
                        gasto -> gasto.getDataGasto().getMonth().toString(),
                        Collectors.reducing(BigDecimal.ZERO,
                                Gasto::getValor,
                                BigDecimal::add)));

        // Categorias mais frequentes
        Map<String, Long> categoriasMaisFrequentes = gastos.stream()
                .collect(Collectors.groupingBy(
                        Gasto::getCategoria,
                        Collectors.counting()));

        Map<String, Object> tendencias = new HashMap<>();
        tendencias.put("gastosPorMes", gastosPorMes);
        tendencias.put("categoriasMaisFrequentes", categoriasMaisFrequentes);

        return tendencias;
    }
}
