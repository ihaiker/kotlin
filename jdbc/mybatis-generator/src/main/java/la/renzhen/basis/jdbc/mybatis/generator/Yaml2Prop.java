package la.renzhen.basis.jdbc.mybatis.generator;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

/**
 * <p>
 *
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 20/06/2018 8:00 PM
 */
class Yaml2Prop {
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object yamlDocument) {
        Map<String, Object> yamlMap = new LinkedHashMap<>();
        // Document is a text block
        if (!(yamlDocument instanceof Map)) {
            yamlMap.put("content", yamlDocument);
            return yamlMap;
        }

        for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) yamlDocument).entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Map) {
                value = convertToMap(value);
            } else if (value instanceof Collection) {
                ArrayList<Map<String, Object>> collection = new ArrayList<>();
                for (Object element : ((Collection) value)) {
                    collection.add(convertToMap(element));
                }
                value = collection;
            }
            yamlMap.put(entry.getKey().toString(), value);
        }
        return yamlMap;
    }

    /**
     * Flatten multi-level map.
     */
    @SuppressWarnings("unchecked")
    Map<String, Object> flatten(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();

        for (String key : source.keySet()) {
            Object value = source.get(key);

            if (value instanceof Map) {
                Map<String, Object> subMap = flatten((Map<String, Object>) value);

                for (String subkey : subMap.keySet()) {
                    result.put(key + "." + subkey, subMap.get(subkey));
                }
            } else if (value instanceof Collection) {
                StringBuilder joiner = new StringBuilder();
                String separator = "";

                for (Object element : ((Collection) value)) {
                    Map<String, Object> subMap = flatten(Collections.singletonMap(key, element));
                    joiner.append(separator).append(subMap.entrySet().iterator().next().getValue().toString());
                    separator = ",";
                }

                result.put(key, joiner.toString());
            } else {
                result.put(key, String.valueOf(value));
            }
        }

        return result;
    }

    public Properties getProperties(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Properties properties = new Properties();
        try (Reader reader = new UnicodeReader(inputStream)) {
            Object object = yaml.load(reader);
            if (object != null) {
                Map<String, Object> yamlAsMap = convertToMap(object);
                properties.putAll(flatten(yamlAsMap));
            }
            return properties;
        } catch (IOException | ScannerException e) {
            throw new IllegalStateException("Unable to load yaml configuration from provided stream", e);
        }
    }
}
