package keyboard;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.zip.*;


public class DownloadImages
{
	private static final int BUFFER_SIZE = 4096;
	public static void download(String path) throws IOException
	{
		String saveTemp=path+"download.zip";
		File zip=new File(path);
		if(!zip.exists())
		{
			zip.mkdirs();
		}
		zip=new File(saveTemp);
		URL website = new URL("https://usfirst.collab.net/sf/frs/do/downloadFile/projects.wpilib/frs.2016_field_images.vision_target_images_for_2016/frs1207?dl=1");
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(saveTemp);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		File save=new File(path);
		if(!save.exists())
		{
			save.mkdirs();
		}
		ZipInputStream in = new ZipInputStream(new FileInputStream(zip));
		ZipEntry entry=in.getNextEntry();
		while (entry != null) {
            String filePath = save.getAbsolutePath() + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(in, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            in.closeEntry();
            entry = in.getNextEntry();
        }
		fos.close();
        in.close();
        //Move files from realfullfield inside subfolder to here
        File source=new File(path+"RealFullField");
        File[] images=source.listFiles();
        for(File file: images)
        {
        	if(file.getName().contains(".jpg"))
        	{
        		file.renameTo(new File(path+file.getName()));
        	}
        }
        deleteDirectory(new File(path+"__MACOSX"));
        deleteDirectory(source);
        File delete;
        final String[] toDelete={"download.zip",".DS_Store"};
        for(int i=0;i<toDelete.length;i++)
        {
        	delete=new File(path+toDelete[i]);
        	delete.delete();
        }
	}
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
    public static boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return(directory.delete());
    }
}
