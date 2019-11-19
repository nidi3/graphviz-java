package guru.nidi.graphviz.engine;

public abstract class AbstractJavascriptEngine implements JavascriptEngine {
    @Override
    public void executeJavascript(String raw) {
        execute(raw + "; result('');");
    }

    @Override
    public String executeJavascript(String pre, String src, String post) {
        return execute(pre + "'" + escape(src) + "'" + post);
    }

    protected abstract String execute(String js);

    private String escape(String js) {
        return js.replace("\\", "\\\\").replace("'", "\\'").replaceAll("\\R", "\\\\n");
    }

    @Override
    public void close() {
    }
}
