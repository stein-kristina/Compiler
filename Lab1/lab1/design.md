## 一.结构描述

### 1.1 总体情况

笔者设计了4个类来实现本次实验。分别是：

1.Show.java:用来与用户交互
2.TaxCalculator.java:接收用户输入计算个人所得税
3.TaxTable.java:保存个人所得税的有关信息，如个人所得税起征点，每级分割金额，每级税率
4.Main.java:主程序部分，用来载入Show.java

### 1.2 Show.java

#### 屏幕输出

这部分代码用于与用于的交互，提示用户应该输入相关选择

```c++
    // 构造函数，初始化税率表、税务计算器和输入扫描器
    public Show() {
        this.taxTable = new TaxTable();
        this.taxCalculator = new TaxCalculator(taxTable);
        this.scanner = new Scanner(System.in);
        System.out.println("A simple Calculator(By:wlz)"); // 输出欢迎信息
    }

    // 显示操作菜单
    public void displayMenu() {
        System.out.println("请选择操作：");
        System.out.println("1. 计算个人所得税");
        System.out.println("2. 修改起征点");
        System.out.println("3. 修改税率的分割金额");
        System.out.println("4. 修改税率表");
        System.out.println("5. 查看个人所得税表");
        System.out.println("6. 退出");
        displaySep(); // 显示分隔线
    }
```

#### 读取用户输入

这部分代码用来读取用户的选择，为了提高通用性，笔者将函数设置为接收一个正整数参数的值，通过这个可以接收1-n的用户输入，并且该部分设置了异常处理，对于小于1或大于n的数字都会判断非法，要求用户重新输入。

```java
 // 获取用户选择的操作编号
    public int getUserChoice(int n) {
        if (n <= 0)
            return -1;// n非法
        int choice = -1;
        while (choice < 1 || choice > n) {
            try {
                System.out.printf("请输入1-%d的数字:", n);
                choice = scanner.nextInt();// 读取数字输入
                if (choice < 1 || choice > n) {// 输入的数字非法
                    System.out.println("输入错误，请重新输入。");
                }
            } catch (Exception e) {// 如果输入的不是数字
                System.out.printf("输入错误,请输入1-%d的数字。\n", n);
                scanner.nextLine();
            }
        }
        return choice;
    }
```

#### 处理用户选择

这部分代码使用一个switch语句来对用户的输入选择进行不同情况的处理，为了化代码，将每个操作都封装成了一个函数(除了退出的6选择和非法情况的提示)。这些处理函数在下面说明。

```java
 // 处理用户选择的操作
    public void handleChoice(int choice) {
        switch (choice) {
            case 1:
                calculateTax();
                break;
            case 2:
                updateThreshold();
                break;
            case 3:
                updateSeparation();
                break;
            case 4:
                updateTaxRate();
                break;
            case 5:
                showTaxTable();// 展示税率信息
                break;
            case 6:
                System.out.println("再见！欢迎下次使用！");
                break;
            default:
                System.out.println("无效的选择！");
                break;
        }
    }
```

#### 计算个人所得税

部分代码要求用户输入收入，并且在代码中包含了异常值的处理(要求用户重新输入)。在得到正确输入后调用``taxCalculator.calculateTax()``函数计算个人所得税

```java
// 计算个人所得税
    public void calculateTax() {
        double income = -1;
        while (income < 0) {
            try {
                System.out.println("请输入您的收入:");
                income = scanner.nextDouble();
                if (income < 0) {
                    System.out.println("收入不能为负数，请重新输入！");
                }
            } catch (Exception e) {
                System.out.println("输入错误，请输入有效的收入！");
                scanner.nextLine();
            }
        }
        double tax = taxCalculator.calculateTax(income);// 计算个人所得税
        System.out.printf("您应缴纳的个人所得税为：%.2f元\n", tax);
        displaySep();
    }
```

#### 更新个人所得税起征点

类似于上面的代码，对异常值输入进行了处理，在得到正常值后调用``taxTable.setThreshold(threshold)``来更新个人所得税起征点。

```java
// 更新个人所得税起征点
    public void updateThreshold() {
        double threshold = -1;
        while (threshold < 0) {
            try {
                System.out.print("请输入新的起征点：");
                threshold = scanner.nextDouble();// 读取输入
                if (threshold < 0) {// 新的数据小于0，异常
                    System.out.println("起征点不能为负数，请重新输入。");
                }
            } catch (Exception e) {// 输入的不是数字
                System.out.println("输入错误，请输入有效的起征点。");
                scanner.nextLine();
            }
        }
        boolean ret = taxTable.setThreshold(threshold);// 更新个人所得税起征点
        if (ret) {// 更新成功
            System.out.printf("起征点已更新为%.2f\n。", threshold);
        } else {// 程序可能出现编写错误
            System.out.println("请检查程序是否编写错误!");
        }
        displaySep();
    }
```

