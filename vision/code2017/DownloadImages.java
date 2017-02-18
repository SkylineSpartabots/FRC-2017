package code2017;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.zip.*;


public class DownloadImages
{
	private static final int BUFFER_SIZE = 4096;
	public static void download() throws IOException
	{
		String path=System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"FRC Field 2017"+File.separator;
		String saveTemp=path+"download.zip";
		File zip=new File(path);
		if(!zip.exists())
		{
			zip.mkdirs();
		}
		zip=new File(saveTemp);
		URL website = new URL("https://usfirst.collab.net/sf/frs/do/downloadFile/projects.wpilib/frs.sample_programs.2017_c_java_vision_sample/frs1255?dl=1");
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
        
        File source=new File(path+"Vision Images");
        File[] imageFolders=source.listFiles();
        for(File file: imageFolders)
        {
        	File[] images=file.listFiles();
			new File(path+file.getName()).mkdirs();
        	for(File image: images)
        	{
        		if(image.getName().contains(".jpg"))
        		{
        			image.renameTo(new File(path+file.getName()+File.separator+image.getName()));
        		}
        	}
        }
        deleteDirectory(new File(path+"__MACOSX"));
        deleteDirectory(source);
        deleteDirectory(new File(path+"Vision Example"));
        File delete;
        final String[] toDelete={"download.zip",".DS_Store"};
        for(int i=0;i<toDelete.length;i++)
        {
        	delete=new File(path+toDelete[i]);
        	if(delete.exists())
        	{
        		delete.delete();
        	}
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
