package hw1;

//税收计算器
public class TaxCalculator {
	private final TaxTable taxTable;

	public TaxCalculator(TaxTable taxTable) {
		this.taxTable = taxTable;
	}
	
	//计算个人所得税
	public double calculateTax(double income) {
		double taxAbleIncome = income - taxTable.getThreshold();//减去起征点
		if(taxAbleIncome <= 0) {//没有超过起征点，个人所得税为0
			return 0;
		}
		
		double tax = 0;//个人所得税总和
		double[] taxRate = taxTable.getTaxRate();//税率
		double[] separation=taxTable.getSeparation();//分割金额
		
		for(int i=0;i<taxRate.length;i++) {
			double taxRateIncome = taxRate[i];//当前一级税率
			double taxRatePercentage = separation[i];//当前一级分割金额
			
			if(i==taxRate.length-1) {//最后一级税率直接乘即可
				tax += taxAbleIncome * taxRateIncome;
				break;
			}
			else {
				double nextTaxRatePercentage = separation[i+1];//获取下一级分割金额
				//这一级能够计算税的金额是剩余金额和两级分割金额差的最小值
				double incomeInThisRate = Math.min(taxAbleIncome, nextTaxRatePercentage-taxRatePercentage);
				//计算本级应得的税收
				tax += incomeInThisRate * taxRateIncome;
				//更新剩余金额
				taxAbleIncome -= incomeInThisRate;
				
				if(taxAbleIncome <= 0) {
					//所有金额都计算过，退出
					break;
				}
			}
		}
		return tax;//返回个人所得税
	}
}
