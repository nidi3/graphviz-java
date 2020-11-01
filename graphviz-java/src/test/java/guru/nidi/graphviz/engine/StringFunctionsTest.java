package guru.nidi.graphviz.engine;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringFunctionsTest {
    @Test
    void replaceRegex() {
        assertEquals("1-a2-b3c", StringFunctions.replaceRegex("1a2b3c", Pattern.compile("([a-z])[0-9]"), a -> "-" + a));
    }

    @Test
    void escapeJs() {
        assertEquals("1\\\\a2\\'3\\n4\\n5\\n6", StringFunctions.escapeJs("1\\a2'3\n4\r5\r\n6"));
    }

    @Test
    void replaceSubSpaces() {
        char[] cs = new char[34];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = (char) i;
        }
        assertEquals("         \t\n  \r                   !", StringFunctions.replaceSubSpaces(new String(cs)));
    }

    @Test
    void replaceNonWordChars() {
        char[] cs = new char[127];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = (char) i;
        }
        assertEquals("----------------------------------------------.-0123456789-------"
                        + "ABCDEFGHIJKLMNOPQRSTUVWXYZ------abcdefghijklmnopqrstuvwxyz----",
                StringFunctions.replaceNonWordChars(new String(cs)));
    }
}
