package fr.insee.pearljam.batch.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.campaign.ClosingCauseType;

/**
 * Service for the State entity that implements the interface associated
 * @author scorcaud
 *
 */
@Service
public class ClosingCauseDaoImpl implements ClosingCauseDao{

	@Autowired
	@Qualifier("pilotageJdbcTemplate")
	JdbcTemplate pilotageJdbcTemplate;
		
	@Override
	public void deleteAllClosingCausesOfSurveyUnit(String surveyUnitId) {
		String qString ="DELETE FROM closing_cause WHERE survey_unit_id=?";
		pilotageJdbcTemplate.update(qString, surveyUnitId);
	}
	
	@Override
	public List<String> getClosingCausesBySuId(String suId) {
		String qString = "SELECT id FROM closing_cause WHERE survey_unit_id=?";
		return pilotageJdbcTemplate.queryForList(qString, new Object[] {suId}, String.class);
	}

	@Override
	public ClosingCauseType getClosingCauseTypeBySurveyUnitId(String surveyUnitId) throws Exception {
		String qString = "SELECT * FROM closing_cause WHERE survey_unit_id=? LIMIT 1";
		List<ClosingCauseType> listRes = pilotageJdbcTemplate.query(qString, new Object[] {surveyUnitId}, new ClosingCauseTypeMapper());
		if(!listRes.isEmpty()) {
			return listRes.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Implements the mapping between the result of the query and the ClosingCauseType entity
	 * @return ClosingCauseTypeMapper
	 */
	private static final class ClosingCauseTypeMapper implements RowMapper<ClosingCauseType> {
        public ClosingCauseType mapRow(ResultSet rs, int rowNum) throws SQLException         {
        	ClosingCauseType c = new ClosingCauseType();
        	c.setDate(String.valueOf(rs.getLong("date")));
        	c.setType(rs.getString("type"));
            return c;
        }
    }
	
	
}
