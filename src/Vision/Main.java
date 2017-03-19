package Vision;

import java.util.*;
import java.text.*;
import java.io.*;

import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.imgcodecs.*;

public class Main {

	public static void main(String[] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

		
		System.out.println( "Start");
		VisionConfig config = new VisionConfig();
		config.saveBitmap = false;	
		
		TraceLog.StartLog("c:/FRC17", "Process");
		ProcessImageFolder("c:/FRC17/Target2", config, 2);
		
		System.out.println( "Finish");
	}
	
	static void ProcessImageFolder(String folderPath, VisionConfig config, int expectedTarget)
	{
		ImageProcessor processor = new ImageProcessor();
		processor.SetConfig(config);
		
		ArrayList<ConfigResultScore> configScoreList = new ArrayList<ConfigResultScore>();
		
		File sourceFolder = new File(folderPath);
		File[] filesList = sourceFolder.listFiles();
		for (File file : filesList) {
		    if (file.isFile()) {
		    	String fileName = file.getName();
		    	if (fileName.startsWith("Raw")){
		    		configScoreList.add(
		    				processor.ProcessImageFile(
		    						sourceFolder.getAbsolutePath(), 
		    						file.getName(), 
		    						expectedTarget));
		    	}
		    }
		}
	}
}
