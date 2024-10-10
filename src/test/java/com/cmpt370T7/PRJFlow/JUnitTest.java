package com.cmpt370T7.PRJFlow;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

/**  
 * Basic example of a non-GUI test 
*/
public class JUnitTest {
    @Test
    void simple_assert_pass() {
        Assertions.assertThat(2 + 1).isEqualTo(3);
    }

    @Test
    void simple_string_assert_pass() {
        Assertions.assertThat("Hello, World!").isEqualTo("Hello, World!");
    }
}
