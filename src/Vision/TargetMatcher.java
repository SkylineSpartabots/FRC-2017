package Vision;

public class TargetMatcher {
		
	public static final double AreaRatioLimit = 2;
	public static final double SideRatioLimit = 1.5;
	public static final double VerticalRatioLimit = 0.25;
	
	public static MatchResult Match2Target(Target target1, Target target2)
	{
		if (target1.m_x < target2.m_x){
			return MatchLeftRight(target1, target2);
		}
		else{
			return MatchLeftRight(target2, target1);
		}
	}	
	
	public static MatchResult MatchLeftRight(Target targetLeft, Target targetRight)
	{
		MatchResult result = new MatchResult();

		result.m_leftIndex = targetLeft.m_index;
		result.m_rightIndex = targetRight.m_index;
		
		result.m_areaCompare = 1.0 * targetLeft.m_area / targetRight.m_area;
		if (result.m_areaCompare < 1) {
			result.m_areaCompare = 1 / result.m_areaCompare;
		}
		result.m_areaCheck= result.m_areaCompare < AreaRatioLimit;

		
		result.m_widthCompare = 1.0 * targetLeft.m_width / targetRight.m_width;
		if (result.m_widthCompare < 1) {
			result.m_widthCompare = 1.0 / result.m_widthCompare;
		}
		result.m_widthCheck = result.m_widthCompare < SideRatioLimit;

		
		result.m_heightCompare = 1.0 * targetLeft.m_height / targetRight.m_height;
		if (result.m_heightCompare < 1) {
			result.m_heightCompare = 1.0 / result.m_heightCompare;
		}
		result.m_heightCheck = result.m_heightCompare < SideRatioLimit;
		
		result.m_horizontalCompare = 1.0 *(targetRight.m_centerX - targetLeft.m_centerX) 
				/ ((1.0*targetRight.m_width + targetLeft.m_width)/2)
				/ (Target.TargetSideDistance /Target.TargetWidth);
		
		if (result.m_horizontalCompare < 1) {
			result.m_horizontalCompare = 1.0 / result.m_horizontalCompare;
		}
		result.m_horizontalCheck = result.m_horizontalCompare < SideRatioLimit;
		
		result.m_verticalCompare = 1.0 * (targetRight.m_centerY - targetLeft.m_centerY) 
				/ ((targetRight.m_height + targetLeft.m_height)/2);
		if (result.m_verticalCompare < 0) {
			result.m_verticalCompare = -result.m_verticalCompare;
		}
		result.m_verticalCheck = result.m_verticalCompare < VerticalRatioLimit;
		
		result.m_match = result.m_areaCheck 
				&& result.m_widthCheck 
				&& result.m_heightCheck 
				&& result.m_horizontalCheck 
				&& result.m_verticalCheck;
		
		return result;
	}

}
