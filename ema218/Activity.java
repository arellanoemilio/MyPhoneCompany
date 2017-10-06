import java.sql.*;
import java.util.Date;

public class Activity {
	
	public String MEID;
	public String phoneNumber;
	public Timestamp start;
	public Timestamp end;
	
	public Activity(String MEID, String phoneNumber, Timestamp start, Timestamp end){
		this.MEID = MEID;
		this.phoneNumber = phoneNumber;
		this.start = start;
		this.end = end;
	}
	
	public static void deactivateDeviceWithNumber(SoldPhone sp, PhoneNumber pn, Connection con, Statement s){
		String query = "select * from activity where meid = '"+ sp.MEID+ "' and phone_number = '" + pn.number+ "' and end_time is null";
		try{
			ResultSet r = s.executeQuery(query);
			if(!r.next()){
				System.out.println("No activity was found");
			}else{
				Activity a = new Activity(r.getString(1), r.getString(2), r.getTimestamp(3), r.getTimestamp(4));
				a.end  = new Timestamp(new Date().getTime());
				updateActivityEndTime(a, con, s);
				sp.active = null;
			}
		}catch(SQLException e){
			System.out.print("Faulty query tring to deactivate device with number");
		}
	}

	private static void updateActivityEndTime(Activity a, Connection con, Statement s) {
		String update = "update activity set end_time = ? where meid = '"+ a.MEID+ "' and phone_number = '" + a.phoneNumber+ "' and end_time is null";
		try{
			PreparedStatement pupdate  = con.prepareStatement(update);
			pupdate.setTimestamp(1, a.end);
			pupdate.executeUpdate();
		}catch(SQLException e){
			System.out.println("Faulty query trying to update activity end_time");
		}
	}

	public static void activatePhoneWithNumber(SoldPhone newPhone, PhoneNumber pn, Connection con, Statement s) {
		Activity a = new Activity(newPhone.MEID, pn.number, newPhone.active, null);
		String  insert = "insert into activity(meid,phone_number,start_time, end_time) values(?,?,?,null)";
		try {
			PreparedStatement pinsert = con.prepareStatement(insert);
			pinsert.setString(1, a.MEID);
			pinsert.setString(2, a.phoneNumber);
			pinsert.setTimestamp(3, a.start);
			pinsert.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.out.println(e1.getMessage());
			System.out.println("Faulty insert of new Activity");
		}
	}

	public static Activity getActivityWithNumber(String number, Connection con, Statement s) {
		String query = "select * from activity where phone_number = '" + number+ "' and end_time is null";
		try{
			ResultSet r  = s.executeQuery(query);
			if(r.next()){
				Activity activity = new Activity(r.getString(1), r.getString(2), r.getTimestamp(3), r.getTimestamp(4));
				return activity;
			}
		}catch(SQLException e){
			System.out.println("Faulty query for activity with number.");
		}
		return null;
	}

}
