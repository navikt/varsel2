package no.nav.varsel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {
	private static DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static String formatDateTime(LocalDateTime localdatetime) {
		return formater.format(localdatetime);
	}

}
