

public class UsageInternet {
	private static int nextId = 41;
	private static int currentYear = 2016; 
	private static int nextMonth = 5;
	
	public int id;
	public String number;
	public int bytes;
	public int year;
	public int month;
	
	public UsageInternet(int i, String n, int b, int y, int m){
		id  = i;
		number = n;
		bytes = b;
		year = y;
		month = m;
	}
	
	public UsageInternet(String n){
		id  = nextId++;
		number = n;
		bytes = 0;
		if(nextMonth <13){
			year = currentYear;
			month = nextMonth++;
		}else{
			year = ++currentYear;
			nextMonth = 1;
			month = nextMonth++;
		}
	}
	
}
