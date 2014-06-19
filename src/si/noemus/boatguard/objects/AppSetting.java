package si.noemus.boatguard.objects;



public class AppSetting {

	public AppSetting(){
		
	}	
	
	private int id;
	private String name;
	private String value;
	private String type;
	
	@Override
	public String toString(){
		return "APPSETTING: id: " + this.id + ", name:" + this.name + ", value:" + this.value + ", type" + this.type;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
