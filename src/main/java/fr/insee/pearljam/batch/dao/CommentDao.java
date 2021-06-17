package fr.insee.pearljam.batch.dao;

import java.sql.SQLException;
import java.util.List;

import fr.insee.pearljam.batch.campaign.CommentType;

/**
 * Interface for the Comment table
 * @author bclaudel
 *
 */
public interface CommentDao {
	/**
     * Get Comments by Survey unit id in database
     * @param surveyUnitId
     * @return List of CommentType
     */
	List<CommentType> getCommentBySurveyUnitId(String surveyUnitId) throws Exception;
	
	/**
     * Delete Comments by Survey unit id in database
     * @param surveyUnitId
     */
	void deleteCommentBySurveyUnitId(String surveyUnitId) throws SQLException;
}
