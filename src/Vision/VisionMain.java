package Vision;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team2976.robot.Robot;
import org.opencv.imgcodecs.Imgcodecs;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
	
public class VisionMain {
	static final int resolutionX= 640;
	static final int resolutionY = 480;
	static final int hue1 = 50;
	static final int hue2 = 120;
	static final int saturation1 = 100;
	static final int saturation2 = 255;
	static final int value1 = 100;
	static final int value2 = 255;
	static final int luminance1 = 60;
	static final int luminance2 = 200;
	
	int round = 0;
	int goodResult = 0;
	long timeBegin = 0;
	long timeTakePicture = 0;
	long timeFilter = 0;
	long timeDisplayFilteredImage = 0;
	long timeFindContours = 0;
	long timeSelectLargeContours = 0;
	long timeBeforeSavePictures = 0;
	long timeAfterSavePictures = 0;
	long timeBeforeChooseTwoTargets = 0;
	long timeChooseTwoTargets = 0;
	long timeBeforePublishValues = 0;
	long timeAfterPublishValues = 0;
	long timeFinish = 0;
	
	UsbCamera camera;
	CvSource outputStream1;
	CvSource outputStream2;
	CvSink cvSink;
	Thread visionThread = null;
	
	Mat rawImage;
	Mat filteredImage;
	
	public boolean saveAllPicture = false;
	public Result LastGoodResult = null;
	public Result CurrentResult = null;
	
	public VisionMain(){
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(resolutionX, resolutionY);
		
		camera.setBrightness(15);
		camera.setExposureManual(15);
		
		cvSink = CameraServer.getInstance().getVideo();
		outputStream1 = CameraServer.getInstance().putVideo("h1", resolutionX, resolutionY);
		outputStream2 = CameraServer.getInstance().putVideo("h2", resolutionX, resolutionY);
		rawImage = new Mat();
		filteredImage = new Mat();
		round = 0;

		String filter = "H:"+hue1+"--"+hue2 + ", S:"+saturation1+"--"+saturation2+", V:"+value1+"--"+value2;
		Robot.traceLog.Log("Filter", filter);
	}
	public void start(){
		if (null == visionThread){
			visionThread = new Thread(() -> {
				while (!Thread.interrupted()) {
					processImage();
					
					String trace = "Round:"+round;
					if ((LastGoodResult != CurrentResult) || (CurrentResult==null))
					{
						trace += ",bad";
					}else
					{
						trace += ",good";
					}
					
					trace += ", good:"+goodResult;
					trace += ", delay:"+(timeFinish-timeBegin);
					

					long age = 0;
					if (LastGoodResult != null)
					{
						age = LastGoodResult.age();
					}
					trace += ", age:"+age;
					trace += ", takePicture:"+(timeTakePicture-timeBegin);
					trace += ", filter:"+(timeFilter-timeTakePicture);
					trace += ", displayFiltered:"+(timeDisplayFilteredImage-timeFilter);
					trace += ", findContours:"+(timeFindContours-timeDisplayFilteredImage);
					trace += ", selectLargeContours:"+(timeSelectLargeContours-timeFindContours);
					trace += ", SavePicture:"+(timeAfterSavePictures-timeBeforeSavePictures);
					trace += ", ChooseTwoTargets:"+(timeChooseTwoTargets-timeBeforeChooseTwoTargets);
					trace += ", Publish:"+(timeAfterPublishValues-timeBeforePublishValues);
					
					Robot.traceLog.Log("VisionMain", trace);
				}
			});
			visionThread.setDaemon(true);
		}
		visionThread.start();
	}
	
	public void stop(){
		visionThread.interrupt();
	}
	
	public Mat TakePicture()
	{
		if (cvSink.grabFrame(rawImage) == 0) {
			return null;
		}
		return rawImage;
	}
	
