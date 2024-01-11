package org.eolang.opeo.compilation;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Hex string.
 * @since 0.1.0
 */
final class HexString {

    /**
     * Hex radix.
     */
    private static final int RADIX = 16;

    /**
     * Hex string.
     * Example:
     * - "48 65 6C 6C 6F 20 57 6F 72 6C 64 21"
     */
    private final String hex;

    /**
     * Constructor.
     * @param hex Hex string.
     */
    HexString(final String hex) {
        this.hex = hex;
    }

    /**
     * Convert hex string to human-readable string.
     * Example:
     *  "48 65 6C 6C 6F 20 57 6F 72 6C 64 21" -> "Hello World!"
     * @return Human-readable string.
     */
    String decode() {
        try {
            final String result;
            if (this.hex.isEmpty()) {
                result = "";
            } else {
                result = Arrays.stream(this.hex.split(" "))
                    .map(ch -> (char) Integer.parseInt(ch, HexString.RADIX))
                    .map(String::valueOf)
                    .collect(Collectors.joining());
            }
            return result;
        } catch (final NumberFormatException exception) {
            throw new IllegalArgumentException(
                String.format("Invalid hex string: %s", this.hex),
                exception
            );
        }
    }

    /**
     * Convert hex string to integer.
     * @return Integer.
     */
    int decodeAsInt() {
        return Integer.parseInt(this.hex.trim().replace(" ", ""), HexString.RADIX);
    }

    /**
     * Convert hex string to boolean.
     * @return Boolean.
     */
    boolean decodeAsBoolean() {
        final String value = this.hex.trim();
        if (value.length() != 2) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid hex boolean string: %s, the expected size is 2: 01 or 00",
                    this.hex
                )
            );
        }
        return value.equals("01");
    }
}
