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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExportacaoServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    @InjectMocks
    private ExportacaoService exportacaoService;

    private Gasto gastoMock;
    private LocalDateTime inicio;
    private LocalDateTime fim;

    @BeforeEach
    void setUp() {
        gastoMock = new Gasto();
        gastoMock.setId(1L);
        gastoMock.setDescricao("Test Expense");
        gastoMock.setValor(new BigDecimal("100.00"));
        gastoMock.setCategoria("TEST");
        gastoMock.setDataGasto(LocalDateTime.of(2025, 1, 1, 12, 0));
        gastoMock.setObservacoes("Test observation");

        inicio = LocalDateTime.of(2025, 1, 1, 0, 0);
        fim = LocalDateTime.of(2025, 1, 31, 23, 59);
    }


    @Test
    void exportarGastosCSV_SemGastos_DeveRetornarApenasHeader() {
        when(gastoRepository.findByDataGastoBetween(any(), any()))
                .thenReturn(Collections.emptyList());

        String csv = exportacaoService.exportarGastosCSV(inicio, fim);

        assertNotNull(csv);
        assertTrue(csv.trim().endsWith("ID,Descrição,Valor,Categoria,Data,Observações"));
    }

    @Test
    void exportarGastosCSV_ComCaracteresEspeciais_DeveEscaparCorretamente() {
        gastoMock.setDescricao("Test \"quoted\" description");
        gastoMock.setCategoria("Category \"with quotes\"");
        gastoMock.setObservacoes("Notes \"with\" quotes");

        when(gastoRepository.findByDataGastoBetween(any(), any()))
                .thenReturn(Arrays.asList(gastoMock));

        String csv = exportacaoService.exportarGastosCSV(inicio, fim);

        assertNotNull(csv);
        assertTrue(csv.contains("\"Test \"\"quoted\"\" description\""));
        assertTrue(csv.contains("\"Category \"\"with quotes\"\"\""));
        assertTrue(csv.contains("\"Notes \"\"with\"\" quotes\""));
    }

    }
