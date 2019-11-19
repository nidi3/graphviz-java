package guru.nidi.graphviz.engine;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8RuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class V8JavascriptEngine extends AbstractJavascriptEngine {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGraphvizEngine.class);
    private final V8 v8;
    private final ResultHandler resultHandler = new ResultHandler();

    public V8JavascriptEngine() {
        this(null);
    }

    public V8JavascriptEngine(@Nullable String extractionPath) {
        LOG.info("Starting V8 runtime...");
        v8 = V8.createV8Runtime(null, extractionPath);
        LOG.info("Started V8 runtime. Initializing javascript...");
        v8.registerJavaMethod((receiver, parameters) -> {
            resultHandler.setResult(parameters.getString(0));
        }, "result");
        v8.registerJavaMethod((receiver, parameters) -> {
            final String rawMsg = parameters.getString(0);
            final String msg = rawMsg.matches("TypeError: Module\\..*? is not a function")
                    ? "Got Error: '" + rawMsg + "'. This is probably an out of memory error."
                    + " Try using the totalMemory method."
                    : rawMsg;
            resultHandler.setError(msg);
        }, "error");
        LOG.info("Initialized javascript.");
    }

    @Override
    protected String execute(String js) {
        try {
            v8.executeVoidScript(js);
            return resultHandler.waitFor();
        } catch (V8RuntimeException e) {
            throw new GraphvizException("Problem executing javascript", e);
        }
    }

    @Override
    public void close() {
        v8.release(true);
    }
}
