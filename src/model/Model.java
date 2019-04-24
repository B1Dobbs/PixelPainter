package model;

import java.util.LinkedHashMap;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;

public class Model{

    private LinkedHashMap<String,Pixel> paintedPixels = new LinkedHashMap<>();
    
    private Pixel[][] boardPixels = new Pixel[50][50];

	private Image background;
    
    private int backgroundOpacity;
    
    private String selectedTool = "paintTool";
   
    private Color selectedColor = Color.BLACK;
    
    private Boolean primarySelected = true;
    
    private Color primaryColor = Color.WHITE;
    private Color secondaryColor = Color.BLACK;
    

    public Color getPrimaryColor() {
		return primaryColor;
	}


	public void setPrimaryColor(Color primaryColor) {
		this.primaryColor = primaryColor;
	}


	public Color getSecondaryColor() {
		return secondaryColor;
	}


	public void setSecondaryColor(Color secondaryColor) {
		this.secondaryColor = secondaryColor;
	}


	public Boolean getPrimarySelected() {
		return primarySelected;
	}


	public void setPrimarySelected(Boolean primarySelected) {
		this.primarySelected = primarySelected;
	}


	public LinkedHashMap<String,Pixel> getPixels() {
		return paintedPixels;
	}


	public void setPixels(LinkedHashMap<String,Pixel> pixels) {
		this.paintedPixels = pixels;
	}
	
	public void addPixel(String coordinates, Pixel pixel) {
		paintedPixels.put(coordinates, pixel);
	}
	
	public void removePixel(String coordinates) {
		paintedPixels.remove(coordinates);
	}
	
	public Pixel getBoardPixel(int x, int y) {
		return boardPixels[x][y];
	}


	public void setBoardPixels(Pixel pixel) {
		this.boardPixels[pixel.x][pixel.y] = pixel;
	}


	public Image getBackground() {
		return background;
	}


	public void setBackground(Image background) {
		this.background = background;
	}


	public int getBackgroundOpacity() {
		return backgroundOpacity;
	}


	public void setBackgroundOpacity(int backgroundOpacity) {
		this.backgroundOpacity = backgroundOpacity;
	}


	public String getSelectedTool() {
		return selectedTool;
	}


	public void setSelectedTool(String selectedTool) {
		this.selectedTool = selectedTool;
	}


	public Color getSelectedColor() {
		return selectedColor;
	}


	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}


	public class Pixel extends StackPane {
    	
    	int y;
    	int x;	
    	Color color;
    	String key;

		public String getKey() {
			return x + ":" + y;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}
       

    }
    
}