import java.sql.*;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

public class PopulateClients {

	public static void main(String[] args){
		Connection con=null;
		Statement s = null;
		System.out.println("login");
		// Login
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =  DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",
					"ema218", "sae3481!3");
			s = con.createStatement();
			 
		} catch (ClassNotFoundException e1) {
			System.out.println("class was not found");
		} catch (SQLException e) { 
			System.out.println("Your username and Password don't match. plase try again");
		}
		
		try {
			System.out.println("delete");
			s.execute("delete from Customer");
			s.execute("delete from billingplan");
			s.execute("delete from onlineStore");
			s.execute("delete from soldphone");
			s.execute("delete from phonenumber");
			s.execute("delete from account");
			s.execute("delete from contains");
			s.execute("delete from activity");
			s.execute("delete from store");
			s.execute("delete from stock");
			s.execute("delete from usageInternet");
			s.execute("delete from usageText");
			s.execute("delete from usageCalls");
			s.execute("delete from purchased");
		} catch (SQLException e) {
			System.out.println("delete to customer did not work");
		}
		
		String[] names = {"Rico Selle","Quentin Smock","Rodrick Punch", "Eddie Maclin", "Avery Bundren", "Rodrigo Hollander","Blaine Parson", "Keith Kile", "Dexter Monahan", "Travis Degreenia", "Neville Pfaff","Christoper Beckham" ,"Walker Fennel",
		                  "Shon Hottle", "Jake Goodman","Deon Helvey", "Ralph Timoteo", "Miquel Yearout", "Curt Carraway", "Donette Maris","Trinity Walston","Rosemary Caesar","Merissa Righter","Katheryn Siegmund", "Genevieve Borquez","Tambra Maestas",
		                  "Dena Sylvia", "Jolene Hisle","Mallory Egger"};
		String[] streets = {"Main St", "2nd St", "4th St", "Frat Rd", "Hillside", "East 5Th", "Hill Rd", "Peak Av", "Junction St", "Independence", "Campus Sq", "Time Sq", "Monclair", "Village Rd", "Penguin", "Loomis"};
		String[] cities = {"Bethlehem", "Philadelphia"};
		String[] zipcode = {"18015", "19019","32468","32463"};
		
		System.out.println("insert cutomer");
		for(int i = 0; i< names.length; i++){
			String name = names[i];
			String street =streets[(int) (Math.random()*streets.length)];
			String city = cities[(int) (Math.random()*cities.length)];
			String zip = zipcode[(int) (Math.random()*zipcode.length)];
			String insert = "insert into customer values(customer_seq.nextval,'"+name+"','"+street+"','"+city+"',"+zip+", 'PA')";
			try {
				s.execute(insert);
			} catch (SQLException e) {
				System.out.println("insert to customer did not work");
			}
		}
		
		String[] billingplans = {"'monthly', 150, 0,0,0","'hybrid', 79.00, 0.50, 0.50, 0.0005","'per_use',0,1.5,.75,.005"};
		String[] billingnames ={"monthly","hybrid","per_use"};
		for(int i =0; i <billingplans.length; i++){
			String insert = "insert into billingplan values("+billingplans[i]+")";
			try {
				s.execute(insert);
			} catch (SQLException e) {
				System.out.println("insert to billing plan failed");
			}
		}
		
		String[] onlineStore =  {"'Apple','iPhone 5'","'Apple','iPhone 5s'","'Apple','iPhone 6'","'Apple','iPhone 6 plus'","'Apple','iPhone 6s'","'Apple','iPhone 6s plus'","'Samsung', 'Galaxy S5'","'Samsung', 'Galaxy S6'","'Samsung', 'Galaxy S7'",
				"'Samsung', 'Galaxy Note'","'Samsung', 'Galaxy Note4'","'Samsung', 'Galaxy Note5'"};
		
		for(int i =0; i <onlineStore.length; i++){
			double price = Math.random()*400d;
			String insert = "insert into onlinestore values(online_seq.nextval," + onlineStore[i]+","+price+ ")";
			try {
				s.execute(insert);
			} catch (SQLException e) {
				System.out.println("insert to onlineStore failed line " + i);
			}
		}
		
		int[] phoneModels = new int[40];
		
		String[] meid = new String[40];
		Timestamp startTimestamp = null;
		java.util.Date date= new java.util.Date();
		for(int i = 0; i < 40;i++){
			String  insert = "insert into soldphone(meid,model_id,active) values(?,?,?)";
			try {
				PreparedStatement pinsert = con.prepareStatement(insert);
				meid[i] = randomMeid();
				startTimestamp = new Timestamp(date.getTime());
				pinsert.setString(1, meid[i]);
				phoneModels[i] = (int)(Math.random()*onlineStore.length+1);
				pinsert.setInt(2, phoneModels[i]);
				pinsert.setTimestamp(3, startTimestamp);
				pinsert.executeUpdate();
			} catch (SQLException e1) {
				System.out.println("failed to insert sold phone");
				System.out.println(e1.getMessage());
			}
		}
		
		String[] numbers = new String[40];
		
		for(int i = 0; i < 40;i++){
			String  insert = "insert into phonenumber(phone_number,meid,active) values(?,?,?)";
			
			try {
				PreparedStatement pinsert = con.prepareStatement(insert);
				numbers[i] = randomPhoneNumber();
				pinsert.setString(2, meid[i]);
				pinsert.setString(1, numbers[i]);
				pinsert.setInt(3, 1);
				pinsert.executeUpdate();
			} catch (SQLException e1) {
				System.out.println("failed to insert phoneNumber");
			}
			
		}

		for(int i = 0; i < 29;i++){
			String  insert = "insert into account(a_id,id,type,phone_number,billing_name) values(account_seq.nextval,?,?,?,?)";
			try {
				PreparedStatement pinsert = con.prepareStatement(insert);
				pinsert.setInt(1, i+1);
				if(i == 0){
					pinsert.setString(2, "Buisness");
					pinsert.setString(3, numbers[0]);
				}else if(i == 1){
					pinsert.setString(2, "Family");
					pinsert.setString(3, numbers[9]);
				}else{
					pinsert.setString(2, "Individual");
					pinsert.setString(3, numbers[11+i]);
				}
				pinsert.setString(4, billingnames[(int)(Math.random()*billingplans.length)]);
				pinsert.executeUpdate();
			} catch (SQLException e1) {
				System.out.println("failed to insert account");
				
			}
		}
		
		for(int i = 0; i < 40;i++){
			String  insert = "insert into contains(a_id,phone_number) values(?,?)";
			
			try {
				PreparedStatement pinsert = con.prepareStatement(insert);
				if(i<9){
					pinsert.setInt(1, 1);
				}else if(i<12){
					pinsert.setInt(1, 2);
				}else{
					pinsert.setInt(1, i-10);
				}
				pinsert.setString(2, numbers[i]);
				pinsert.executeUpdate();
			} catch (SQLException e1) {
				System.out.println("failed to insert phoneNumber\n" + e1.getMessage());
			}
			
		}
		
		
		for(int i = 0; i < 40;i++){
			String  insert = "insert into activity(meid,phone_number,start_time, end_time) values(?,?,?,null)";
			
			try {
				PreparedStatement pinsert = con.prepareStatement(insert);
				pinsert.setString(1, meid[i]);
				pinsert.setString(2, numbers[i]);
				pinsert.setTimestamp(3, startTimestamp);
				pinsert.executeUpdate();
			} catch (SQLException e1) {
				System.out.println("failed to insert phoneNumber\n" + e1.getMessage());
			}
			
		}
		
		for(int i = 0; i < 40;i++){
			String  insert = "insert into purchased(a_id,model_id,time) values(?,?,?)";
			
			try {
				PreparedStatement pinsert = con.prepareStatement(insert);
				if(i<9){
					pinsert.setInt(1, 1);
					pinsert.setInt(2, phoneModels[i]);
				}else if(i<12){
					pinsert.setInt(1, 2);pinsert.setInt(2, phoneModels[i]);
				}else{
					pinsert.setInt(1, i-10);
					pinsert.setInt(2, phoneModels[i]);
				}
				pinsert.setTimestamp(3, startTimestamp);
				pinsert.executeUpdate();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println(i);
				System.out.println("failed to insert purchased\n" + e1.getMessage());
			}
			
		}
	
		for(int i = 0; i< 3; i++){
			String street =streets[(int) (Math.random()*streets.length)];
			String city = cities[(int) (Math.random()*cities.length)];
			String zip = zipcode[(int) (Math.random()*zipcode.length)];
			String insert = "insert into store values(store_seq.nextval,'"+street+"','"+city+"',"+zip+", 'PA')";
			try {
				s.execute(insert);
			} catch (SQLException e) {
				System.out.println("insert to store did not work");
			}
		}
		
		for(int i = 0; i < 3; i++){
			for(int j = 0; j<onlineStore.length;j++){
				String insert = "insert into stock values("+ (j+1) +","+ (i+1) +","+ ((int)(Math.random()*50)) +")";
				try {
					s.execute(insert);
				} catch (SQLException e) {
					System.out.println("insert to stock did not work");
				}
			}
		}
		System.out.print("wait");
		Scanner p = new Scanner(System.in);
		p.next(); 
		
		int counter = 0;
		for(int i =0; i < numbers.length;i++){
			int year = 2016;
			int month = 5;
			counter++;
			String insert = "insert into usageInternet values(internet_seq.nextval,'"+ numbers[i] +"',"+ ((int)(Math.random()*5000000)) +","+year+","+month+")";
			try {
				s.execute(insert);
				
			} catch (SQLException e) {
				System.out.println("insert to usage internet did not work\n"+e.getMessage());
			}		
		}
		
		
		
		counter = 0;
		for(int i =0; i < numbers.length;i++){
			for(int call = 0; call < 1;call++){
				String  insert = "insert into usageCalls(c_id,start_time,duration_sec,destination_number,phone_number) values(call_seq.nextval,?,?,?,?)";
				try {
					PreparedStatement pinsert = con.prepareStatement(insert);
					pinsert.setTimestamp(1, startTimestamp);
					int duration = (int)(Math.random()*5000);
					pinsert.setInt(2, duration);
					pinsert.setString(3, randomPhoneNumber());
					pinsert.setString(4, numbers[i]);
					pinsert.executeUpdate();
				} catch (SQLException e1) {
					System.out.println("failed to insert usage calls\n" + e1.getMessage());
				}
				
			}
		}
		
		counter = 0;
		for(int i =0; i < numbers.length+1;i++){
			for(int call = 0; call < 1;call++){
				String  insert = "insert into usageText(t_id,time,destination_number,phone_number) values(text_seq.nextval,?,?,?)";
				try {
					PreparedStatement pinsert = con.prepareStatement(insert);
					pinsert.setTimestamp(1, startTimestamp);
					int duration = (int)(Math.random()*5000);
					pinsert.setString(2, randomPhoneNumber());
					pinsert.setString(3, numbers[i]);
					pinsert.executeUpdate();
				} catch (SQLException e1) {
					System.out.println("insert into usageText values("+(i+1)+",current_timestamp,'"+randomPhoneNumber()+"','"+numbers[i]+"');");
					//System.out.println("failed to insert usage texts\n" + e1.getMessage());
				}
				
			}
		}
		
		System.out.println("close conection");
		try {
			s.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public static String randomMeid(){
		String meid = "";
		while(meid.length() < 56){
			meid += Integer.toString((int)(Math.random()*9));
		}
		return meid;
	}

	public static String randomPhoneNumber(){
		String phone = "";
		while(phone.length() < 10){
			phone += Integer.toString((int)(Math.random()*9));
		}
		return phone;
	}
}
