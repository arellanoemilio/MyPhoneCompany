

public class PhoneModel{
	private static int nextId =13;
	
	public int id;
	public String manufacturer;
	public String model;
	double price;

	public PhoneModel(int id, String man, String mod, double price){
		this.id = id;
		manufacturer = man;
		model = mod;
		this.price = price;
	}
	
	public PhoneModel(String man, String mod){
		this.id = nextId++;
		manufacturer = man;
		model = mod;
	}
	
	public String toString(){
		String s = ""+manufacturer+" "+model+" $"+ price;
		return s;
	}
}