package fr.insee.pearljam.batch.dao;

import fr.insee.pearljam.batch.campaign.InseeSampleIdentiersType;

/**
 * Interface for the SampleIdentifier table
 * @author scorcaud
 *
 */
public interface SampleIdentifierDao {
	/**
     * Create a SampleIdentifiers in database
     * @param inseeSampleIdentiers
     */
	Long createSampleIdentifier(InseeSampleIdentiersType inseeSampleIdentiers);
	/**
     * update a SampleIdentifiers in database
     * @param inseeSampleIdentiers
     * @param surveyUnitId
     */
	void updateSampleIdentifier(InseeSampleIdentiersType inseeSampleIdentiers, String surveyUnitId);
	/**
     * Get a SampleIdentifiers by surveyUnitId in database
     * @param surveyUnitId
     * @return InseeSampleIdentiersType
     */
	InseeSampleIdentiersType getSampleIdentiersBySurveyUnitId(String surveyUnitId);
	/**
     * Delete a SampleIdentifiers in database
     * @param sampleIdentifiersId
     */
	void deleteSampleIdentifiersById(Long sampleIdentifiersId);
}
