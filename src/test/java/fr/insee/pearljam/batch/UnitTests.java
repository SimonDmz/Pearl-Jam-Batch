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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.insee.pearljam.batch.config.ApplicationContext;
import fr.insee.pearljam.batch.enums.BatchOption;
import fr.insee.pearljam.batch.exception.ArgumentException;
import fr.insee.pearljam.batch.exception.ValidateException;
import fr.insee.pearljam.batch.service.PilotageLauncherService;
import fr.insee.pearljam.batch.utils.BatchErrorCode;
import fr.insee.pearljam.batch.utils.PathUtils;
import fr.insee.pearljam.batch.utils.XmlUtils;
import fr.insee.queen.batch.service.DatasetService;



public class UnitTests {

	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationContext.class);
	
	/* Instantiate the Launcher class via Lanceur.class */
	public Lanceur launcher = new Lanceur();
	
	/* Create a temporary service for the tests*/	
    private PilotageLauncherService pilotageLauncherService = context.getBean(PilotageLauncherService.class);
	
	DatasetService datasetService = context.getBean(DatasetService.class);

	private static final String PROCESSING = "src/test/resources/in/sampleprocessing/testScenarios/processing";

	/**
	 * This method is executed before each test in this class.
	 * It setup the environment by inserting the data and copying the necessaries files.
	 * @throws Exception 
	 */
	@Before
	public void setUp() throws Exception {
		PearlJamBatchApplicationTests.initData();
		PearlJamBatchApplicationTests.copyFiles("sampleprocessing");
		
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
		assertEquals(true, PathUtils.isDirContainsFileExtension(Path.of("src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario5"), "sampleProcessing.xml"));
	}
	
	@Test
	public void fileShouldExist() throws IOException {
		assertEquals(true, PathUtils.isFileExist("src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario5/sampleProcessing.xml"));
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
			XmlUtils.validateXMLSchema(Constants.MODEL_SAMPLEPROCESSING, "src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario5/sampleProcessing.xml");
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
			XmlUtils.validateXMLSchema(Constants.MODEL_SAMPLEPROCESSING, "src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario1/sampleProcessing.xml");
		} catch (Exception e) {
			error = true;
		}
		assertEquals(true, error);
	}
	
	/* Load */
	
	@Test
	public void loadSampleProcessingWithoutError() throws Exception {
			datasetService.createDataSet();
			assertEquals(BatchErrorCode.OK, pilotageLauncherService.load(BatchOption.SAMPLEPROCESSING, "src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario5/sampleProcessing.xml", "src/test/resources/out/sampleprocessing/testScenarios", PROCESSING));
	}
	
	@Test
	public void loadSampleProcessingWithError() throws Exception {

		datasetService.createDataSet();
		File deleteOutFile = new File("src/test/resources/out/sampleprocessing/testScenarios");
		FileUtils.cleanDirectory(deleteOutFile);

		try {
			pilotageLauncherService.load(BatchOption.SAMPLEPROCESSING,
			"src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario4/sampleProcessing.xml",
			"src/test/resources/out/sampleprocessing/testScenarios/", PROCESSING);
		} catch(ValidateException ve) {
			assertEquals(true, PathUtils.isDirContainsErrorFile(
			Path.of("src/test/resources/out/sampleprocessing/testScenarios"), "sampleProcessing", ".error.xml"));
		}
	}
	
	/* Clean and reset */
	
	/**
	 * This method tests the clean and reset step for the nomenclature part
	 * when there is no errors during the batch execution.
	 * @throws Exception
	 */
	@Test
	public void cleandAndResetCampaignWithoutError() throws Exception {
		datasetService.createDataSet();
		File deleteOutFile = new File("src/test/resources/out/sampleprocessing/testScenarios");
		FileUtils.cleanDirectory(deleteOutFile);
		pilotageLauncherService.cleanAndReset("sampleProcessing", "src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario5/sampleProcessing.xml", "src/test/resources/out/sampleprocessing/testScenarios", PROCESSING, BatchErrorCode.OK, BatchOption.SAMPLEPROCESSING);
		assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of("src/test/resources/out/sampleprocessing/testScenarios"),"sampleProcessing", ".done.xml"));
	}
	
	/**
	 * This method tests the clean and reset step for the nomenclature part.
	 * when there is errors during the batch execution.
	 * @throws Exception
	 */
	@Test
	public void cleandAndResetCampaignWithError() throws Exception {
		datasetService.createDataSet();
		File deleteOutFile = new File("src/test/resources/out/sampleprocessing/testScenarios");
		FileUtils.cleanDirectory(deleteOutFile);
		pilotageLauncherService.cleanAndReset("sampleProcessing", "src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario4/sampleProcessing.xml", "src/test/resources/out/sampleprocessing/testScenarios", PROCESSING, BatchErrorCode.KO_FONCTIONAL_ERROR, BatchOption.SAMPLEPROCESSING);
		assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of("src/test/resources/out/sampleprocessing/testScenarios"), "sampleProcessing",".error.xml"));
	}

	@After
	public void cleanOutFolder() {
		purgeDirectory(new File("src/test/resources/out/sampleprocessing/testScenarios"));
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
		File deleteUnitTestsOutDir = new File("src/test/resources/out/sampleprocessing/testScenarios");
		FileUtils.deleteDirectory(deleteUnitTestsOutDir);
	}
}
