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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GastoServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private GastoService gastoService;

    private Gasto gastoMock;

    @BeforeEach
    void setUp() {
        gastoMock = new Gasto();
        gastoMock.setId(1L);
        gastoMock.setDescricao("Test Expense");
        gastoMock.setValor(new BigDecimal("100.00"));
        gastoMock.setDataGasto(LocalDateTime.now());
        gastoMock.setCategoria("TEST");
    }

    @Test
    void salvar_ComDataGasto_DeveSalvarGasto() {
        when(gastoRepository.save(any(Gasto.class))).thenReturn(gastoMock);
        when(usuarioService.getEmailUsuarioLogado()).thenReturn("test@example.com");
        doNothing().when(notificacaoService).notificarGastoAlto(any(Gasto.class), anyString());

        Gasto resultado = gastoService.salvar(gastoMock);

        assertNotNull(resultado);
        assertEquals(gastoMock.getValor(), resultado.getValor());
        verify(gastoRepository).save(any(Gasto.class));
        verify(notificacaoService).notificarGastoAlto(any(Gasto.class), anyString());
    }

    @Test
    void salvar_SemDataGasto_DeveDefinirDataAtual() {
        gastoMock.setDataGasto(null);
        when(gastoRepository.save(any(Gasto.class))).thenReturn(gastoMock);
        when(usuarioService.getEmailUsuarioLogado()).thenReturn("test@example.com");

        Gasto resultado = gastoService.salvar(gastoMock);

        assertNotNull(resultado);
        assertNotNull(resultado.getDataGasto());
        verify(gastoRepository).save(any(Gasto.class));
    }

    @Test
    void listarTodos_DeveRetornarListaDeGastos() {
        List<Gasto> gastos = Arrays.asList(gastoMock);
        when(gastoRepository.findAll()).thenReturn(gastos);

        List<Gasto> resultado = gastoService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(gastoRepository).findAll();
    }

    @Test
    void buscarPorId_QuandoExiste_DeveRetornarGasto() {
        when(gastoRepository.findById(1L)).thenReturn(Optional.of(gastoMock));

        Optional<Gasto> resultado = gastoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(gastoMock.getId(), resultado.get().getId());
        verify(gastoRepository).findById(1L);
    }

    @Test
    void calcularTotalGastos_DeveRetornarSomaCorreta() {
        Gasto gasto2 = new Gasto();
        gasto2.setValor(new BigDecimal("200.00"));
        List<Gasto> gastos = Arrays.asList(gastoMock, gasto2);
        
        when(gastoRepository.findAll()).thenReturn(gastos);

        BigDecimal total = gastoService.calcularTotalGastos();

        assertEquals(new BigDecimal("300.00"), total);
        verify(gastoRepository).findAll();
    }

    @Test
    void contarGastos_DeveRetornarQuantidadeCorreta() {
        when(gastoRepository.count()).thenReturn(5L);

        long quantidade = gastoService.contarGastos();

        assertEquals(5L, quantidade);
        verify(gastoRepository).count();
    }

    @Test
    void calcularTotalGastosMes_DeveRetornarSomaDoMesAtual() {
        LocalDateTime dataAtual = LocalDateTime.now();
        gastoMock.setDataGasto(dataAtual);
        
        Gasto gastoMesPassado = new Gasto();
        gastoMesPassado.setValor(new BigDecimal("200.00"));
        gastoMesPassado.setDataGasto(dataAtual.minusMonths(1));
        
        List<Gasto> gastos = Arrays.asList(gastoMock, gastoMesPassado);
        when(gastoRepository.findAll()).thenReturn(gastos);

        BigDecimal total = gastoService.calcularTotalGastosMes();

        assertEquals(new BigDecimal("100.00"), total);
        verify(gastoRepository).findAll();
    }
}
