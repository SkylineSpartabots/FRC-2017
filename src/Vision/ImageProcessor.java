package Vision;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageProcessor {

	VisionConfig m_config;
	Mat m_bitmap = new Mat();
	
	public void SetConfig(VisionConfig inputConfig)
	{
		m_config = inputConfig;
	}
	
	public VisionConfig GetConfig()
	{
		return m_config;
	}
	
	
	public static void SavePicture(String folder, String name, Mat mat)
	{
		String fileName = folder+File.separator+name;
		TraceLog.Log("SavePicture", "Write into folder=" + fileName);
		Imgcodecs.imwrite(fileName, mat);
	}
	

	public ProcessResult ProcessImage(Mat image, String rawFileName)
	{
		TraceLog.Log("ProcessImage", "Image="+rawFileName);
		FilterBitmap(image);
		if (m_config.saveBitmap) {
			SavePicture(TraceLog.Instance.GetLogFolder(), "Bit_"+rawFileName, m_bitmap);			
		}
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(m_bitmap, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		ArrayList<Target> targetList = SelectLargeContours(contours, 20);
		if (targetList.isEmpty())
		{
			TraceLog.Log("ProcessImage", "No target found");
			return new ProcessResult(null, null);
		}
		
		SortTargetsByScore(targetList);
		ProcessResult result = ChooseTwoTarget(targetList);
		if (2 == result.m_targetCount){
			return result;
		}
		
		return ChooseOneTarget(targetList);
	}
	
	private ProcessResult ChooseOneTarget(ArrayList<Target> targetList)
	{
		Target topTarget = targetList.get(0);
		if (topTarget.m_score > m_config.targetScoreUpperBound)
		{
			return new ProcessResult(null, null);
		}
		
		if (topTarget.m_centerX < VisionConfig.ImageResolutionX/2)
		{
			return new ProcessResult(topTarget, null);
		}
		else
		{
			return new ProcessResult(null, topTarget);
		}
	}

	public ConfigResultScore ProcessImageFile(String folder, String fileName, int expectedTarget)
	{
		ConfigResultScore configResultScore = new ConfigResultScore();
		String fullFileName = folder+File.separator+fileName;
		TraceLog.Log("ProcessImageFile", fullFileName);	
		Mat image = Imgcodecs.imread(fullFileName, Imgcodecs.CV_LOAD_IMAGE_COLOR); 
		if(image.empty())                              // Check for invalid input
		{
			System.out.println( "failed to load file " + fullFileName);
			configResultScore.m_score = 0;
			return configResultScore;
		}
		
		ProcessResult result = ProcessImage(image, fileName);
		configResultScore.Evaluate(result, expectedTarget);
		TraceLog.Log("Current", result.toString());
		
		return configResultScore;
	}
	

	public void FilterBitmap(Mat rawImage)
	{
		Imgproc.cvtColor(rawImage, m_bitmap, Imgproc.COLOR_BGR2HSV);
		Core.inRange(
			m_bitmap, 
			new Scalar(m_config.hueLowerBound, m_config.saturationLowerBound, m_config.valueLowerBound), 
			new Scalar(m_config.hueUpperBound, m_config.saturationUpperBound, m_config.valueUpperBound), 
			m_bitmap);
	}


	private ArrayList<Target> SelectLargeContours(List<MatOfPoint> contours, int numberOfTargets)
	{
		ArrayList<Target> targetList = new ArrayList<Target>();
		for (int i= 0; i<contours.size(); i++) {
			targetList.add(new Target(contours.get(i), m_config));
		}
		Collections.sort(targetList, new TargetAreaComparator());
		
		ArrayList<Target> resultList = new ArrayList<Target>();
		for (int i= 0; i<contours.size() && i<numberOfTargets; i++) {
			Target target = targetList.get(i);
			target.QuickProcess();
			resultList.add(target);
		}
		
		return resultList;
	}


	private void SortTargetsByScore(ArrayList<Target> targetList)
	{
		Collections.sort(targetList, new TargetScoreComparator());

		for (int i= 0; i<targetList.size(); i++) {
			Target target = targetList.get(i);
			target.m_index = i;
			target.CompleteProcess();
			if (m_config.logTopTarget)
			{
				TraceLog.Log("TopTargets", target.toString());
			}
		}
	}
	
	
	private ProcessResult ChooseTwoTarget(ArrayList<Target> targetList)
	{
		MatchResult matchResult = new MatchResult();
		for (int i = 0; i < targetList.size() && !matchResult.m_match; i++) {
			Target targetI = targetList.get(i);
			if (targetI.m_score > m_config.targetScoreUpperBound)
			{
				continue;
			}
				
			for (int j = i + 1; j < targetList.size() && !matchResult.m_match; j++) {
				Target targetJ = targetList.get(j);
				if (targetJ.m_score > m_config.targetScoreUpperBound)
				{
					continue;
				}
				matchResult = TargetMatcher.Match2Target(targetI, targetJ);				
			}
		}
		
		Target leftTarget = null;
		Target rightTarget = null;
		if (matchResult.m_match){
			if (matchResult.m_leftIndex != MatchResult.InvalidTargetIndex)
			{
				leftTarget = targetList.get(matchResult.m_leftIndex);
			}
	
			if (matchResult.m_rightIndex != MatchResult.InvalidTargetIndex)
			{
				rightTarget = targetList.get(matchResult.m_rightIndex);
			}
		}
		
		ProcessResult result = new ProcessResult(leftTarget, rightTarget);
		if (m_config.logMatchTarget)
		{
			TraceLog.Log("ChooseTwoTarget-Match", matchResult.toString());
			TraceLog.Log("ChooseTwoTarget-Result",result.m_string);
		}
		return result;
	}
}

