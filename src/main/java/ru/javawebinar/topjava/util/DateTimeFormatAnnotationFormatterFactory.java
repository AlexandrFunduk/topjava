package ru.javawebinar.topjava.util;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DateTimeFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<TopjavaDateTimeFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        return new HashSet<Class<?>>(Arrays.asList(LocalDate.class, LocalTime.class));
    }

    @Override
    public Printer<?> getPrinter(TopjavaDateTimeFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation, fieldType);
    }

    @Override
    public Parser<?> getParser(TopjavaDateTimeFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation, fieldType);
    }

    private Formatter<?> configureFormatterFrom(TopjavaDateTimeFormat annotation, Class<?> fieldType) {
            return new DateFormatter();

    }
}

