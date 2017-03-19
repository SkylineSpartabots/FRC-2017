package Vision;

import java.util.*;
import java.io.*;
import java.text.*;

public class TraceLog {

	public static void StartLog(String root, String folder)
	{
		Instance = new TraceLog(root);
		Instance.SetFolder(folder);
	}
	
	public static void Log(String source, String content)
	{
		if (null == Instance)
		{
			System.out.println("TraceLog.Log() -- Have to call StartLog first");
			return;
		}
		
		Instance.WriteLog(source, content);
	}
	

	public void SetFolder(String folder)
	{
		SimpleDateFormat ft = new SimpleDateFormat ("_HHmmss_MMdd");
		m_folder = folder+ft.format(new Date());
		File dir = new File(m_root, m_folder);
		dir.mkdirs();

		String fileName = "Trace_" + (new SimpleDateFormat("MMdd_HHmmss")).format(new Date())+ ".log";
		m_file = new File(m_root+File.separator+m_folder,  fileName);
		m_fileWriter = null;
	}

	public void WriteLog(String source, String content){
		
		String line = m_timeDateFormater.format(new Date())+"\t"+source+"\t"+content+"\n";

		try {
			if (null == m_fileWriter)
			{
				OpenFile();
			}

			m_fileWriter.write(line);
			m_fileWriter.flush();
		} catch (IOException e) {
			System.out.println(line + "ex="+e.getMessage());
		}
	}
	
	
	public static double Round2(double value){
		return Math.round(value*100)/100.0;
	}
	
	public static double Round3(double value){
		return Math.round(value*1000)/1000.0;
	}
	
	public String GetLogFolder()
	{
		return m_root+File.separator+m_folder;
	}
	
	public static TraceLog Instance;
	
	String m_root;
	String m_folder;
	File m_file;
	FileWriter m_fileWriter = null;
	SimpleDateFormat m_timeDateFormater = new SimpleDateFormat ("MMdd HHmmss.SSS");
	
	TraceLog(String root) {
		m_root = root;
	}
	
	void OpenFile() throws IOException
	{
		m_fileWriter = new FileWriter(m_file);
		m_fileWriter.write("Start at " + (new SimpleDateFormat("yyyyMMdd HHmmss")).format(new Date())+"\n");
	}
}
