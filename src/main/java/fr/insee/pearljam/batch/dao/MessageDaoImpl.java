package fr.insee.pearljam.batch.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageDaoImpl implements MessageDao{
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public void deleteByCampaign(String campaignId) {
		String qString = "DELETE FROM campaign_message_recipient WHERE campaign_id=?";
		jdbcTemplate.update(qString, campaignId);
	}
	
	@Override
	public void deleteById(Long id) {
		String qString = "DELETE FROM message WHERE id=?";
		jdbcTemplate.update(qString, id);
	}
	
	@Override
	public void deleteCampaignMessageById(Long id) {
		String qString = "DELETE FROM campaign_message_recipient WHERE message_id=?";
		jdbcTemplate.update(qString, id);
	}
	
	
	@Override
	public void deleteOuMessageById(Long id) {
		String qString = "DELETE FROM oumessage_recipient WHERE message_id=?";
		jdbcTemplate.update(qString, id);
	}
	
	@Override
	public void deleteStatusMessageById(Long id) {
		String qString = "DELETE FROM message_status WHERE message_id=?";
		jdbcTemplate.update(qString, id);
	}
	
	@Override
	public List<Long> getIdsToDelete(Long passedDate){
		String qString = "SELECT mes.id FROM message mes WHERE mes.date < ?";
		return jdbcTemplate.queryForList(qString, new Object[]{passedDate}, Long.class);
	}
	
	@Override
	public boolean isIdPresentForCampaign(Long id) {
		String qString = "SELECT COUNT(*) FROM campaign_message_recipient WHERE message_id=?";
		Long nbRes = jdbcTemplate.queryForObject(qString, new Object[]{id}, Long.class);
		return nbRes>0;	
	}
	
	@Override
	public boolean isIdPresentForCampaignId(String id) {
		String qString = "SELECT COUNT(*) FROM campaign_message_recipient WHERE campaign_id=?";
		Long nbRes = jdbcTemplate.queryForObject(qString, new Object[]{id}, Long.class);
		return nbRes>0;	
	}
	
	@Override
	public boolean isIdPresentForOu(Long id) {
		String qString = "SELECT COUNT(*) FROM oumessage_recipient WHERE message_id=?";
		Long nbRes = jdbcTemplate.queryForObject(qString, new Object[]{id}, Long.class);
		return nbRes>0;	
	}
	
	@Override
	public boolean isIdPresentForIntw(Long id) {
		String qString = "SELECT COUNT(*) FROM interviewer_message_recipient WHERE message_id=?";
		Long nbRes = jdbcTemplate.queryForObject(qString, new Object[]{id}, Long.class);
		return nbRes>0;	
	}
	
	@Override
	public boolean isIdPresentInStatus(Long id) {
		String qString = "SELECT COUNT(*) FROM message_status WHERE message_id=?";
		Long nbRes = jdbcTemplate.queryForObject(qString, new Object[]{id}, Long.class);
		return nbRes>0;	
	}
}
