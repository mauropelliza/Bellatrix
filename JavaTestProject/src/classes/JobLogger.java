package classes;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLogger {
	private static boolean logToFile;
	private static boolean logToConsole;
	private static boolean logToDatabase;
	private boolean initialized;
	private static Map dbParams;
	private static Logger logger;

	public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam, Map dbParamsMap) {
		logger = Logger.getLogger("MyLog");  
		logToDatabase = logToDatabaseParam;
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
		dbParams = dbParamsMap;
	}
/**
 * @param messageText the message that you want to log
 * @param kindOfLogging a String value, "M" for Message, "W" for Warning and "E" for Error
 * @throws Exception
 */
	public void LogMessage(String messageText, String kindOfLogging) throws Exception {
		if (messageText == null || messageText.length() == 0) {
			return;
		}
		//we do this only if the String is not null
		messageText.trim();
		//a way to log must be specified
		if (!logToConsole && !logToFile && !logToDatabase) {
			throw new Exception("Invalid configuration");
		}
		if (kindOfLogging == null || kindOfLogging.equals("")) {
			throw new Exception("Error or Warning or Message must be specified");
		}

		String l = "";
		
		
		if (kindOfLogging.equalsIgnoreCase("E")) {
			l = "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + " " + messageText;
		} else if (kindOfLogging.equalsIgnoreCase("W")) {
			l = "warning " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + " " + messageText;
		} else if (kindOfLogging.equalsIgnoreCase("M")) {
			l = "message " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + " " + messageText;
		}
		
		if(logToFile) {
			File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt");
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt");

			logger.addHandler(fh);
			logger.log(Level.INFO, l);	
		}
		
		if(logToConsole) {
			ConsoleHandler ch = new ConsoleHandler();
			logger.addHandler(ch);
			logger.log(Level.INFO, l);
		}
		
		if(logToDatabase) {
			if (dbParams == null) {
				throw new Exception("there are no parameters to set the connection to the database");
			}
			Connection connection = null;
			Properties connectionProps = new Properties();
			connectionProps.put("user", dbParams.get("userName"));
			connectionProps.put("password", dbParams.get("password"));
			Statement stmt = null; 
			try {
			connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
					+ ":" + dbParams.get("portNumber") + "/", connectionProps);

			stmt = connection.createStatement();

			stmt.executeUpdate("insert into Log_Values('" + l + "', " + ")");
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				stmt.close();
				connection.close();
			}
		}
	}
}
