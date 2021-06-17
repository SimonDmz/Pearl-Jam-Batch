package fr.insee.pearljam.batch.exception;

/**
 * Class to throw a ValidateException during the step of validation
 * @author scorcaud
 *
 */
public class TooManyReaffectationsException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Defaut constructor of a ValidateException
	 */
	public TooManyReaffectationsException() {
		super();
	}

	/**
	 * Constructor for a ValidateException
	 * @param message
	 */
	public TooManyReaffectationsException(String s) {
		super(s);
	}
}
