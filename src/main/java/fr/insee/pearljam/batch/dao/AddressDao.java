package fr.insee.pearljam.batch.dao;

import fr.insee.pearljam.batch.campaign.InseeAddressType;
/**
 * Interface for the Address table
 * @author bclaudel
 *
 */
public interface AddressDao {
	/**
	 * Get an address id by geographical location
	 * @param geographicalLocationId
	 * @return Long
	 */
	Long getAddressIdByGeographicalLocationId(String geographicalLocationId);

	/**
	 * Create an Address in database
	 * @param inseeAddress
	 * @return long
	 */
	Long createAddress(InseeAddressType inseeAddress);

	/**
	 * Update an Address in database
	 * @param inseeAddress
	 * @param surveyUnitId
	 */
	void updateAddress(InseeAddressType inseeAddress, String surveyUnitId);
	
	/**
	 * Get an address by survey unit id
	 * @param surveyUnitId
	 * @return InseeAddressType
	 */
	InseeAddressType getAddressBySurveyUnitId(String surveyUnitId);
	
	/**
	 * Delete an Address in database
	 * @param addressId
	 */
	void deleteAddressById(Long addressId);
}
