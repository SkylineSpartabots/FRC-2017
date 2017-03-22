package Vision;

import java.io.File;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
	
public class VisionMain {
	VisionConfig config = new VisionConfig();
	ImageProcessor processor = new ImageProcessor();
	
	int round = 0;
	int goodResult = 0;
	
	CvSink cvSink;
	Thread visionThread = null;
	
	Mat rawImage;
	public boolean saveAllPicture = false;
	public ProcessResult LastGoodResult = null;
	public ProcessResult CurrentResult = null;
	
	public VisionMain(){
		config.logTopTarget = true;
		config.logMatchTarget = true;	
		config.saveBitmap = true;	
		processor.SetConfig(config);
		
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(VisionConfig.ImageResolutionX, VisionConfig.ImageResolutionY);
		camera.setBrightness(30);
		camera.setExposureManual(30);
		
		cvSink = CameraServer.getInstance().getVideo();
		rawImage = new Mat();
		round = 0;
	}
	public void start(){
		if (null == visionThread){
			visionThread = new Thread(() -> {
				while (!Thread.interrupted()) {
					processImage();
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

	public void processImage() {
		round++;
		CurrentResult = null;
		
		if (cvSink.grabFrame(rawImage) == 0) {
			TraceLog.Log("grabFrame", "Failed "+ cvSink.getError());
			return;
		}
		long timeTakePicture = System.currentTimeMillis();
		TraceLog.Log("grabFrame", "Success");
		String fullImageName = TraceLog.Instance.GetLogFolder()+File.separator+ "Raw_" + round+".jpg";
		if (saveAllPicture)
		{
			ImageProcessor.SavePicture(TraceLog.Instance.GetLogFolder(), "Raw_Good_" + round+".jpg", rawImage);
		}
		
		CurrentResult = processor.ProcessImage(rawImage, fullImageName);
		CurrentResult.m_pictureTimestamp = timeTakePicture;
		
		if (CurrentResult.m_targetCount<2 && !saveAllPicture)
		{
			ImageProcessor.SavePicture(TraceLog.Instance.GetLogFolder(), "Raw_bad_" + round+".jpg", rawImage);
		}
		
			
		if (2 == CurrentResult.m_targetCount){
			goodResult++;
			LastGoodResult = CurrentResult;
		}
	}
	
}


