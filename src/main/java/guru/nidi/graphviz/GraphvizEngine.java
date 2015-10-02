package guru.nidi.graphviz;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class GraphvizEngine {
    private static class State {
        private static final int START = 2, INITING = 1, INITED = 0;
    }

    private static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByExtension("js");
    private static CountDownLatch state = new CountDownLatch(State.START);
    private static Exception initException;

    public static void init() {
        final Thread starter = new Thread(GraphvizEngine::doInit);
        starter.setDaemon(true);
        starter.start();
    }

    static String execute(String dot) {
        checkInited();
        try {
            return (String) ENGINE.eval("$$prints=[]; Viz('" + dot + "');");
        } catch (ScriptException e) {
            if (e.getMessage().startsWith("abort")) {
                try {
                    String msgs = "";
                    for (final Object message : ((Map<Integer, Object>) ENGINE.eval("$$prints")).values()) {
                        msgs += message + "\n";
                    }
                    throw new GraphvizException(msgs);
                } catch (ScriptException e1) {
                    //fall through to general exception
                }
            }
            throw new GraphvizException("Problem executing graphviz", e);
        }
    }

    private static void doInit() {
        try {
            state.countDown();
            ENGINE.eval("var $$prints=[], print=function(s){$$prints.push(s);};");
            ENGINE.eval(new InputStreamReader(GraphvizEngine.class.getResourceAsStream("/viz.js"), "utf-8"));
            ENGINE.eval("Viz('digraph g { a -> b; }');");
            state.countDown();
        } catch (Exception e) {
            initException = e;
        }
    }

    private static void checkInited() {
        if (state.getCount() == State.START) {
            doInit();
        }
        if (initException != null) {
            throw new GraphvizException("Could not start graphviz engine", initException);
        }
        try {
            if (!state.await(30, TimeUnit.SECONDS)) {
                throw new GraphvizException("Initializing graphviz engine took too long");
            }
        } catch (InterruptedException e) {
            //ignore
        }
    }

}
