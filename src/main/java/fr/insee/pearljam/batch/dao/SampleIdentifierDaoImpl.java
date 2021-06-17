package fr.insee.pearljam.batch.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.campaign.InseeSampleIdentiersType;

/**
 * Interface for the Address entity
 * 
 * @author scorcaud
 *
 */
@Service
public class SampleIdentifierDaoImpl implements SampleIdentifierDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public Long createSampleIdentifier(InseeSampleIdentiersType inseeSampleIdentiers) {

		String qString = "INSERT INTO sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier',?,?,?,?,?,?,?,?,?,?,?) RETURNING id";
		return jdbcTemplate.queryForObject(qString,
				new Object[] { inseeSampleIdentiers.getAutre(), inseeSampleIdentiers.getBs(),
						inseeSampleIdentiers.getEc(), inseeSampleIdentiers.getLe(), inseeSampleIdentiers.getNograp(),
						inseeSampleIdentiers.getNoi(), inseeSampleIdentiers.getNole(), inseeSampleIdentiers.getNolog(),
						inseeSampleIdentiers.getNumfa(), inseeSampleIdentiers.getRges(),
						inseeSampleIdentiers.getSsech() },
				Long.class);
	}

	@Override
	public void updateSampleIdentifier(InseeSampleIdentiersType inseeSampleIdentiers, String surveyUnitId) {
		String qString = new StringBuilder("UPDATE public.sample_identifier as si ")
				.append("SET autre=?, bs=?, ec=?, le=?, nograp=?, noi=?, nole=?, nolog=?, numfa=?, rges=?, ssech=? ")
				.append("FROM survey_unit su " + "WHERE su.sample_identifier_id=si.id AND su.id=?")
				.toString();
		jdbcTemplate.update(qString, inseeSampleIdentiers.getAutre(), inseeSampleIdentiers.getBs(),
				inseeSampleIdentiers.getEc(), inseeSampleIdentiers.getLe(), inseeSampleIdentiers.getNograp(),
				inseeSampleIdentiers.getNoi(), inseeSampleIdentiers.getNole(), inseeSampleIdentiers.getNolog(),
				inseeSampleIdentiers.getNumfa(), inseeSampleIdentiers.getRges(), inseeSampleIdentiers.getSsech(),
				surveyUnitId);
	}

	public InseeSampleIdentiersType getSampleIdentiersBySurveyUnitId(String surveyUnitId) {
		String qString = "SELECT * FROM sample_identifier si INNER JOIN survey_unit su on su.sample_identifier_id = si.id WHERE su.id=?";
		List<InseeSampleIdentiersType> sampleList = jdbcTemplate.query(qString, new Object[] {surveyUnitId}, new SampleIdentiersTypeMapper());
		if(!sampleList.isEmpty()) {
			return sampleList.get(0);
		} else {
			return null;
		}
	}
	
	private static final class SampleIdentiersTypeMapper implements RowMapper<InseeSampleIdentiersType> {
        public InseeSampleIdentiersType mapRow(ResultSet rs, int rowNum) throws SQLException {
        	InseeSampleIdentiersType in = new InseeSampleIdentiersType();
        	in.setAutre(rs.getString("autre"));
        	in.setBs(rs.getInt("bs"));
        	in.setEc(rs.getString("ec"));
        	in.setLe(rs.getInt("le"));
        	in.setNograp(rs.getString("nograp"));
        	in.setNoi(rs.getInt("noi"));
        	in.setNole(rs.getInt("nole"));
        	in.setNolog(rs.getInt("nolog"));
        	in.setNumfa(rs.getInt("numfa"));
        	in.setRges(rs.getInt("rges"));
        	in.setSsech(rs.getInt("ssech"));
            return in;
        }
    }
	
	public void deleteSampleIdentifiersById(Long sampleIdentifiersId) {
		String qString = "DELETE FROM sample_identifier where id=?";
		jdbcTemplate.update(qString, sampleIdentifiersId);
	}
}