#### 更新税率

这部分代码包括对输入税率的异常处理。并且允许用户修改单个税率或者所有税率updateAlITaxRate()设计用来修改所有税率，而updateNthTaxRate()用来处理修改单个税率。处理的方式也和上面类似(合法的税率应该在0-1之间)，在对输入值异常处理后得到合法的税率调用taxTable.setTaxRate(newtaxRate)或taxTable.setNthTaxRate(n,newTaxRate)修改税率。因为在现实情况下，更高级的税率应该要比更低级的税率高，因此在taxTable的税率更新函数中要求税率更新后是逐级递增的。因此修改有可能失败，这时候会返回-1，程序可以根据这个返回值确定是否成功修改税率

```java
// 修改单个税率
    public void updateNthTaxRate() {
        int taxRankNum = taxTable.getTaxRankNum();
        double newTaxRate = -1;
        int n = -1;
        // 读取修改位置
        while (n < 1 || n > taxRankNum) {
            try {
                System.out.printf("请输入要修改税率(1-%d)的位置：", taxRankNum);
                n = scanner.nextInt();// 读取数字输入
                if (n < 1 || n > taxRankNum) {// 输入的数字非法
                    System.out.println("输入错误，请重新输入。");
                }
            } catch (Exception e) {// 如果输入的不是数字
                System.out.printf("输入错误，请输入1-%d的数字。\n", taxRankNum);
                scanner.nextLine();
            }
        }
        // 读取修改税率
        while (newTaxRate <= 0 || newTaxRate > 1) {
            try {
                System.out.printf("请输入要修改的税率：");
                newTaxRate = scanner.nextDouble();// 读取数字输入
                if (newTaxRate <= 0 || newTaxRate > 1) {// 输入的数字非法
                    System.out.println("输入错误，请重新输入。");
                }
            } catch (Exception e) {// 如果输入的不是数字
                System.out.printf("输入错误，请输入0~1的浮点数。\n");
                scanner.nextLine();
            }
        }
        int res = taxTable.setNthTaxRate(n, newTaxRate);
        if (res != -1) {
            System.out.printf("第%d级税率已经修改为%.2f\n", n, newTaxRate);
        } else {
            System.out.println("请检查程序是否编写错误!");
        }
    }

    // 更新所有税率
    public void updateAllTaxRate() {
        int taxRankNum = taxTable.getTaxRankNum();// 获取税率级数
        double[] newtaxRate = new double[taxRankNum];// 存放税率的数组
        System.out.printf("请输入%d个新税率，你需要保证是单调上升的\n", taxRankNum);
        int succeed = -1;
        while (succeed == -1) {
            int i = 0;
            while (i < taxRankNum) {
                try {
                    newtaxRate[i] = scanner.nextDouble();// 读取输入
                    if (newtaxRate[i] <= 0 || newtaxRate[i] > 1) {// 输入小于等于0或大于1，异常
                        System.out.println("税率不能为负数且不能大于1，请重新输入。");
                    } else {
                        ++i;
                    }
                } catch (Exception e) {// 输入不是数字
                    System.out.println("输入错误，请输入有效的税率。");
                    scanner.nextLine();
                }
            }
            succeed = taxTable.setTaxRate(newtaxRate);
            if (succeed == -1) {// 输入的序列不是单调上升，被拒绝更新
                System.out.println("您输入的税率序列并不是单调上升的，请重新输入！");
            }
        }
        System.out.println("税率已经全部更新成功!");
    }
```

#### 更新分割金额

这部分处理和更新税率类似，只是对分割金额的异常判断稍微修改了下。并且要保证第一级分割金额保证为0(在笔者的实现中，第一级分割金额是从个人所得税起征点开始的，如果设置大于0，将会有一部分金额没有被计算个人所得税。因为个人所得税起征点的意义就是从该金额开始应该收税了，如果第一级分割金额不为0则违反了个人所得税起征点的实际意义)

