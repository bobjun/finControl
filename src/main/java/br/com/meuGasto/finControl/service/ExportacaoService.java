package br.com.meuGasto.finControl.service;

import br.com.meuGasto.finControl.entity.Gasto;
import br.com.meuGasto.finControl.repository.GastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportacaoService {

    private final GastoRepository gastoRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String exportarGastosCSV(LocalDateTime inicio, LocalDateTime fim) {
        List<Gasto> gastos = gastoRepository.findByDataGastoBetween(inicio, fim);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        // Cabeçalho
        printWriter.println("ID,Descrição,Valor,Categoria,Data,Observações");

        // Dados
        gastos.forEach(gasto -> printWriter.println(String.format("%d,\"%s\",%.2f,\"%s\",%s,\"%s\"",
                gasto.getId(),
                escapeCSV(gasto.getDescricao()),
                gasto.getValor(),
                escapeCSV(gasto.getCategoria()),
                gasto.getDataGasto().format(DATE_FORMATTER),
                escapeCSV(gasto.getObservacoes()))));

        return stringWriter.toString();
    }

    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }
}
