import java.sql.*;


public class PhoneNumber {
	public String number;
	public String MEID;
	public int active;
	
	public PhoneNumber(String n, String m, int a){
		number = n;
		MEID = m;
		active = a;
	}
	
	public PhoneNumber(Connection con, Statement s){
		this(randomPhoneNumber(con,s), null, 0);
	}
	
	//done
	public static PhoneNumber getPhoneNumber(String number, Connection con, Statement s){
		String query = "select * from PhoneNumber where phone_number = "+number;
		try{
			ResultSet r = s.executeQuery(query);
			if(!r.next()){
				System.out.println("Phone Number was not found");
				return null;
			}else{
				return new PhoneNumber(r.getString(1),r.getString(2),r.getInt(3));
			}
		}catch(SQLException e){
			System.out.println("Faulty query");
			return null;
		}
		
	}

	//done
	public static String randomPhoneNumber(Connection con, Statement s){
		boolean done = false;
		while(!done){
			String phone = "";
			while(phone.length() < 10){
				phone += Integer.toString((int)(Math.random()*9));
			}
			String query = "select * from phonenumber where phone_number = " + phone;
			ResultSet r = null;
			try {
				r = s.executeQuery(query);
			} catch (SQLException e) {
				continue;
			}
			try {
				if(!r.next())
					return phone;
			} catch (SQLException e) {
				continue;
			}
		}
		return "";
	}

	//done
	public static void insert(PhoneNumber newNumber, Connection con, Statement s) {
		String insert = "insert into PhoneNumber values('"+newNumber.number+"', '"+newNumber.MEID+"',"+ newNumber.active+")"; 
		try{
			s.executeUpdate(insert);
		}catch(SQLException e){
			System.out.println("Faulty insert into Phone Number");
		}
	}

	//needs work
	public static PhoneNumber[] getDeactivatedNumbers(Connection con, Statement s) {
		String query = "select * from phoneNumber where active = 0";
		try{
			ResultSet numbers = s.executeQuery(query);
			PhoneNumber[] deactivated = new PhoneNumber[3];
			if(numbers.next()){
				int count =  0;
				do{
					deactivated[count] = new PhoneNumber(numbers.getString(1),numbers.getString(2), numbers.getInt(3));
					count++;
				}while(numbers.next() && count < deactivated.length);
				if(count == 3){
					return deactivated;
				}
			}
 		}catch(SQLException e){
 			System.out.println("faulty query for deactivated numbers");
 		}
		return null;
	}

	//done
	public static void updateActive(PhoneNumber disPhone, Connection con, Statement s) {
		String update = "update PhoneNumber set active = "+ disPhone.active +" where phone_number = '"+disPhone.number+"'";
		try{
			s.executeUpdate(update);
		}catch(SQLException e){
			System.out.println("Faulty update of activity in phone number");
		}
	}

	
	//done
	public static void updateMEID(PhoneNumber newNumber, Connection con, Statement s) {
		String update ="";
		if(newNumber.MEID != null)
			update = "update PhoneNumber set MEID = '"+ newNumber.MEID +"' where phone_number = '"+newNumber.number+"'";
		else
			update = "update PhoneNumber set MEID = null where phone_number = '"+newNumber.number+"'";
		try{
			s.executeUpdate(update);
		}catch(SQLException e){
			System.out.println("Faulty update of MEID in phone number");
		}
		
	}
}
