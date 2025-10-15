package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgendamentoServiceTest {

    @Mock
    private RelatorioService relatorioService;

    @Mock
    private NotificacaoService notificacaoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Usuario usuario1;
    private Usuario usuario2;
    private Map<String, Object> estatisticasMock;

    @BeforeEach
    void setUp() {
        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setEmail("user1@example.com");
        usuario1.setAtivo(true);

        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setEmail("user2@example.com");
        usuario2.setAtivo(true);

        estatisticasMock = new HashMap<>();
        estatisticasMock.put("totalGasto", new BigDecimal("1000.00"));
        estatisticasMock.put("categoriaMaisCara", "ALIMENTACAO");
        estatisticasMock.put("quantidadeGastos", 10);

        Map<String, Object> gastoPorCategoria = new HashMap<>();
        gastoPorCategoria.put("ALIMENTACAO", new BigDecimal("500.00"));
        gastoPorCategoria.put("TRANSPORTE", new BigDecimal("300.00"));
        gastoPorCategoria.put("OUTROS", new BigDecimal("200.00"));
        
        estatisticasMock.put("gastoPorCategoria", gastoPorCategoria);
    }

    @Test
    void enviarRelatorioMensal_DeveEnviarParaTodosUsuarios() {
        when(usuarioService.listarTodos()).thenReturn(Arrays.asList(usuario1, usuario2));
        when(relatorioService.getEstatisticasMensais(any(LocalDateTime.class))).thenReturn(estatisticasMock);
        doNothing().when(notificacaoService).enviarRelatorioMensal(anyString(), anyString());

        agendamentoService.enviarRelatorioMensal();

        verify(usuarioService).listarTodos();
        verify(relatorioService, times(2)).getEstatisticasMensais(any(LocalDateTime.class));
        verify(notificacaoService, times(2)).enviarRelatorioMensal(anyString(), anyString());
    }

    @Test
    void enviarRelatorioMensal_QuandoErroEmUsuario_DeveContinuarParaProximo() {
        when(usuarioService.listarTodos()).thenReturn(Arrays.asList(usuario1, usuario2));
        when(relatorioService.getEstatisticasMensais(any(LocalDateTime.class)))
                .thenReturn(estatisticasMock)
                .thenThrow(new RuntimeException("Erro ao gerar estatísticas"));

        agendamentoService.enviarRelatorioMensal();

        verify(notificacaoService, times(1)).enviarRelatorioMensal(anyString(), anyString());
    }

    @Test
    void enviarRelatorioMensal_ComConteudoRelatorioCorreto() {
        when(usuarioService.listarTodos()).thenReturn(Arrays.asList(usuario1));
        when(relatorioService.getEstatisticasMensais(any(LocalDateTime.class))).thenReturn(estatisticasMock);

        agendamentoService.enviarRelatorioMensal();

        verify(notificacaoService).enviarRelatorioMensal(
            eq("user1@example.com"),
            argThat(conteudo -> 
                conteudo.contains("Total Gasto: R$ 1000.00") &&
                conteudo.contains("Categoria Mais Cara: ALIMENTACAO") &&
                conteudo.contains("Quantidade de Gastos: 10") &&
                conteudo.contains("ALIMENTACAO: R$ 500.00") &&
                conteudo.contains("TRANSPORTE: R$ 300.00") &&
                conteudo.contains("OUTROS: R$ 200.00")
            )
        );
    }
}
