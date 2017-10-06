import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class BillingStatement {
	
	int aId;
	String billingName;
	int month;
	int year;
	double total;
	
	public BillingStatement(int aId, String billingName, int month, int year, double total) {
		this.aId = aId;
		this.billingName = billingName;
		this.month = month;
		this.year = year;
		this.total = total;
	}
	
	public String toString(){
		return "Billing Statement for Account :"+aId+"\nMonth: "+month+"Year: "+year+"\n Billing Plan: "+billingName+"\n The Total Charged is $"+total;
	}

	public static void insert(BillingStatement bs, Connection con, Statement s) {
		String update = "insert into billingstatement values("+bs.aId+",'"+bs.billingName+"',"+bs.month+","+bs.year+","+bs.total+")";
		try{
			s.executeUpdate(update);
		}catch(SQLException e){
			System.out.println("Faulty insert to billing statement");
		}
		
	}
}
