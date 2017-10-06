import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Account {
	
	private static Scanner scan = new Scanner(System.in);;
	public int Id;
	public int cId;
	public String type;
	public String primary;
	public String billingName;
	
	//done
	public Account(int id,int cId,String type,String primary,String billingName){
		this.Id = id;
		this.cId = cId;
		this.type  = type;
		this.primary = primary;
		this.billingName = billingName;
	}
	
	//done
	public Account(int cId,String type,String primary,String billingName){
		this(0,cId,type,primary,billingName);
	}

	//done
	public static void updatePrimary(Account client, Connection con, Statement s) {
		String update = "";
		if(client.primary != null)
			 update = "update account set phone_number = '" + client.primary +"' where a_id = " + client.Id;
		else
			update = "update account set phone_number = null where a_id = " + client.Id;
		try{
			s.executeUpdate(update);
		}catch(SQLException e){
			System.out.println("Faulty update of primary number");
		}
	}

	//done
	public static void updateBillingPlan(Account client, Connection con, Statement s) {
		String update = "update account set billing_name = '" + client.billingName +"' where a_id = " + client.Id;
		try{
			s.executeUpdate(update);
		}catch(SQLException e){
			System.out.println("Faulty update of billing name");
		}
	}

	//done
	public static void chooseAccountType(Account newAccount, Connection con, Statement s) {
		boolean done = false;
		while(!done){
			System.out.println("What type of account do you want to hire?\n"
					+ "1) Individual\n"
					+ "2) Family\n"
					+ "3) Buisness");
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
				newAccount.type = "Individual";
				
				done = true;
				break;
			case 2:
				newAccount.type = "Family";
				done = true;
				break;
			case 3:
				newAccount.type = "Buisness";
				done = true;
				break;
			default:
				System.out.println("Please enter one of the valid integers.");
			}
		}
		String update = "update account set type = '"+newAccount.type+"' where a_id = "+newAccount.Id;
		try{
			s.executeUpdate(update);
		}catch(SQLException e){
			System.out.println("Was not able to set type");
		}
	}

	//done
	public static void newAccount(Account newAccount, Connection con, Statement s) {
		String statement = "{? = call newaccount("+newAccount.cId+","+newAccount.type+","+newAccount.primary+","+newAccount.billingName+")}";
		
		try{
			CallableStatement callable = con.prepareCall(statement);
			callable.registerOutParameter(1, Types.INTEGER);
			callable.execute();
			int id = callable.getInt(1);
			newAccount.Id = id;
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Could not create the new Account");
		}
	}

	//done
	public static Account getAccountWithId(int accountId, Connection con, Statement s) {
		Account account = null;
		String query = "select * from account where A_id = " + accountId;
		try{
			ResultSet r = s.executeQuery(query);
			if(r.next()){
				account = new Account(r.getInt(1),r.getInt(2), r.getString(3),r.getString(4),r.getString(5));
			}
		}catch(SQLException e){
			System.out.println("Faulty query of account");
		}
		return account;
	}
	
}
