package br.com.meuGasto.finControl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.ParseException;
import java.time.YearMonth;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new org.springframework.format.Formatter<YearMonth>() {
            @Override
            public YearMonth parse(String text, Locale locale) throws ParseException {
                if (text == null || text.isBlank()) {
                    throw new ParseException("texto de mes/ano vazio", 0);
                }
                try {
                    return YearMonth.parse(text);
                } catch (Exception e) {
                    throw new ParseException("formato inválido para YearMonth: " + text, 0);
                }
            }

            @Override
            public String print(YearMonth object, Locale locale) {
                return object == null ? "" : object.toString();
            }
        });
    }
}
