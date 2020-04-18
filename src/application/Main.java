package application;
	
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


public class Main extends Application {
	
	@SuppressWarnings({"static-access"})
	@Override
	public void start(Stage primaryStage) {
		try {
			
			//Create new Grid Pane for our app
			GridPane grid = new GridPane();
			grid.setVgap(10); //element padding so it looks nice
			grid.setHgap(10);
			grid.setPadding(new Insets(20, 20, 20, 20));
			
			//header text
			Text header = new Text();
			header.setText("ISU Dining Menu Searcher");
			header.setId("header");
			grid.add(header, 0, 0, 2, 1); //put it in the grid at 0,0 with 2 colspan and 1 rowspan
			
			Text keywordLabel = new Text(); //create label for the keyword field
			keywordLabel.setText("Keywords:");
			keywordLabel.setTextAlignment(TextAlignment.RIGHT);
			keywordLabel.setId("textlabel1"); //set the CSS id
			grid.add(keywordLabel, 0, 1); //add it to the grid at 0,1
			grid.setHalignment(keywordLabel, HPos.RIGHT); //sets the horizontal alignment of the elements within a grid pane
			TextArea keywordField = new TextArea(); //create field for the keywords
			keywordField.setPromptText("Enter keywords here...");
			keywordField.prefWidth(Region.USE_COMPUTED_SIZE); //sets width and stuff
			keywordField.prefHeight(Region.USE_COMPUTED_SIZE);
			keywordField.setWrapText(true); //we want it to wrap the text
			grid.add(keywordField, 1, 1); //add the field next to the label at 1,1
			
			Text daysLabel = new Text(); //create label for the number of days ahead to search
			daysLabel.setText("# of days to look ahead:");
			daysLabel.setId("textlabel2"); //set the CSS id
			grid.add(daysLabel, 0, 2); //add it to the grid at 0,2
			TextField daysField = new TextField(); //create the field for the days
			keywordField.setPrefColumnCount(3); //set the column count of the days field to pretty small
			grid.add(daysField, 1, 2); //add the field at 1,2
			
			Button search = new Button("Search");
			search.setPrefWidth(80);
			search.setId("searchbutton");
			List<Text> resultList = new ArrayList<Text>();
			search.setOnAction(new EventHandler<ActionEvent>() {
				
				@SuppressWarnings("deprecation")
				@Override
				public void handle(ActionEvent arg0) {
					for (Text t : resultList) {
						grid.getChildren().remove(t);
					}
					resultList.clear();
					int numDays = Integer.parseInt(daysField.getText());
					String[] keywords = keywordField.getText().split(", ");
					for (int i = 0; i < numDays; i++) {
						int days = i;
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DAY_OF_MONTH, days);
						Date d1 = new Date(1969, 11, 31);
						Date d2 = new Date();
						d2.setDate(cal.get(Calendar.DAY_OF_MONTH));
						d2.setMonth(cal.get(Calendar.MONTH));
						d2.setYear(cal.get(Calendar.YEAR));
						
						long diff = d2.getTime() - d1.getTime();
						long daysBetween = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
						
						if (daysBetween < 0) continue;
						long urlId = (86400 * (daysBetween + 1));
						String[] diningCenters = {
								"union-drive-marketplace-2-2",
								"seasons-marketplace-2-2",
								"friley-windows-2-2",
								"conversations-2",
								"storms-dining"
						};
						for (String dc : diningCenters) {
							String url = "https://www.dining.iastate.edu/wp-json/dining/menu-hours/get-single-location/?slug=" + dc + "&time=" + urlId;
							String data = null;
							String dcName = dc.replaceAll("-2", "").replaceAll("-", " ");
							try {
								HTMLMenuGrabber grabber = new HTMLMenuGrabber(url);
								data = grabber.grab().toLowerCase();
								for (String key : keywords) {
									if (data.contains(key.toLowerCase())) {
										Text newText = new Text();
										newText.setText("Found \"" + key + "\" on " + (d2.getMonth() + 1) + "/" + d2.getDate() + "/" + d2.getYear() + " at " + capitalize(dcName));
										resultList.add(newText);
										grid.add(newText, 0, resultList.size() + 3);
									}
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
				}
				
			});
			grid.add(search, grid.getColumnCount() - 1, grid.getRowCount());
			grid.setHalignment(search, HPos.RIGHT);
			
			ColumnConstraints constraints = new ColumnConstraints();
			constraints.setMinWidth(100);
			grid.getColumnConstraints().add(constraints);
			
			ScrollPane scroll = new ScrollPane();
			grid.setId("pane");
			scroll.setPrefSize(400, 500);
			grid.setPrefSize(primaryStage.getWidth(),primaryStage.getHeight());
			scroll.setContent(grid);
			
			Scene scene = new Scene(scroll);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private static String capitalize(String s) {
		String finalString = "";
		String[] split = s.split(" ");
		for (String word : split) {
			word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
			finalString += word + " ";
		}
		return finalString;
	}
}
