package controller;

import model.Model;
import model.Model.Pixel;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class Controller {
    Model model;
    EventHandler<MouseEvent> handler = MouseEvent::consume;
    Stage stage;

    @FXML
    SplitPane splitPane1;
    @FXML
    Button paintTool;
    @FXML
    Button eraserTool;
    @FXML
    Slider imageOpacity;
    @FXML
    Slider drawingOpacity;
    @FXML 
    Button eyedropTool;
    @FXML 
    Button undoTool;
    @FXML
    ImageView backgroundImage;
    @FXML
    GridPane pixelBoard;
    @FXML
    ColorPicker colorPicker;
    @FXML
    Pane primaryColor;
    @FXML
    Pane secondaryColor;
    @FXML
    MenuItem playMenuItem;
    @FXML
    MenuItem stopMenuItem;
    @FXML
    ImageView playImage;
    @FXML
    ImageView stopImage;
    @FXML
    CheckBox showGrid;
    
    private int NumPixelsX = 50;
    private int NumPixelsY = 50;
    ArrayList<PauseTransition> pixelTransitions = new ArrayList<>();

    public void initialize() {
        this.model = new Model();
        //splitPane1.setResizableWithParent(pixelBoard, true);
    	
        setUIElements();
        setGrid();
        addActionListeners();
    }
    

     public EventHandler<MouseEvent> getPixelEventHandler(Pixel pixel){ 
    	 return new EventHandler<MouseEvent>() 
        { 
            @Override 
            public void handle(MouseEvent e) 
            { 
            	pixel.startFullDrag();
            	paintPixels(pixel);
            } 
        };  
    }
     
     public EventHandler<MouseEvent> getPixelDragEventHandler(Pixel pixel){ 
    	 return new EventHandler<MouseEvent>() 
         { 
             @Override 
             public void handle(MouseEvent e) 
             { 
            	 paintPixels(pixel);
             }
         };  
     }
     
    public void paintPixels(Pixel pixel){
    	if(model.getSelectedTool() == "eraserTool") {
    		pixel.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
    		if(model.getPixels().containsKey(pixel.getKey())) {
    			model.removePixel(pixel.getKey());
    		}
    	} else if (model.getSelectedTool() == "paintTool") {
    		Color color = model.getSelectedColor();
        	pixel.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        	pixel.setColor(color);
        	if(model.getPixels().containsKey(pixel.getKey())) {
        		model.removePixel(pixel.getKey().toString());
        	}
        	model.addPixel(pixel.getKey(), pixel);
    	} else if (model.getSelectedTool() == "eyedropTool") {
    		int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
        	int y = (int) MouseInfo.getPointerInfo().getLocation().getY();
        	Color newColor = new Color(0,0,0,0);
        	try {
				Robot robot = new Robot();
				
				java.awt.Color awtColor = robot.getPixelColor(x, y) ;
				int r = awtColor.getRed();
				int g = awtColor.getGreen();
				int b = awtColor.getBlue();
				int a = awtColor.getAlpha();
				double opacity = a / 255.0 ;
				newColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		model.setSelectedColor(newColor);
    		setPrimarySecondaryColors(newColor);
    	} 
    }
    
    
     
     public void setUIElements() {
    	Image paintToolIcon = new Image("/images/paintTool.png",30,30,false,true);
 		paintTool.setGraphic(new ImageView(paintToolIcon));
 		paintTool.setTooltip(new Tooltip("Paint"));
 		Image eraserToolIcon = new Image("/images/eraserTool.png",30,30,false,true);
 		eraserTool.setGraphic(new ImageView(eraserToolIcon));
 		eraserTool.setTooltip(new Tooltip("Eraser"));
 		Image eyedropToolIcon = new Image("/images/eyedropTool.png",30,30,false,true);// "C:\Users\BrookeDobbins\Development\HCI\PixelPainter\src\images\eyedropTool.png"
 		eyedropTool.setGraphic(new ImageView(eyedropToolIcon));
 		eyedropTool.setTooltip(new Tooltip("Eye Dropper"));
 		Image undoToolIcon = new Image("/images/undoTool.png",25,25,false,true);
 		undoTool.setGraphic(new ImageView(undoToolIcon));
 		undoTool.setTooltip(new Tooltip("Undo"));
 		Image playImageIcon = new Image("/images/play.png",125,125,false,true);
 		playImage.setImage(playImageIcon);
 		Image stopImageIcon = new Image("/images/stop.png",125,125,false,true);
 		stopImage.setImage(stopImageIcon);
     }
     
     public void addActionListeners(){
    	 colorPicker.setOnAction(getColorPickerEventHandler());
    	 imageOpacity.valueProperty().addListener(new ChangeListener<Number>() {
             public void changed(ObservableValue<? extends Number> ov,
                 Number old_val, Number new_val) {
                     backgroundImage.setOpacity(new_val.doubleValue());
             }
         });
    	 drawingOpacity.valueProperty().addListener(new ChangeListener<Number>() {
             public void changed(ObservableValue<? extends Number> ov,
                 Number old_val, Number new_val) {
                     pixelBoard.setOpacity(new_val.doubleValue());
             }
         });
     }
     
     @FXML
     public void playStopAnimation(ActionEvent e) {
    	 if(e.getSource() == playMenuItem) {
    		 System.out.println("Clearing transistions");
    		 for(PauseTransition entry : pixelTransitions) {
    			 entry.stop();	
    		 }
    		 pixelTransitions.clear();
	    	 for(Map.Entry<String, Pixel> entry : model.getPixels().entrySet()) {
		            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0));
		            BackgroundFill fill = new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY);
		            Pixel pixel = entry.getValue();
		            pauseTransition.setOnFinished(event -> pixel.setBackground(new Background(fill)));
		            pauseTransition.play();
		        }
		    	double index = 0.1;
		        for(Map.Entry<String, Pixel> entry : model.getPixels().entrySet()) {
		        	Pixel pixel = entry.getValue();
		            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(index));
		            BackgroundFill fill = new BackgroundFill(pixel.getColor(), CornerRadii.EMPTY, Insets.EMPTY);
		            pauseTransition.setOnFinished(event -> pixel.setBackground(new Background(fill)));
		            pauseTransition.play();
		            pixelTransitions.add(pauseTransition);
		            index += 0.1;
		        }
    	 } else if(e.getSource() == stopMenuItem) {
    		 for(PauseTransition entry : pixelTransitions) {
    			 entry.stop();
		        	
    		 }
    		 for(Map.Entry<String, Pixel> entry : model.getPixels().entrySet()) {
		            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0));
		            Pixel pixel = entry.getValue();
		            BackgroundFill fill = new BackgroundFill(pixel.getColor(), CornerRadii.EMPTY, Insets.EMPTY);
		            pauseTransition.setOnFinished(event -> pixel.setBackground(new Background(fill)));
		            pauseTransition.play();
		        }
    		 
    	 }
    	 
     }
     
     public EventHandler<ActionEvent> getColorPickerEventHandler(){ 
    	 return new EventHandler<ActionEvent>() 
         { 
             @Override 
             public void handle(ActionEvent e) 
             { 
            	 Color color = colorPicker.getValue();
            	 model.setSelectedColor(color);
            	 setPrimarySecondaryColors(color);
             } 
         };  
     }
     
     public void setPrimarySecondaryColors(Color color) {
    	 if(model.getPrimarySelected()) {
    		 model.setPrimaryColor(color);
    		 primaryColor.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    	 } else {
    		 model.setSecondaryColor(color);
    		 secondaryColor.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    	 } 
     }
     
     public EventHandler<ActionEvent> getEyeDropperEventHandler(){ 
    	 return new EventHandler<ActionEvent>() 
         { 
             @Override 
             public void handle(ActionEvent e) 
             {
            	 double x = MouseInfo.getPointerInfo().getLocation().getX();
            	 double y = MouseInfo.getPointerInfo().getLocation().getY();
            	 
            	 Pixel pixel = (Pixel) e.getSource();
            	 Color color = colorPicker.getValue();
            	 model.setSelectedColor(color);
            	 
             } 
         };  
     }
     
     public void setGrid() {
    	double pixelDimentionX = pixelBoard.getPrefWidth() / NumPixelsX;
     	double pixelDimentionY = pixelBoard.getPrefHeight() / NumPixelsY;
    	System.out.println(pixelDimentionX + " " + pixelDimentionY);
     	pixelBoard.addEventFilter(MouseEvent.MOUSE_DRAGGED, handler);
     	for(int x = 0; x < NumPixelsX; x++){
           for(int y = 0; y < NumPixelsY; y++){
               Pixel pixel = model.new Pixel();
               pixel.setMinHeight(pixelDimentionY);
               pixel.setMinWidth(pixelDimentionX);
               pixelBoard.add(pixel, x + 1, y + 1);
               pixel.setX(x);
               pixel.setY(y);
               model.setBoardPixels(pixel);
               pixel.addEventHandler(MouseEvent.MOUSE_CLICKED, getPixelDragEventHandler(pixel));
               pixel.addEventHandler(MouseEvent.DRAG_DETECTED, getPixelEventHandler(pixel));
               pixel.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, getPixelDragEventHandler(pixel));
              
           }
     	}
    	 
     }
     
     @FXML
     private void changeTool(ActionEvent event) {
         if (event.getSource() == paintTool) {
             model.setSelectedTool("paintTool");
         } else if (event.getSource() == eraserTool) {
        	 model.setSelectedTool("eraserTool");
         } else if (event.getSource() == eyedropTool) {
        	 model.setSelectedTool("eyedropTool");
         }
     }
     
     @FXML
     private void undoAction(ActionEvent event) {
	 
    	 if(!model.getPixels().isEmpty()) {
	 		LinkedHashMap<String,Pixel> pixels = model.getPixels();
	 		Map.Entry entry = null;
	 		Iterator iter = pixels.entrySet().iterator();
	 		while (iter.hasNext()) { entry = (Map.Entry) iter.next(); }
	 		Pixel pixel = (Pixel) entry.getValue();
	 		pixel.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
	 		model.removePixel(entry.getKey().toString());
    	 }
         
     } 
     
     @FXML
     private void openFileExplorer(ActionEvent event) {
    	 Button fileButton = (Button) event.getSource();
    	 FileChooser fileChooser = new FileChooser();
    	 fileChooser.setTitle("Open Resource File");
    	 fileChooser.getExtensionFilters().addAll(
                 new FileChooser.ExtensionFilter("All Images", "*.*"),
                 new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                 new FileChooser.ExtensionFilter("PNG", "*.png")
          );
    	 fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    	 File selectedFile = fileChooser.showOpenDialog(fileButton.getScene().getWindow());
    	 //File selectedFile = fileChooser.showOpenDialog(null);
    	 if (selectedFile != null) {
    		 Image image = new Image(selectedFile.toURI().toString());
    		 backgroundImage.setImage(image);
    		// backgroundImage.imageProperty().set
		}
		else {
		    System.out.println("File selection cancelled");
		}
    	 
     }
     
     @FXML
     private void toggleGridLines(ActionEvent event) {
    	 Boolean isVisible = pixelBoard.gridLinesVisibleProperty().get();
    	 pixelBoard.setGridLinesVisible(!isVisible);
     }
     
     @FXML
     private void clearDrawing(ActionEvent event) {
    	 if(!pixelTransitions.isEmpty()) {
    		 for(PauseTransition entry : pixelTransitions) {
    			 entry.stop();
    		 }
    	 }
    	 for(Map.Entry<String, Pixel> entry : model.getPixels().entrySet()) {
	            BackgroundFill fill = new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY);
	            Pixel pixel = entry.getValue();
	            pixel.setBackground(new Background(fill));
	     } 
    	 model.getPixels().clear();
     }
     
     @FXML
     private void setPrimaryColor(MouseEvent event) {
    	 primaryColor.toFront();
    	 model.setPrimarySelected(true);
    	 model.setSelectedColor(model.getPrimaryColor());
     }
     
     @FXML
     private void setSecondaryColor(MouseEvent event) {
    	 secondaryColor.toFront();
    	 model.setPrimarySelected(false);
    	 model.setSelectedColor(model.getSecondaryColor());
     }
     
     @FXML
     public void saveDrawing()
     {
    	 pixelBoard.setGridLinesVisible(false);
    	 WritableImage image = pixelBoard.snapshot(new SnapshotParameters(), null);
    	 
    	 String separator = System.getProperty("file.separator");
    	 String fileLocation = System.getProperty("user.dir") + separator + "drawings" + separator;
    	 String fileName = "PixelPainter";
	    // TODO: probably use a file chooser here
	    File file = new File(fileLocation + fileName + ".png");
	    try {
	    	int index = 1;
	    	while(!file.createNewFile()) {
	    		//System.out.println("Next Name");
	    		file = new File(fileLocation + fileName + index + ".png");
	    		index++;
	    	}
	    	System.out.println("Saving Image");
	        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	        
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    if(showGrid.isSelected()) {
	    	pixelBoard.setGridLinesVisible(true);
	    }
	   
     } 
     
     @FXML
     public void saveAsDrawing(ActionEvent event) {
    	 MenuItem fileButton = (MenuItem) event.getSource();
    	 FileChooser fileChooser = new FileChooser();
    	 fileChooser.setTitle("Save As");
    	 fileChooser.getExtensionFilters().addAll(
                 new FileChooser.ExtensionFilter("All Images", "*.*"),
                 new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                 new FileChooser.ExtensionFilter("PNG", "*.png")
          );
    	 File selectedFile = fileChooser.showSaveDialog(fileButton.getParentPopup().getOwnerWindow());
    	 System.out.println(selectedFile.toString());
    	 pixelBoard.setGridLinesVisible(false);
    	 WritableImage image = pixelBoard.snapshot(new SnapshotParameters(), null);
    	 if (selectedFile != null) {
    		 try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", selectedFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
		    System.out.println("File selection cancelled");
		} 
    	 pixelBoard.setGridLinesVisible(true);
     }

}