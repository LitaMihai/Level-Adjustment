package packWork;

/**
 * Exceptie pe care o folosim cand argumentele nu sunt corecte
 */
public class InvalidArgumentsException extends Exception {

    /**
	 * Default serialVersionID
	 */
	private static final long serialVersionUID = 1L;

	public InvalidArgumentsException(String message) {
        super(message);
    }
}
