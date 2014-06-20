package si.noemus.boatguard.objects;



public class Alarm {

	public Alarm(){
		
	}	
	
	private int id;
	private int idState;
	private String name;
	private String code;
	private String value;
	private String operand;
	private String previous;
	private String message;
	private String message_short;
	private String title;
	private String action;
	private String type;
	private int active;
	
	@Override
	public String toString(){
		return "ALARM: id: " + this.id + ", idState:" + this.idState + ", name:" + this.name + ", code:" + this.code + 
				", value:" + this.value + ", operand " + this.operand + ", previous " + this.previous + 
				", message" + this.message + ", messageShort" + this.message_short + ", title" + this.title + 
				", action" + this.action + ", messageShort" + this.message_short + ", active" + this.active;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdState() {
		return idState;
	}
	public void setIdState(int idState) {
		this.idState = idState;
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOperand() {
		return operand;
	}
	public void setOperand(String operand) {
		this.operand = operand;
	}
	public String getPrevious() {
		return previous;
	}
	public void setPrevious(String previous) {
		this.previous = previous;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessageShort() {
		return message_short;
	}
	public void setMessageShort(String message_short) {
		this.message_short = message_short;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
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
