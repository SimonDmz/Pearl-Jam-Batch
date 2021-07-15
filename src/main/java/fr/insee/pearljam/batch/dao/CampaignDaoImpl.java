package fr.insee.pearljam.batch.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.campaign.Campaign;

/**
 * Service for the Campaign entity that implements the interface associated
 * @author scorcaud
 *
 */
@Service
public class CampaignDaoImpl implements CampaignDao {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
		
	@Override
	public boolean existCampaign(String id){
		String qString = "SELECT COUNT(id) FROM campaign WHERE id=?";
		Long nbRes = jdbcTemplate.queryForObject(qString, new Object[]{id}, Long.class);
		return nbRes>0;	
	}
	
	/**
	 * Implements the creation of a Campaign in database
	 * @param nomenclature
	 * @throws ParseException 
	 * @throws SQLException
	 */
	@Override
	public void createCampaign(Campaign campaign) {
		String qString = "INSERT INTO campaign (id, label) VALUES (?, ?)";
		
		jdbcTemplate.update(qString, campaign.getId().toUpperCase(), campaign.getLabel());
    }
	
	@Override
	public void deleteCampaign(Campaign campaign) {
		String qString ="DELETE FROM campaign WHERE id=?";
		jdbcTemplate.update(qString, campaign.getId());
	}
	
	public void updateCampaignById(Campaign campaign) {
		String qString ="UPDATE campaign SET label=? WHERE id=?";

		jdbcTemplate.update(qString, campaign.getLabel(), campaign.getId().toUpperCase());
	}

	@Override
	public List<Campaign> findAll() {
		String qString = "SELECT * FROM campaign";
		return jdbcTemplate.query(qString, new CampaignTypeMapper());
	}
	
	/**
	 * Implements the mapping between the result of the query and the Campaign entity
	 * @return CommentTypeMapper
	 */
	private static final class CampaignTypeMapper implements RowMapper<Campaign> {
        public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException         {
        	Campaign c = new Campaign();
            c.setId(rs.getString("id"));
            c.setLabel(rs.getString("label"));
            c.setOrganizationalUnits(null);
            c.setSurveyUnits(null);
            return c;
        }
    }
	
}