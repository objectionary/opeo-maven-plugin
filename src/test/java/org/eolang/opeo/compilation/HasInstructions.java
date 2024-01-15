package org.eolang.opeo.compilation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.opeo.ast.OpcodeName;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher for {@link List} of {@link XmlNode} to have specific instructions.
 * @since 0.1
 */
public final class HasInstructions extends TypeSafeMatcher<List<XmlNode>> {

    /**
     * Expected opcodes.
     */
    private final List<Integer> opcodes;

    /**
     * Bag of collected warnings.
     */
    private final List<String> warnings;

    /**
     * Constructor.
     * @param opcodes Expected opcodes.
     */
    public HasInstructions(final int... opcodes) {
        this(IntStream.of(opcodes).boxed().collect(Collectors.toList()));
    }

    /**
     * Constructor.
     * @param opcodes Expected opcodes.
     */
    private HasInstructions(final List<Integer> opcodes) {
        this.opcodes = opcodes;
        this.warnings = new ArrayList<>(0);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(
            String.format(
                "Expected to have %d opcodes, but got %d instead. %n%s%n",
                this.opcodes.size(),
                this.warnings.size(),
                String.join("\n", this.warnings)
            )
        );
    }

    @Override
    public boolean matchesSafely(final List<XmlNode> item) {
        boolean result = true;
        final int size = item.size();
        for (int index = 0; index < size; ++index) {
            if (!this.matches(item.get(index), index)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Check if node matches expected opcode.
     * @param node Node.
     * @param index Index.
     * @return True if node matches expected opcode.
     */
    private boolean matches(final XmlNode node, final int index) {
        final boolean result;
        final String base = node.attribute("base").orElseThrow();
        if (base.equals("opcode")) {
            result = this.verifyName(node, index);
        } else {
            this.warnings.add(
                String.format(
                    "Expected to have opcode at index %d, but got %s instead",
                    index,
                    base
                )
            );
            result = false;
        }
        return result;
    }

    /**
     * Verify opcode name.
     * @param node Node.
     * @param index Index.
     * @return True if opcode name is correct.
     */
    private boolean verifyName(final XmlNode node, final int index) {
        final boolean result;
        final Optional<String> oname = node.attribute("name");
        if (oname.isPresent()) {
            final String expected = new OpcodeName(this.opcodes.get(index)).simplified();
            final String name = oname.get();
            if (name.contains(expected)) {
                result = true;
            } else {
                this.warnings.add(
                    String.format(
                        "Expected to have opcode with name %s at index %d, but got %s instead",
                        expected,
                        index,
                        name
                    )
                );
                result = false;
            }
        } else {
            this.warnings.add(
                String.format(
                    "Expected to have opcode name at index %d, but got nothing instead",
                    index
                )
            );
            result = false;
        }
        return result;
    }
}
