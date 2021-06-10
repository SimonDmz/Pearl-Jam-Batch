package fr.insee.pearljam.batch.dao;

import java.sql.SQLException;
import java.util.List;

import fr.insee.pearljam.batch.campaign.ContactAttemptType;

/**
 * Interface for the ContactAttempt table
 * @author bclaudel
 *
 */
public interface ContactAttemptDao {
	/**
     * Get ContactAttempts by Survey unit id in database
     * @param surveyUnitId
     * @return List of ContactAttemptType
     */
	List<ContactAttemptType> getContactAttemptTypeBySurveyUnitId(String surveyUnitId);
	/**
     * Delete ContactAttempts by Survey unit id in database
     * @param surveyUnitId
     */
	void deleteContactAttemptBySurveyUnitId(String surveyUnitId) throws SQLException;
}
