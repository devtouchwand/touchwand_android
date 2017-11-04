/**
 * 
 */
package com.touchwand.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author helios
 *
 */
public class GUIUtils {

	private static DateFormat dateFormat = new SimpleDateFormat("dd_MM_yy-ss");
	
	public static String formatDate(Date date){
		
		return dateFormat.format(date);
		
	}
	
	
}
