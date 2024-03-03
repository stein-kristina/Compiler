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
