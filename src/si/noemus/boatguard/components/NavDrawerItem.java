package si.noemus.boatguard.components;

public class NavDrawerItem {
	private String title;
    private String text;
    private int image;
     
    public NavDrawerItem(){}
 
    public NavDrawerItem(String title){
        this.title = title;
    }
     
    public NavDrawerItem(String title, String text, int image){
        this.title = title;
        this.text = text;
        this.image = image;
    }
     
    public String getTitle(){
        return this.title;
    }
     
    public String getText(){
        return this.text;
    }
     
    public void setTitle(String title){
        this.title = title;
    }
     
    public void setText(String text){
        this.text = text;
    }

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}
    
}
