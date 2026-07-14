package io.rsug.zatupka;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Читает tab-separated текстовый файл, выгруженный из SE16
 * Строки 0,1,2,4 - мусор
 * Строка 3 - заголовки столбцов
 * Строки 5 и далее - содержимое таблицы
 */
public class SE16 {
    public final List<String> fields = new LinkedList<>();
    public final List<List<String>> rows = new ArrayList<>(512);

    public SE16(InputStream is) throws IOException {
        Scanner scanner = new Scanner(is);
        int rownum = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            List<String> parts = new ArrayList<>(32);
            parts.addAll(List.of(line.split("\\t")));
            if (!parts.isEmpty()) parts.removeFirst();

            if (rownum == 3) {
                for (String p: parts) {
                    //ISSUE почему-то иногда SE16 выгружает заголовки таблиц с пробелами
                    // se16n вместо названий столбцов выгружает метки
                    fields.add(p.trim());
                }
            } else if (rownum >= 5 && !parts.isEmpty()) {
                rows.add(parts);
            }
            rownum++;
        }
        scanner.close();
        is.close();
    }

    public String getField(List<String> row, String name) {
        int index = fields.indexOf(Objects.requireNonNull(name));
        if (index == -1) {
            throw new NoSuchElementException("No such field: " + name);
        }
        if (index < row.size())
            return Objects.requireNonNull(row).get(index);
        else
            return null;
    }

    public List<String> getFieldDistinct(List<List<String>> rows, String name) {
        int index = fields.indexOf(Objects.requireNonNull(name));
        if (index == -1) {
            throw new NoSuchElementException("No such field: " + name);
        }
        List<String> rez = new LinkedList<>();
        for (List<String> row: rows) {
            if (index < row.size()) {
                String value = row.get(index);
                if (!rez.contains(value)) rez.add(value);
            }
        }
        return rez;
    }

    public Map<String, String> getFields(List<String> row, String... names) {
        Map<String, String> rez = new LinkedHashMap<>();
        if (names!=null) {
            for (String name : names) {
                rez.put(name, getField(row, name));
            }
        } else {
            for (String name: fields) {
                rez.put(name, getField(row, name));
            }
        }
        return rez;
    }

    public List<List<String>> selectAND(List<List<String>> rows, String ... fieldNamePatternMatches) {
        List<List<String>> rez = new LinkedList<>();
        for (List<String> row: rows) {
            boolean even = true, matches = true;
            String value = null;
            for (String f: fieldNamePatternMatches) {
                if (even) {
                    value = getField(row, f);
                } else {
                    matches = matches && value.matches(f);
                }
                even = !even;
            }
            if (matches) {
                rez.add(row);
            }
        }
        return rez;
    }
}
