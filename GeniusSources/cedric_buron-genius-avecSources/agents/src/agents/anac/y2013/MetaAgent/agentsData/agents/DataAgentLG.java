package agents.anac.y2013.MetaAgent.agentsData.agents;

import agents.anac.y2013.MetaAgent.agentsData.AgentData;


public class DataAgentLG extends AgentData {
	String data="Node number 1: 10080 observations,    complexity param=0.02493494\n  mean=0.0729892, MSE=0.01801301 \n  left son=2 (4620 obs) right son=3 (5460 obs)\n  Primary splits:\n      DomainSize   < 498       to the left,  improve=0.02493494, (0 missing)\n      numOfIssues  < 4.5       to the left,  improve=0.02216970, (0 missing)\n      WeightStdev  < 0.2608656 to the right, improve=0.02210477, (0 missing)\n      AvgUtilStdev < 0.2020739 to the right, improve=0.01452261, (0 missing)\n      AvgSize      < 3.708333  to the left,  improve=0.01331829, (0 missing)\n  Surrogate splits:\n      numOfIssues    < 4.5       to the left,  agree=0.917, adj=0.818, (0 split)\n      AvgSize        < 3.875     to the left,  agree=0.833, adj=0.636, (0 split)\n      AvgUtil        < 0.6061409 to the right, agree=0.729, adj=0.409, (0 split)\n      AvgUtilStdev   < 0.1994493 to the right, agree=0.708, adj=0.364, (0 split)\n      RelevantStdevU < 0.1533694 to the right, agree=0.688, adj=0.318, (0 split)\n\nNode number 2: 4620 observations,    complexity param=0.01568038\n  mean=0.04994972, MSE=0.01815066 \n  left son=4 (210 obs) right son=5 (4410 obs)\n  Primary splits:\n      WeightStdev      < 0.2608656 to the right, improve=0.033952270, (0 missing)\n      ReservationValue < 0.125     to the right, improve=0.015317570, (0 missing)\n      DiscountFactor   < 0.875     to the left,  improve=0.014254130, (0 missing)\n      AvgUtilStdev     < 0.2020739 to the right, improve=0.006667712, (0 missing)\n      DomainSize       < 154       to the left,  improve=0.005572263, (0 missing)\n\nNode number 3: 5460 observations,    complexity param=0.01257387\n  mean=0.09248414, MSE=0.01706732 \n  left son=6 (4620 obs) right son=7 (840 obs)\n  Primary splits:\n      AvgSize          < 5.728571  to the left,  improve=0.024499520, (0 missing)\n      RelevantEU       < 0.459599  to the right, improve=0.012823110, (0 missing)\n      numOfIssues      < 7.5       to the right, improve=0.011076810, (0 missing)\n      DomainSize       < 289392.5  to the right, improve=0.011076810, (0 missing)\n      ReservationValue < 0.125     to the right, improve=0.009895196, (0 missing)\n  Surrogate splits:\n      DomainSize < 64260.5   to the left,  agree=0.923, adj=0.50, (0 split)\n      RelevantEU < 0.459599  to the right, agree=0.885, adj=0.25, (0 split)\n\nNode number 4: 210 observations\n  mean=-0.06381047, MSE=0.02340217 \n\nNode number 5: 4410 observations\n  mean=0.05536687, MSE=0.01725499 \n\nNode number 6: 4620 observations\n  mean=0.08376487, MSE=0.0161764 \n\nNode number 7: 840 observations\n  mean=0.1404401, MSE=0.01924947 \n\n";
	public String getText() {
		return data;
	}
}
