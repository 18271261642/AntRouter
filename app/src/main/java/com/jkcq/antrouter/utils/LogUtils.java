/**
 * 
 */
package com.jkcq.antrouter.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * @author WuJianhua
 * 日志保存
 */
public class LogUtils {
	
	private static final String LOG_DIR = "/physical/log";
	
	/** 日志保存 */
	public synchronized static void saveLog(String logContent){
		try {
			String sdPath = "/sdcard";
			File fDir = new File(sdPath+LOG_DIR);
			if(!fDir.exists()) fDir.mkdirs();
			File logFile = new File(fDir, "log.txt");
			if(!logFile.exists()) logFile.createNewFile();
			String time = getDateStr2(System.currentTimeMillis());
			logContent = "\n\n"+time+"\n"+logContent;
			OutputStream out = new FileOutputStream(logFile, true);
			out.write(logContent.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 通过long转成时间格式 */
	public static String getDateStr2(long time){
	    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    try {
			return formatter.format(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return "";
	}
	
	/** 清理日志逻辑: 文件超过10M则删除
	 * @param isForced 是否强制删除,否则超过10M才删 */
	public static void clearLog(boolean isForced){
		String sdPath = "/sdcard";
		File logFile = new File(sdPath+LOG_DIR+"/log.txt");
		if(!logFile.exists()) return;
		if(isForced){
			logFile.delete();
		}else if(logFile.length()>=1024*1024*10){
			logFile.delete();
		}
	}
}