```java
// 修改分割金额
    public void updateSeparation() {
        System.out.println("请选择操作：");
        System.out.println("1. 修改单个分割金额");
        System.out.println("2. 修改全部分割金额");
        displaySep();
        int choice = getUserChoice(2);// 读取用户选择
        switch (choice) {
            case 1:
                updateNthSeparation();// 修改单个分割金额
                break;
            case 2:
                updateAllSeparation();// 修改全部分割金额
                break;
            default:
                System.out.println("无效的选择！");
        }
        displaySep();
    }

    // 修改单个分割金额
    public void updateNthSeparation() {
        int taxRankNum = taxTable.getTaxRankNum();// 获取税率级数
        double newSeparation = -1;// 新分割金额
        int n = -1;// 修改的位置
        // 读取修改位置
        while (n <= 1 || n > taxRankNum)// 修改位置非法
        {
            try {
                System.out.printf("请输入要修改金额(2-%d)的位置：", taxRankNum);
                n = scanner.nextInt();// 读取数字输入
                if (n < 1 || n > taxRankNum) {// 输入的数字非法
                    System.out.println("输入错误，请重新输入。");
                }
                if (n == 1) {
                    System.out.println("禁止修改第一级分割金额(第一级分割金额固定为)");// 第一级分割金额固定为0
                }
            } catch (Exception e) {// 如果输入的不是数字
                System.out.printf("输入错误，请输入2-%d的数字。\n", taxRankNum);
                scanner.nextLine();
            }
        }
        // 读取新的分割金额
        while (newSeparation < 0) {// 分割金额小于0，非法
            try {
                System.out.print("请输入分割金额：");
                newSeparation = scanner.nextDouble();// 读取数字输入
                if (newSeparation < 0) {// 输入的数字非法
                    System.out.println("输入错误，请重新输入。");
                }
            } catch (Exception e) {// 如果输入的不是数字
                System.out.printf("输入错误，请输入正数。\n");
                scanner.nextLine();
            }
        }
        int res = taxTable.setNthSeparation(n, newSeparation);
        if (res != -1) {
            System.out.printf("第%d级分割金额已经修改为%.2f\n", n, newSeparation);
        } else {
            System.out.println("请检查程序是否编写错误!");
        }
    }

    // 修改全部分割金额
    public void updateAllSeparation() {
        int taxRankNum = taxTable.getTaxRankNum();// 获取税率级数
        double[] newSeparation = new double[taxRankNum];// 存放分割金额的数组
        System.out.printf("请输入%d个新分割金额，你需要保证是单调上升的\n", taxRankNum);
        int succeed = -1;
        while (succeed == -1) {
            int i = 0;
            while (i < taxRankNum) {
                try {
                    newSeparation[i] = scanner.nextDouble();// 读取输入
                    if (newSeparation[i] < 0) {// 分割金额异常
                        System.out.println("分割不能为负数，请重新输入。");
                    } else {
                        if (i == 0 && newSeparation[i] != 0) {
                            System.out.println("第一级分割金额必须为0，请重新输入。");
                            continue;
                        }
                        ++i;
                    }
                } catch (Exception e) {
                    System.out.println("输入错误，请输入有效的分割金额。");
                    scanner.nextLine();
                }
            }
            succeed = taxTable.setSeparation(newSeparation);// 修改所有分割金额
            if (succeed == -1) {// 修改失败，说明不是单调上升的
                System.out.println("您输入的分割金额序列并不是单调上升的，请重新输入！");
            }
        }
        System.out.println("分割金额已经全部更新成功!");
    }
```

#### 展示个人所得税信息

这部分代码用于获取个人所得税的有关信息，通过taxTable.getThreshold()，taxTable.getSeparation()和taxTable.getTaxRate()可以获取个人所得税起征点，分割金额和各级税率。将它们逐个输出即可。

```java
 // 展示税率信息
    public void showTaxTable() {
        System.out.printf("当前个人所得税起征点为%.2f\n", taxTable.getThreshold());
        double[] separation = taxTable.getSeparation();
        double[] taxRate = taxTable.getTaxRate();
        for (int i = 0; i < taxRate.length; i++) {
            if (i == taxRate.length - 1) {
                // 最后一位特殊打印
                System.out.printf("超过%.2f元的税率为%.2f\n", separation[i], taxRate[i]);
            } else {
                System.out.printf("超过%.2f到%.2f元税率为%.2f\n", separation[i], separation[i + 1], taxRate[i]);
            }
        }
        displaySep();
    }
```

### 1.3 TaxCalculator.java

这部分代码用于计算个人所得税。核心思想是遍历每一个税收段，在每个税收段中增加的收入应该是本段的税率乘上本段可用的金额，而本段可用的金额要么是前后两端分割金额差，要么是剩余金额(剩余金额不足以跨过这一段，此时所有金额都计算了个人所得税)。

```java
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
```

### 1.4 TaxTable.java

#### 初始化

这部分代码用于对本类的初始化，初始化初始化的数据从代码中可以看出

```java
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
```

#### 获取和更新个人所得税

