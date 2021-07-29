package fr.insee.pearljam.batch;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.FileSystemUtils;

import fr.insee.pearljam.batch.config.ApplicationContext;
import fr.insee.pearljam.batch.enums.BatchOption;
import fr.insee.pearljam.batch.exception.ValidateException;
import fr.insee.pearljam.batch.service.PilotageLauncherService;
import fr.insee.pearljam.batch.utils.BatchErrorCode;
import fr.insee.pearljam.batch.utils.PathUtils;

public class TestsEndToEndExtractCampaign {
	
	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationContext.class);
	
	PilotageLauncherService pilotageLauncherService = context.getBean(PilotageLauncherService.class);
	
	private static final String OUT = "src/test/resources/out/sampleprocessing/testScenarios";

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
	
	public static UnitTests unitTests = new UnitTests();
	
	/**
	 * Scenario 1 : XML file is not valid
	 * @throws ValidateException
	 */
	@Test
	public void testScenario1() throws Exception {
		String in = "src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario1";
		try {
			pilotageLauncherService.validateLoadClean(BatchOption.SAMPLEPROCESSING, in, OUT);
		} catch(ValidateException ve) {
			assertEquals(true, ve.getMessage().contains("Error validating campaign.to.sampleprocessing.xml : "));
			assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of(OUT), "campaign","sampleprocessing.error.xml"));
		}
	}
	
	
	/**
	 * Scenario 2 : Campaing in XML file not exist in Datacollection DB
	 * @throws Exception
	 */
	@Test
	public void testScenario2() throws Exception {
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING, pilotageLauncherService.validateLoadClean(BatchOption.SAMPLEPROCESSING, "src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario2", OUT));
	}
	
	/**
	 * Scenario 3 : Campaing in XML file not exist in Pilotage DB
	 * @throws Exception
	 */
	@Test
	public void testScenario3() throws Exception {
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING, pilotageLauncherService.validateLoadClean(BatchOption.SAMPLEPROCESSING, "src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario2", OUT));
	}
	
	/**
	 * Scenario 4 : XML ok and campaign exist in both DB
	 * @throws Exception
	 */
	@Test
	public void testScenario4() throws Exception {
		assertEquals(BatchErrorCode.OK, pilotageLauncherService.validateLoadClean(BatchOption.SAMPLEPROCESSING, "src/test/resources/in/sampleprocessing/testScenarios/sampleprocessingScenario4", OUT));
		
	}
	
	
	@After
	public void cleanOutFolder() {
		purgeDirectory(new File(OUT));
	}
	
	void purgeDirectory(File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isFile())
	            file.delete();
	    }
	}
	
	@AfterClass
	public static void deleteFiles() throws IOException {
		File deleteFolderInDeleteForTest = new File("src/test/resources/in/sampleprocessing/testScenarios");
		FileSystemUtils.deleteRecursively(deleteFolderInDeleteForTest);
	}
}
