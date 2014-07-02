package si.noemus.boatguard.components;

public class NavDrawerItem {
	private String title;
    private String text;
    private int image;
    private int colorBackground;
    private int titleColor;
    private int textColor;
     
    public NavDrawerItem(){}
 
    public NavDrawerItem(String title){
        this.title = title;
    }
     
    public NavDrawerItem(String title, String text, int image, int colorBackground, int titleColor, int textColor){
        this.title = title;
        this.text = text;
        this.image = image;
        this.colorBackground = colorBackground;
        this.titleColor = titleColor;
        this.textColor = textColor;
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

	public int getColorBackground() {
		return colorBackground;
	}

	public void setColorBackground(int colorBackground) {
		this.colorBackground = colorBackground;
	}

	public int getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(int titleColor) {
		this.titleColor = titleColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
    
}
