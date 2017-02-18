package piCode;

public class VisionProcessing {

	public static void main(String[] args) 
	{
		Search search=new Search();
		search.initializeTable();
		while(true)
		{
			search.getNextCommand();
			if(search.processCommand())
			{
				break;
			}
		}
	}

}
