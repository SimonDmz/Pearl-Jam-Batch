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

public class TestsEndToEndContext {
	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationContext.class);
	PilotageLauncherService pilotageLauncherService = context.getBean(PilotageLauncherService.class);
	
	private static final String OUT = "src/test/resources/out/context/testScenarios";

	@Before
	public void setUp() throws Exception {
		PearlJamBatchApplicationTests.initData();
		PearlJamBatchApplicationTests.copyFiles("context");
	}
	
	public static UnitTests unitTests = new UnitTests();

	/**
	 * This method represent a scenario when the validate context part has errors A
	 * ValidateException should be thrown if the step of "context validation" gets
	 * an error.
	 * 
	 * @throws ValidateException
	 */
	@Test
	public void testScenario1() throws Exception {
		String in = "src/test/resources/in/context/testScenarios/contextScenario1";
		try {
			pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT, in, OUT);
		} catch(ValidateException ve) {
			assertEquals(true, ve.getMessage().contains("Error validating context.xml : "));
			assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of("src/test/resources/out/context/testScenarios"), "context","error.xml"));
		}
	}


	/**
	 * Scenario 2 : XML OK , at least 1 user already exists
	 */
	@Test
	public void testScenario2() throws Exception {
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING,
				pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT,
						"src/test/resources/in/context/testScenarios/contextScenario2",
						OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 3 : XML OK , XML contains only users
	 */
	@Test
	public void testScenario3() throws Exception {
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING,
				pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT,
						"src/test/resources/in/context/testScenarios/contextScenario3",
						OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 4 : XML OK , at least 1 interviewer already exists
	 */
	@Test
	public void testScenario4() throws Exception {
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING,
				pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT,
						"src/test/resources/in/context/testScenarios/contextScenario4",
						OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 5 : XML OK , XML contains only interviewers
	 */
	@Test
	public void testScenario5() throws Exception {
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING,
				pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT,
						"src/test/resources/in/context/testScenarios/contextScenario5",
						OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 6 : XML OK , at least 1 geographical location already exists
	 */
	@Test
	public void testScenario6() throws Exception {
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING,
				pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT,
						"src/test/resources/in/context/testScenarios/contextScenario6",
						OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 7 : XML OK , XML contains only geographical location
	 */
	@Test
	public void testScenario7() throws Exception {
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING,
				pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT,
						"src/test/resources/in/context/testScenarios/contextScenario7",
						OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 8 : XML OK , Organization unit already exists
	 */	
	@Test
	public void testScenario8() throws Exception {
		String in = "src/test/resources/in/context/testScenarios/contextScenario8";
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING, pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT, in, OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 9 : XML OK , XML contains only Organization unit
	 */
	@Test
	public void testScenario9() throws Exception {
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING,
				pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT,
						"src/test/resources/in/context/testScenarios/contextScenario9",
						OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 10 : XML OK , at least 1 UserRef not exists in DB
	 */
	@Test
	public void testScenario10() throws Exception {
		String in = "src/test/resources/in/context/testScenarios/contextScenario10";
		try {
			pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT, in, OUT);
		} catch(ValidateException ve) {
			assertEquals(true, ve.getMessage().contains("Error during creating Context : the user"));
			assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of("src/test/resources/out/context/testScenarios"), "context","error.xml"));
		}
	}
	
	/**
	 * Scenario 11 : XML OK , at least 1 UserRef null
	 */
	@Test
	public void testScenario11() throws Exception {
		String in = "src/test/resources/in/context/testScenarios/contextScenario11";
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING, pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT, in, OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 12 : XML OK , at least 1 UserRef already associated in DB
	 */
	@Test
	public void testScenario12() throws Exception {
		String in = "src/test/resources/in/context/testScenarios/contextScenario12";
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING, pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT, in, OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 13 : XML OK , at least 1 InterviewerRef not exists in DB
	 */
	@Test
	public void testScenario13() throws Exception {
		String in = "src/test/resources/in/context/testScenarios/contextScenario13";
		try {
			pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT, in, OUT);
		} catch(ValidateException ve) {
			assertEquals(true, ve.getMessage().contains("Error during creating Context : The interviewer"));
			assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of("src/test/resources/out/context/testScenarios"), "context","error.xml"));
		}
	}
	
	/**
	 * Scenario 16 : XML OK , at least 1 OrganizationUnitRef not exists in DB
	 */
	@Test
	public void testScenario16() throws Exception {
		String in = "src/test/resources/in/context/testScenarios/contextScenario16";
		try {
			pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT, in, OUT);
		} catch(ValidateException ve) {
			assertEquals(true, ve.getMessage().contains("Error during creating Context : The organization unit"));
			assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of("src/test/resources/out/context/testScenarios"), "context","error.xml"));
		}
	}
	
	/**
	 * Scenario 17 : XML OK , at least 1 OrganizationUnitRef null
	 */
	@Test
	public void testScenario17() throws Exception {
		String in = "src/test/resources/in/context/testScenarios/contextScenario17";
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING, pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT, in, OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}
	
	
	/**
	 * Scenario 18 : XML OK , at least 1 OrganizationUnitRef already associated in
	 * DB
	 */
	@Test
	public void testScenario18() throws Exception {
		String in = "src/test/resources/in/context/testScenarios/contextScenario18";
		assertEquals(BatchErrorCode.OK_FONCTIONAL_WARNING, pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT, in, OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "warning.xml"));
	}

	/**
	 * Scenario 19 : XML OK, Organisation Unit OK, Geographical locations OK, Users
	 * OK and interviewers OK
	 */
	@Test
	public void testScenario19() throws Exception {
		assertEquals(BatchErrorCode.OK,
				pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT,
						"src/test/resources/in/context/testScenarios/contextScenario19",
						OUT));
		assertEquals(true, PathUtils.isDirContainsErrorFile(
				Path.of("src/test/resources/out/context/testScenarios"), "context", "done.xml"));
	}
	
	/**
	 * Scenario 20 : XML with wrong encoding
	 */
	@Test
	public void testScenario20() throws Exception {
		String in = "src/test/resources/in/context/testScenarios/contextScenario20";
		try {
			pilotageLauncherService.validateLoadClean(BatchOption.LOADCONTEXT, in, OUT);
		} catch(ValidateException ve) {
			assertEquals(true, ve.getMessage().contains("Error validating context.xml : "));
			assertEquals(true, PathUtils.isDirContainsErrorFile(Path.of("src/test/resources/out/context/testScenarios"), "context","error.xml"));
		}
	}
	
	@After
	public void cleanOutFolder() {
		purgeDirectory(new File("src/test/resources/out/context/testScenarios"));
	}
	
	void purgeDirectory(File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isFile())
	            file.delete();
	    }
	}
	
	@AfterClass
	public static void deleteFiles() throws IOException {
		File deleteFolderInContextForTest = new File("src/test/resources/in/context/testScenarios");
		FileSystemUtils.deleteRecursively(deleteFolderInContextForTest);
	}
}
