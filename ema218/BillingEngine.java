import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BillingEngine {
	private static Scanner scan = new Scanner(System.in);
	
	//done
	public static void run(Connection con, Statement s) {	
		boolean quit = false;
		while(!quit){
			System.out.println("Welcome to Jogs Billing Engine.\n"
				+ "1)Retreive Billing Statement for Specific Account\n"
				+ "2)Calculate every Accounts billing Statmente\n");
			int selection;
			try{
				selection = scan.nextInt();
				scan.nextLine();
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter one of the valid integers.");
				continue;
			}
			switch(selection){
			case 1:
				runIndividual(con,s);
				break;
			case 2:
				runAll(con,s);
				break;
			case 3:
				quit = true;
				break;
			default:
				System.out.println("Please enter one of the valid integers.");
			}
		}
	
	
	}

	//done
	private static void runAll(Connection con, Statement s) {
		BillingStatement bs = null;
		int month = getMonth();
		int year = getYear();
		Calendar calendar = Calendar.getInstance();
		if((year > calendar.get(Calendar.YEAR)) || (year == calendar.get(Calendar.YEAR) && month > (1+calendar.get(Calendar.MONTH)))){
			System.out.println("Cannot create Billing Statement for future dates.");
			return;
		}
		int max = 0;
		
		String maxAccount = "select max(a_id) from account";
		try{
			ResultSet r = s.executeQuery(maxAccount);
			r.next();
			max = r.getInt(1);
		}catch(SQLException e){
			System.out.println("could not find max accout");
		}
		for(int i = 1; i <= max; i++){
			Account account = Account.getAccountWithId(i, con, s);
			bs = getSavedBillingStatement(account,month,year,con,s);
			if(bs != null && (year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH))){
				BillingPlan bp = BillingPlan.getBillingPlanForAccount(account,con,s);
				int secondsCalled = getSecondCalls(account.Id,month,year,con,s);
				int textsSent = getTextsSent(account.Id,month,year,con,s);
				int bytes = getBytesUsed(account.Id,month,year,con,s);
				double costOfPhonesBought = getCostOfPhonesBought(account.Id,month,year,con,s);
				double total = costOfPhonesBought+bp.monthly+(secondsCalled/60)*bp.call+textsSent*bp.text+bytes*bp.bytes;
				bs = new BillingStatement(account.Id,bp.name,month,year,total);
				BillingPlan.updateTotalAndBillingName(bs,con,s);
			}else if(bs == null){
				BillingPlan bp = BillingPlan.getBillingPlanForAccount(account,con,s);
				int secondsCalled = getSecondCalls(account.Id,month,year,con,s);
				int textsSent = getTextsSent(account.Id,month,year,con,s);
				int bytes = getBytesUsed(account.Id,month,year,con,s);
				double costOfPhonesBought = getCostOfPhonesBought(account.Id,month,year,con,s);
				double total = costOfPhonesBought+bp.monthly+(secondsCalled/60)*bp.call+textsSent*bp.text+bytes*bp.bytes;
				bs = new BillingStatement(account.Id,bp.name,month,year,total);
				BillingStatement.insert(bs,con,s);
				
			}
			System.out.println(bs);
			System.out.print("\n------------------------------------\n");
		}
		
	}

	//done
	private static void runIndividual(Connection con, Statement s) {
		BillingStatement bs = null;
		Account account = getAccount(con,s);
		BillingPlan bp = BillingPlan.getBillingPlanForAccount(account,con,s);
		int month = getMonth();
		int year = getYear();
		Calendar calendar = Calendar.getInstance();
		if((year > calendar.get(Calendar.YEAR)) || (year == calendar.get(Calendar.YEAR) && month > (1+calendar.get(Calendar.MONTH)))){
			System.out.println("Cannot create Billing Statement for future dates.");
			return;
		}
		bs = getSavedBillingStatement(account, month,year,con,s);
		if(bs != null && (year == calendar.get(Calendar.YEAR) && month == (1+calendar.get(Calendar.MONTH)))){
			int secondsCalled = getSecondCalls(account.Id,month,year,con,s);
			int textsSent = getTextsSent(account.Id,month,year,con,s);
			int bytes = getBytesUsed(account.Id,month,year,con,s);
			double costOfPhonesBought = getCostOfPhonesBought(account.Id,month,year,con,s);
			double total = costOfPhonesBought+bp.monthly+(secondsCalled/60)*bp.call+textsSent*bp.text+bytes*bp.bytes;
			bs = new BillingStatement(account.Id,bp.name,month,year,total);
			BillingPlan.updateTotalAndBillingName(bs,con,s);
		}else if(bs == null){
			int secondsCalled = getSecondCalls(account.Id,month,year,con,s);
			int textsSent = getTextsSent(account.Id,month,year,con,s);
			int bytes = getBytesUsed(account.Id,month,year,con,s);
			double costOfPhonesBought = getCostOfPhonesBought(account.Id,month,year,con,s);
			double total = costOfPhonesBought+bp.monthly+(secondsCalled/60)*bp.call+textsSent*bp.text+bytes*bp.bytes;
			bs = new BillingStatement(account.Id,bp.name,month,year,total);
			BillingStatement.insert(bs,con,s);
			System.out.println(bs);
		}
		

	}

	//done
	private static BillingStatement getSavedBillingStatement(Account account, int month, int year, Connection con, Statement s) {
		String query = "select * from billingStatement where a_id = "+account.Id+" and month = "+month+" and year = "+year;
		try{
			ResultSet r = s.executeQuery(query);
			if(r.next()){
				BillingStatement bs = new BillingStatement(r.getInt(1),r.getString(2),r.getInt(3),r.getInt(4),r.getDouble(5));
				return bs;
			}
			return null;
		}catch(SQLException e){
			return null;
		}
	}

	//done
	private static double getCostOfPhonesBought(int id, int month, int year, Connection con, Statement s) {
		double price = 0;
		String exec = "{? = call GETPRICEFORPHONES("+id+","+month+","+year+")";
		try{
			CallableStatement callable = con.prepareCall(exec);
			callable.registerOutParameter(1, Types.DOUBLE);
			callable.execute();
			price = callable.getInt(1);
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Could not get phone prices");
		}
		return price;
		
	}

	//done
	private static int getBytesUsed(int id, int month, int year, Connection con, Statement s) {
		int bytes = 0;
		String exec = "{? = call getNumberOfBytes("+id+","+month+","+year+")";
		try{
			CallableStatement callable = con.prepareCall(exec);
			callable.registerOutParameter(1, Types.INTEGER);
			callable.execute();
			bytes = callable.getInt(1);
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Could not get bytes used");
		}
		return bytes;
	}

	//done
	private static int getTextsSent(int id, int month, int year, Connection con, Statement s) {
		int texts = 0;
		String exec = "{? = call getNumberOfTexts("+id+","+month+","+year+")";
		try{
			CallableStatement callable = con.prepareCall(exec);
			callable.registerOutParameter(1, Types.INTEGER);
			callable.execute();
			texts = callable.getInt(1);
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Could not get seconds called");
		}
		return texts;
	}

	//done
	private static int getSecondCalls(int id, int month, int year, Connection con, Statement s) {
		int seconds = 0;
		String exec = "{? = call getSecondsCalled("+id+","+month+","+year+")";
		try{
			CallableStatement callable = con.prepareCall(exec);
			callable.registerOutParameter(1, Types.INTEGER);
			callable.execute();
			seconds = callable.getInt(1);
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Could not get seconds called");
		}
		return seconds;
	}

	//done
	private static int getYear() {
		int year = 0;
		boolean done = false;
		while(!done){
			System.out.println("Please enter the year for which you wish to get the Billing Statement\n");
			try{
				year =scan.nextInt();
				scan.nextLine();
				done = true;
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid month.");
			}
		}
		return year;
	}

	//done
	private static int getMonth() {
		int month = 0;
		boolean done = false;
		while(!done){
			System.out.println("Please enter the month for which you wish to get the Billing Statement\n"
					+ "1) January\n"
					+ "2) February\n"
					+ "3) March\n"
					+ "4) April\n"
					+ "5) May\n"
					+ "6) June\n]"
					+ "7) July\n"
					+ "8) Agust\n"
					+ "9) September\n"
					+ "10)October\n"
					+ "11)November\n"
					+ "12)December\n ");
			
			try{
				month =scan.nextInt();
				if(month < 1 || month > 12){
					throw new InputMismatchException();
				}
				scan.nextLine();
				done = true;
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid month.");
			}
		}
		return month;
	}

	//done
	private static Account getAccount(Connection con, Statement s) {
		Account account = null;
		boolean done = false;
		while(!done){
			System.out.println("Please enter the Account ID for who you wish to get the Billing Statement: ");
			int accountId = 0;
			try{
				accountId =scan.nextInt();
				scan.nextLine();
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid Account Id.");
				continue;
			}
			account = Account.getAccountWithId(accountId,con,s);
			if(account != null){
				done = true;
			}else{
				System.out.println("Please enter a valid Account Id.");
			}
		}
		return account;
	}
	
}
