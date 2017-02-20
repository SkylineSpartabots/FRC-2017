package Vision;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import Vision.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint;

/**
 * This is a demo program showing the use of OpenCV to do vision processing. The
 * image is acquired from the USB camera, then a rectangle is put on the image
 * and sent to the dashboard. OpenCV has many methods for different types of
 * processing.
 */
public class VisionMain {
	double x_distance;
	double y_distance;

	public double[] getResultant() {
		double[] resultant = new double[2];
		resultant[0] = x_distance; // put values for strafe and forward here
		resultant[1] = y_distance;
		return resultant;
	}

	UsbCamera camera;
	int round;
	Mat mat;
	CvSource outputStream1;
	CvSource outputStream2;
	CvSink cvSink;

	public VisionMain() {
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(320, 240);
		cvSink = CameraServer.getInstance().getVideo();
		outputStream1 = CameraServer.getInstance().putVideo("h1", 320, 240);
		outputStream2 = CameraServer.getInstance().putVideo("h2", 320, 240);
		mat = new Mat();
		round = 0;
	}

	public void compute() {
		round++;
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2HSV);
		SmartDashboard.putString("Is this working part 2??", "Yes! :((((");
		Core.inRange(mat, new Scalar(40, 0, 130), new Scalar(180, 255, 255), mat);
		outputStream1.putFrame(mat);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(mat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		SmartDashboard.putNumber("Number of Contours:", contours.size());
		ArrayList<Target> targetList = new ArrayList<Target>();
		int totalContours = contours.size();
		// Sorting the largest 10 contours by decreasing area (size) and
		// convert to rectangles.
		while (targetList.size() != totalContours && targetList.size() < 5) {
			int largestContourIndex = 0;
			int maxArea = -1000000000;
			for (int j = 0; j < contours.size(); j++) {
				if (maxArea < contours.get(j).width() * contours.get(j).height()) {
					maxArea = contours.get(j).width() * contours.get(j).height();
					largestContourIndex = j;
				}
			}
			Target target = new Target(Imgproc.boundingRect(contours.get(largestContourIndex)),
					contours.get(largestContourIndex));

			targetList.add(target);
			contours.remove(largestContourIndex);
		}
		TargetComparator comparator = new TargetComparator();
		Collections.sort(targetList, comparator);
		Result result = chooseTarget(targetList);
		
		//Publish values
		y_distance = result.distance();
		x_distance = result.sideDistance();
	
		SmartDashboard.putNumber("CenterX", result.m_centerX);
		SmartDashboard.putNumber("CenterY", result.m_centerY);
		SmartDashboard.putNumber("Distance", result.distance());
		SmartDashboard.putNumber("LeftX", result.targetLeftX());
		SmartDashboard.putNumber("Left Height", result.targetLeftHeight());
		
		SmartDashboard.putNumber("Pixel To Center", result.pixelsToCenter());
		SmartDashboard.putNumber("Distance To Center", result.sideDistance());

		SmartDashboard.putBoolean("HasBoth", result.hasBothTarget());
		SmartDashboard.putBoolean("HasOne", result.hasOneTarget());
		SmartDashboard.putBoolean("HasNone", result.hasNoTarget());
		SmartDashboard.putNumber("Number of Targets", result.targetNumber());

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

		Imgproc.line(mat, new Point(155, 120), new Point(165, 120), new Scalar(255, 255, 255), 2);
		Imgproc.line(mat, new Point(160, 115), new Point(160, 125), new Scalar(255, 255, 255), 2);

		if (result.hasBothTarget()) {
			Imgproc.circle(mat, new Point(result.m_centerX, result.m_centerY), 5, new Scalar(255, 255, 255), 2);
		}

		SmartDashboard.putNumber("Round", round);

		outputStream2.putFrame(mat);
	}

	public Result chooseTarget(ArrayList<Target> targetList) {
		Target targetLeft = null;
		Target targetRight = null;
		for (int i = 0; i < 4; i++) {
			for (int j = i + 1; j < 4; j++) {
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

	public boolean checkTarget(Target target1, Target target2) {
		double areaCompare = target1.m_area / target2.m_area;
		if (areaCompare < 1) {
			areaCompare = 1 / areaCompare;
		}
		boolean areaCheck = areaCompare < 1.5;
		double widthDistance = (target2.m_rect.x - target1.m_rect.x) / target1.m_rect.width;
		boolean widthCheck = widthDistance > 2.4;
		double contToRect1 = target1.m_contourArea / target1.m_rect.area();
		boolean contRect1Check = contToRect1 > 0.75;
		double contToRect2 = target2.m_contourArea / target2.m_rect.area();
		boolean contRect2Check = contToRect2 > 0.75;
		return areaCheck && widthCheck && contRect1Check && contRect2Check;
	}
}
