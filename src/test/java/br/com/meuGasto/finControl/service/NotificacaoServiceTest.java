package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Gasto;
import br.com.meuGasto.finControl.exception.EmailSendException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private NotificacaoService notificacaoService;

    private Gasto gastoAlto;
    private Gasto gastoBaixo;

    @BeforeEach
    void setUp() {
        RetryTemplate retryTemplate = RetryTemplate.builder()
                .maxAttempts(3)
                .fixedBackoff(100)
                .build();

        notificacaoService = new NotificacaoService(mailSender, retryTemplate);

        ReflectionTestUtils.setField(notificacaoService, "limiteGasto", new BigDecimal("1000.00"));
        ReflectionTestUtils.setField(notificacaoService, "remetente", "teste@teste.com");

        gastoAlto = new Gasto();
        gastoAlto.setValor(new BigDecimal("1500.00"));
        gastoAlto.setDescricao("Gasto Alto Teste");
        gastoAlto.setCategoria("Teste");
        gastoAlto.setDataGasto(LocalDateTime.now());

        gastoBaixo = new Gasto();
        gastoBaixo.setValor(new BigDecimal("500.00"));
        gastoBaixo.setDescricao("Gasto Baixo Teste");
        gastoBaixo.setCategoria("Teste");
        gastoBaixo.setDataGasto(LocalDateTime.now());
    }

    @Test
    void deveNotificarQuandoGastoExcederLimite() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
            notificacaoService.notificarGastoAlto(gastoAlto, "junbob@gmail.com")
        );

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void naoDeveNotificarQuandoGastoAbaixoDoLimite() {
        notificacaoService.notificarGastoAlto(gastoBaixo, "junbob@gmail.com");
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailNulo() {
        assertThrows(IllegalArgumentException.class, () ->
            notificacaoService.notificarGastoAlto(gastoAlto, null)
        );
    }

    @Test
    void deveLancarExcecaoQuandoGastoNulo() {
        assertThrows(IllegalArgumentException.class, () ->
            notificacaoService.notificarGastoAlto(null, "junbob@gmail.com")
        );
    }

    @Test
    void deveLancarEmailSendExceptionQuandoErroNoEnvio() {
        doThrow(new MailSendException("Erro ao enviar email")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(EmailSendException.class, () ->
            notificacaoService.notificarGastoAlto(gastoAlto, "junbob@gmail.com")
        );
    }

    @Test
    void deveEnviarRelatorioMensalComSucesso() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
            notificacaoService.enviarRelatorioMensal("junbob@gmail.com", "Conteúdo do relatório")
        );

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void deveLancarExcecaoQuandoConteudoRelatorioNulo() {
        assertThrows(IllegalArgumentException.class, () ->
            notificacaoService.enviarRelatorioMensal("junbob@gmail.com", null)
        );
    }
}
