package util;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;

public class TraceLog {
	FileWriter fileWriter;
	File file;
	SimpleDateFormat timeDateFormater = new SimpleDateFormat ("yyMMdd HHmmss:SSS");
	
	public TraceLog (String folder){
		SimpleDateFormat ft = new SimpleDateFormat ("HHmmss_MMdd");
		String fileName = "T"+ft.format(new Date())+".log";
		file = new File(folder, fileName);
		try {
			fileWriter = new FileWriter(new File(folder, fileName));
		} catch (IOException e) {
			System.out.println("ex="+e.getMessage());
		}
	}
	
	public void Log(String source, String content){
		String line = timeDateFormater.format(new Date())+"\t"+source+"\t"+content+"\n";
		try {
			if (null == fileWriter)
			{
				fileWriter = new FileWriter(file);
			}
			fileWriter.write(line);
			fileWriter.flush();
		} catch (IOException e) {
			System.out.println(line + "ex="+e.getMessage());
		}
	}
	
}
