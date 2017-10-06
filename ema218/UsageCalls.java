import java.sql.Timestamp;

public class UsageCalls {
	private static int nextId = 41; 
	
	public int id;
	public Timestamp start;
	public int duration;
	public String destination;
	public String number;
	
	public UsageCalls(int i, Timestamp s, int d, String des, String n){
		id  = i;
		start = s;
		duration = d;
		destination =des;
		number = n;
	}
	
	public UsageCalls(Timestamp s, int d, String des, String n){
		id  = nextId++;
		start = s;
		duration = d;
		destination =des;
		number = n;
	}
	
}
