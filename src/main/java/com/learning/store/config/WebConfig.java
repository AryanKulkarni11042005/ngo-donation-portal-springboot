package com.learning.store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Query parameters arrive lowercase ("?status=pending") because that is what the
     * database and frontend use, but the enum constants are uppercase.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new ConverterFactory<String, Enum>() {
            @Override
            public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
                return source -> {
                    if (source == null || source.isBlank()) {
                        return null;
                    }
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    T value = (T) Enum.valueOf((Class<? extends Enum>) targetType,
                            source.trim().toUpperCase());
                    return value;
                };
            }
        });
    }
}
