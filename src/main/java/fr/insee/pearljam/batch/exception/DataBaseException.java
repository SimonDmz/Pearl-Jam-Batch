package fr.insee.pearljam.batch.exception;

/**
 * Class to throw a DataBaseException
 * @author scorcaud
 *
 */
public class DataBaseException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Defaut constructor of a DataBaseException
	 */
	public DataBaseException() {
		super();
	}

	/**
	 * Constructor for a DataBaseException
	 * @param message
	 */
	public DataBaseException(String s) {
		super(s);
	}
}
