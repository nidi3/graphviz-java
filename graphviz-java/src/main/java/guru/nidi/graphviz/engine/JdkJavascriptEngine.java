package guru.nidi.graphviz.engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JdkJavascriptEngine implements JavascriptEngine {
    static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByExtension("js");
    private final JavascriptEngine engine;

    public JdkJavascriptEngine() {
        engine = newEngine();
    }

    private JavascriptEngine newEngine() {
        try {
            return new GraalJavascriptEngine();
        } catch (ExceptionInInitializerError | NoClassDefFoundError | IllegalStateException e) {
            return new NashornJavascriptEngine();
        }
    }

    @Override
    public void executeJavascript(String raw) {
        engine.executeJavascript(raw);
    }

    @Override
    public String executeJavascript(String pre, String src, String post) {
        return engine.executeJavascript(pre, src, post);
    }

    @Override
    public void close() throws Exception {
        engine.close();
    }
}