这部分代码用于获取和更新个人所得税。在修改个人所得税的输入部分，其实已经保证了输入的合理性，但是为了分离两部分的异常处理，因此笔者在这部分代码仍然保留了异常处理的代码。

```java
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
```

#### 剩余部分

考虑到篇幅以及这次任务的难度，笔者觉得剩余部分简单写写就好，不辛苦助教了。

剩余部分的功能包括获取税率级数`rankNum`、分割金额`separation`、税率`TaxTable`以及他们的设置。理论上不需要对这些部分的修改再做异常处理，因为在输入的时候已经确保了输入是合法的。但是笔者认为输入和实现部分代码的关联性相对较小，因此在具体的实现部分仍然保留了异常处理，如果出现错误则返回-1，部分函数是返回false。

### 1.5 Main.java

这部分是主程序部分，用来载入Show.java与用户交互，调用Show.java的有改观函数来实现与用户的交互，并判断何时终止程序

```java
package hw1;

public class Main {
	public static void main(String[] args) {
		Show show = new Show();
		int choice;
		do{
			show.displayMenu();//打印菜单
			choice = show.getUserChoice(6);//获取用户的输入
			show.handleChoice(choice);//处理用户输入
		}while(choice != 6);
	}
}
```

## 二、结果测试

### 2.1 计算个人所得税

点击`start.bat`，输入1,50000

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301112239685.png" alt="image-20240301112239685" style="zoom: 67%;" />

结果与样例一样

### 2.2 查看税表信息

输入5，会显示当前税表

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301112410325.png" alt="image-20240301112410325" style="zoom:67%;" />

### 2.3 修改起征点

输入2修改起征点，再输入6000，最后输入5查看税表信息

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301112619582.png" alt="image-20240301112619582" style="zoom:67%;" />

### 2.4 修改分割金额

输入3，再输入1选择修改单个分割金额，再输入3选择第三级，把第三级分割金额从12000改成15000，输入5验证

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301113052797.png" alt="image-20240301113052797" style="zoom:67%;" />

### 2.5 修改税率

输入4修改第三级税率为0.18，再输入5打印税表验证

![image-20240301144337797](C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301144337797.png)

### 2.6 再一次计算个人所得税

输入1，再输入金额88000，应缴税金额为：

1. 88000-6000=82000
2. 3000\*0.03+12000\*0.1+10000\*0.18+10000\*0.25+20000\*0.3+25000\*0.35+2000*0.45=21240

与程序计算出的结果一致

![image-20240301144310144](C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301144310144.png)

### 2.7 修改所有分割金额

输入3，选择2，再输入所有分割金额，分割金额一共7个，按序输入：0,1000,2000,3000,4000,5000，6000，最后输入5验证

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301145456280.png" alt="image-20240301145456280" style="zoom:80%;" />

修改成功

### 2.8 修改所有税率

 输入4，选择2，再依次输入税率0.05,0.1,0.2,0.3,0.4,0.5,0.6，最后输入5验证

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301150300494.png" alt="image-20240301150300494" style="zoom: 80%;" />

### 2.9 最后一次计算个人所得税

这一次计算初始金额为20000的个人所得税：

1. 20000-6000=14000
2. 1000*0.05+1000\*0.1+1000\*0.2+1000\*0.3+1000\*0.4+1000\*0.5+8000\*0.6=6350

与程序计算的结果一致

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301151718954.png" alt="image-20240301151718954" style="zoom:80%;" />

至此，笔者可以认为程序的逻辑是正确的，衔接是紧密无误的。接下来就要测试程序的健壮性。

## 三、异常检测

### 3.1 负数起征点

当笔者输入了-100的起征点时，程序检测到并提醒重新输入，再输入200后修改成功

![image-20240301152515778](C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301152515778.png)

### 3.2 错误的分割金额

支持检测两种错误，一种是不满足分割金额单调上升，一种是金额为负数

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301153444261.png" alt="image-20240301153444261" style="zoom:80%;" />

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301153654043.png" alt="image-20240301153654043" style="zoom:80%;" />

### 3.3 错误的税率

支持检测两种错误，一种是不满足税率单调上升（有钱人肯定交的税更多啊），一种是金额为负数或>=1的情况

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301153812098.png" alt="image-20240301153812098" style="zoom:80%;" />

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301154141598.png" alt="image-20240301154141598" style="zoom:80%;" />

### 3.4 选择错误

输入的数字不在选择范围之内

<img src="C:\Users\Amadeus\Desktop\大三下\编译原理\实验\Lab1\lab1\design.assets\image-20240301154230193.png" alt="image-20240301154230193" style="zoom:80%;" />



