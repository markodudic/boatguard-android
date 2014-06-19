package si.noemus.boatguard.objects;



public class State {

	public State(){
		
	}	
	
	private int id;
	private int idComponent;
	private String name;
	private String code;
	private String position;
	private String type;
	private int active;
	
	@Override
	public String toString(){
		return "STATE: id: " + this.id + ", idComponent:" + this.idComponent + ", name:" + this.name + ", code:" + this.code + 
				", type" + this.type + ", active" + this.active;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getIdComponent() {
		return idComponent;
	}

	public void setIdComponent(int idComponent) {
		this.idComponent = idComponent;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}


}
