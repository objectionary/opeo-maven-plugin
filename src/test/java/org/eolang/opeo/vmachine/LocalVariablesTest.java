package org.eolang.opeo.vmachine;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;


/**
 * Test for {@link LocalVariables}.
 * @since 0.1
 */
class LocalVariablesTest {

    @Test
    void createsForEmptyStaticMethod() {
        final int res = new LocalVariables(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC, "()V").size();
        MatcherAssert.assertThat(
            String.format(
                "Local variables size for static method with no arguments should be 0, but was %d",
                res
            ),
            res,
            Matchers.equalTo(0)
        );
    }

    @Test
    void createsForEmptyInstanceMethod() {
        MatcherAssert.assertThat(
            "Local variables size for instance method with no arguments should contain 'this', but wasn't",
            new LocalVariables(Opcodes.ACC_PUBLIC, "()V").size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void createsForNonEmptyStaticMethod() {
        MatcherAssert.assertThat(
            "Local variables size for static method with arguments should be equal to arguments count from descriptor",
            new LocalVariables(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC, "(II)V").size(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void createsForNonEmptyInstanceMethod() {
        MatcherAssert.assertThat(
            "Local variables size for instance method with arguments should be equal to arguments count from descriptor + 1 for 'this'",
            new LocalVariables(Opcodes.ACC_PUBLIC, "(II)V").size(),
            Matchers.equalTo(3)
        );
    }
}