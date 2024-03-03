package hw1;

public class TaxTable {
    private int rankNum;//税率分界点个数
    private double threshold;// 个人所得税起点
    private double[] taxRate;// 税率
    private double[] separation;// 每一级税率的分割金额

    // 初始化函数
    public TaxTable() {
        threshold = 5000;
        rankNum = 7;
        separation = new double[] { 0, 3000, 12000, 25000, 35000, 55000, 80000 };
        taxRate = new double[] { 0.03, 0.1, 0.2, 0.25, 0.3, 0.35, 0.45 };
    }

    // 获取个人所得税起点
    public double getThreshold() {
        return this.threshold;
    }

    // 设置个人所得税起征点
    public boolean setThreshold(double newThreshold) {
        // 小于0，非法输入
        if (newThreshold < 0)
            return false;
        this.threshold = newThreshold;
        return true;
    }

    // 获取税率级数
    public int getTaxRankNum() {
        return this.rankNum;
    }

    // 获取每一级税率的分割金额
    public double[] getSeparation() {
        return this.separation;
    }

    // 获取每一级税率
    public double[] getTaxRate() {
        return this.taxRate;
    }

    // 设置税率
    public int setTaxRate(double[] newTaxRate) {
        for (int i = 1; i < newTaxRate.length; i++) {
            // 如果新的税率不是单调上升，返回-1
            if (newTaxRate[i - 1] > newTaxRate[i])
                return -1;
            // 如果i-1个税率不在(0,1]，则非法
            if (newTaxRate[i - 1] <= 0 || newTaxRate[i-1] > 1)
                return -1;
        }
        // 如果最后一个税率不在(0,1]，则非法
        if (newTaxRate.length >= 1)
            if (newTaxRate[newTaxRate.length - 1] <= 0 || newTaxRate[newTaxRate.length - 1] > 1) {
                return -1;
            }
        this.taxRate = newTaxRate;
        return 0;
    }

    // 设置税率级数
    public int setTaxRankNum(int newTaxRankNum) {
        // 如果税率级数小于等于0，属于异常情况，返回错误
        if (newTaxRankNum <= 0)
            return -1;
        this.rankNum = newTaxRankNum;
        return 0;
    }

    // 设置分割金额
    public int setSeparation(double[] newSeparation) {
        for (int i = 1; i < newSeparation.length; ++i) {
            // 如果新的分割金额不是单调上升，返回错误
            if (newSeparation[i] <= newSeparation[i - 1])
                return -1;
            // 如果i-1个分割金额小于0，则非法
            if (newSeparation[i - 1] < 0)
                return -1;
        }
        // 如果最后一个分割金额小于0，则非法
        if (newSeparation.length >= 1) {
            double lastNewSeparation = newSeparation[newSeparation.length - 1];
            if (lastNewSeparation < 0)
                return -1;
        }
        this.separation = newSeparation;
        return 0;
    }

    // 设置某一级分割金额
    public int setNthSeparation(int n, double newSeparation) {
        // 修改位置异常
        if (n <= 0 || n >= this.separation.length)
            return -1;
        // 修改分割金额异常
        if (newSeparation < 0)
            return -1;
        // 如果修改的分割金额比前面的任意一级小属于非法情况
        for (int i = 1; i < n - 1; i++) {
            if (this.separation[i] >= newSeparation)
                return -1;
        }
        this.separation[n - 1] = newSeparation;
        return 0;
    }

    // 设置某一级税率
    public int setNthTaxRate(int n, double newTaxRate) {
        // 修改位置异常
        if (n <= 0 || n >= this.taxRate.length)
            return -1;
        // 修改税率异常
        if (newTaxRate <= 0 || newTaxRate > 1)
            return -1;
        // 如果修改的税率比任意前一级小属于非法情况
        for (int i = 1; i < n - 1; i++) {
            if (this.taxRate[i] > newTaxRate)
                return -1;
        }
        this.taxRate[n - 1] = newTaxRate;
        return 0;
    }

}
