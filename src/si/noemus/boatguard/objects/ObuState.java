package si.noemus.boatguard.objects;



public class ObuState {

	public ObuState(){
		
	}	
	
	private int idState;
	private int idObu;
	private String value;
	private String dateState;
	
	@Override
	public String toString(){
		return "OBUSTATE: idState: " + this.idState + ", idObu:" + this.idObu + ", value:" + this.value + ", dateState" + this.dateState;
	}

	public int getIdState() {
		return idState;
	}

	public void setIdState(int idState) {
		this.idState = idState;
	}

	public int getIdObu() {
		return idObu;
	}

	public void setIdObu(int idObu) {
		this.idObu = idObu;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDateState() {
		return dateState;
	}

	public void setDateState(String dateState) {
		this.dateState = dateState;
	}



}
