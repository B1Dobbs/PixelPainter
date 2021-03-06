package application;
	
import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("ViewHtml.fxml"));
			primaryStage.setTitle("PixelPainter");
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewHtml.fxml"));
//			Controller controller = (Controller) loader.getController();
//			controller.setStage(primaryStage); // or what you want to do
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
