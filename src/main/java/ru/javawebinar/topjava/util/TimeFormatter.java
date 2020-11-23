package ru.javawebinar.topjava.util;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeFormatter implements Formatter<LocalTime> {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_TIME;

    @Override
    public LocalTime parse(String time, Locale locale) throws ParseException {
        if (StringUtils.hasText(time)) {
            return LocalTime.parse(time);
        }
        return null;
    }

    @Override
    public String print(LocalTime time, Locale locale) {
        return time.format(DateTimeFormatter.ISO_TIME);
    }
}
