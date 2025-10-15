package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Gasto;
import br.com.meuGasto.finControl.repository.GastoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RelatorioServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    private Gasto gasto1;
    private Gasto gasto2;
    private LocalDateTime inicio;
    private LocalDateTime fim;

    @BeforeEach
    void setUp() {
        inicio = LocalDateTime.of(2025, 1, 1, 0, 0);
        fim = LocalDateTime.of(2025, 1, 31, 23, 59);

        gasto1 = new Gasto();
        gasto1.setId(1L);
        gasto1.setDescricao("Alimentação");
        gasto1.setValor(new BigDecimal("100.00"));
        gasto1.setCategoria("ALIMENTACAO");
        gasto1.setDataGasto(LocalDateTime.of(2025, 1, 15, 12, 0));

        gasto2 = new Gasto();
        gasto2.setId(2L);
        gasto2.setDescricao("Transporte");
        gasto2.setValor(new BigDecimal("50.00"));
        gasto2.setCategoria("TRANSPORTE");
        gasto2.setDataGasto(LocalDateTime.of(2025, 1, 15, 14, 0));
    }

    @Test
    void getGastosPorCategoria_DeveRetornarMapaCorreto() {
        List<Gasto> gastos = Arrays.asList(gasto1, gasto2);
        when(gastoRepository.findByDataGastoBetween(any(), any())).thenReturn(gastos);

        Map<String, BigDecimal> resultado = relatorioService.getGastosPorCategoria(inicio, fim);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(new BigDecimal("100.00"), resultado.get("ALIMENTACAO"));
        assertEquals(new BigDecimal("50.00"), resultado.get("TRANSPORTE"));
    }

    @Test
    void getEstatisticasMensais_DeveRetornarEstatisticasCorretas() {
        List<Gasto> gastos = Arrays.asList(gasto1, gasto2);
        when(gastoRepository.findByDataGastoBetween(any(), any())).thenReturn(gastos);

        Map<String, Object> estatisticas = relatorioService.getEstatisticasMensais(inicio);

        assertNotNull(estatisticas);
        assertTrue(estatisticas.containsKey("totalGasto"));
        assertTrue(estatisticas.containsKey("gastoPorCategoria"));
        assertTrue(estatisticas.containsKey("categoriaMaisCara"));
        
        assertEquals(new BigDecimal("150.00"), estatisticas.get("totalGasto"));
        assertEquals("ALIMENTACAO", estatisticas.get("categoriaMaisCara"));
        
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> gastoPorCategoria = (Map<String, BigDecimal>) estatisticas.get("gastoPorCategoria");
        assertEquals(2, gastoPorCategoria.size());
        assertEquals(new BigDecimal("100.00"), gastoPorCategoria.get("ALIMENTACAO"));
        assertEquals(new BigDecimal("50.00"), gastoPorCategoria.get("TRANSPORTE"));
    }

    @Test
    void getGastosPorCategoria_SemGastos_DeveRetornarMapaVazio() {
        when(gastoRepository.findByDataGastoBetween(any(), any())).thenReturn(Arrays.asList());

        Map<String, BigDecimal> resultado = relatorioService.getGastosPorCategoria(inicio, fim);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void getEstatisticasMensais_SemGastos_DeveRetornarEstatisticasZeradas() {
        when(gastoRepository.findByDataGastoBetween(any(), any())).thenReturn(Arrays.asList());

        Map<String, Object> estatisticas = relatorioService.getEstatisticasMensais(inicio);

        assertNotNull(estatisticas);
        assertEquals(BigDecimal.ZERO, estatisticas.get("totalGasto"));
        assertEquals("Nenhuma", estatisticas.get("categoriaMaisCara"));
        
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> gastoPorCategoria = (Map<String, BigDecimal>) estatisticas.get("gastoPorCategoria");
        assertTrue(gastoPorCategoria.isEmpty());
    }
}
