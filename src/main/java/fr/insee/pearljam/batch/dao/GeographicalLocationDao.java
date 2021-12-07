package fr.insee.pearljam.batch.dao;

import fr.insee.pearljam.batch.context.GeographicalLocationType;

/**
 * Interface for the GeographicalLocation table
 * @author scorcaud
 *
 */
public interface GeographicalLocationDao {
	/**
     * Get a GeographicalLocation by id in database
     * @param id
     * @return boolean
     */
	boolean existGeographicalLocation(String id);
	/**
	 * Create a GeographicalLocation in database
	 * @param geographicalLocation
	 */
	void createGeographicalLocation(GeographicalLocationType geographicalLocation);
	
}
