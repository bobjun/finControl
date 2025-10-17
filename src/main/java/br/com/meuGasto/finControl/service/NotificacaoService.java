package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Gasto;
import br.com.meuGasto.finControl.exception.EmailSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoService {

    private final JavaMailSender mailSender;
    private final RetryTemplate retryTemplate;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @Value("${app.notificacao.limite-gasto:1000.00}")
    private BigDecimal limiteGasto;

    @Value("${spring.mail.username}")
    private String remetente;

    private void enviarEmail(SimpleMailMessage message) {
        try {
            retryTemplate.execute(context -> {
                try {
                    mailSender.send(message);
                    return null;
                } catch (MailException e) {
                    log.warn("Tentativa {} de envio de email falhou. Tentando novamente...",
                            context.getRetryCount(), e);
                    throw e;
                }
            });
        } catch (Exception e) {
            log.error("Todas as tentativas de envio de email falharam", e);
            throw new EmailSendException("Falha no envio de email após várias tentativas", e);
        }
    }

    public void notificarGastoAlto(Gasto gasto, String emailUsuario) {
        if (gasto == null || emailUsuario == null || emailUsuario.isBlank()) {
            log.error("Parâmetros inválidos para notificação: gasto={}, emailUsuario={}", gasto, emailUsuario);
            throw new IllegalArgumentException("Gasto e email do usuário são obrigatórios");
        }

        if (gasto.getValor().compareTo(limiteGasto) > 0) {
            SimpleMailMessage message = criarMensagemGastoAlto(gasto, emailUsuario);
            enviarEmail(message);
            log.info("Notificação de gasto alto enviada para {}: {}", emailUsuario,
                    CURRENCY_FORMATTER.format(gasto.getValor()));
        } else {
            log.debug("Gasto não excedeu o limite de {}", CURRENCY_FORMATTER.format(limiteGasto));
        }
    }

    public void enviarRelatorioMensal(String emailUsuario, String conteudoRelatorio) {
        if (emailUsuario == null || emailUsuario.isBlank() || conteudoRelatorio == null) {
            log.error("Parâmetros inválidos para relatório mensal: emailUsuario={}", emailUsuario);
            throw new IllegalArgumentException("Email do usuário e conteúdo do relatório são obrigatórios");
        }

        SimpleMailMessage message = criarMensagemRelatorioMensal(emailUsuario, conteudoRelatorio);
        enviarEmail(message);
        log.info("Relatório mensal enviado para {}", emailUsuario);
    }

    private SimpleMailMessage criarMensagemGastoAlto(Gasto gasto, String emailUsuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remetente);
        message.setTo(emailUsuario);
        message.setSubject("Alerta de Gasto Alto");
        message.setText(String.format(
            "Foi registrado um gasto de %s na categoria %s.\n" +
            "Descrição: %s\n" +
            "Data: %s\n\n" +
            "Este alerta é enviado quando um gasto excede %s.",
            CURRENCY_FORMATTER.format(gasto.getValor()),
            gasto.getCategoria(),
            gasto.getDescricao(),
            gasto.getDataGasto().format(DATE_FORMATTER),
            CURRENCY_FORMATTER.format(limiteGasto)
        ));
        return message;
    }

    private SimpleMailMessage criarMensagemRelatorioMensal(String emailUsuario, String conteudoRelatorio) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remetente);
        message.setTo(emailUsuario);
        message.setSubject("Relatório Mensal de Gastos");
        message.setText(conteudoRelatorio);
        return message;
    }
}
