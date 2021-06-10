package fr.insee.pearljam.batch.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableTransactionManagement
@ComponentScan("fr.insee.pearljam.batch.*")
@PropertySource(value = {"classpath:/pearljam-bo.properties", "file:${properties.path}/pearljam-bo.properties"}, ignoreResourceNotFound = true)
public class ApplicationContext {
	
	/**
	 * These values are read in the property file
	 */
	@Value("${fr.insee.pearljam.persistence.database.host}")
	String dbHost;

	@Value("${fr.insee.pearljam.persistence.database.port}")
	String dbPort;

	@Value("${fr.insee.pearljam.persistence.database.schema}")
	String dbSchema;

	@Value("${fr.insee.pearljam.persistence.database.user}")
	private String dbUser;

	@Value("${fr.insee.pearljam.persistence.database.password}")
	private String dbPassword;

	@Value("${fr.insee.pearljam.persistence.database.driver}")
	private String dbDriver;
	
	@Value("${fr.insee.pearljam.folder.in}")
	private String FOLDER_IN;
	
	@Value("${fr.insee.pearljam.folder.out}")
	private String FOLDER_OUT;
	
	@Value("${fr.insee.pearljam.folder.processing}")
	private String FOLDER_PROCESSING;
	
	@Value("${fr.insee.pearljam.context.referential.service.url.scheme:#{null}}")
	private String contextReferentialScheme;
	
	@Value("${fr.insee.pearljam.context.referential.service.url.host:#{null}}")
	private String contextReferentialHost;
	
	@Value("${fr.insee.pearljam.context.referential.service.url.port:#{null}}")
	private String contextReferentialPort;
	
	@Value("${fr.insee.pearljam.context.referential.service.url.path:#{null}}")
	private String contextReferentialPath;
	
	@Value("${keycloak.auth-server-url:#{null}}")
	private String authServerURL;
	
	@Value("${keycloak.realm:#{null}}")
	private String realm;
	
	private String filename = "";

	
	
	/***
	 * This method create a new Datasource object
	 * @return new Datasource
	 */
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(dbDriver);
		dataSource.setUrl(String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbSchema));
		dataSource.setUsername(dbUser);
		dataSource.setPassword(dbPassword);
		return dataSource;
	}
	
	@Bean
	public PlatformTransactionManager txManager() {
	    return new DataSourceTransactionManager(dataSource()); // (2)
	}
	
	/***
	 * This method return datasource connection
	 * @param dataSource
	 * @return Connection
	 * @throws SQLException 
	 */
	@Bean
	public Connection connection(DataSource dataSource) throws SQLException {
		return dataSource.getConnection();
	}

	/***
	 * Create a new JdbcTemplate with a datasource passed in parameter
	 * @param dataSource
	 * @return JdbcTemplate
	 */
	@Bean
	public JdbcTemplate jdbcTemplate(Connection connection) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
		jdbcTemplate.setResultsMapCaseInsensitive(true);
		return jdbcTemplate;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public String getFolderIn() {
		return FOLDER_IN;
	}
	
	@Bean
	public String getFolderOut() {
		return FOLDER_OUT;
	}
	
	@Bean
	public String getFolderProcessing() {
		return FOLDER_PROCESSING;
	}
	
	/**
	 * Bean to get the filename
	 * @return
	 */
	@Bean(name = "filename")
	public String getFilename() {
		return filename;
	}
	
	/***
	 * This method create a new Datasource object
	 * @return new Datasource
	 */
	@Bean(name = "contextReferentialBaseUrl")
	public String getContextReferentialBaseUrl() {
		return contextReferentialScheme + "://" + contextReferentialHost + ":" + contextReferentialPort 
				+ contextReferentialPath;
	}
	
	@Bean(name = "keycloakAuthUrl")
	public String getKeycloakAuthUrl() {
		return authServerURL + "/realms/" + realm + "/protocol/openid-connect/token";
	}
  
  


}
