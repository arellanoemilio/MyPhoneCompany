import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class BillingPlan {
	public String name;
	public double monthly;
	public double call;
	public double text;
	public double bytes;
	
	public BillingPlan(String name, double m, double c, double t, double b){
		this.name = name;
		monthly = m;
		call = c;
		text = t;
		bytes = b;
	}

	public String toString(){
		return name + " Monthly rateP: $"+ monthly + " Rate per minute: $"+ call + " Rate per text: $"+ text + " Rate per byte: " + bytes;
	}
	
	public static ArrayList<BillingPlan> getBillingPlans(Connection con, Statement s) {
		ArrayList<BillingPlan> list = new ArrayList<BillingPlan>();
		String query = "select * from Billingplan";
		try{
			ResultSet r = s.executeQuery(query);
			while(r.next()){
				BillingPlan bp = new BillingPlan(r.getString(1),r.getDouble(2), r.getDouble(3), r.getDouble(4), r.getDouble(5));
				list.add(bp);
			}
		}catch(SQLException e){
			System.out.println("Could not find any billingplans for this account");
		}
		return list;
	}

	public static BillingPlan getBillingPlanForAccount(Account account, Connection con, Statement s) {
		BillingPlan bp = null;
		String query = "select * from billingplan where billing_name = '" + account.billingName +"'" ;
		try{
			ResultSet r = s.executeQuery(query);
			if(r.next()){
				bp = new BillingPlan(r.getString(1),r.getDouble(2), r.getDouble(3),r.getDouble(4),r.getDouble(5));
			}
		}catch(SQLException e){
			System.out.println("Faulty query of billing plan");
		}
		return bp;
	}

	public static void updateTotalAndBillingName(BillingStatement bs, Connection con, Statement s) {
		String update = "update billingplan set total = "+bs.total+" and billing_name = '"+
						bs.billingName+" where a_id = "+bs.aId+" and month = "+bs.month+" and year = "+bs.year;
		try{
			s.executeUpdate(update);
			
		}catch(SQLException e){
			System.out.println("Faulty update of billing plan");
		}
	}
	
}
