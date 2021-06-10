package fr.insee.pearljam.batch;

import java.net.URL;

/**
 * Constant class : define all constant values
 * 
 * @author Claudel Benjamin
 * 
 */
public class Constants {
	/**
	 * The folder path to access to XSD
	 */
	public static final String SCHEMAS_FOLDER_PATH = "/xsd";

	public static final String NATIONAL = "NATIONAL";
	
	/**
	 * Format for the dates
	 */
	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	public static final String DATE_FORMAT_2 = "dd/MM/yyyy";

	/**
	 * The URL to to access to campaign.xsd
	 */
	public static final URL MODEL_CAMPAIGN = Constants.class.getResource(SCHEMAS_FOLDER_PATH+"/campaign.xsd");
	/**
	 * The URL to to access to context.xsd
	 */
	public static final URL MODEL_CONTEXT = Constants.class.getResource(SCHEMAS_FOLDER_PATH+"/context.xsd");
	
	/**
	 * The message for return batch code
	 */
	public static final String MSG_RETURN_CODE = "RETURN BATCH CODE : {}";
	/**
	 * The message when file move failed
	 */
	public static final String MSG_FAILED_MOVE_FILE = "Failed to move the file {}";
	/**
	 * The message when file move success
	 */
	public static final String MSG_FILE_MOVE_SUCCESS = "File {} renamed and moved successfully";
	
	public static final String AUTHORIZATION = "Authorization";
	
	// Opale endpoints
	public static final String API_OPALE_INTERVIEWERS = "/sabiane/interviewers";
	public static final String API_OPALE_ORGANIZATION_UNITS = "/sabiane/organization-units";
	public static final String API_OPALE_INTERVIEWERS_AFFECTATIONS = "/sabiane/interviewers/survey-units";
	public static final String API_OPALE_ORGANIZATION_UNITS_AFFECTATIONS = "/sabiane/organization-units/survey-units";
	public static final String API_OPALE_SURVEY_UNIT_OU_AFFECTATION = "/sabiane/organization-units/survey-unit/%s";
	public static final String API_OPALE_SURVEY_UNIT_INTERVIEWER_AFFECTATION = "/sabiane/survey-unit/%s/interviewer";

	private Constants() {}
	
}