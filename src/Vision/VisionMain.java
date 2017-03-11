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
import org.opencv.imgcodecs.Imgcodecs;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
	
public class VisionMain {
	UsbCamera camera;
	int round;
	Mat mat;
	CvSource outputStream1;
	CvSource outputStream2;
	CvSink cvSink;
	Thread visionThread = null;
	public Result result = null;
	static final int resolutionX= 640;
	static final int resolutionY = 480;
	static final int hue1 = 50;
	static final int hue2 = 180;
	static final int saturation1 = 120;
	static final int saturation2 = 255;
	static final int value1 = 20;
	static final int value2 = 255;
	
	public VisionMain(){
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(resolutionX, resolutionY);
		
		//camera.setBrightness(10);
		//camera.setExposureManual(10);
		
		cvSink = CameraServer.getInstance().getVideo();
		outputStream1 = CameraServer.getInstance().putVideo("h1", resolutionX, resolutionY);
		outputStream2 = CameraServer.getInstance().putVideo("h2", resolutionX, resolutionY);
		mat = new Mat();
		round = 0;
	}
	public void start(){
		if (null == visionThread){
			visionThread = new Thread(() -> {
				while (!Thread.interrupted()) {
					long begin = System.currentTimeMillis();
					compute();
					SmartDashboard.putNumber("Delay", System.currentTimeMillis()-begin);
					if (result != null){
						SmartDashboard.putNumber("Age", result.age());
					}
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
		if (cvSink.grabFrame(mat) == 0) {
			return null;
		}
		return mat;
	}
	
	public boolean SavePicture(String folder, String name, Mat mat)
	{
		SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd.hhmmss.SSS");
		String fileName = folder+File.separator+ft.format(new Date())+".jpg";
		Imgcodecs.imwrite(fileName, mat);
		return true;
	}

	
	public void compute() {
		round++;
		if (cvSink.grabFrame(mat) == 0) {
			// Send the output the error.
			outputStream2.notifyError(cvSink.getError());
			// skip the rest of the current iteration
			return;
		}
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2HSV);
		Core.inRange(mat, new Scalar(hue1, saturation1, value1), new Scalar(hue2, saturation2, value2), mat);
		outputStream1.putFrame(mat);
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();Imgproc
		.findContours(mat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		ArrayList<Target> targetList = selectLargeContours(contours, 10);
		//Sort by Ratio Score (absolute value of 2.5 - height/width)
		TargetComparator comparator = new TargetComparator();
		Collections.sort(targetList, comparator);
		Result tempResult = chooseTwoTarget(targetList);
		SmartDashboard.putBoolean("Result????", tempResult.hasBothTarget());
		if (tempResult.hasBothTarget()){
			result = tempResult;}
		/*else {
			SmartDashboard.putString("Not two targets", "NOPE");
			tempResult = chooseOneTarget(targetList);
			SmartDashboard.putBoolean("Has one target:", tempResult.hasOneTarget());
			SmartDashboard.putBoolean("Right target", tempResult.hasRightTarget());
			SmartDashboard.putBoolean("Left Target", tempResult.hasLeftTarget());
			if (tempResult.hasOneTarget()) {
				result = tempResult;	
			}
		}*/
		if (result != null){
		publishValues(result);
		}
		
		
		///if (result!=null){
		//	if (!result.hasNoTarget()){
		//		publishValues(result);
		//	}
		//}*/
	}
	
	public void publishValues (Result result){
		SmartDashboard.putNumber("CenterX", result.m_centerX);
		SmartDashboard.putNumber("CenterY", result.m_centerY);
		SmartDashboard.putNumber("Distance", result.distance());
		SmartDashboard.putNumber("Distance To Center!!", result.sideDistance());
		SmartDashboard.putNumber("Number of Targets", result.targetNumber());
		SmartDashboard.putNumber("Rotate Distance", result.rotateDistance());
		
		if (result.m_targetLeft != null) {
			Imgproc.rectangle(mat, new Point(result.m_targetLeft.m_rect.x, result.m_targetLeft.m_rect.y),
					new Point(result.m_targetLeft.m_rect.x + result.m_targetLeft.m_rect.width,
							result.m_targetLeft.m_rect.y + +result.m_targetLeft.m_rect.height),
					new Scalar(255, 255, 255), 2);
		}

		if (result.m_targetRight != null) {
			Imgproc.rectangle(mat, new Point(result.m_targetRight.m_rect.x, result.m_targetRight.m_rect.y),
					new Point(result.m_targetRight.m_rect.x + result.m_targetRight.m_rect.width,
							result.m_targetRight.m_rect.y + +result.m_targetRight.m_rect.height),
					new Scalar(255, 255, 255), 6);
		}

		Imgproc.line(mat, new Point(resolutionX/2-5, resolutionY/2), new Point(resolutionX/2+5, resolutionY/2), new Scalar(255, 255, 255), 2);
		Imgproc.line(mat, new Point(resolutionX/2, resolutionY/2-5), new Point(resolutionX/2, resolutionY/2+5), new Scalar(255, 255, 255), 2);

		if (result.hasBothTarget()) {
			Imgproc.circle(mat, new Point(result.m_centerX, result.m_centerY), 5, new Scalar(255, 255, 255), 2);
		}

		SmartDashboard.putNumber("Round", round);
		outputStream2.putFrame(mat);
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
			
			//SmartDashboard.putString("Target candidate" + i, 
			//		"RatioScore:"+target.ratioScore()+",FillRatio:"+target.fillRatio()+",area="+contour.width()*Imgproc.contourArea(contour));
			if(target.ratioScore()<.6  && target.fillRatio()>0.65){
				targetList.add(target);
			//	SmartDashboard.putString("Target candidate" + i+ "select", 
			//			"Yes");
			}else
			{
			//	SmartDashboard.putString("Target candidate" + i+ "select", 
			//			"No");
			}
			contours.remove(largestContourIndex);
		}
		
		for (int j=0; j<targetList.size(); j++){
			SmartDashboard.putNumber("Target" + j, targetList.get(j).ratioScore());
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


