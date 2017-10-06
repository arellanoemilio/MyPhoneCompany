
public class Store {
	private static int nextId = 4;
	
	public int id;
	public String street;
	public String city;
	public String zipcode;
	public String state;
	
	public Store(int i,  String str, String c, String zip, String st){
		id = i;
		street = str;
		city = c;
		zipcode = zip;
		state = st;
	}
	
	public Store(String str, String c, String zip, String st){
		id = nextId++;
		street = str;
		city = c;
		zipcode = zip;
		state = st;
	}
}
