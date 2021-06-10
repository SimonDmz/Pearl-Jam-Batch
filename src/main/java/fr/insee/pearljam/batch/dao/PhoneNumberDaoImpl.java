package fr.insee.pearljam.batch.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.campaign.PhoneNumberType;

/**
 * Interface for the PhoneNumber entity
 * 
 * @author scorcaud
 *
 */
@Service
public class PhoneNumberDaoImpl implements PhoneNumberDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void createPhoneNumber(PhoneNumberType phoneNumber, Long personId) {
		String qString = "INSERT INTO phone_number (favorite, number, source, person_id) VALUES (false, ?,?, ?)";
		Integer source;
		switch(phoneNumber.getSource().toLowerCase()) {
			case "fiscal":
				source = 0;
				break;
			case "directory":
				source = 1;
				break;
			case "interviewer": 
				source = 2;
				break;
			default:
				source = null;
		}
		
		jdbcTemplate.update(qString, phoneNumber.getNumber(), source, personId);
	}

	@Override
	public void deletePhoneNumbersByPersonId(Long personId) {
		String qString = "DELETE FROM phone_number WHERE person_id=?";
		jdbcTemplate.update(qString, personId);
	}
	
	@Override
	public void deletePhoneNumbersBySurveyUnitId(String surveyUnitId) {
		String qString = new StringBuilder("DELETE FROM phone_number WHERE person_id IN ")
				.append("(SELECT id FROM person WHERE survey_unit_id=?)")
				.toString();
		jdbcTemplate.update(qString, surveyUnitId);
	}
	
	private static final class PhoneNumberTypeMapper implements RowMapper<PhoneNumberType> {
        public PhoneNumberType mapRow(ResultSet rs, int rowNum) throws SQLException         {
        	PhoneNumberType phoneNumber = new PhoneNumberType();
        	phoneNumber.setNumber(rs.getString("number"));
        	Integer source = rs.getInt("source");
        	if(!rs.wasNull()) {
        		switch(source) {
	    			case 0:
	    				phoneNumber.setSource("fiscal");
	    				break;
	    			case 1:
	    				phoneNumber.setSource("directory");
	    				break;
	    			case 2: 
	    				phoneNumber.setSource("interviewer");
	    				break;
	    			default:
	    				break;
        		}
        	}
            return phoneNumber;
        }
    }
	
	@Override
	public List<PhoneNumberType> getPhoneNumbersByPersonId(Long id) {
		String qString = "SELECT phone_number.* FROM phone_number WHERE person_id=?";
		return jdbcTemplate.query(qString, new Object[] {id}, new PhoneNumberTypeMapper());
	}
}
