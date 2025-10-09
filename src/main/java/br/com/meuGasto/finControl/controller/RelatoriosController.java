package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.entity.Gasto;
import br.com.meuGasto.finControl.service.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {

    @Autowired
    private GastoService gastoService;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM/yyyy", new Locale("pt", "BR"));

    @GetMapping
    public String mostrarRelatorios(Model model) {
        try {
            List<Gasto> todosGastos = gastoService.listarTodos();

            if (todosGastos.isEmpty()) {
                preencherModelVazio(model);
                return "relatorios";
            }

            // Total de gastos
            BigDecimal totalGastos = todosGastos.stream()
                    .map(Gasto::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Média mensal
            BigDecimal mediaMensal = calcularMediaMensal(todosGastos);

            // Maior e menor gasto
            BigDecimal maiorGasto = todosGastos.stream()
                    .map(Gasto::getValor)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            BigDecimal menorGasto = todosGastos.stream()
                    .map(Gasto::getValor)
                    .filter(valor -> valor.compareTo(BigDecimal.ZERO) > 0)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            // Gastos por categoria
            Map<String, BigDecimal> gastosPorCategoria = todosGastos.stream()
                    .collect(Collectors.groupingBy(
                            Gasto::getCategoria,
                            TreeMap::new,
                            Collectors.reducing(BigDecimal.ZERO,
                                    Gasto::getValor,
                                    BigDecimal::add)
                    ));

            // Gastos por mês
            Map<String, BigDecimal> gastosPorMes = calcularGastosPorMes(todosGastos);

            // Últimos gastos (limitado a 10)
            List<Gasto> ultimosGastos = todosGastos.stream()
                    .sorted(Comparator.comparing(Gasto::getDataGasto).reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            // Adicionar dados ao modelo
            model.addAttribute("totalGastos", totalGastos);
            model.addAttribute("mediaMensal", mediaMensal);
            model.addAttribute("maiorGasto", maiorGasto);
            model.addAttribute("menorGasto", menorGasto);
            model.addAttribute("gastosPorCategoria", gastosPorCategoria);
            model.addAttribute("gastosPorMes", gastosPorMes);
            model.addAttribute("ultimosGastos", ultimosGastos);

        } catch (Exception e) {
            preencherModelVazio(model);
        }

        return "relatorios";
    }

    private void preencherModelVazio(Model model) {
        model.addAttribute("totalGastos", BigDecimal.ZERO);
        model.addAttribute("mediaMensal", BigDecimal.ZERO);
        model.addAttribute("maiorGasto", BigDecimal.ZERO);
        model.addAttribute("menorGasto", BigDecimal.ZERO);
        model.addAttribute("gastosPorCategoria", new TreeMap<String, BigDecimal>());
        model.addAttribute("gastosPorMes", new TreeMap<String, BigDecimal>());
        model.addAttribute("ultimosGastos", new ArrayList<Gasto>());
    }

    private BigDecimal calcularMediaMensal(List<Gasto> gastos) {
        if (gastos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Encontrar o primeiro e último mês
        LocalDateTime dataInicial = gastos.stream()
                .map(Gasto::getDataGasto)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        LocalDateTime dataFinal = gastos.stream()
                .map(Gasto::getDataGasto)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        // Calcular número de meses entre as datas usando YearMonth
        YearMonth mesInicial = YearMonth.from(dataInicial);
        YearMonth mesFinal = YearMonth.from(dataFinal);

        // Diferença em meses
        long meses = ChronoUnit.MONTHS.between(mesInicial, mesFinal) + 1; // +1 para incluir o mês atual

        // Calcular média
        BigDecimal totalGastos = gastos.stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalGastos.divide(BigDecimal.valueOf(meses), 2, RoundingMode.HALF_UP);
    }

    private Map<String, BigDecimal> calcularGastosPorMes(List<Gasto> gastos) {
        // Criar mapa ordenado por data
        TreeMap<YearMonth, BigDecimal> gastosPorMes = new TreeMap<>();

        // Preencher meses sem gastos com zero
        if (!gastos.isEmpty()) {
            YearMonth inicio = YearMonth.from(gastos.stream()
                    .map(Gasto::getDataGasto)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now()));

            YearMonth fim = YearMonth.from(gastos.stream()
                    .map(Gasto::getDataGasto)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now()));

            YearMonth atual = inicio;
            while (!atual.isAfter(fim)) {
                gastosPorMes.put(atual, BigDecimal.ZERO);
                atual = atual.plusMonths(1);
            }
        }

        // Somar os gastos por mês
        gastos.forEach(gasto -> {
            YearMonth mes = YearMonth.from(gasto.getDataGasto());
            gastosPorMes.merge(mes, gasto.getValor(), BigDecimal::add);
        });

        // Converter para o formato de exibição
        return gastosPorMes.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().format(MONTH_FORMATTER),
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        TreeMap::new
                ));
    }
}
