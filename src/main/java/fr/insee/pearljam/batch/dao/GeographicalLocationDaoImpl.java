package fr.insee.pearljam.batch.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.context.GeographicalLocationType;

/**
 * Service for the GeographicalLocation entity that implements the interface associated
 * @author scorcaud
 *
 */
@Service
public class GeographicalLocationDaoImpl implements GeographicalLocationDao{
	@Autowired
	@Qualifier("pilotageJdbcTemplate")
	JdbcTemplate pilotageJdbcTemplate;
	
	@Override
	public boolean existGeographicalLocation(String id) {
		String qString = "SELECT COUNT(id) FROM geographical_location WHERE id=?";
		Long nbRes = pilotageJdbcTemplate.queryForObject(qString, new Object[]{id}, Long.class);
		return nbRes>0;	
	}

	@Override
	public void createGeographicalLocation(GeographicalLocationType geographicalLocation) {
		String qString = "INSERT INTO public.geographical_location(id, label) VALUES (?, ?)";
		pilotageJdbcTemplate.update(qString, geographicalLocation.getId(), geographicalLocation.getLabel());
	}
}
