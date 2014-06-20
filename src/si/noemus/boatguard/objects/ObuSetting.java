package si.noemus.boatguard.objects;



public class ObuSetting {

	public ObuSetting(){
		
	}	
	
	private int id_setting;
	private String code;
	private String value;
	private String type;
	
	@Override
	public String toString(){
		return "OBUSETTING: idSetting: " + this.id_setting + ", code:" + this.code + ", value:" + this.value + ", type" + this.type;
	}


	public int getIdSetting() {
		return id_setting;
	}
	public void setIdSetting(int id_setting) {
		this.id_setting = id_setting;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
