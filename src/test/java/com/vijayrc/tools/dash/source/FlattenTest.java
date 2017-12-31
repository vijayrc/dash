package com.vijayrc.tools.dash.source;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Vijay Chakravarthy@vijayrc.com
 * @version %I% %G%
 * @since 1.0
 */
public class FlattenTest {

    @Test
    public void shouldFlattenESJson() throws IOException {
        start();

    }

    private void start() throws IOException {
        String json = FileUtils.readFileToString(new File(this.getClass().getResource("/es-stats.json").getFile()));

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> inputMap = fromJsonToGenericPojo(mapper, json, Map.class, String.class, Object.class);
        Map<String, Object> outputMap = new TreeMap<>();
        flatten(inputMap, outputMap, null);

        JSONObject jsonObject = new JSONObject(outputMap);
        String jsonStr = jsonObject.toString(4);
        System.out.println(jsonStr);
    }

    private void flatten(Map<String, Object> map, Map<String, Object> output, String key) {
        String prefix = "";
        if (key != null) {
            prefix = key + ".";
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String currentKey = prefix + entry.getKey();
            if (entry.getValue() instanceof Map) {
                flatten((Map<String, Object>) entry.getValue(), output, prefix + entry.getKey());
            } else if (entry.getValue() instanceof List) {
                output.put(currentKey, entry.getValue());
            } else {
                output.put(currentKey, entry.getValue());
            }
        }
    }

    private <T> T fromJsonToGenericPojo(ObjectMapper objectMapper, String json, Class<?> classType, Class<?>... genericTypes) {
        JavaType javaType = TypeFactory.defaultInstance().constructParametricType(classType, genericTypes);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}
