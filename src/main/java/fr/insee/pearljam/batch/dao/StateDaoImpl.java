package fr.insee.pearljam.batch.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.campaign.StateType;
import fr.insee.pearljam.batch.campaign.SurveyUnitType;

/**
 * Service for the State entity that implements the interface associated
 * @author scorcaud
 *
 */
@Service
public class StateDaoImpl implements StateDao{

	@Autowired
	JdbcTemplate jdbcTemplate;
		
	@Override
	public void createState(Long date, String type, String surveyUnitId) {
		String qString = "INSERT INTO state (date, type, survey_unit_id) VALUES (?,?,?)";
		jdbcTemplate.update(qString, date, type, surveyUnitId);
	}
	
	public void deleteStateForSurveyUnitList(List<SurveyUnitType> surveyUnitIdList) {
		String qString ="DELETE FROM state WHERE survey_unit_id IN (?)";
		jdbcTemplate.update(qString, surveyUnitIdList);
	}
	
	public List<StateType> getStateBySurveyUnitId(String surveyUnitId) {
		String qString ="SELECT * FROM state WHERE survey_unit_id=?";
		return jdbcTemplate.query(qString, new Object[] {surveyUnitId}, new StateTypeMapper());
	}
	
	private static final class StateTypeMapper implements RowMapper<StateType> {
        public StateType mapRow(ResultSet rs, int rowNum) throws SQLException         {
        	StateType state = new StateType();
        	state.setDate(String.valueOf(rs.getLong("date")));
            state.setType(rs.getString("type"));
            return state;
        }
    }
	
	public void deleteStateBySurveyUnitId(String surveyUnitId) {
		String qString = "DELETE FROM state WHERE survey_unit_id=?";
		jdbcTemplate.update(qString, surveyUnitId);
	}
	
}
