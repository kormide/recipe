package ca.derekcormier.recipe;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Cake {
    public static final String SEPARATOR = ".";
    private final Map<String,Object> entries = new HashMap<>();
    private final LinkedList<String> prefixStack = new LinkedList<>();

    public static String key(String...subKeys) {
        if (subKeys.length == 0) {
            throw new IllegalArgumentException("cannot form cake key; no keys supplied");
        }
        Arrays.asList(subKeys).forEach(Cake::validateKey);
        return StringUtils.join(subKeys, Cake.SEPARATOR);
    }

    private static void validateKey(String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("keys cannot be empty");
        }
        if (key.contains(Cake.SEPARATOR)) {
            throw new IllegalArgumentException("keys cannot contain the namespace separator: " + Cake.SEPARATOR);
        }
    }

    public void publish(String key, Object value) {
        getSubKeysAndValidateFullKey(key);
        String newKey = getPrefixWithSeparator(prefixStack) + key;
        entries.put(newKey, value);
    }

    public void inNamespace(String key, Runnable runnable) {
        List<String> keys = getSubKeysAndValidateFullKey(key);
        keys.forEach(prefixStack::addLast);

        try {
            runnable.run();
        } finally {
            keys.forEach(k -> prefixStack.removeLast());
        }
    }

    public List<String> getSubKeysAndValidateFullKey(String fullKey) {
        List<String> keys = (null == fullKey) ? new ArrayList<>() : Arrays.asList(StringUtils.split(fullKey, Cake.SEPARATOR));
        if (keys.isEmpty() || StringUtils.countMatches(fullKey, Cake.SEPARATOR) != keys.size() - 1) {
            throw new IllegalArgumentException("cannot publish value for empty key");
        }

        keys.forEach(Cake::validateKey);
        return keys;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String... key) {
        List<String> keys = (key == null) ? new ArrayList<>() : Arrays.asList(key);

        if (keys.isEmpty()) {
            throw new IllegalArgumentException("cannot get value for empty key");
        }

        keys.forEach(Cake::validateKey);
        String fullKey = StringUtils.join(keys, Cake.SEPARATOR);

        // search within current namespace
        String searchKey = getPrefixWithSeparator(prefixStack) + fullKey;
        if (entries.containsKey(searchKey)) {
            return (T)entries.get(searchKey);
        }

        // search within each ancestor namespace up to the root
        LinkedList<String> namespaces = new LinkedList<>(prefixStack);
        while (!namespaces.isEmpty()) {
            namespaces.removeLast();
            searchKey = getPrefixWithSeparator(namespaces) + fullKey;
            if (entries.containsKey(searchKey)) {
                return (T)entries.get(searchKey);
            }
        }

        // search within any other namespace (if unambiguous)
        List<String> candidates = entries.keySet().stream().filter(k -> k.endsWith(fullKey)).collect(Collectors.toList());
        if (candidates.size() == 1) {
            return (T)entries.get(candidates.get(0));
        }
        else if (candidates.isEmpty()) {
            throw new RuntimeException("cake does not contain key '" + fullKey + "'");
        }
        else {
            throw new RuntimeException("cannot retrieve ambiguous key '" + fullKey + "'");
        }
    }

    public String getPublishedKeyForValue(Object value, boolean fullyQualified) {
        List<String> matchingKeys = entries.entrySet().stream().filter(e -> e.getValue().getClass() == value.getClass() && e.getValue().equals(value)).map(Map.Entry::getKey).collect(Collectors.toList());
        if (matchingKeys.size() == 1) {
            if (fullyQualified) {
                return matchingKeys.get(0);
            }
            String[] splitKey = StringUtils.split(matchingKeys.get(0), Cake.SEPARATOR);
            return splitKey[splitKey.length - 1];
        }
        if (matchingKeys.size() > 1) {
            throw new IllegalArgumentException("multiple keys found for object " + value);
        }
        throw new IllegalArgumentException("no key found for object " + value);
    }


    private String getPrefixWithSeparator(List<String> namespaces) {
        return StringUtils.join(namespaces, Cake.SEPARATOR) + (namespaces.size() > 0 ? Cake.SEPARATOR : "");
    }

    @JsonAnyGetter
    private Map<String,Object> getEntries() {
        return entries;
    }

    @JsonAnySetter
    private void setEntry(String key, Object value) {
        entries.put(key, value);
    }
}
