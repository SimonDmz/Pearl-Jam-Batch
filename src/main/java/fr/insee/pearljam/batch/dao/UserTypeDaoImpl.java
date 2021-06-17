package fr.insee.pearljam.batch.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.context.UserType;

@Service
public class UserTypeDaoImpl implements UserTypeDao{
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Value("${fr.insee.pearljam.defaultSchema}")
	String schema;
	
	@Override
	public boolean existUser(String id) {
		String qString = "SELECT COUNT(id) FROM "+schema+".user WHERE id=?";
		Long nbRes = jdbcTemplate.queryForObject(qString, new Object[]{id}, Long.class);
		return nbRes>0;	
	}
	
	@Override
	public void createUser(UserType user) {
		String qString = "INSERT INTO "+schema+".user (id, first_name, last_name)VALUES (?, ?, ?)";
		jdbcTemplate.update(qString, user.getId(), user.getFirstName(), user.getLastName());
	}

	@Override
	public void updateOrganizationalUnitByUserId(String userId, String id) {
		String updateQuery = "UPDATE "+schema+".user SET organization_unit_id = ? where id = ?";
		jdbcTemplate.update(updateQuery, id, userId);
	}

	@Override
	public boolean userAlreadyAssociated(List<String> userId, String organizationUnitId) {
		String qString =String.format("SELECT COUNT(id) FROM %s.user WHERE id IN (?) AND organization_unit_id IS NOT NULL AND organization_unit_id<>?", schema);
		Long nbRes = jdbcTemplate.queryForObject(qString, new Object[]{String.join(",", userId), organizationUnitId}, Long.class);
		return nbRes>0;	
	}

	@Override
	public boolean userAlreadyAssociatedToOrganizationUnitId(String userId, String organizationalUnitId) {
		String qString =String.format("SELECT COUNT(id) FROM %s.user WHERE id=? AND organization_unit_id IS NOT NULL AND organization_unit_id=?", schema);
		Long nbRes = jdbcTemplate.queryForObject(qString, new Object[]{userId, organizationalUnitId}, Long.class);
		return nbRes>0;	
	}

	@Override
	public List<String> findAllUsersByOrganizationUnit(String organizationUnitId) {
		String qString = new StringBuilder("SELECT u.id ")
			.append("FROM "+schema+".user u ")
			.append("WHERE u.organization_unit_id=? ")
			.append("OR ((SELECT ou.organization_unit_parent_id FROM organization_unit ou WHERE ou.id=?) IS NOT NULL ")
			.append("AND ")
			.append("u.organization_unit_id=(SELECT ou.organization_unit_parent_id FROM organization_unit ou WHERE ou.id=?))")
			.toString();
		return jdbcTemplate.queryForList(qString, new Object[]{organizationUnitId,organizationUnitId,organizationUnitId}, String.class);
	}

	@Override
	public List<String> findAllUsersWithoutOrganizationUnit() {
		String qString = "SELECT id FROM "+schema+".user WHERE organization_unit_id IS NULL";
		return jdbcTemplate.queryForList(qString, String.class);
	}
}
