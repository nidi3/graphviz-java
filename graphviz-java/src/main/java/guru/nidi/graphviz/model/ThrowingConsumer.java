package guru.nidi.graphviz.model;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T value) throws Exception;
}
