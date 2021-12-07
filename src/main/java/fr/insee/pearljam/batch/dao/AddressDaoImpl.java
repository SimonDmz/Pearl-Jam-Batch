package fr.insee.pearljam.batch.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.campaign.InseeAddressType;

/**
 * Interface for the Address entity
 * 
 * @author scorcaud
 *
 */
@Service
public class AddressDaoImpl implements AddressDao {

	@Autowired
	@Qualifier("pilotageJdbcTemplate")
	JdbcTemplate pilotageJdbcTemplate;

	/**
	 * Retrieve the AddressId by the GeographicLocationId passed in parameter
	 * 
	 * @param id
	 * @return AddressId
	 */
	@Override
	public Long getAddressIdByGeographicalLocationId(String geographicalLocationId) {
		String qString = "SELECT id FROM address WHERE geographical_location_id=?";
		return pilotageJdbcTemplate.queryForObject(qString, new Object[] { geographicalLocationId }, Long.class);
	}

	@Override
	public Long createAddress(InseeAddressType inseeAddress) {
		String qString = "INSERT INTO address (dtype, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES ('InseeAddress',?,?,?,?,?,?,?,?) RETURNING id";
		return pilotageJdbcTemplate.queryForObject(qString,
				new Object[] { inseeAddress.getL1(), inseeAddress.getL2(), inseeAddress.getL3(), inseeAddress.getL4(),
						inseeAddress.getL5(), inseeAddress.getL6(), inseeAddress.getL7(),
						inseeAddress.getGeographicalLocationId() },
				Long.class);
	}

	@Override
	public void updateAddress(InseeAddressType inseeAddress, String surveyUnitId) {
		String qString = new StringBuilder("UPDATE public.address as adrs ")
				.append("SET l1=?, l2=?, l3=?, l4=?, l5=?, l6=?, l7=?, geographical_location_id=? " + "FROM survey_unit su ")
				.append("WHERE su.address_id=adrs.id AND su.id=?")
				.toString();
		pilotageJdbcTemplate.update(qString, inseeAddress.getL1(), inseeAddress.getL2(), inseeAddress.getL3(),
				inseeAddress.getL4(), inseeAddress.getL5(), inseeAddress.getL6(), inseeAddress.getL7(),
				inseeAddress.getGeographicalLocationId(), surveyUnitId);
	}
	
	public InseeAddressType getAddressBySurveyUnitId(String surveyUnitId) {
		String qString = "SELECT * FROM address ad INNER JOIN survey_unit su on su.address_id = ad.id WHERE su.id=?";
		List<InseeAddressType> addList = pilotageJdbcTemplate.query(qString, new Object[] {surveyUnitId}, new AddressMapper());
		if(!addList.isEmpty()) {
			return addList.get(0);
		} else {
			return null;
		}
	}
	
	private static final class AddressMapper implements RowMapper<InseeAddressType> {
        public InseeAddressType mapRow(ResultSet rs, int rowNum) throws SQLException         {
        	InseeAddressType in = new InseeAddressType();
        	in.setGeographicalLocationId(rs.getString("geographical_location_id"));
        	in.setL1(rs.getString("l1"));
        	in.setL2(rs.getString("l2"));
        	in.setL3(rs.getString("l3"));
        	in.setL4(rs.getString("l4"));
        	in.setL5(rs.getString("l5"));
        	in.setL6(rs.getString("l6"));
        	in.setL7(rs.getString("l7"));
            return in;
        }
    }
	
	public void deleteAddressById(Long addressId) {
		String qString = "DELETE FROM address WHERE id=?";
		pilotageJdbcTemplate.update(qString, addressId);
	}
}
