package ru.javawebinar.topjava.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateFormatter implements Formatter<LocalDate> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public LocalDate parse(String date, Locale locale) throws ParseException {
        if (date.length() == 0) {
            return null;
        }
        return LocalDate.parse(date);
    }

    @Override
    public String print(LocalDate date, Locale locale) {
        return date.format(DateTimeFormatter.ISO_DATE);
    }
}
