package Vision;

public class ConfigResultScore {
	public static final int TargetCountDiff_Weight = 10000;
	
	public static final int TargetScore_Weight = 6000;
	public static final int TargetIndex_Weight = 200;
	public static final int TargetFillFactor_Weight = 50;
	public static final int TargetHwRation_Weight = 20;

	public String m_source = "ConfigScore";
	
	public double m_score = 0;
	int m_targetDiff = 0;
	double m_targetScoreError = 0;
	double m_targetIndexError = 0;
	double m_fillFactorError = 0;
	double m_hwRatioError = 0;
	

	@Override
	public String toString()
    {
		return GenerateString();
    }
	
	
	String GenerateString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(m_source);
		builder.append(":: Score="+TraceLog.Round3(m_score));
		builder.append(", targetDiff="+TraceLog.Round3(m_targetDiff));
		builder.append(", targetScoreError="+TraceLog.Round3(m_targetScoreError));
		builder.append(", targetIndexError="+TraceLog.Round3(m_targetIndexError));
		builder.append(", fillFactorError="+TraceLog.Round3(m_fillFactorError));
		builder.append(", hwRatioError="+TraceLog.Round3(m_hwRatioError));
		return builder.toString();
	}
	
	
	public void SumUp(ConfigResultScore other)
	{
		m_score += other.m_score;
		m_targetDiff += other.m_targetDiff;
		m_targetScoreError += other.m_targetScoreError;
		m_targetIndexError += other.m_targetIndexError;
		m_fillFactorError += other.m_fillFactorError;
		m_hwRatioError += other.m_hwRatioError;
	}
	
	public void Evaluate(ProcessResult result, int expectedTarget) {
		m_targetDiff = Math.abs(expectedTarget - result.m_targetCount);
		m_score = m_targetDiff * TargetCountDiff_Weight;
		
		if (result.m_targetCount == 2){
			m_targetScoreError = (result.m_targetLeft.m_score 
					+ result.m_targetRight.m_score)
					* TargetScore_Weight;
			m_score += m_targetScoreError;
			
			m_targetIndexError = (result.m_targetLeft.m_index
					+ result.m_targetRight.m_index)
					* TargetIndex_Weight;
			m_score += m_targetIndexError;
			
			m_fillFactorError = Math.abs(result.m_targetLeft.m_fillFactorScore
					+ result.m_targetRight.m_fillFactorScore)
					* TargetFillFactor_Weight;
			m_score += m_fillFactorError;
			
			m_hwRatioError = Math.abs(result.m_targetLeft.m_hwRatioScore
					+ result.m_targetRight.m_hwRatioScore)
					* TargetHwRation_Weight;
			m_score += m_hwRatioError;
		}

		if (result.m_targetCount == 1){
			Target target = result.m_targetLeft;
			if (null != result.m_targetRight){
				target = result.m_targetRight;
			}
			
			m_targetScoreError = target.m_score * TargetScore_Weight * 2;
			m_score += m_targetScoreError;
			
			m_targetIndexError = target.m_index * TargetIndex_Weight * 2;
			m_score += m_targetIndexError;
			
			m_fillFactorError = Math.abs(target.m_fillFactorScore) * TargetFillFactor_Weight * 2;
			m_score += m_fillFactorError;
			
			m_hwRatioError = Math.abs(target.m_hwRatioScore) * TargetHwRation_Weight * 2;
			m_score += m_hwRatioError;
		}

	}
}
