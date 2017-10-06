import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Customer {
	
	public int id;
	public String name;
	public String street;
	public String city;
	public String zipcode;
	public String state;
	private static Scanner scan = new Scanner(System.in);
	
	public Customer(int i, String n, String str, String c, String zip, String st){
		id = i;
		name = n;
		street = str;
		city = c;
		zipcode = zip;
		state = st;
	}
	
	public Customer(String n, String str, String c, String zip, String st){
		id = 0;
		name = n;
		street = str;
		city = c;
		zipcode = zip;
		state = st;
	}

	//done
	public static Customer newCustomer(Connection con, Statement s) {
		Customer newCustomer = null;
		System.out.println("I need  some information to open your account. Lets get started");
		String name = Customer.prompt("Please enter your full name:",40);
		String street = Customer.prompt("Please enter your street address:",40);
		String city = Customer.prompt("Please enter the city where you live:",20);
		String zipcode = Customer.promptZipcode("Please enter your zipcode:");
		String state = Customer.promptState();
		
		String statement = "{? = call newCustomer('"+name+"','"+street+"','"+city+"','"+zipcode+"','"+state+"')}";
		
		try{
			CallableStatement callable = con.prepareCall(statement);
			callable.registerOutParameter(1, Types.INTEGER);
			callable.execute();
			int id = callable.getInt(1);
			newCustomer = new Customer(id, name, street,city,zipcode,state);
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("Could not create the new customer");
			
		}
		
		return newCustomer;
	}

	//done
	private static String promptState() {
		String[] states = {"AL","AK","AZ","AR","CA","CO","CT","DE","FL","GA","HI","ID","IL","IN","IA","KS","KY","LA", "ME",
						"MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","OH", "OK","OR","PA",
						"RI","SC","SD","TN", "TX", "UT","VT","VA","WA","WV","WI","WY","DC"};
		scan  = new Scanner(System.in);
		System.out.println("Please enter the two letter abbreviation of the state you live in: ");
		String answer = scan .nextLine();
		answer = answer.toUpperCase();
		boolean done = false;
		while(!done){
			for(int i  = 0; i <states.length;i++){
				if(states[i].equals(answer)){
					done = true;
					break;
				}
			}
			if(!done){
				System.out.println("please enter a valid state abbreviation.");
				answer = scan .nextLine();
				answer = answer.toUpperCase();
			}
		}
		done = false;
		while(!done){
			System.out.println("Please confirm, your state is: " + answer + ". (Y/N)");
			String x = scan .nextLine();
			if(x.equalsIgnoreCase("y")){
				done = true;
			}else if(x.equalsIgnoreCase("n")){
				System.out.println("Please enter the two letter abbreviation of the state you live in: ");
				answer = scan .nextLine();
				answer = answer.toUpperCase();
				boolean done2 = false;
				while(!done2){
					for(int i  = 0; i <states.length;i++){
						if(states[i].equals(answer)){
							done2 = true;
							break;
						}
					}
					if(!done){
						System.out.println("please enter a valid state abbreviation.");
						answer = scan .nextLine();
						answer = answer.toUpperCase();
					}
				}				
			}else{
				System.out.println("That is not a valid input. Please try again.");
			}
			
		}
		return answer;
	}

	//done
	private static String promptZipcode(String prompt) {
		scan = new Scanner(System.in);
		System.out.println(prompt);
		String answer = "";
		boolean done = false;
		while(!done){
			try{
				answer = scan.nextLine();
				if(answer.length() != 5){
					throw new InputMismatchException();
				}
				Integer.parseInt(answer);
				done = true;
			}catch(InputMismatchException e){
				System.out.println("Please enter a valid zipcode");
			}
		}
		done = false;
		while(!done){
			System.out.println("Please confirm, your answer is: " + answer + ". (Y/N)");
			String x = scan.nextLine();
			if(x.equalsIgnoreCase("y")){
				done = true;
			}else if(x.equalsIgnoreCase("n")){
				System.out.println(prompt);
				boolean done2 = false;
				while(!done2){
					try{
						answer = scan.nextLine();
						if(answer.length() != 5){
							throw new InputMismatchException();
						}
						Integer.parseInt(answer);
						done2 = true;
					}catch(InputMismatchException e){
						System.out.println("Please enter a valid zipcode");
					}
				}
			}else{
				System.out.println("That is not a valid input. Please try again.");
			}
			
		}
		return answer;
	}

	//done
	private static String prompt(String prompt, int length) {
		boolean done = false;
		scan = new Scanner(System.in);
		String answer = "";
		while(!done){
			System.out.println(prompt);
			answer = scan.nextLine();
			if(answer.contains("'") || answer.contains("%") || answer.contains(";")){
				System.out.println("You may not use the following characters: ', %, ;\n Please try again.");
			}
			else{ 
				done = true;
			}
		}
		done = false;
		while(!done){
			if(answer.length() > length){
				System.out.println("your input is too long please abbreviate it.");
				answer = scan.nextLine();
			}else{
				done = true;
			}
		}
		done = false;
		while(!done){
			System.out.println("Please confirm, your answer is: " + answer+". (Y/N)");
			String x = scan.nextLine();
			if(x.equalsIgnoreCase("y")){
				done = true;
			}else if(x.equalsIgnoreCase("n")){
				System.out.println(prompt);
				boolean good = false;
				while(!good){
					System.out.println(prompt);
					answer = scan.nextLine();
					if(answer.contains("'") || answer.contains("%") || answer.contains(";")){
						System.out.println("You may not use the following characters: ', %, ;\n Please try again.");
					}
					else{ 
						good = true;
					}
				}
				good = false;
				while(!good){
					if(answer.length() > length){
						System.out.println("your input is too long please abbreviate it.");
						answer = scan.nextLine();
					}else{
						good = true;
					}
				}
			}else{
				System.out.println("That is not a valid input. Please try again.");
			}
			
		}
		return answer;
	}

	//done
	public static Customer retreiveCustomer(Connection con, Statement s) {
		Customer newCustomer = null;
		boolean done  = false;
		System.out.println("I need your Customer ID and Full name. Lets get started");
		while(!done){
			int id = Customer.promptID();
			String name = Customer.prompt("Please enter your full name:",40);
			String retrieve = "select * from Customer where id = "+id+" and name = '"+name+"'";
			try{
				ResultSet r = s.executeQuery(retrieve);
				if(!r.next()){
					boolean quit = false;
					while(!quit){
						System.out.println("Your Customer ID and Name did not match.\n"
								+ "Do you wish to:\n"
								+ "1) Try Again\n"
								+ "2) Create a new Customer\n");
						int selection;
						try{
							selection = scan.nextInt();
						}catch(InputMismatchException e){
							scan.nextLine();
							System.out.println("Please enter one of the valid integers.");
							continue;
						}
						if(selection == 1){
							quit = true;
						}else if(selection == 2){
							break;
						}else{
							System.out.println("Please enter one of the valid integers.");
						}
					}
					if(!quit){
						break;
					}
					System.out.println("Ok lets try again.");
				}else{
					newCustomer = new Customer(r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getString(6));
					done = true;
				}
			}catch(SQLException e){
				System.out.println("Cannot retreive your account at this point. Please create a new account");
				done = true;
			}
		}
		return newCustomer;
	}

	//done
	private static int promptID() {
		scan = new Scanner(System.in);
		boolean done = false;
		int id = 0;
		while(!done){
			System.out.println("Please enter your Customer ID:");
			try{
				id = scan.nextInt();
				scan.nextLine();
				done = true;
			}catch(InputMismatchException e){
				System.out.println("You did not enter a valid Customer ID.");
			}
		}
		return id;
	}

}	