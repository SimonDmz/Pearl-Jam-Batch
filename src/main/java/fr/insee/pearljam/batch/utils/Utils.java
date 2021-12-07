package fr.insee.pearljam.batch.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	
	private Utils() {
		throw new IllegalStateException("Utility class");
	}
	
	public static String getTimestamp() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date dateNow = new Date();
		return formatter.format(dateNow);
	}
}
