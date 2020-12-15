package ru.javawebinar.topjava.util;

import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Locale;

public class Util {
    private Util() {
    }

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T value, @Nullable T start, @Nullable T end) {
        return (start == null || value.compareTo(start) >= 0) && (end == null || value.compareTo(end) < 0);
    }

    public static String getMessage(MessageSource messageSource, HttpServletRequest req, String codeMessage) {
        String cookieName = "org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE";
        String languageTag = Arrays.stream(req.getCookies())
                .filter(cookie -> cookie.getName().equalsIgnoreCase(cookieName))
                .findFirst().map(Cookie::getValue)
                .orElse("ru");
        return messageSource.getMessage(codeMessage, null, Locale.forLanguageTag(languageTag));
    }
}