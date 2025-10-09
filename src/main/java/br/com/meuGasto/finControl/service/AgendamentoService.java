package br.com.meuGasto.finControl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgendamentoService {

    private final RelatorioService relatorioService;
    private final NotificacaoService notificacaoService;
    private final UsuarioService usuarioService;

    @Scheduled(cron = "0 0 7 1 * *") // Executa às 7h do primeiro dia de cada mês
    public void enviarRelatorioMensal() {
        log.info("Iniciando envio de relatórios mensais");

        usuarioService.listarTodos().forEach(usuario -> {
            try {
                LocalDateTime mesAnterior = LocalDateTime.now().minusMonths(1);
                Map<String, Object> estatisticas = relatorioService.getEstatisticasMensais(mesAnterior);

                String conteudoRelatorio = gerarConteudoRelatorio(estatisticas);
                notificacaoService.enviarRelatorioMensal(usuario.getEmail(), conteudoRelatorio);

                log.info("Relatório mensal enviado para: {}", usuario.getEmail());
            } catch (Exception e) {
                log.error("Erro ao enviar relatório para: " + usuario.getEmail(), e);
            }
        });
    }

    private String gerarConteudoRelatorio(Map<String, Object> estatisticas) {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("Relatório Mensal de Gastos\n\n");
        relatorio.append("Total Gasto: R$ ").append(estatisticas.get("totalGasto")).append("\n");
        relatorio.append("Categoria Mais Cara: ").append(estatisticas.get("categoriaMaisCara")).append("\n");
        relatorio.append("Quantidade de Gastos: ").append(estatisticas.get("quantidadeGastos")).append("\n\n");

        @SuppressWarnings("unchecked")
        Map<String, Object> gastoPorCategoria = (Map<String, Object>) estatisticas.get("gastoPorCategoria");

        relatorio.append("Gastos por Categoria:\n");
        gastoPorCategoria.forEach((categoria, valor) ->
            relatorio.append(categoria).append(": R$ ").append(valor).append("\n")
        );

        return relatorio.toString();
    }
}
