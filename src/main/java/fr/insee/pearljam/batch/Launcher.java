package fr.insee.pearljam.batch;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.insee.pearljam.batch.config.ApplicationContext;
import fr.insee.pearljam.batch.enums.BatchOption;
import fr.insee.pearljam.batch.exception.ArgumentException;
import fr.insee.pearljam.batch.exception.BatchException;
import fr.insee.pearljam.batch.exception.DataBaseException;
import fr.insee.pearljam.batch.exception.FolderException;
import fr.insee.pearljam.batch.exception.ValidateException;
import fr.insee.pearljam.batch.service.DatabaseService;
import fr.insee.pearljam.batch.service.FolderService;
import fr.insee.pearljam.batch.service.LauncherService;
import fr.insee.pearljam.batch.service.TriggerService;
import fr.insee.pearljam.batch.utils.BatchErrorCode;
import fr.insee.pearljam.batch.utils.PathUtils;

/**
 * Launcher : Pearl Jam Batch main class
 * 
 * @author Claudel Benjamin
 * 
 */
public abstract class Launcher {
	/**
	 * The folder in use to insert datas
	 */
	public static String FOLDER_IN;
	/**
	 * The folder out use to store logs and file treated
	 */
	public static String FOLDER_OUT;
	
	/**
	 * The Application context
	 */
	static AnnotationConfigApplicationContext context;

	static DatabaseService databaseService;
	static FolderService folderService;
	static LauncherService launcherService;
	static TriggerService triggerService;
	
	/**
	 * The class logger
	 */
	private static final Logger logger = LogManager.getLogger(Launcher.class);

	public static void main(String[] args) throws IOException, ValidateException, SQLException, XMLStreamException {
		context = new AnnotationConfigApplicationContext(ApplicationContext.class);
		databaseService = context.getBean(DatabaseService.class);
		folderService = context.getBean(FolderService.class);
		launcherService = context.getBean(LauncherService.class);
		BatchErrorCode batchErrorCode = BatchErrorCode.OK;
		try {
			initBatch();
			checkFolderTree();
			batchErrorCode = runBatch(args);
		} catch (ArgumentException | FolderException | IOException | SQLException | DataBaseException te) {
			logger.log(Level.ERROR, te.getMessage(), te);
			batchErrorCode = BatchErrorCode.KO_TECHNICAL_ERROR;
		} catch (BatchException | XMLStreamException | ValidateException fe) {
			logger.log(Level.ERROR, fe.getMessage(), fe);
			batchErrorCode = BatchErrorCode.KO_FONCTIONAL_ERROR;
		} 
		finally {
			logger.log(Level.INFO, Constants.MSG_RETURN_CODE, batchErrorCode);
			databaseService.closeConnection();
			context.close();
			System.exit(batchErrorCode.getCode());
		}
	}

	/**
	 * Init Batch check all prerequisities before run batch : folder properties and
	 * database structure
	 * 
	 * @throws FolderException
	 * @throws SQLException
	 * @throws DataBaseException
	 */
	public static void initBatch() throws FolderException, DataBaseException, SQLException {
		// Check folder properties
		FOLDER_IN = folderService.getFolderIn();
		FOLDER_OUT = folderService.getFolderOut();
		if (StringUtils.isBlank(FOLDER_IN) || "${fr.insee.pearljam.folder.in}".equals(FOLDER_IN)) {
			throw new FolderException("property fr.insee.queen.batch.folder.in is not define in properties");
		}
		if (StringUtils.isBlank(FOLDER_OUT) || "${fr.insee.pearljam.folder.out}".equals(FOLDER_OUT)) {
			throw new FolderException("property fr.insee.queen.batch.folder.out is not define in properties");
		}
		logger.log(Level.INFO, "Folder properties are OK");

		// Check database
		
		databaseService.checkDatabaseAccess();
		logger.log(Level.INFO, "Database is OK");
	}

