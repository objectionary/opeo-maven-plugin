package org.eolang.opeo.ast;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.cactoos.map.MapEntry;

public final class Attributes {

    private final LinkedHashMap<String, String> all;

    public Attributes(final String raw) {
        this(Attributes.parse(raw));
    }

    public Attributes(final String... entries) {
        this(Attributes.fromEntries(entries));
    }

    public Attributes(final Map<String, String> all) {
        this.all = new LinkedHashMap<>(all);
    }

    @Override
    public String toString() {
        return this.all.entrySet().stream()
            .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("|"));
    }

    public String descriptor() {
        return this.find("descriptor");
    }

    public String type() {
        return this.find("type");
    }

    public String owner() {
        return this.find("owner");
    }

    private String find(final String key) {
        if (this.all.containsKey(key)) {
            return this.all.get(key);
        } else {
            throw new IllegalArgumentException(
                String.format("'%s' is not defined: %s", key, this));
        }
    }

    private static Map<String, String> fromEntries(final String[] entries) {
        final int length = entries.length;
        if (length % 2 != 0) {
            throw new IllegalArgumentException("Entries must be even");
        }
        final Map<String, String> res = new LinkedHashMap<>(0);
        for (int idx = 0; idx < length; idx += 2) {
            res.put(entries[idx], entries[idx + 1]);
        }
        return res;
    }

    private static Map<String, String> parse(final String raw) {
        return Arrays.stream(raw.split("\\|")).map(entry -> entry.split("="))
            .map(entry -> new MapEntry<>(entry[0], entry[1]))
            .collect(Collectors.toMap(MapEntry::getKey, MapEntry::getValue));
    }

}