	public boolean SavePicture(String folder, String name, Mat mat)
	{
		//SimpleDateFormat ft = new SimpleDateFormat ("MMdd.HHmmss.SSS");
		String fileName = folder+File.separator+name+".jpg";
		Imgcodecs.imwrite(fileName, mat);
		return true;
	};

	
	public void processImage() {
		round++;
		CurrentResult = null;
		timeBegin = System.currentTimeMillis();
		if (cvSink.grabFrame(rawImage) == 0) {
			Robot.traceLog.Log("grabFrame failed", cvSink.getError());
			return;
		}
		timeTakePicture = System.currentTimeMillis();
		
		Imgproc.cvtColor(rawImage, filteredImage, Imgproc.COLOR_BGR2HLS);
		//Core.inRange(filteredImage, new Scalar(hue1, saturation1, value1), new Scalar(hue2, saturation2, value2), filteredImage);
		Core.inRange(filteredImage, new Scalar(hue1, luminance1, saturation1), new Scalar(hue2, luminance2, saturation2), filteredImage);
		timeFilter = System.currentTimeMillis();
		outputStream1.putFrame(filteredImage);
		timeDisplayFilteredImage = System.currentTimeMillis();
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(filteredImage, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		timeFindContours = System.currentTimeMillis();
	
		timeSelectLargeContours = System.currentTimeMillis();
		ArrayList<Target> targetList = selectLargeContours(contours, 10);
		timeBeforeSavePictures = System.currentTimeMillis();
		if (targetList.size()<2)
		{
			SavePicture(Robot.traceFolder, "Raw_Bad_" + round, rawImage);
			SavePicture(Robot.traceFolder, "Filtered_Bad_" + round, filteredImage);	
		}
		else if (saveAllPicture)
		{
			SavePicture(Robot.traceFolder, "Raw_Good_" + round +"_", rawImage);
			SavePicture(Robot.traceFolder, "Filtered_Good_" + round +"_", filteredImage);				
		}
		timeAfterSavePictures = System.currentTimeMillis();
		
		timeBeforeChooseTwoTargets = System.currentTimeMillis();
		//Sort by Ratio Score (absolute value of 2.5 - height/width)
		TargetComparator comparator = new TargetComparator();
		Collections.sort(targetList, comparator);
		CurrentResult = chooseTwoTarget(targetList);
		timeChooseTwoTargets = System.currentTimeMillis();
		if (CurrentResult.hasBothTarget()){
			goodResult++;
			LastGoodResult = CurrentResult;
		}
		else {
			//CurrentResult = chooseOneTarget(targetList);
		}
		if (!CurrentResult.hasNoTarget()){
			timeBeforePublishValues = System.currentTimeMillis();
			publishValues(CurrentResult);
			SmartDashboard.putString("current", getTrace(CurrentResult));
			timeAfterPublishValues = System.currentTimeMillis();
		}
		if (LastGoodResult != null)
		{
			SmartDashboard.putString("LastGood", getTrace(LastGoodResult));
		}
		timeFinish = System.currentTimeMillis();
	}
	
	String getTrace(Result result)
	{
		String trace = "Targets:"+ result.targetNumber();
		trace += ", distance:"+ (int)(result.distance()*100)/100.0;
		trace += ", side:"+ (int)(result.sideDistance()*100)/100.0;
		trace += ", Rotate:"+ (int)(result.rotateDistance()*100)/100.0;
		trace += ", X:"+ result.m_centerX;
		trace += ", Y:"+ result.m_centerY;
		return trace;
		
	}
	public void publishValues (Result result){
		Robot.traceLog.Log("Vision", getTrace(result));
		if (result.m_targetLeft != null) {
			Imgproc.rectangle(filteredImage, new Point(result.m_targetLeft.m_rect.x, result.m_targetLeft.m_rect.y),
					new Point(result.m_targetLeft.m_rect.x + result.m_targetLeft.m_rect.width,
							result.m_targetLeft.m_rect.y + +result.m_targetLeft.m_rect.height),
					new Scalar(255, 255, 255), 2);
		}

		if (result.m_targetRight != null) {
			Imgproc.rectangle(filteredImage, new Point(result.m_targetRight.m_rect.x, result.m_targetRight.m_rect.y),
					new Point(result.m_targetRight.m_rect.x + result.m_targetRight.m_rect.width,
							result.m_targetRight.m_rect.y + +result.m_targetRight.m_rect.height),
					new Scalar(255, 255, 255), 6);
		}

		Imgproc.line(filteredImage, new Point(resolutionX/2-5, resolutionY/2), new Point(resolutionX/2+5, resolutionY/2), new Scalar(255, 255, 255), 2);
		Imgproc.line(filteredImage, new Point(resolutionX/2, resolutionY/2-5), new Point(resolutionX/2, resolutionY/2+5), new Scalar(255, 255, 255), 2);
		Imgproc.circle(filteredImage, new Point(result.m_centerX, result.m_centerY), 5, new Scalar(255, 255, 255), 2);
		outputStream2.putFrame(filteredImage);
	}
	
	public ArrayList<Target> selectLargeContours(List<MatOfPoint> contours, int desiredContours){
		ArrayList<Target> targetList = new ArrayList<Target>();
		int loop = desiredContours;
		if (contours.size() < loop)
		{
			loop = contours.size();
		}
		for (int i= 0; i<loop; i++) {
			int largestContourIndex = 0;
			double maxArea = -1;
			for (int j = 0; j < contours.size(); j++) {
				MatOfPoint contourj = contours.get(j);
				double contourjArea=Imgproc.contourArea(contourj);
				if (maxArea < contourjArea) {
					maxArea = contourjArea;
					largestContourIndex = j;
				}
			}

			MatOfPoint contour = contours.get(largestContourIndex);
			Target target = new Target(contour);
			
			boolean selected = false;
			if(target.ratioScore()<.5  && target.fillRatio()>0.7){
				targetList.add(target);
				selected = true;
			}
			contours.remove(largestContourIndex);
			
			String trace = "Candidate:" + i;
			trace += ", ratioScore=" + (int)(target.ratioScore()*100)/100.0;
			trace += ", fillRatio=" + (int)(target.fillRatio()*100)/100.0;
			trace += ", selected=" + selected;
			trace += target.getRect();
			Robot.traceLog.Log("LargeContour", trace);
		}
		return targetList;
	}
	
	public Result chooseTwoTarget(ArrayList<Target> targetList) {
		Target targetLeft = null;
		Target targetRight = null;
		for (int i = 0; i < targetList.size(); i++) {
			for (int j = i + 1; j < targetList.size(); j++) {
				Target target1 = targetList.get(i);
				Target target2 = targetList.get(j);
				boolean check = false;
				if (target1.m_rect.x < target2.m_rect.x) {
					check = checkTarget(target1, target2);
					if (check) {
						targetLeft = target1;
						targetRight = target2;
					}
				} else {
					check = checkTarget(target2, target1);
					if (check) {
						targetLeft = target2;
						targetRight = target1;
					}
				}
			}
		}
		return new Result(targetLeft, targetRight);
	}

	public Result chooseOneTarget(ArrayList<Target> targetList) {
		Target targetLeft = null;
		Target targetRight = null;
			
		if (targetList.size()>0){
			Target target = targetList.get(0);
			if (target.m_rect.x < resolutionX/2){
				targetRight = target;
			}
			else
			{
				targetLeft = target;
			}		
		}
		return new Result(targetLeft, targetRight);
	}
	
	public boolean checkTarget(Target target1, Target target2) {
		double areaCompare = target1.m_area / target2.m_area;
		if (areaCompare < 1) {
			areaCompare = 1 / areaCompare;
		}
		boolean areaCheck = areaCompare < 1.5;
		double widthDistance = (target2.m_rect.x - target1.m_rect.x) / target1.m_rect.width;
		boolean widthCheck = widthDistance > 2.4;
		return areaCheck && widthCheck;
	}
}