	/**
	 * Check folder tree : folders define in properties exist or not. If folder not
	 * exist, folder is create
	 * 
	 * @throws FolderException
	 */
	public static void checkFolderTree() throws FolderException {
		if (!PathUtils.isDirectoryExist(FOLDER_IN)) {
			logger.log(Level.WARN, "Folder tree '{}' does not exist", FOLDER_IN);
			try {
				FileUtils.forceMkdir(new File(FOLDER_IN));
			} catch (IOException e) {
				throw new FolderException("Error during " + FOLDER_IN + " creation : " + e.getMessage());
			}
		}
		logger.log(Level.INFO, "Folder tree '{}' is OK", FOLDER_IN);

		if (!PathUtils.isDirectoryExist(FOLDER_OUT)) {
			logger.log(Level.WARN, "Folder tree '{}' does not exist", FOLDER_OUT);
			try {
				FileUtils.forceMkdir(new File(FOLDER_OUT));
			} catch (IOException e) {
				throw new FolderException("Error during " + FOLDER_OUT + " creation : " + e.getMessage());
			}
    }
    
    if (!PathUtils.isDirectoryExist(FOLDER_IN + "/processing")) {
			logger.log(Level.WARN, "Folder tree '{}' does not exist", FOLDER_IN + "/processing");
			try {
				FileUtils.forceMkdir(new File(FOLDER_IN + "/processing"));
			} catch (IOException e) {
				throw new FolderException("Error during " + FOLDER_IN + "/processing  creation : " + e.getMessage());
			}
		}
		logger.log(Level.INFO, "Folder tree '{}' is OK", FOLDER_IN + "/processing");
  }

  /**
	 * Checks if the "synchro" folder exists in the out folder
	 * if not, it is created
	 * 
	 * @throws FolderException
	 */
  public static void checkSynchroFolder() throws FolderException{
    if (!PathUtils.isDirectoryExist(FOLDER_OUT + "/synchro")) {
			logger.log(Level.WARN, "Creating /synchro folder in output directory");
			try {
				FileUtils.forceMkdir(new File(FOLDER_OUT + "/synchro"));
			} catch (IOException e) {
				throw new FolderException("Error during " + FOLDER_OUT + "/synchro folder creation : " + e.getMessage());
			}
		}
  }

	/**
	 * run batch : Check if argument is well fielded ant start to run the batch
	 * @param options arguments define on cmd execution
	 * @return BatchErrorCode of batch execution
	 * @throws ArgumentException
	 * @throws ValidateException
	 * @throws BatchException
	 * @throws IOException
	 * @throws SQLException 
	 * @throws XMLStreamException 
	 * @throws FolderException 
	 */
	public static BatchErrorCode runBatch(String[] options)
			throws ArgumentException, ValidateException, BatchException, IOException, SQLException, XMLStreamException, FolderException {
		if (options.length == 0) {
			throw new ArgumentException(
					"No batch type found in parameter, you must choose between [TRIGGERSTATES], [DELETECAMPAIGN], [LOADCAMPAIGN] or [LOADCONTEXT]");
		}
		BatchOption batchOption = null;
		try {
			batchOption = BatchOption.valueOf(options[0].trim());
		} catch (Exception e) {
			throw new ArgumentException("Batch type [" + options[0].trim()
					+ "] not exist, you must choose between [TRIGGERSTATES], [DELETECAMPAIGN], [LOADCAMPAIGN] or [LOADCONTEXT]");
		}
		logger.log(Level.INFO, "Batch is running with option {}", batchOption.getLabel());
		switch(batchOption) {
		case DAILYUPDATE: 
			triggerService = context.getBean(TriggerService.class);
			return triggerService.updateStates();
		case SYNCHRONIZE:
			checkSynchroFolder();
			logger.log(Level.INFO, "Running synchronization with context referential");
			triggerService = context.getBean(TriggerService.class);
			return triggerService.synchronizeWithOpale(FOLDER_OUT);
		case EXTRACT:
			triggerService = context.getBean(TriggerService.class);
			return triggerService.extractCampaigns(FOLDER_OUT);
		default:
			return launcherService.validateLoadClean(batchOption, FOLDER_IN, FOLDER_OUT);
		}
		

	}
}
