import java.sql.*;
import java.util.ArrayList;

public class Contains {
	public int aId;
	public String number;
	
	public Contains(int aId, String number){
		this.aId = aId;
		this.number = number;
	}
	
	public String toString(){
		return "Account #: " + aId+ " Phone #: "+number; 
	}
	
	
	public static ArrayList<Contains> getContainsForAccount(Account a, Connection con, Statement s){
		ArrayList<Contains> list = new ArrayList<Contains>();
		String query = "select * from contains where a_id = "+a.Id;
		try{
			ResultSet r = s.executeQuery(query);
			while(r.next()){
				Contains c = new Contains(r.getInt(1),r.getString(2));
				list.add(c);
			}
		}catch(SQLException e){
			System.out.println("Could not find any phone numbers linked with this account");
		}
		return list;
	}

	public static void insert(Contains contains, Connection con, Statement s) {
		String insert = "insert into Contains values(" + contains.aId + ","+ contains.number+")"; 
		try{
			s.executeUpdate(insert);
		}catch(SQLException e){
			System.out.println("Faulty insert into contains");
		}
	}
	
	public static void delete(Contains discontinue, Connection con, Statement s) {
		String delete = "delete from contains where phone_number = '"+discontinue.number+"'";
		try{
			s.executeUpdate(delete);
		}catch(SQLException e){
			System.out.println("Faulty deletion from contains");
		}
	}
}
