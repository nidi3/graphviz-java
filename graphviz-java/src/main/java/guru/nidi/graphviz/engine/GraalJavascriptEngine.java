package guru.nidi.graphviz.engine;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

class GraalJavascriptEngine extends AbstractJavascriptEngine {
    private final ResultHandler resultHandler = new ResultHandler();
    private final Context context = Context.newBuilder("js").allowAllAccess(true).build();

    GraalJavascriptEngine() {
        context.getPolyglotBindings().putMember("handler", resultHandler);
        eval("function result(r){ Polyglot.import('handler').setResult(r); }"
                + "function error(r){ Polyglot.import('handler').setError(r); }");
    }

    @Override
    protected String execute(String js) {
        try {
            eval(js);
            return resultHandler.waitFor();
        } catch (PolyglotException e) {
            throw new GraphvizException("Problem executing javascript", e);
        }
    }

    private void eval(String code) {
        context.eval("js", code);
    }
}
