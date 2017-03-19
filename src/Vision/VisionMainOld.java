package Vision;

import java.io.File;

import org.opencv.core.Mat;
//import org.opencv.imgcodecs.Imgcodecs;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

	
public class VisionMainOld {
	VisionConfig config = new VisionConfig();
	ImageProcessor processor = new ImageProcessor();
	
	int round = 0;
	int goodResult = 0;
	
	UsbCamera camera;
	CvSource outputStream1;
	CvSource outputStream2;
	CvSink cvSink;
	Thread visionThread = null;
	
	Mat rawImage;
	
	public boolean saveAllPicture = false;
	
	public ProcessResult LastGoodResult = null;
	public ProcessResult CurrentResult = null;
	
	long timeBegin = 0;
	long timeTakePicture = 0;
	long timeFinish = 0;

	
	public VisionMainOld(){
		config.logTopTarget = true;
		config.logMatchTarget = true;	
		config.saveBitmap = true;	
		processor.SetConfig(config);
		
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(VisionConfig.ImageResolutionX, VisionConfig.ImageResolutionY);
		
		camera.setBrightness(15);
		camera.setExposureManual(15);
		
		cvSink = CameraServer.getInstance().getVideo();
		outputStream1 = CameraServer.getInstance().putVideo("h1", VisionConfig.ImageResolutionX, VisionConfig.ImageResolutionY);
		outputStream2 = CameraServer.getInstance().putVideo("h2", VisionConfig.ImageResolutionX, VisionConfig.ImageResolutionY);
		rawImage = new Mat();
		round = 0;
	}
	public void start(){
		if (null == visionThread){
			visionThread = new Thread(() -> {
				while (!Thread.interrupted()) {
					processLiveImage();
				}
			});
			visionThread.setDaemon(true);
		}
		
		if (!visionThread.isAlive()){
			visionThread.start();
		}
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
	
	public void processLiveImage() {
		round++;
		
		CurrentResult = null;
		timeBegin = System.currentTimeMillis();
		if (cvSink.grabFrame(rawImage) == 0) {
			TraceLog.Log("grabFrame failed", cvSink.getError());
			return;
		}
		timeTakePicture = System.currentTimeMillis();
		
		CurrentResult = processor.ProcessImage(rawImage);
		CurrentResult.m_pictureTimestamp = timeTakePicture;
		
		if (CurrentResult.m_targetCount<2)
		{
			ImageProcessor.SavePicture(TraceLog.Instance.GetLogFolder(), "Raw_bad_" + round, rawImage);
		}
		else if (saveAllPicture)
		{
			ImageProcessor.SavePicture(TraceLog.Instance.GetLogFolder(), "Raw_Good_" + round, rawImage);
		}
			
		if (2 == CurrentResult.m_targetCount){
			goodResult++;
			LastGoodResult = CurrentResult;
		}
		timeFinish = System.currentTimeMillis();
	}
}


