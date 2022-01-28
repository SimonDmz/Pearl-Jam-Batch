package fr.insee.pearljam.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.FileSystemUtils;

import fr.insee.pearljam.batch.campaign.StateType;
import fr.insee.pearljam.batch.config.ApplicationContext;
import fr.insee.pearljam.batch.dao.MessageDao;
import fr.insee.pearljam.batch.dao.StateDao;
import fr.insee.pearljam.batch.service.TriggerService;

public class TestsEndToEndDailyUpdate {
	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationContext.class);
	TriggerService triggerService = context.getBean(TriggerService.class);
	
	/**
	 * This method is executed before each test in this class.
	 * It setup the environment by inserting the data
	 * @throws Exception 
	 */
	@Before
	public void setUp() throws Exception {
		PearlJamBatchApplicationTests.initData();
	}
	
	//Testing update of states and delete of noifications
	@Test
	public void testScenario1() throws Exception {
		MessageDao messageDao = context.getBean(MessageDao.class);
		StateDao stateDao = context.getBean(StateDao.class); 
		TimeZone utcTimeZone =TimeZone.getTimeZone("UTC");
		Calendar c = Calendar.getInstance(utcTimeZone);
		c.add(Calendar.MONTH, -1);
		assertEquals(5, messageDao.getIdsToDelete(c.getTimeInMillis()).size());

		// set a fixed clock in triggerService to avoid exceeding test data dates and failing tests
		Instant fixedInstant = LocalDateTime.of(2020, 7, 14, 0, 0).atZone(ZoneOffset.UTC).toInstant();
		triggerService.setClock(Clock.fixed(fixedInstant, utcTimeZone.toZoneId()));
		triggerService.updateStates();
		triggerService.initDefaultClock();
		
		assertTrue(messageDao.getIdsToDelete(c.getTimeInMillis()).isEmpty());
		assertEquals(List.of("NVM"), stateDao.getStateBySurveyUnitId("24").stream().map(StateType::getType).collect(Collectors.toList()));
		assertEquals(List.of("NVM","ANV"), stateDao.getStateBySurveyUnitId("25").stream().map(StateType::getType).collect(Collectors.toList()));
		assertEquals(List.of("NVM","ANV","VIN"), stateDao.getStateBySurveyUnitId("26").stream().map(StateType::getType).collect(Collectors.toList()));
		assertEquals(List.of("NVM","ANV","VIN"), stateDao.getStateBySurveyUnitId("27").stream().map(StateType::getType).collect(Collectors.toList()));
		assertEquals(List.of("NVM","ANV","VIN","NVA"), stateDao.getStateBySurveyUnitId("28").stream().map(StateType::getType).collect(Collectors.toList()));
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
