public class DimensionNotMatchException extends Exception {
    private static final long serialVersionUID = 1L;

    public DimensionNotMatchException() {
        super("The two operators' dimensions do not match.");
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
