package application;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
	public static FoodData foodData = null;
	public MealBuilder mealList = new MealBuilder();
	public ListView<FoodItem> listOfFoods;
	public Label numberLabel;
	public ListView<FoodItem> mealViewer;
	public BarChart nutriAnalysis;
	public FormType[] forms = new FormType[6];
	public TextField[] fields = new TextField[7];
	public Button apply = new Button("Apply");
	public static Comparator<FoodItem> lexicographicOrder = new Comparator<FoodItem>() {

		@Override
		public int compare(FoodItem arg0, FoodItem arg1) {
			return arg0.getName().compareTo(arg1.getName());
		}
    	
    };
	@Override
	public void start(Stage primaryStage) {
		try {

			BorderPane root = new BorderPane();
			MenuBar bar = new MenuBar();
			Menu fileMenu = new Menu("File");
			fileMenu.getItems().add(new MenuItem("Close"));
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
						ObservableList<FoodItem> items = FXCollections.observableArrayList(foodData.getAllFoodItems());
						listOfFoods.setItems(items);
						numberLabel.setText("# of items in Food List: " + listOfFoods.getItems().size());
					});
				}
			});
			HBox title = new HBox(10);
			Label titleOfApp = new Label("Welcome to Weight Watchers 2.0");
			titleOfApp.setFont(new Font("Arial", 40));
			title.getChildren().add(titleOfApp);
			title.setPadding(new Insets(0, 0, 0, 150));
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
					listOfFoods.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
					this.getChildren()
							.add(numberLabel = new Label("# of items in Food List: " + listOfFoods.getItems().size()));
					this.getChildren().add(listOfFoods);
					this.getChildren().add(new Button("Add Selected Foods to Meal List") {
						{
							this.setOnAction(e -> {
								for (FoodItem f : listOfFoods.getSelectionModel().getSelectedItems()) {
									mealList.addToMeal(f);
								}
								mealViewer.setItems(FXCollections.observableArrayList(mealList.getMeal()));
							});
						}
					});
					this.getChildren().add(new Button("Add To Food List") {
						{
							this.setOnAction(e -> {
								apply.setOnAction(ee -> {
									FoodItem f = new FoodItem(fields[1].getText(), fields[0].getText());
									f.addNutrient("calories", Double.parseDouble(fields[2].getText()));
									f.addNutrient("carbohydrate", Double.parseDouble(fields[3].getText()));
									f.addNutrient("protein", Double.parseDouble(fields[4].getText()));
									f.addNutrient("fiber", Double.parseDouble(fields[5].getText()));
									f.addNutrient("fat", Double.parseDouble(fields[6].getText()));
									listOfFoods.getItems().add(f);
									listOfFoods.getItems().sort(lexicographicOrder);
									foodData.addFoodItem(f);
								});
								Stage popupStage = new Stage();
								AnchorPane popupPane = new AnchorPane();
								popupPane.setMaxHeight(400);
								popupPane.setMaxWidth(400);
								popupPane.getChildren().add(new VBox(10) {
									{
										this.getChildren().add(new HBox(10) {
											{
												this.setPadding(new Insets(25, 25, 25, 25));
												this.getChildren().add(new Label("Name:"));
												this.getChildren().add(fields[0] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10) {
											{
												this.setPadding(new Insets(0, 25, 0, 25));
												this.getChildren().add(new Label("ID:"));
												this.getChildren().add(fields[1] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10) {
											{
												this.setPadding(new Insets(0, 25, 0, 25));
												this.getChildren().add(new Label("Calories:"));
												this.getChildren().add(fields[2] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10) {
											{
												this.setPadding(new Insets(0, 25, 0, 25));
												this.getChildren().add(new Label("Carbs:"));
												this.getChildren().add(fields[3] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10) {
											{
												this.setPadding(new Insets(0, 25, 0, 25));
												this.getChildren().add(new Label("Protein:"));
												this.getChildren().add(fields[4] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10) {
											{
												this.setPadding(new Insets(0, 25, 0, 25));
												this.getChildren().add(new Label("Fiber:"));
												this.getChildren().add(fields[5] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10) {
											{
												this.setPadding(new Insets(0, 25, 0, 25));
												this.getChildren().add(new Label("Fat:"));
												this.getChildren().add(fields[6] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10) {
											{
												this.setPadding(new Insets(0, 25, 0, 25));
												this.getChildren().add(apply);
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
					mealViewer = new ListView<FoodItem>();
					mealViewer.setPrefHeight(900);
					mealViewer.setPrefWidth(400);
					mealViewer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
					this.getChildren().add(mealViewer);

					this.getChildren().add(new HBox(10) {
						{
							this.getChildren().add(new Button("Remove From Meal") {
								{
									this.setOnAction(e -> {

										for (FoodItem f : mealViewer.getSelectionModel().getSelectedItems()) {
											mealList.removeFromMeal(f);
										}
										mealViewer.setItems(FXCollections.observableArrayList(mealList.getMeal()));
									});
								}
							});
							this.getChildren().add(new Button("Analyze Nutritional Analysis") {
								{
									this.setOnAction(e -> {
										Stage popupStage = new Stage();
										AnchorPane popupPane = new AnchorPane();
										popupPane.setMaxHeight(400);
										popupPane.setMaxWidth(900);
										CategoryAxis xAxis = new CategoryAxis();
										xAxis.setLabel("Nutrient");
										NumberAxis yAxis = new NumberAxis();
										yAxis.setLabel("Total");
										BarChart nutriAnalysis = new BarChart(xAxis, yAxis);
										nutriAnalysis.setPrefHeight(400);
										nutriAnalysis.setPrefWidth(500);
										XYChart.Series dataSeries1 = new XYChart.Series();
										dataSeries1.setName("Nutrients within Selected Meal List");
										mealList.nutriAnalysis();
										dataSeries1.getData()
												.add(new XYChart.Data("Calories", mealList.getTotalCals()));
										dataSeries1.getData()
												.add(new XYChart.Data("Fat (grams)", mealList.getTotalFat()));
										dataSeries1.getData()
												.add(new XYChart.Data("Carbs (grams)", mealList.getTotalCarbs()));
										dataSeries1.getData()
												.add(new XYChart.Data("Fiber (grams)", mealList.getTotalFiber()));
										dataSeries1.getData()
												.add(new XYChart.Data("Protein (grams)", mealList.getTotalProtein()));
										nutriAnalysis.getData().add(dataSeries1);
										popupPane.getChildren().add(nutriAnalysis);
										Scene scene = new Scene(popupPane, 900, 400);
										popupStage.setScene(scene);
										popupStage.show();
									});

								}
							});
						}
					});
//                    Label analysis = new Label("Nutritional Analysis");
//                    analysis.setFont(new Font("Arial", 30));
//                    this.getChildren().add(analysis);

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
//			middleLeft.getChildren().add(new VBox(10) {
//				{
//					this.getChildren().add(checkboxes[0] = new CheckBox("Name Filter"));
//					this.getChildren().add(fields[0] = new TextField() {
//						{
//							this.setOnAction(e -> {
//								ObservableList<FoodItem> items = FXCollections
//										.observableArrayList(foodData.filterByName(this.getText()));
//								listOfFoods.setItems(items);
//								numberLabel.setText("# of items in Food List: " + listOfFoods.getItems().size());
//							});
//						}
//					});
//					this.getChildren().add(checkboxes[1] = new CheckBox("Calorie Filter"));
//					this.getChildren().add(fields[1] = new TextField());
//					this.getChildren().add(checkboxes[2] = new CheckBox("Carb Filter"));
//					this.getChildren().add(fields[2] = new TextField());
//				}
//			});
			middleLeft.getChildren().add(new VBox(10) {
				{
					this.getChildren().add(forms[0] = new FormType("Name Filter"));
					this.getChildren().add(forms[1] = new FormType("Calorie Filter"));
					this.getChildren().add(forms[2] = new FormType("Carb Filter"));
				}
			});
			middleRight.setPadding(new Insets(10, 50, 0, 0));
			middleRight.setMaxHeight(400);
			middleRight.setMaxWidth(200);
			middleRight.getChildren().add(new VBox(10) {
				{
//					this.getChildren().add(new CheckBox("Protein Filter"));
//					this.getChildren().add(new TextField());
//					this.getChildren().add(new CheckBox("Fiber Filter"));
//					this.getChildren().add(new TextField());
//					this.getChildren().add(new CheckBox("Fat Filter"));
//					this.getChildren().add(new TextField());
					this.getChildren().add(forms[3] = new FormType("Protein Filter"));
					this.getChildren().add(forms[4] = new FormType("Fiber Filter"));
					this.getChildren().add(forms[5] = new FormType("Fat Filter"));
				}
			});

			middle.setPadding(new Insets(100, 0, 0, 105));
			middle.getChildren().add(middleLeft);
			middle.getChildren().add(middleRight);
			all.getChildren().add(middle);
			HBox button = new HBox(10);
			Button na = new Button("Apply");
			na.setOnAction(e -> {
				List<FoodItem> list = new ArrayList<>(foodData.getAllFoodItems());
				for (FormType f : forms) {
					if(f.isSelected())
						f.filter(true, list);
				}
				ObservableList<FoodItem> items = FXCollections.observableArrayList(list);
				listOfFoods.setItems(items);
				numberLabel.setText("# of items in Food List: " + listOfFoods.getItems().size());
			});
			button.getChildren().addAll(na);
			Button help = new Button("Help");
			help.setTooltip(new Tooltip(
					"Select the filters you want by clicking the appropriate checkboxes. Input the correct comparator and number value (Ex: > 50)"));

			button.getChildren().add(help);
			button.setPadding(new Insets(10, 0, 0, 225));

			all.getChildren().add(button);
			root.setCenter(all);

			Scene scene = new Scene(root, 1280, 800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setOnCloseRequest(e -> {
				System.exit(1);
			});
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}