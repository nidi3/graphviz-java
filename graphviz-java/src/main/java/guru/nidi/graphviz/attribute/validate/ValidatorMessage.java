package guru.nidi.graphviz.attribute.validate;

import java.util.Objects;

public class ValidatorMessage {
    public enum Severity {ERROR, WARNING, INFO}

    public final Severity severity;
    public final String attribute;
    public final String message;
    public final int line;
    public final int column;

    ValidatorMessage(Severity severity, String message) {
        this(severity, "", message);
    }

    ValidatorMessage(Severity severity, String attribute, String message) {
        this(severity, attribute, message, 0, 0);
    }

    ValidatorMessage(Severity severity, String attribute, String message, int line, int column) {
        this.severity = severity;
        this.attribute = attribute;
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public ValidatorMessage at(int line, int column) {
        return new ValidatorMessage(severity, attribute, message, line, column);
    }

    ValidatorMessage at(String attribute) {
        return new ValidatorMessage(severity, attribute, message, line, column);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValidatorMessage that = (ValidatorMessage) o;
        return line == that.line &&
                column == that.column &&
                severity == that.severity &&
                attribute.equals(that.attribute) &&
                message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(severity, attribute, message, line, column);
    }

    @Override
    public String toString() {
        return "ValidatorMessage{" +
                line + ":" + column + " " + severity +
                ", attribute='" + attribute + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
