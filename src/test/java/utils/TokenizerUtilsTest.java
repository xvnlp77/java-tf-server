package utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @Author chouxiaohui
 * @Date 2020/11/4 5:37 PM
 * @Version 1.0
 */
public class TokenizerUtilsTest {



    @Test
    public void _is_control() {
        assertTrue("_is_control error 1", TokenizerUtils._is_control('\u0005'));

        assertFalse("_is_control error 2", TokenizerUtils._is_control('A'));
        assertFalse("_is_control error 3", TokenizerUtils._is_control(' '));
        assertFalse("_is_control error 4", TokenizerUtils._is_control('\t'));
        assertFalse("_is_control error 5", TokenizerUtils._is_control('\r'));

        System.out.println("_is_control() test passed");

    }

    @Test
    public void _is_whitespace() {
        assertTrue("_is_whitespace error 1", TokenizerUtils._is_whitespace(' '));
        assertTrue("_is_whitespace error 2", TokenizerUtils._is_whitespace('\t'));
        assertTrue("_is_whitespace error 3", TokenizerUtils._is_whitespace('\r'));
        assertTrue("_is_whitespace error 4", TokenizerUtils._is_whitespace('\n'));
        assertTrue("_is_whitespace error 5", TokenizerUtils._is_whitespace('\u00A0'));

        assertFalse("_is_whitespace error 6",TokenizerUtils._is_whitespace('A'));
        assertFalse("_is_whitespace error 7",TokenizerUtils._is_whitespace('-'));

        System.out.println("_is_whitespace() test passed");

    }


    @Test
    public void _is_punctuation() {
        assertTrue("_is_punctuation error 1", TokenizerUtils._is_punctuation('-'));
        assertTrue("_is_punctuation error 2", TokenizerUtils._is_punctuation('$'));
        assertTrue("_is_punctuation error 3", TokenizerUtils._is_punctuation('`'));
        assertTrue("_is_punctuation error 4", TokenizerUtils._is_punctuation('.'));

        assertFalse("_is_punctuation error 65",TokenizerUtils._is_punctuation('A'));
        assertFalse("_is_punctuation error 6",TokenizerUtils._is_punctuation(' '));

        System.out.println("_is_punctuation() test passed");

    }
}