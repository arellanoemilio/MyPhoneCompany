import java.sql.*;
import java.util.*;

public class InteractiveInterface {

	private static Scanner scan = new Scanner(System.in);
	private static Connection con = null;
	private static Statement s = null;
	
	//needs work
	public static void run(Connection connection, Statement statement){
		con = connection;
		s = statement;
		
		boolean quit = false;
		while(!quit){
			System.out.println("Welcome to our online platform. Please select one of the following.\n"
					+ "1) Log In to Account\n"
					+ "2) New Account\n"
					+ "3) Exit interface");
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
				runClient();
				break;
			case 2:
				runNewClient();
				break;
			case 3:
				quit = true;
				break;
			default:
				System.out.println("Please enter one of the valid integers.");
			}
		}
	}

	//needs work
	private static void runClient() {
		Account client = logIn();
		
		boolean quit = false;
		while(!quit){
			System.out.println("Select one of the following.\n"
					+ "1) Order New Phone for Pre-Existing Number\n"
					+ "2) Contract Additional Phone Number\n"
					+ "3) Discontinue Phone Number\n"
					//+ "4) See Account Usage\n"
					+ "4) Change Billing Plan\n"
					+ "5) Change Primary Phone Number\n"
					+ "6) Log out of Client Portal");
			int selection;
			try{
				selection = scan.nextInt();
				scan.nextLine();
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid ID.");
				continue;
			}
			switch(selection){
			case 1:
				buyPhoneOnline(client);
				break;
			case 2:
				
				if(client.type.equals("family")){	
					ArrayList<Contains> contains =Contains.getContainsForAccount(client, con, s);
					if(contains.size() <6){
						newLine(client);
					}
				}
				else if(!client.type.equals("Buisness"))
					newLine(client);
				else{
					System.out.println("You hava an individual account, you cannot add a second phone number.");
				}
				break;
			case 3:
				discontinueLine(client);
				if(client.primary == null){
					quit = true;
				}
				break;
//			case 4:
//				runAccountUsage(client);
//				break;
			case 4:
				runSelectBillingPlan(client);
				break;
			case 5:
				selectNewPrimary(client);
				break;
			case 6:
				quit = true;
				break;
			default:
				System.out.println("Please enter one of the valid integers.");
			}
		}
	}
	
	//needs work
	//private static void runAccountUsage(Account client) {
	//}

	//done
	private static void runSelectBillingPlan(Account client) {
		ArrayList<BillingPlan> billingPlans = BillingPlan.getBillingPlans(con, s);
		System.out.println("Which billing plan do you wish to hire?");
		for(int i = 1; i <= billingPlans.size(); i++){
			System.out.println(i+") "+ billingPlans.get(i-1));
		}
		boolean done = false;
		int selection = 0;
		while(!done){
			try{
				selection = scan.nextInt();
				scan.nextLine();
				if (selection > billingPlans.size() || selection < 1){
					throw new InputMismatchException();
				}
				done = true;
			}catch(InputMismatchException e){
				System.out.println("Please enter a valid integer.");
			}
		}
		client.billingName = billingPlans.get(selection - 1).name;
		Account.updateBillingPlan(client, con, s);
		System.out.println("Your new billing plan is: " + client.billingName);
	}

	//done
	private static Account logIn(){
		ResultSet r = null;
		boolean done = false;
		Account a = null;
		while(!done){
			System.out.println("What is your Client Account ID:");
			int aId;
			try{
				aId = scan.nextInt();
				scan.nextLine();
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid ID.");
				continue;
			}
			System.out.println("What is your primary phone Number:");
			String phone;
			try{
				phone = scan.next();
				if(phone.contains("'") || phone.contains("%") || phone.contains(";")){
					throw new InputMismatchException();
				}
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid Phone Number.");
				continue;
			}
			String query = "select * from account where a_id = " + aId + " and phone_number = '" + phone+"'";
			try{
				 r = s.executeQuery(query);
			}catch(SQLException e){
				System.out.println("Unable to find account please try again.");
				continue;
			}
			try{
				if(!r.next()){
					System.out.println("The Account ID and Primary Phone Number are incorrect. Please try again.");
					continue;
				}else{
					a = new Account(r.getInt(1), r.getInt(2),r.getString(3),r.getString(4), r.getString(5));
					done = true;
				}
			}catch(SQLException e){
				continue;
			}
		}
		
		return a;
	}
	
	//done
	private static void buyPhoneOnline(Account client){
		Object[] newPhoneInfo = selectPhoneFromOnline(client);
		double cost =(double) newPhoneInfo[0];
		SoldPhone newPhone = (SoldPhone) newPhoneInfo[1];
		System.out.println("What phone number do you want to tie this phone with?");
		ArrayList<Contains> contains = Contains.getContainsForAccount(client, con, s);
		for(int i = 1; i <= contains.size(); i++){
			System.out.println(i+") "+ contains.get(i-1).number);
		}
		boolean done = false;
		int selection = 0;
		while(!done){
			try{
				selection = scan.nextInt();
				scan.nextLine();
				if (selection > contains.size() || selection < 1){
					throw new InputMismatchException();
				}
				done = true;
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid integer.");
			}
		}
		
		PhoneNumber  pn= PhoneNumber.getPhoneNumber(contains.get(selection - 1).number, con, s);
		SoldPhone oldPhone = SoldPhone.getSoldPhone(pn.MEID, con, s);
		Activity.deactivateDeviceWithNumber(oldPhone,pn, con, s);
		SoldPhone.insert(newPhone, con, s);
		Activity.activatePhoneWithNumber(newPhone, pn, con, s);
		pn.MEID = newPhone.MEID;
		PhoneNumber.updateMEID(pn, con, s);
		SoldPhone.updateDeactivatedPhone(oldPhone, con, s);
		SoldPhone.addPurchased(client.Id,newPhone.modelID,newPhone.active,con,s);
		System.out.println("You have succesfully purchased your new phone. your account will receive a charge of $"+ cost);
	}

	//done
	public static String randomMeid(){
		String meid = "";
		while(meid.length() < 56){
			meid += Integer.toString((int)(Math.random()*9));
		}
		return meid;
	}
	
	//done
	private static void runNewClient(){
		boolean done = false;
		String response = null;
		while(!done){
			System.out.println("Are you a first time user?\n"
					+ "(Answer 'Y' if you do not remember the Customer ID and name under which the account was created)\n"
					+ " (Y/N)");
			response = scan.nextLine();
			if(response.equalsIgnoreCase("y")){
				Customer newCustomer = Customer.newCustomer(con, s);
				if (newCustomer != null){
					Account newAccount = newClientAccount(newCustomer,con,s);
					runLoggedClient(newAccount);
					done = true;
				}else{
					System.out.println("Sorry, there was a system failure and at the moment we cannot create your account.");
					return;
				}
			}else if (response.equalsIgnoreCase("n")){
				Customer oldCustomer = Customer.retreiveCustomer(con,s);
				if(oldCustomer != null){
					String retrieve = "select * from account where phone_number is null";
					try{
						ResultSet r = s.executeQuery(retrieve);
						if(!r.next()){
							Account newAccount = newClientAccount(oldCustomer,con,s);
							runLoggedClient(newAccount);
							done = true;
						}else{
							Account oldAccount =  new Account(r.getInt(1),r.getInt(2),r.getString(3),r.getString(4),r.getString(5));
							runSelectBillingPlan(oldAccount);
							newLine(oldAccount);
							boolean newLine = true;
							String answer = null;
							int count = 1;
							while(newLine && (oldAccount.type.equals("Buisness")||(oldAccount.type.equals("Family") && count<6)) ){
								System.out.println("Do you wish to hire another phone number? (Y/N)");
								answer = scan.nextLine();
								if(answer.equalsIgnoreCase("y")){
									newLine(oldAccount);
									count++;
								}else if (answer.equalsIgnoreCase("n")){
									newLine = false;
								}else{
									System.out.println("Your answer is not valid.");
								}
							}
							selectNewPrimary(oldAccount);
							runLoggedClient(oldAccount);
							done = true;
						}
					}catch(SQLException e){
						Account newAccount = newClientAccount(oldCustomer,con,s);
						runLoggedClient(newAccount);
						done = true;
					}
				}
				done = true;
			}else{
				System.out.println("Your answer is not valid.");
			}
		}
	}
	
	//done
	private static Account newClientAccount(Customer newCustomer, Connection con, Statement s){
		Account newAccount = new Account(newCustomer.id,null,null,null);
		Account.newAccount(newAccount,con,s);
		Account.chooseAccountType(newAccount,con,s);
		runSelectBillingPlan(newAccount);
		newLine(newAccount);
		boolean newLine = true;
		String answer = null;
		int count = 1;
		while(newLine && (newAccount.type.equals("Buisness")||(newAccount.type.equals("Family") && count<6))){
			System.out.println("Do you wish to hire another phone number? (Y/N)");
			answer = scan.nextLine();
			if(answer.equalsIgnoreCase("y")){
				newLine(newAccount);
				count++;
			}else if (answer.equalsIgnoreCase("n")){
				newLine = false;
			}else{
				System.out.println("Your answer is not valid.");
			}
		}
		selectNewPrimary(newAccount);
		return newAccount;
	}
	
	//need work
	private static void runLoggedClient(Account client) {
		System.out.println("Welcome to your Online Portal.\n"
				+ "The next time you wish to log in you will need your Account id and Primary number.\n"
				+ "Account ID: " + client.Id+" Primary number: " + client.primary +"\n"
				+ "Please remember this information since there is no possible way to retreive it if forgotten.\n\n");
		boolean quit = false;
		while(!quit){
			System.out.println("Select one of the following.\n"
					+ "1) Order New Phone for Pre-Existing Number\n"
					+ "2) Contract Additional Phone Number\n"
					+ "3) Discontinue Phone Number\n"
					//+ "4) See Account Usage\n"
					+ "4) Change Billing Plan\n"
					+ "5) Change Primary Phone Number\n"
					+ "6) Log out of Client Portal");
			int selection;
			try{
				selection = scan.nextInt();
				scan.nextLine();
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid ID.");
				continue;
			}
			switch(selection){
			case 1:
				buyPhoneOnline(client);
				break;
			case 2:
				if(client.type.equals("family")){	
					ArrayList<Contains> contains =Contains.getContainsForAccount(client, con, s);
					if(contains.size() <6){
						newLine(client);
					}
				}
				else if(!client.type.equals("Buisness"))
					newLine(client);
				else{
					System.out.println("You hava an individual account, you cannot add a second phone number.");
				}
				break;
			case 3:
				discontinueLine(client);
				if(client.primary == null){
					quit = true;
				}
				break;
//			case 4:
//				runAccountUsage(client);
//				break;
			case 4:
				runSelectBillingPlan(client);
				break;
			case 5:
				selectNewPrimary(client);
				break;
			case 6:
				quit = true;
				break;
			default:
				System.out.println("Please enter one of the valid integers.");
			}
		}
		
	}

	//needs work
	private static void newLine(Account client){
		PhoneNumber[] numbers = PhoneNumber.getDeactivatedNumbers(con, s);
		PhoneNumber[] option2 = {new PhoneNumber(con, s),new PhoneNumber(con, s),new PhoneNumber(con, s)};
		boolean reuse = true;
		if(numbers == null){
			numbers =  option2;
			reuse = false;
		}
		System.out.println("Please select one of the following for as your new Phone number.");
		for(int i = 0; i < numbers.length; i++){
			System.out.println((i+1)+") "+numbers[i].number);
		}
		int selection = 0;
		boolean done = false;
		while(!done){
			try{
				selection = scan.nextInt();
				scan.nextLine();
				if (selection > numbers.length || selection < 1){
					throw new InputMismatchException();
				}
				done = true;
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid integer.");
			}
		}
		PhoneNumber newNumber = numbers[selection - 1];
		System.out.println("You have chosen the number: " + newNumber.number+"\n What device would you want this number on?");
		Object[] newPhoneInfo = selectPhoneFromOnline(client);
		double cost =  (double) newPhoneInfo[0];
		SoldPhone newPhone = (SoldPhone) newPhoneInfo[1];
		
		newNumber.MEID = newPhone.MEID;
		SoldPhone.insert(newPhone, con, s);
		newNumber.active = 1;
		if(!reuse)
			PhoneNumber.insert(newNumber, con, s);
		else{
			PhoneNumber.updateActive(newNumber, con, s);
			PhoneNumber.updateMEID(newNumber, con, s);
		}
		Contains contains = new Contains(client.Id, newNumber.number);
		Contains.insert(contains, con, s);
		Activity.activatePhoneWithNumber(newPhone, newNumber, con, s);
		SoldPhone.addPurchased(client.Id,newPhone.modelID,newPhone.active,con,s);
		System.out.println("Thank you for your purchase. You will be charged $" + cost + " for the device.");
	}
	
	//done
	private static Object[] selectPhoneFromOnline(Account client){
		String modelsQuerry = "Select * from OnlineStore order by model_id";
		ResultSet models = null;
		int counter = 0;
		boolean complete = false;
		ArrayList<PhoneModel> phones = new ArrayList<PhoneModel>();
		while (!complete && counter < 10)
		try {
			models = s.executeQuery(modelsQuerry);
			complete = true;
		} catch (SQLException e) {
			System.out.println("Encountered an error tring to access Database");
			counter++;
		}
		System.out.println("Here are all of the phone Models we offer.");
		if(models == null){
			System.out.println("The database is unresponsive at this moment.\n"+
				"Please try again later.");
		return null;
		}
		try {
			if(!models.next()){
				System.out.println("At this point the online platform is not selling phone devices");
				return null;
			 }
			else{
				System.out.println("Select the phone you would like to purchase.");

				do{
					PhoneModel m = null;
					try{
						m = new PhoneModel(models.getInt(1), models.getString(1), models.getString(3), models.getDouble(4));
						phones.add(m);
						System.out.println(m);
					}catch(SQLException e){
						e.printStackTrace();;
					}
					
				}while(models.next());
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		int selection = 0;
		boolean done = false;
		while(!done){
			try{
				selection = scan.nextInt();
				scan.nextLine();
				if (selection > phones.size() || selection < 1){
					throw new InputMismatchException();
				}
				done = true;
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid integer.");
			}
		}
		double cost = phones.get(selection-1).price;
		System.out.println("Thank you for selecting: " + phones.get(selection - 1));
		SoldPhone newPhone = new SoldPhone(SoldPhone.randomMeid(con, s),selection, null);
		Object[]  o= new Object[2];
		o[0] = cost;
		o[1] = newPhone;
		return o;
	}
	
	//done
	private static void discontinueLine(Account client){
		ArrayList<Contains> contains= Contains.getContainsForAccount(client, con, s);
		System.out.println("Which line do you wish to discontinue?");
		for(int i = 1; i <= contains.size(); i++){
			System.out.println(i+") "+ contains.get(i-1).number);
		}
		boolean done = false;
		int selection = 0;
		while(!done){
			try{
				selection = scan.nextInt();
				scan.nextLine();
				if (selection > contains.size() || selection < 1){
					throw new InputMismatchException();
				}
				done = true;
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid integer.");
			}
		}
		Contains discontinue = contains.get(selection-1);
		Contains.delete(discontinue, con, s);
		PhoneNumber disPhone = PhoneNumber.getPhoneNumber(discontinue.number, con, s);
		disPhone.active = 0;
		SoldPhone oldPhone = SoldPhone.getSoldPhone(disPhone.MEID, con, s);
		Activity.deactivateDeviceWithNumber(oldPhone,disPhone, con, s);
		PhoneNumber.updateActive(disPhone, con, s);
		
		if(discontinue.number.equals(client.primary) && contains.size() == 1){
			done = false;
			String response = null;
			while(!done){
				System.out.println("This was the last phone number in your account.\n "
									+ "Do you wish you get a new phone number and device? (Y/N)");
				response = scan.nextLine();
				if(response.equalsIgnoreCase("y")){
					newLine(client);
					client.primary = null;
					ArrayList<Contains> newContains = Contains.getContainsForAccount(client, con, s);
					client.primary = newContains.get(0).number;
					Account.updatePrimary(client, con, s);
					Activity oldActivity = Activity.getActivityWithNumber(disPhone.number, con, s);
					if(oldActivity != null){
						SoldPhone oldDevice = SoldPhone.getSoldPhone(oldActivity.MEID, con, s);
						Activity.deactivateDeviceWithNumber(oldDevice, disPhone, con, s);
						
					}
					done = true;{
					}
				}else if (response.equalsIgnoreCase("n")){
					client.primary = null;
					Account.updatePrimary(client, con, s);
					Activity oldActivity = Activity.getActivityWithNumber(disPhone.number, con, s);
					if(oldActivity != null){
						SoldPhone oldDevice = SoldPhone.getSoldPhone(oldActivity.MEID, con, s);
						Activity.deactivateDeviceWithNumber(oldDevice, disPhone, con, s);
					}
					System.out.println("Because your account has no active lines it will be discontinued.\n"
							+ "If at any point you wish to re-hire Jog please create a new Account with your Customer ID:" + client.cId +".");
					done = true;
				}else{
					System.out.println("Your answer is not valid.");
				}
			}
			disPhone.MEID = null;
			PhoneNumber.updateMEID(disPhone, con, s);
		}else if(discontinue.number.equals(client.primary)){
			client.primary = null;
			Account.updatePrimary(client, con, s);
			Activity oldActivity = Activity.getActivityWithNumber(disPhone.number, con, s);
			if(oldActivity != null){
				SoldPhone oldDevice = SoldPhone.getSoldPhone(oldActivity.MEID, con, s);
				Activity.deactivateDeviceWithNumber(oldDevice, disPhone, con, s);
			}
			disPhone.MEID = null;
			PhoneNumber.updateMEID(disPhone, con, s);
			selectNewPrimary(client);
		}
		
	}

	//done
	private static void selectNewPrimary(Account client) {
		ArrayList<Contains> contains = Contains.getContainsForAccount(client, con, s);
		System.out.println("Select a new primary number.");
		for(int i = 1; i <= contains.size(); i++){
			System.out.println(i+") "+ contains.get(i-1).number);
		}
		boolean done = false;
		int selection = 0;
		while(!done){
			try{
				selection = scan.nextInt();
				scan.nextLine();
				if (selection > contains.size() || selection < 1){
					throw new InputMismatchException();
				}
				done = true;
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Please enter a valid integer.");
			}
		}
		client.primary = contains.get(selection-1).number;
		Account.updatePrimary(client, con, s);
		System.out.println("Your new Primary number is: "+ client.primary);
	}
}
