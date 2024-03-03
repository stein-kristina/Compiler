package hw1;

import java.util.Scanner;

// 显示税务信息和操作菜单的类
public class Show {
    private final TaxTable taxTable; // 税率表对象
    private final TaxCalculator taxCalculator; // 税务计算器对象
    private final Scanner scanner; // 输入扫描器对象

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
            System.out.printf("起征点已更新为%.2f。\n", threshold);
        } else {// 程序可能出现编写错误
            System.out.println("请检查程序是否编写错误!");
        }
        displaySep();
    }

    // 更新税率表
    public void updateTaxRate() {
        System.out.println("请选择操作：");
        System.out.println("1. 修改单个税率");
        System.out.println("2. 修改全部税率");
        displaySep();
        int choice = getUserChoice(2);// 读取用户选择
        switch (choice) {
            case 1:
                updateNthTaxRate();// 修改单个税率
                break;
            case 2:
                updateAllTaxRate();// 修改全部税率
                break;
            default:
                System.out.println("无效的选择！");
        }
        displaySep();
    }

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
            System.out.println("修改失败！税率不满足单调上升条件!");
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
            System.out.println("修改失败！分割金额不满足单调上升条件!");
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

    // 显示分隔线
    public void displaySep() {
        System.out.println("--------------------");
    }
}
