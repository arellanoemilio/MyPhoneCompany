import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Interface {
	
	private static Scanner scan = new Scanner(System.in);
	
	public static String getLoginInfo(){
		System.out.print("Enter Password For ema218: ");
		String password = scan.next(); 
		return password;
	}
	
	
	public static void main(String[] args){
		scan = new Scanner(System.in);
		Connection con=null;
//		
//		Calendar calendar = Calendar.getInstance();
//		System.out.println(calendar.get(Calendar.MONTH));
		boolean wrongPassword = true;
		Statement s = null;
		
		// Login
		while(wrongPassword)
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				String loginInfo = getLoginInfo();
				con =  DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",
						"ema218", loginInfo);
				s = con.createStatement();
				wrongPassword = false;
			} catch (ClassNotFoundException e1) {
			} catch (SQLException e) { 
				System.out.println("Your username and Password don't match. plase try again");
			}
		boolean quit = false;
		while(!quit){
			System.out.println("Select the number of the interface you wish to enter.\n"
					+ "1) Interactive\n"
					+ "2) Billing Engine\n"
					+ "3) Exit program");
			int selection;
			try{
				selection = scan.nextInt();
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter one of the valid integers.");
				continue;
			}
			switch(selection){
			case 1:
				InteractiveInterface.run(con,s);
				break;
			case 2:
				BillingEngine.run(con,s);
				break;
			case 3:
				quit = true;
				break;
			default:
				System.out.println("Please enter one of the valid integers.");
			}
		}
		System.out.println("Thank you for using Emilio Arellano's Jog interfaces");
		try {
			s.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
