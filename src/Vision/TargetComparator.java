package Vision;

import java.util.Comparator;
import java.util.Collections;

@SuppressWarnings("hiding")
public class TargetComparator implements Comparator<Target> {
	public int compare(Target target1, Target target2){
		double value1= target1.ratioScore();
		double value2=target2.ratioScore();
		if (value1 < value2){
			return -1;
		} 
		
		if (value1 == value2) {
			return 0;
		}
		 
		return 1;
	}
	
	

}



