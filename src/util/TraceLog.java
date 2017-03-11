package util;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;

public class TraceLog {
	FileWriter fileWriter;
	SimpleDateFormat timeDateFormater = new SimpleDateFormat ("yyyyMMdd.hhmmss:SSS");
	
	public TraceLog (String folder){
		
		SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd.hhmmss");
		String fileName = "Trace_"+ft.format(new Date())+".log";
		try {
			fileWriter = new FileWriter(new File(folder, fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Log(String source, String content){
		String timeStamp = timeDateFormater.format(new Date());
		String line = timeStamp+"\t"+source+"\t"+content+"\n";
		if (null == fileWriter)
		{
			System.out.print(line);
		}
		else 
		{
			try {
				fileWriter.write(line);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
