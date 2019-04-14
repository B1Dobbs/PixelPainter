package controller;

import model.Model;
import model.Model.Pixel;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent; 
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class Controller {
    Model model;
    EventHandler<MouseEvent> handler = MouseEvent::consume;
    
    @FXML
    Button paintTool;
    @FXML
    Button eraserTool;
    @FXML
    Slider imageOpacity;
    @FXML
    Slider drawingOpacity;
//    @FXML 
//    Button moveTool;
//    @FXML 
//    Button selectionTool;
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
    
    private int NumPixelsX = 50;
    private int NumPixelsY = 50;

    public void initialize() {
        this.model = new Model();
    	
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
        	if(!model.getPixels().containsKey(pixel.getKey())) {
        		pixel.setColor(color);
        		model.addPixel(pixel.getKey(), pixel);
        	}
    	}
    }
     
     public void setUIElements() {
    	Image paintToolIcon = new Image("/images/paintTool.png",30,30,false,true);
 		paintTool.setGraphic(new ImageView(paintToolIcon));
 		Image eraserToolIcon = new Image("/images/eraserTool.png",30,30,false,true);
 		eraserTool.setGraphic(new ImageView(eraserToolIcon));
 		Image moveToolIcon = new Image("/images/moveTool.png",25,25,false,true);
 		//moveTool.setGraphic(new ImageView(moveToolIcon));
 		Image selectionToolIcon = new Image("/images/selectionTool.png",25,25,false,true);
 		//selectionTool.setGraphic(new ImageView(selectionToolIcon));
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
    	 
    	 playMenuItem.setOnAction((new EventHandler<ActionEvent>() {
    		    @Override
    		    public void handle(ActionEvent arg0) {
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
    		            index += 0.1;
    		        }
    		    }
    		}));
     }
     
     public EventHandler<ActionEvent> getColorPickerEventHandler(){ 
    	 return new EventHandler<ActionEvent>() 
         { 
             @Override 
             public void handle(ActionEvent e) 
             { 
            	 Color color = colorPicker.getValue();
            	 model.setSelectedColor(color);
            	 if(model.getPrimarySelected()) {
            		 model.setPrimaryColor(color);
            		 primaryColor.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
            	 } else {
            		 model.setSecondaryColor(color);
            		 secondaryColor.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
            	 }
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
         }
     }
     
     @FXML
     private void openFileExplorer(ActionEvent event) {
    	 Button fileButton = (Button) event.getSource();
    	 FileChooser fileChooser = new FileChooser();
    	 fileChooser.setTitle("Open Resource File");
    	 fileChooser.getExtensionFilters().add(new ExtensionFilter("*.png", "*.pdf", "*.jpg"));
    	 File selectedFile = fileChooser.showOpenDialog(fileButton.getScene().getWindow());
    	 //File selectedFile = fileChooser.showOpenDialog(null);
    	 if (selectedFile != null) {
    		 Image image = new Image(selectedFile.toURI().toString());
    		 backgroundImage.setImage(image);
		}
		else {
		    System.out.println("File selection cancelled");
		}
    	 
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
	    pixelBoard.setGridLinesVisible(true);
	   
     }

}