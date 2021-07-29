package fr.insee.pearljam.batch.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.exception.DataBaseException;

@Service
public class PilotageDBService {
	@Autowired
	@Qualifier("pilotageConnection")
	Connection pilotageConnection;
	
	List<String> missingTable = new ArrayList<>();
	List<String> lstTable = List.of("interviewer", "sample_identifier", "contact_attempt", "geographical_location",
			"campaign", "comment", "state", "visibility", "user", "survey_unit", "contact_outcome",
			 "address", "organization_unit", "preference");

	/**
	 * Check database connection and check if all table exist in database 
	 * @throws DataBaseException
	 * @throws SQLException
	 */
	public void checkDatabaseAccess() throws DataBaseException, SQLException {
		ResultSet rs = null;
		try {
			DatabaseMetaData metaData = pilotageConnection.getMetaData();
			for (String tableName : lstTable) {
				rs = metaData.getTables(null, null, tableName, null);
				if (!rs.next())
					missingTable.add(tableName);
			}
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			throw new DataBaseException("Error during connection to database");
		}
		if (!missingTable.isEmpty()) {
			throw new DataBaseException(String.format("Missing tables in database : [%s]", String.join(",", missingTable)));
		}
	}
	
	
	public void closeConnection() throws SQLException {
		if (pilotageConnection!=null) pilotageConnection.close();
	}
}
