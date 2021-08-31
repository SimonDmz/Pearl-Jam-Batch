package fr.insee.pearljam.batch;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.insee.pearljam.batch.config.ApplicationContext;
import fr.insee.pearljam.batch.dto.InterviewerDto;
import fr.insee.pearljam.batch.dto.KeycloakResponseDto;
import fr.insee.pearljam.batch.dto.SimpleIdDto;
import fr.insee.pearljam.batch.enums.BatchOption;
import fr.insee.pearljam.batch.exception.ArgumentException;
import fr.insee.pearljam.batch.service.PilotageLauncherService;
import fr.insee.pearljam.batch.utils.BatchErrorCode;
import fr.insee.pearljam.batch.utils.PathUtils;
import fr.insee.pearljam.batch.utils.XmlUtils;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;      
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;


public class UnitTests {

	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationContext.class);
	
	/* Instantiate the Launcher class via Lanceur.class */
	public Lanceur launcher = new Lanceur();
	
    private RestTemplate restTemplate = context.getBean(RestTemplate.class);
	
	/* Create a temporary service for the tests*/	
    private PilotageLauncherService pilotageLauncherService = context.getBean(PilotageLauncherService.class);
	
	private String keycloakTokenUrl = (String) context.getBean("keycloakAuthUrl");
	private String contextReferentialBaseUrl = (String) context.getBean("contextReferentialBaseUrl");
	
	private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();
    
	private static final String PROCESSING = "src/test/resources/in/campaign/testScenarios/processing";

	/**
	 * This method is executed before each test in this class.
	 * It setup the environment by inserting the data and copying the necessaries files.
	 * @throws Exception 
	 */
	@Before
	public void setUp() throws Exception {
		PearlJamBatchApplicationTests.initData();
		PearlJamBatchApplicationTests.copyFiles("campaign");
		
		MockitoAnnotations.initMocks(this);
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}
		
	/* Tests for PathUtils.java */
	
	@Test
	public void directoryShouldExist() throws IOException {
		assertEquals(true, PathUtils.isDirectoryExist("src/test/resources/in"));
	}
	
	@Test
	public void directoryShouldntExist() throws IOException {
		assertEquals(false, PathUtils.isDirectoryExist("src/test/resources/test"));
	}
	
	@Test
	public void directoryShouldContainsExtension() throws IOException {
		assertEquals(true, PathUtils.isDirContainsFileExtension(Path.of("src/test/resources/in/campaign/testScenarios/"), "campaign.xml"));
	}
	
	@Test
	public void fileShouldExist() throws IOException {
		assertEquals(true, PathUtils.isFileExist("src/test/resources/in/campaign/testScenarios/campaign.xml"));
	}
	
	/* Run Batch */
	
	@SuppressWarnings("static-access")
	@Test(expected = ArgumentException.class)
	public void noOptionDefine() throws Exception {
		String[] options= {}; 
		assertEquals(BatchErrorCode.KO_TECHNICAL_ERROR, launcher.runBatch(options));
	}
 			
	/* Validation */
	
	/**
	 * This method tests the validation part of the file campaign.xml.
	 * @throws Exception
	 */
	@Test
	public void shouldValidateCampaignWithoutError() throws Exception {
		boolean error = false;
		try {
			XmlUtils.validateXMLSchema(Constants.class.getResource("/xsd/campaign.xsd"), "src/test/resources/in/campaign/testScenarios/campaign.xml");
		} catch (Exception e) {
			error = true;
		}
		assertEquals(false, error);
	}
	
	/**
	 * This method tests the validation part of the file campaignWithErrors.xml.
	 * @throws Exception
	 */
	@Test
	public void shouldValidateCampaignWithError() throws Exception {
		boolean error = false;
		try {
			XmlUtils.validateXMLSchema(Constants.MODEL_CAMPAIGN, "src/test/resources/in/campaign/testScenarios/campaignScenario1/campaign.xml");
		} catch (Exception e) {
			error = true;
		}
		assertEquals(true, error);
	}
	
	/* Load */
	
	@Test
	public void loadCampaignWithoutError() throws Exception {
		SimpleIdDto idDto = new SimpleIdDto(); 
		idDto.setId("BLA");
		
		InterviewerDto intDto = new InterviewerDto();
		intDto.setIdep("INTW1");
		
		KeycloakResponseDto resp = new KeycloakResponseDto();
		resp.setAccess_token("token");
		
		
		expectExternalCall(keycloakTokenUrl, resp);
		expectExternalCall(contextReferentialBaseUrl + "/sabiane/organization-units/survey-unit/simpsons2022_1", idDto);
		expectExternalCall(keycloakTokenUrl, resp);
		expectExternalCall(contextReferentialBaseUrl + "/sabiane/organization-units/survey-unit/simpsons2022_2", idDto);

		assertEquals(BatchErrorCode.OK, pilotageLauncherService.load(BatchOption.LOADCAMPAIGN, "src/test/resources/in/campaign/testScenarios/campaign.xml", "src/test/resources/out/unitTests", PROCESSING));
	}
	
	@Test
	public void loadCampaignWithError() throws Exception {
		File deleteOutFile = new File("src/test/resources/out/unitTests");
		FileUtils.cleanDirectory(deleteOutFile);
		
		KeycloakResponseDto resp = new KeycloakResponseDto();
		resp.setAccess_token("token");
		SimpleIdDto idDto = new SimpleIdDto(); 
		idDto.setId("BLA");
		
		expectExternalCall(keycloakTokenUrl, resp);
		expectExternalCall(contextReferentialBaseUrl + "/sabiane/organization-units/survey-unit/su1234", idDto);

        
		pilotageLauncherService.load(BatchOption.LOADCAMPAIGN, "src/test/resources/in/campaign/testScenarios/campaignScenario9/campaign.xml", "src/test/resources/out/unitTests", PROCESSING);
		assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of("src/test/resources/out/unitTests"), "campaign","error.list"));
	}
	
	/* Clean and reset */
	
	/**
	 * This method tests the clean and reset step for the nomenclature part
	 * when there is no errors during the batch execution.
	 * @throws Exception
	 */
	@Test
	public void cleandAndResetCampaignWithoutError() throws Exception {
		File deleteOutFile = new File("src/test/resources/out/unitTests");
		FileUtils.cleanDirectory(deleteOutFile);
		pilotageLauncherService.cleanAndReset("campaign", "src/test/resources/in/campaign/testScenarios/campaignScenario5/campaign.xml", "src/test/resources/out/unitTests/", PROCESSING, BatchErrorCode.OK, BatchOption.LOADCAMPAIGN);
		assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of("src/test/resources/out/unitTests"),"campaign", ".done.xml"));
	}
	
	/**
	 * This method tests the clean and reset step for the nomenclature part.
	 * when there is errors during the batch execution.
	 * @throws Exception
	 */
	@Test
	public void cleandAndResetCampaignWithError() throws Exception {
		File deleteOutFile = new File("src/test/resources/out/unitTests");
		FileUtils.cleanDirectory(deleteOutFile);
		pilotageLauncherService.cleanAndReset("campaign", "src/test/resources/in/campaign/testScenarios/campaignScenario5/campaign.xml", "src/test/resources/out/unitTests/", PROCESSING, BatchErrorCode.KO_FONCTIONAL_ERROR, BatchOption.LOADCAMPAIGN);
		assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of("src/test/resources/out/unitTests"),"campaign", ".error.xml"));
	}
	
	private void expectExternalCall(String url, Object resp) throws JsonProcessingException {
		mockServer.expect(ExpectedCount.once(), 
		          requestTo(url))
		          .andRespond(withStatus(HttpStatus.OK)
		          .contentType(MediaType.APPLICATION_JSON)
		          .body(mapper.writeValueAsString(resp))
		        ); 
	}
	
	@After
	public void cleanOutFolder() {
		purgeDirectory(new File("src/test/resources/out/unitTests"));
		purgeDirectory(new File(PROCESSING));
	}
	
	void purgeDirectory(File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isFile())
	            file.delete();
	    }
	}
	
	@AfterClass
	public static void deleteFiles() throws IOException {
		File deleteUnitTestsOutDir = new File("src/test/resources/out/unitTests");
		FileUtils.deleteDirectory(deleteUnitTestsOutDir);
	}
}
