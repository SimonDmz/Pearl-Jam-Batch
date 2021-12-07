package fr.insee.pearljam.batch.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.context.InterviewerType;
import fr.insee.pearljam.batch.dto.InterviewerDto;

/**
 * Service for the Interviewer entity that implements the interface associated
 * @author scorcaud
 *
 */
@Service
public class InterviewerTypeDaoImpl implements InterviewerTypeDao {

	@Autowired
	@Qualifier("pilotageJdbcTemplate")
	JdbcTemplate pilotageJdbcTemplate;
	
	@Override
	public boolean existInterviewer(String id) {
		String qString = "SELECT COUNT(id) FROM interviewer WHERE id=?";
		Long nbRes = pilotageJdbcTemplate.queryForObject(qString, new Object[]{id}, Long.class);
		return nbRes>0;	
	}
	

	@Override
	public void createInterviewer(InterviewerType interviewer) {
		String qString = "INSERT INTO public.interviewer(id, email, first_name, last_name, phone_number) VALUES (?, ?, ?, ?, ?)";
		pilotageJdbcTemplate.update(qString, interviewer.getId(), interviewer.getEmail(), interviewer.getFirstName(), interviewer.getLastName(), interviewer.getPhoneNumber());
	}
	
	@Override
	public void createInterviewerFromDto(InterviewerDto interviewer) {
		String qString = "INSERT INTO public.interviewer(id, email, first_name, last_name, phone_number) VALUES (?, ?, ?, ?, ?)";
		pilotageJdbcTemplate.update(qString, interviewer.getIdep(), interviewer.getMailInsee(), interviewer.getPrenom(), interviewer.getNom(), interviewer.getTelInsee());
	}
	
	@Override
	public void updateInterviewerFromDto(InterviewerDto interviewer) {
		String qString = "UPDATE public.interviewer SET email=?, first_name=?, last_name=?, phone_number=? WHERE id=?";
		pilotageJdbcTemplate.update(qString, interviewer.getMailInsee(), interviewer.getPrenom(), interviewer.getNom(), interviewer.getTelInsee(), interviewer.getIdep());
	}
	
	@Override
	public boolean isDifferentFromDto(InterviewerDto interviewer) {
		String qString = "SELECT count(1) FROM public.interviewer WHERE  id=? AND email=? AND first_name=? AND last_name=? AND phone_number=?";
		Long nbRes = pilotageJdbcTemplate.queryForObject(qString, new Object[]{ interviewer.getIdep(), interviewer.getMailInsee(), interviewer.getPrenom(), interviewer.getNom(), interviewer.getTelInsee()}, Long.class);
		return nbRes<1;	
	}

	@Override
	public void updateOrganizationalUnitByInterviewerId(String interviewerId, String id) {
		String updateQuery = "UPDATE interviewer SET organization_unit_id = ? where id = ?";
		pilotageJdbcTemplate.update(updateQuery, id, interviewerId);
	}

	@Override
	public boolean interviewerAlreadyAssociated(List<String> interviewerId, String organizationUnitId) {
		String qString ="SELECT COUNT(id) FROM interviewer WHERE id IN (?) AND organization_unit_id IS NOT NULL AND organization_unit_id<>?";
		Long nbRes = pilotageJdbcTemplate.queryForObject(qString, new Object[]{String.join(",",interviewerId), organizationUnitId}, Long.class);
		return nbRes>0;	
	}

	@Override
	public boolean interviewerAlreadyAssociatedToOrganizationUnitId(String interviewerId, String organizationUnitId) {
		String qString ="SELECT COUNT(id) FROM interviewer WHERE id=? AND organization_unit_id IS NOT NULL AND organization_unit_id=?";
		Long nbRes = pilotageJdbcTemplate.queryForObject(qString, new Object[]{interviewerId, organizationUnitId}, Long.class);
		return nbRes>0;	
	}

	@Override
	public List<String> findAllInterviewrsWithoutOrganizationUnit() {
		String qString = "SELECT id FROM interviewer WHERE organization_unit_id IS NULL";
		return pilotageJdbcTemplate.queryForList(qString, String.class);
	}
}
