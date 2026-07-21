package io.rsug.kartadsl;

import org.apache.commons.text.StringEscapeUtils;
import org.stringtemplate.v4.AttributeRenderer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class HtmlEscapeStringRenderer implements AttributeRenderer<String> {
    // см. org.stringtemplate.v4.StringRenderer#toString
    @Override
    public String toString(String value, String formatString, Locale locale) {
        if (formatString == null) {
            return value;
        } else if (formatString.equals("upper")) {
            return value.toUpperCase(locale);
        } else if (formatString.equals("lower")) {
            return value.toLowerCase(locale);
        } else if (formatString.equals("cap")) {
            return !value.isEmpty() ? Character.toUpperCase(value.charAt(0)) + value.substring(1) : value;
        } else {
            if (formatString.equals("url")) {
                return URLEncoder.encode(value, StandardCharsets.UTF_8);
            }
            return formatString.equals("html") ? StringEscapeUtils.escapeHtml4(value) : String.format(locale, formatString, value);
        }
    }
}
