package guru.nidi.graphviz.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SvgSizeAnalyzerTest {
    private static final String SVG =
            "<svg width=\"1000pt\" height=\"500pt\" viewBox=\"0.00 0.00 7272.00 3618.00\" "
                    + "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                    + "<g id=\"graph0\" class=\"graph\" transform=\"scale(1.0 2.5) rotate(30.0) translate(360.0 32.0)\">";

    @Test
    void getValues() {
        final SvgSizeAnalyzer a = new SvgSizeAnalyzer(SVG);
        assertEquals(1000, a.getWidth());
        assertEquals(500, a.getHeight());
        assertEquals("pt", a.getUnit());
        assertEquals(1.0, a.getScaleX());
        assertEquals(2.5, a.getScaleY());
        assertEquals(30, a.getRotate());
        assertEquals(360, a.getTranslateX());
        assertEquals(32, a.getTranslateY());
    }

    @Test
    void adjustedWithoutChanges() {
        final SvgSizeAnalyzer a = new SvgSizeAnalyzer(SVG);
        assertEquals(SVG, a.adjusted());
    }

    @Test
    void adjustedWithChanges() {
        final SvgSizeAnalyzer a = new SvgSizeAnalyzer(SVG);
        a.setSize(1, 2);
        a.setScale(3.5, 4.0);
        assertEquals("<svg width=\"1px\" height=\"2px\" viewBox=\"0.00 0.00 7272.00 3618.00\" "
                        + "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                        + "<g id=\"graph0\" class=\"graph\" transform=\"scale(3.5 4.0) rotate(30.0) translate(360.0 32.0)\">",
                a.adjusted());
    }
}
