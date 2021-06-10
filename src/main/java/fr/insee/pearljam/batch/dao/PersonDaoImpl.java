package fr.insee.pearljam.batch.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.Constants;
import fr.insee.pearljam.batch.campaign.PersonType;

/**
 * Service for the Person entity that implements the interface associated
 * @author pguillemet
 *
 */
@Service
public class PersonDaoImpl implements PersonDao{
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LogManager.getLogger(PersonDaoImpl.class);
	

	@Override
	public Long createPerson(PersonType person, String surveyUnitId) {
		String qString = new StringBuilder("INSERT INTO person (birthdate, email, favorite_email, first_name, last_name, title, survey_unit_id, privileged) ")
				.append("VALUES (?, ?, false, ?, ?, ?, ?, ?)")
				.toString();
		Long parsedDate = null;
		Integer parsedTitle = null;
		try{
			parsedDate = new SimpleDateFormat(Constants.DATE_FORMAT_2).parse(person.getDateOfBirth()).getTime();
		} catch (ParseException e) {
			logger.log(Level.ERROR, e.getMessage());
		}
		String lowercaseTitle = person.getTitle().toLowerCase();
		if(lowercaseTitle.contains("miss")) {
			parsedTitle = 1;
		}
		else if(lowercaseTitle.equals("mister")) {
			parsedTitle = 0;
		}
		else {
			logger.log(Level.ERROR,"Could not parse title of person '{} {}'", person.getFirstName(), person.getLastName());
		}
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final Long tempDate = parsedDate;
		final Integer tempTitle = parsedTitle;
		jdbcTemplate.update(
		    new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps = connection.prepareStatement(qString, Statement.RETURN_GENERATED_KEYS);
		            ps.setLong(1, tempDate);
		            ps.setString(2, person.getEmail());
		            ps.setString(3, person.getFirstName());
		            ps.setString(4, person.getLastName());
		            ps.setLong(5, tempTitle);
		            ps.setString(6, surveyUnitId);
		            ps.setBoolean(7, person.isPrivileged());
		            return ps;
		        }
		    },
		    keyHolder);
		return (Long) keyHolder.getKeyList().get(0).get("id");
    }
	
	@Override
	public void deletePersonBySurveyUnitId(String surveyUnitId) {
		String qString = "DELETE FROM person WHERE survey_unit_id=?";
		jdbcTemplate.update(qString, surveyUnitId);
	}
	

	private static final class PersonTypeTypeMapper implements RowMapper<Entry<Long,PersonType>> {
        public Entry<Long,PersonType> mapRow(ResultSet rs, int rowNum) throws SQLException         {
        	PersonType person = new PersonType();
        	Integer title = rs.getInt("title");
        	if(!rs.wasNull()) {
                person.setTitle(title == 0 ? "Miss" : "Mister");
        	}
            person.setFirstName(rs.getString("first_name"));
            person.setLastName(rs.getString("last_name"));
            person.setEmail(rs.getString("email"));
            Long dateTime = rs.getLong("birthdate");
            if(!rs.wasNull()) {
            	DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_2);
                person.setDateOfBirth(df.format(new Date(dateTime)));
        	}
            person.setPrivileged(rs.getBoolean("privileged"));
            
            Long id = rs.getLong("id");
            
            return new AbstractMap.SimpleEntry<>(id, person);
        }
    }
	

	@Override
	public List<Entry<Long, PersonType>> getPersonsBySurveyUnitId(String id) {
		String qString = "SELECT person.* FROM person WHERE survey_unit_id=?";
		return jdbcTemplate.query(qString, new Object[] {id}, new PersonTypeTypeMapper());
	}
	
	
}