package application;

import java.io.File;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Main extends Application {
	public File file = null;
	public FoodData foodData = null;
	public ListView<FoodItem> listOfFoods;
	public Label numberLabel;
	public ListView<FoodItem> mealList;
	@Override
	public void start(Stage primaryStage) {
		try {

			BorderPane root = new BorderPane();
			MenuBar bar = new MenuBar();
			Menu fileMenu = new Menu("File");
			fileMenu.getItems().add(new MenuItem("Clos"));
			Menu helpMenu = new Menu("Help");
			helpMenu.getItems().add(new MenuItem("About"));
			bar.getMenus().add(fileMenu);
			bar.getMenus().add(helpMenu);
			// root.setPadding(new Insets(40, 20, 10, 40));
			HBox h = new HBox(10);
			h.setAlignment(Pos.CENTER_LEFT);
			TextField t = new TextField();
			h.getChildren().add(new Label("Selected File:"));
			h.getChildren().add(t);
			h.getChildren().add(new Button("Browse") {
				{
					this.setOnAction(e -> {
						FileChooser fileChooser = new FileChooser();
						fileChooser.setTitle("Open Resource File");
						file = fileChooser.showOpenDialog(null);
						t.setText(file.getName());
						foodData = new FoodData();
				        foodData.loadFoodItems(file.getAbsolutePath());
				        String[] names = new String[foodData.getAllFoodItems().size()];
//				        int index = 0;
//				        for(FoodItem f: foodData.getAllFoodItems()) {
//				        	names[index++] = f.getName();
//				        }
				        ObservableList<FoodItem> items = FXCollections.observableArrayList(foodData.getAllFoodItems());
				        listOfFoods.setItems(items);
						numberLabel.setText("# of items in Food List: " + listOfFoods.getItems().size());
					});
				}
			});
			HBox title = new HBox(10);
			Label titleOfApp = new Label("Welcome to Weight Watchers 2.0");
			titleOfApp.setFont(new Font("Arial",40));
			title.getChildren().add(titleOfApp);
			title.setPadding(new Insets(0,0,0,150));
			h.getChildren().add(title);
			h.setPadding(new Insets(15, 20, 10, 40));
			VBox v = new VBox();
			// v.setPadding(new Insets(40, 20, 10, 40));
			v.setSpacing(40);
			v.getChildren().add(bar);
			v.getChildren().add(h);
			v.getChildren().add(new Separator(Orientation.HORIZONTAL));
			HBox main = new HBox(10);
			main.setPadding(new Insets(10, 0, 0, 50));
			main.setMaxHeight(400);
			main.setMaxWidth(400);
			main.getChildren().add(new VBox(10) {
				{
					Label smallTitle = new Label("List Of Foods");
					smallTitle.setFont(new Font("Arial", 30));
					this.getChildren().add(smallTitle);
					listOfFoods = new ListView<FoodItem>();
					listOfFoods.setMinHeight(400);
//					ObservableList<String> items = FXCollections.observableArrayList("Apple Pie", "Banana",
//							"Carrot Cake", "Donut", "Elephant Ears", "Funnel Cake", "Garlic Bread", "HotDog",
//							"Ice Cream", "Jalapeno Poppers", "Kale", "Lasagna");
//					listOfFoods.setItems(items);
					listOfFoods.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
					this.getChildren().add(numberLabel = new Label("# of items in Food List: " + listOfFoods.getItems().size()));
					this.getChildren().add(listOfFoods);
					this.getChildren().add(new Button("Add Selected Foods to Meal List") {
					    {
					        this.setOnAction(e -> {
					            mealList.setItems(FXCollections.observableArrayList(listOfFoods.getSelectionModel().getSelectedItems()));
					        });
					    }
					});
					this.getChildren().add(new Button("Add To Food List") {
						{
							this.setOnAction(e -> {
								Stage popupStage = new Stage();
								AnchorPane popupPane = new AnchorPane();
								popupPane.setMaxHeight(400);
								popupPane.setMaxWidth(400);
								popupPane.getChildren().add(new VBox(10){
									{
										this.getChildren().add(new HBox(10){
											{
												this.setPadding(new Insets(25,25,25,25));
												this.getChildren().add(new Label("Name:"));
												this.getChildren().add(new TextField());
											}
										});this.getChildren().add(new HBox(10){
											{
												this.setPadding(new Insets(0,25,0,25));
												this.getChildren().add(new Label("Calories:"));
												this.getChildren().add(new TextField());
											}
										});this.getChildren().add(new HBox(10){
											{
												this.setPadding(new Insets(0,25,0,25));
												this.getChildren().add(new Label("Carbs:"));
												this.getChildren().add(new TextField());
											}
										});this.getChildren().add(new HBox(10){
											{
												this.setPadding(new Insets(0,25,0,25));
												this.getChildren().add(new Label("Protein:"));
												this.getChildren().add(new TextField());
											}
										});this.getChildren().add(new HBox(10){
											{
												this.setPadding(new Insets(0,25,0,25));
												this.getChildren().add(new Label("Fiber:"));
												this.getChildren().add(new TextField());
											}
										});this.getChildren().add(new HBox(10){
											{
												this.setPadding(new Insets(0,25,0,25));
												this.getChildren().add(new Label("Fat:"));
												this.getChildren().add(new TextField());
											}
										});this.getChildren().add(new HBox(10){
											{
												this.setPadding(new Insets(0,25,0,25));
												this.getChildren().add(new Button("Apply"));
												this.getChildren().add(new Button("Close"));
											}
										});
									}
								});
								Scene scene = new Scene(popupPane, 400, 400);
								popupStage.setScene(scene);
								popupStage.show();
							});
						}
					});
				}
			});
			root.setLeft(main);
			root.setTop(v);
			HBox meal = new HBox(10);
			meal.setPadding(new Insets(10, 50, 0, 0));
			meal.setMaxHeight(600);
			meal.setMaxWidth(400);
			meal.getChildren().add(new VBox(20) {
				{
					Label selectedFoods = new Label("Selected Foods for Meal");
					selectedFoods.setAlignment(Pos.CENTER_RIGHT);
					selectedFoods.setFont(new Font("Arial", 30));
					this.getChildren().add(selectedFoods);
					mealList = new ListView<FoodItem>();
					mealList.setPrefHeight(900);
					mealList.setPrefWidth(400);
					this.getChildren().add(mealList);
					this.getChildren().add(new HBox(10) {
					    {
					        this.getChildren().add(new Button("Remove From Meal"));
					        this.getChildren().add(new Button("Analyze Nutritional Analysis"));
					    }
					});
					Label analysis = new Label("Nutritional Analysis");
					analysis.setFont(new Font("Arial", 30));
					this.getChildren().add(analysis);
					ListView<String> nutriAnalysis = new ListView<String>();
					nutriAnalysis.setPrefHeight(900);
					nutriAnalysis.setPrefWidth(400);
					this.getChildren().add(nutriAnalysis);
				}
			});
			root.setRight(meal);
			VBox all = new VBox(10);
			HBox middle = new HBox(10);
			VBox middleLeft = new VBox(10);
			VBox middleRight = new VBox(10);

			middleLeft.setPadding(new Insets(10, 50, 0, 0));
			middleLeft.setMaxHeight(400);
			middleLeft.setMaxWidth(200);
			middleLeft.getChildren().add(new VBox(10) {
				{
					this.getChildren().add(new CheckBox("Name Filter"));
					this.getChildren().add(new TextField());
					this.getChildren().add(new CheckBox("Calorie Filter"));
					this.getChildren().add(new TextField());
					this.getChildren().add(new CheckBox("Carb Filter"));
					this.getChildren().add(new TextField());
				}
			});
			middleRight.setPadding(new Insets(10, 50, 0, 0));
			middleRight.setMaxHeight(400);
			middleRight.setMaxWidth(200);
			middleRight.getChildren().add(new VBox(10) {
				{
					this.getChildren().add(new CheckBox("Protein Filter"));
					this.getChildren().add(new TextField());
					this.getChildren().add(new CheckBox("Fiber Filter"));
					this.getChildren().add(new TextField());
					this.getChildren().add(new CheckBox("Fat Filter"));
					this.getChildren().add(new TextField());
				}
			});

			middle.setPadding(new Insets(100, 0, 0, 105));
			middle.getChildren().add(middleLeft);
			middle.getChildren().add(middleRight);
			all.getChildren().add(middle);
			HBox button = new HBox(10);
			Button na = new Button("Apply");
			button.getChildren().addAll(na);
			Button help = new Button("Help");
			help.setTooltip(new Tooltip("Select the filters you want by clicking the appropriate checkboxes. Input the correct comparator and number value (Ex: > 50)"));
			
			button.getChildren().add(help);
			button.setPadding(new Insets(10,0,0,225));
    
			all.getChildren().add(button);
			root.setCenter(all);

			Scene scene = new Scene(root, 1280, 800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setOnCloseRequest(e -> {System.exit(1);});
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	    launch(args);
	}
}
