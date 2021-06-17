package fr.insee.pearljam.batch.exception;

/**
 * Class to throw a ValidateException during the step of validation
 * @author scorcaud
 *
 */
public class SynchronizationException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Defaut constructor of a ValidateException
	 */
	public SynchronizationException() {
		super();
	}

	/**
	 * Constructor for a ValidateException
	 * @param message
	 */
	public SynchronizationException(String s) {
		super(s);
	}
}
