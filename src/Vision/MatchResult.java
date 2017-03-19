package Vision;

public class MatchResult {
		public boolean m_match = false;
		
		public double m_areaCompare = 0;
		public boolean m_areaCheck = false;
		
		public double m_widthCompare = 0;
		public boolean m_widthCheck = false;
		
		public double m_heightCompare = 0;
		public boolean m_heightCheck = false;
		
		public double m_horizontalCompare = 0;
		public boolean m_horizontalCheck = false;
		
		public double m_verticalCompare = 0;
		public boolean m_verticalCheck = false;
		
		public static final int InvalidTargetIndex = 10000;
		public int m_leftIndex = InvalidTargetIndex;
		public int m_rightIndex = InvalidTargetIndex;
		String m_string= "";

		
		@Override
		public String toString()
	    {
			if (m_string.isEmpty())
			{
				GenerateString();
			}
			return m_string;
	    }
		
		
		void GenerateString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("left="+m_leftIndex);
			builder.append(", right="+m_rightIndex);
			builder.append(", match="+m_match);
			
			builder.append(", areaCompare="+TraceLog.Round2(m_areaCompare));
			builder.append(", areaCheck="+m_areaCheck);
			
			builder.append(", widthCompare="+TraceLog.Round2(m_widthCompare));
			builder.append(", widthCheck="+m_widthCheck);
			
			builder.append(", heightCompare="+TraceLog.Round2(m_heightCompare));
			builder.append(", heightCheck="+m_heightCheck);

			builder.append(", horizontalCompare="+TraceLog.Round2(m_horizontalCompare));
			builder.append(", horizontalCheck="+m_horizontalCheck);
			
			builder.append(", verticalCompare="+TraceLog.Round2(m_verticalCompare));
			builder.append(", verticalCheck="+m_verticalCheck);
		}
}
