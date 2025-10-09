package br.com.meuGasto.finControl.controller;

import br.com.meuGasto.finControl.service.ExportacaoService;
import br.com.meuGasto.finControl.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "API de relatórios e estatísticas de gastos")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final ExportacaoService exportacaoService;

    @GetMapping("/categoria")
    @Operation(summary = "Obtém gastos por categoria em um período")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getGastosPorCategoria(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(Map.of("gastosPorCategoria", relatorioService.getGastosPorCategoria(inicio, fim)));
    }

    @GetMapping("/mensal")
    @Operation(summary = "Obtém estatísticas mensais")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getEstatisticasMensais(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime mes) {
        return ResponseEntity.ok(relatorioService.getEstatisticasMensais(mes));
    }

    @GetMapping("/tendencias")
    @Operation(summary = "Analisa tendências de gastos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getTendencias(
            @RequestParam(defaultValue = "6") int mesesAnteriores) {
        return ResponseEntity.ok(relatorioService.analisarTendencias(mesesAnteriores));
    }

    @GetMapping("/exportar/csv")
    @Operation(summary = "Exporta gastos em formato CSV")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> exportarGastosCSV(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        String csvContent = exportacaoService.exportarGastosCSV(inicio, fim);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "gastos.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
    }
}
