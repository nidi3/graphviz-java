package guru.nidi.graphviz;

/**
 *
 */
public class Name {
    final String value;
    final boolean html;

    private Name(String value, boolean html) {
        this.value = value;
        this.html = html;
    }

    public static Name of(String value) {
        return new Name(value, false);
    }

    public static Name html(String value) {
        return new Name(value, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Name name = (Name) o;

        return !(value != null ? !value.equals(name.value) : name.value != null);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return value;
    }
}
