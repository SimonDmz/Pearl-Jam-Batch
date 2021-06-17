package fr.insee.pearljam.batch.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for the State entity that implements the interface associated
 * @author scorcaud
 *
 */
@Service
public class ClosingCauseDaoImpl implements ClosingCauseDao{

	@Autowired
	JdbcTemplate jdbcTemplate;
		
	@Override
	public void deleteAllClosingCausesOfSurveyUnit(String surveyUnitId) {
		String qString ="DELETE FROM closing_cause WHERE survey_unit_id=?";
		jdbcTemplate.update(qString, surveyUnitId);
	}
	
	@Override
	public List<String> getClosingCausesBySuId(String suId) {
		String qString = "SELECT id FROM closing_cause WHERE survey_unit_id=?";
		return jdbcTemplate.queryForList(qString, new Object[] {suId}, String.class);
	}
	
	
}
