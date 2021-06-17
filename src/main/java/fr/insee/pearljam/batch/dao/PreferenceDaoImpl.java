package fr.insee.pearljam.batch.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for the Preference entity that implements the interface associated
 * @author scorcaud
 *
 */
@Service
public class PreferenceDaoImpl implements PreferenceDao {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public void deletePreferenceByCampaignId(String campaignId) {
		String qString = "DELETE FROM preference WHERE id_campaign=?";
		jdbcTemplate.update(qString, campaignId);
	}

	@Override
	public void createPreference(String userId, String campaignId) {
		String qString = "INSERT INTO preference (id_user, id_campaign) VALUES (?,?)";
		jdbcTemplate.update(qString, userId, campaignId);
	}
}
