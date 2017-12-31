package com.vijayrc.tools.dash.parse;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

/**
 * @author Vijay Chakravarthy@vijayrc.com
 * @version %I% %G%
 * @since 1.0
 */
public class Flattener {

    private static Logger log = Logger.getLogger(Flattener.class);

    public String flatten(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final Map<String, Object> inputMap = convertJsonToMap(mapper, json, Map.class, String.class, Object.class);
        final Map<String, Object> outputMap = new TreeMap<>();

        flatten(inputMap, outputMap, null);
        return new JSONObject(outputMap).toString(4);
    }

    @SuppressWarnings("unchecked")
    private void flatten(Map<String, Object> inputMap, Map<String, Object> outputMap, String key) {
        String prefix = "";
        if (key != null) prefix = key + ".";

        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            String currentKey = prefix + entry.getKey();
            Object currentValue = entry.getValue();

            if (currentValue instanceof Map) {
                flatten((Map<String, Object>) currentValue, outputMap, prefix + entry.getKey());
            } else if (currentValue instanceof List) {
                List list = (List) currentValue;
                if (!list.isEmpty() && list.get(0) instanceof Map) { //list of Maps
                    int count = 0;
                    for (Object item : list) {
                        flatten((Map<String, Object>) item, outputMap, prefix + entry.getKey() + "." + count);
                        count++;
                    }
                } else {
                    outputMap.put(currentKey, currentValue);
                }
            } else {
                outputMap.put(currentKey, currentValue);
            }
        }
    }

    private <T> T convertJsonToMap(ObjectMapper objectMapper, String json, Class<?> classType, Class<?>... genericTypes) {
        JavaType javaType = TypeFactory.defaultInstance().constructParametricType(classType, genericTypes);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            log.error(getFullStackTrace(e));
            throw new IllegalStateException(e);
        }
    }
}
