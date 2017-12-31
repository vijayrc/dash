package com.vijayrc.tools.dash.parse;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.removeEnd;

/**
 * @author Vijay Chakravarthy
 * @version %I% %G%
 * @since 1.0
 */
public class CsvLine {
    private Map<String, String> map = new TreeMap<>();
    private List<String> fields;

    public CsvLine(String... fields) {
        this.fields = asList(fields);
    }

    public boolean canAdd() {
        return map.keySet().size() < fields.size();
    }

    public void append(String field, String value) {
        map.put(field, value);
    }

    public String flush() {
        final StringBuilder builder = new StringBuilder();
        fields.forEach(key -> {
            String value = map.get(key);
            value = isNotBlank(value) ? value : "0";
            builder.append(value).append(",");
        });
        map.clear();
        return removeEnd(builder.toString(), ",") + "\n";
    }

    public boolean isNotEmpty() {
        return !map.isEmpty();
    }

}
