package Vision;

import java.util.Comparator;


public class TargetScoreComparator implements Comparator<Target> {

	public int compare(Target target1, Target target2){
		double value1= target1.m_score;
		double value2=target2.m_score;

		if (value1 < value2){
			return -1;
		} 
		

		if (value1 == value2) {
			return 0;
		}

		return 1;
	}
}