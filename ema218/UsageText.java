import java.sql.Timestamp;

public class UsageText {
private static int nextId = 41; 
	
	public int id;
	public Timestamp time;
	public String destination;
	public String number;
	
	public UsageText(int i, Timestamp s, String des, String n){
		id  = i;
		time = s;
		destination =des;
		number = n;
	}
	
	public UsageText(Timestamp s, String des, String n){
		id  = nextId++;
		time = s;
		destination =des;
		number = n;
	}
}
