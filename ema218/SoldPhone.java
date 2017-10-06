import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class SoldPhone {
	public String MEID;
	public int modelID;
	public Timestamp active;
	
	public SoldPhone(String m, int id, Timestamp a){
		MEID = m;
		modelID = id;
		active  = a;
	}
	
	//done
	public static SoldPhone getSoldPhone(String meid, Connection con, Statement s){
		String query = "select * from soldPhone where meid = "+meid;
		try{
			ResultSet r = s.executeQuery(query);
			if(!r.next()){
				System.out.println("Phone device has not been sold");
				return null;
			}else{
				return new SoldPhone(r.getString(1),r.getInt(2),r.getTimestamp(3));
			}
		}catch(SQLException e){
			System.out.println("Faulty query");
			return null;
		}
		
	}
	
	//done
	public static String randomMeid(Connection con, Statement s){
		boolean done = false;
		while(!done){
			String meid = "";
			while(meid.length() < 56){
				meid += Integer.toString((int)(Math.random()*9));
			}
			String query = "select * from soldphone where meid = " + meid;
			ResultSet r = null;
			try {
				r = s.executeQuery(query);
			} catch (SQLException e) {
				continue;
			}
			try {
				if(!r.next())
					return meid;
			} catch (SQLException e) {
				continue;
			}
		}
		return "";
	}

	//done
	public static void insert(SoldPhone newPhone, Connection con, Statement s) {
		newPhone.active = new Timestamp(new Date().getTime());
		String  insert = "insert into soldPhone(meid,model_id, active) values(?,?,?)";
		try {
			PreparedStatement pinsert = con.prepareStatement(insert);
			pinsert.setString(1, newPhone.MEID);
			pinsert.setInt(2, newPhone.modelID);
			pinsert.setTimestamp(3, newPhone.active);
			pinsert.executeUpdate();
		} catch (SQLException e1) {
			System.out.println("Faulty insert of new soldPhone");
		}
		
	}

	//done
	public static void updateDeactivatedPhone(SoldPhone oldPhone, Connection con, Statement s) {
		String update = "update soldphone set active = null where meid = '"+ oldPhone.MEID+"'";
		try{
			s.executeUpdate(update);
		}catch(SQLException e){
			System.out.println("Faulty deactivation of old phone");
		}
	}

	public static void addPurchased(int id, int modelID, Timestamp active, Connection con, Statement s) {
		String  insert = "insert into purchased(A_id,model_id, time) values(?,?,?)";
		try {
			PreparedStatement pinsert = con.prepareStatement(insert);
			pinsert.setInt(1, id);
			pinsert.setInt(2, modelID);
			pinsert.setTimestamp(3, active);
			pinsert.executeUpdate();
		} catch (SQLException e1) {
			System.out.println("Faulty insert of new purchased");
		}
	}
}
