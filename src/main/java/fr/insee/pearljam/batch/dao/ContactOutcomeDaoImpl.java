package fr.insee.pearljam.batch.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.campaign.ContactOutcomeType;

/**
 * Service for the ContactOutcome entity that implements the interface associated
 * @author scorcaud
 *
 */
@Service
public class ContactOutcomeDaoImpl implements ContactOutcomeDao{
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public ContactOutcomeType getContactOutcomeTypeBySurveyUnitId(String surveyUnitId) {
		String qString = "SELECT * FROM contact_outcome WHERE survey_unit_id=? LIMIT 1";
		List<ContactOutcomeType> listCont = jdbcTemplate.query(qString, new Object[] {surveyUnitId}, new ContactOutcomeTypeMapper());
		if(!listCont.isEmpty()) {
			return listCont.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Implements the mapping between the result of the query and the ContactOutcomeType entity
	 * @return ContactOutcomeTypeMapper
	 */
	private static final class ContactOutcomeTypeMapper implements RowMapper<ContactOutcomeType> {
        public ContactOutcomeType mapRow(ResultSet rs, int rowNum) throws SQLException         {
        	ContactOutcomeType cont = new ContactOutcomeType();
        	cont.setDate(String.valueOf(rs.getLong("date")));
        	cont.setOutcomeType(rs.getString("type"));
        	cont.setTotalNumberOfContactAttempts(rs.getInt("total_number_of_contact_attempts"));
            return cont;
        }
    }
	
	public void deleteContactOutcomeBySurveyUnitId(String surveyUnitId) throws SQLException {
		String qString = "DELETE FROM contact_outcome WHERE survey_unit_id=?";
		jdbcTemplate.update(qString, surveyUnitId);
	}
}
