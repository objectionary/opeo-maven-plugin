package org.eolang.opeo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 * Remove this test as soon as you start developing your own code.
 * @since 0.1
 */
class AppTest {

    /**
     * Rigorous Test.
     */
    @Test
    void shouldAnswerWithTrue() {
        MatcherAssert.assertThat(
            "Dummy test. Remove it!",
            new App(),
            Matchers.notNullValue()
        );
    }
}
