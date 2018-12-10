package application;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
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
	public static Comparator<FoodItem> lexicographicOrder = (e,e1)->{
		return e.getName().compareTo(e1.getName());
	};
	public Stage popupStage = new Stage();

	@Override
	public void start(Stage primaryStage) {
		try {

			BorderPane root = new BorderPane();
			MenuBar bar = new MenuBar();
			Menu fileMenu = new Menu("File");
			fileMenu.getItems().add(new MenuItem("Close") {
				{
					this.setOnAction(e -> {
						Platform.exit();
					});
				}
			});
			foodData = new FoodData();
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
						if(file != null) {
							fileChooser.setInitialDirectory(file.getParentFile());
						}
						File choosenFile = fileChooser.showOpenDialog(null);
						if (choosenFile != null) {
							file = choosenFile;
							t.setText(file.getName());
							foodData = new FoodData();
							foodData.loadFoodItems(file.getAbsolutePath());
							ObservableList<FoodItem> items = FXCollections
									.observableArrayList(foodData.getAllFoodItems());
							listOfFoods.setItems(items);
							numberLabel.setText("# of items in Food List: " + listOfFoods.getItems().size());
						}
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
							popupStage = new Stage();
							this.setOnAction(e -> {
								apply.setOnAction(ee -> {
									FoodItem f = new FoodItem(fields[1].getText(), fields[0].getText().toLowerCase());
									f.addNutrient("Calories", Double.parseDouble(fields[2].getText()));
									f.addNutrient("Carbohydrate", Double.parseDouble(fields[3].getText()));
									f.addNutrient("Protein", Double.parseDouble(fields[4].getText()));
									f.addNutrient("Fiber", Double.parseDouble(fields[5].getText()));
									f.addNutrient("Fat", Double.parseDouble(fields[6].getText()));
									listOfFoods.getItems().add(f);
									listOfFoods.getItems().sort(lexicographicOrder);
									foodData.addFoodItem(f);
									numberLabel.setText("# of items in Food List: " + listOfFoods.getItems().size());
									popupStage.close();
								});
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
												this.getChildren().add(new Button("Close") {
													{
														this.setOnAction(e -> {
															popupStage.close();
														});
													}
												});
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
										BarChart<String, Double> nutriAnalysis = new BarChart(xAxis, yAxis);
										nutriAnalysis.setPrefHeight(400);
										nutriAnalysis.setPrefWidth(500);
										XYChart.Series<String, Double> dataSeries1 = new XYChart.Series();
										dataSeries1.setName("Nutrients within Selected Meal List");
										mealList.nutriAnalysis();
										dataSeries1.getData()
												.add(new XYChart.Data<String, Double>("Calories", mealList.getTotalCals()));
										dataSeries1.getData()
												.add(new XYChart.Data<String, Double>("Fat (grams)", mealList.getTotalFat()));
										dataSeries1.getData()
												.add(new XYChart.Data<String, Double>("Carbs (grams)", mealList.getTotalCarbs()));
										dataSeries1.getData()
												.add(new XYChart.Data<String, Double>("Fiber (grams)", mealList.getTotalFiber()));
										dataSeries1.getData()
												.add(new XYChart.Data<String, Double>("Protein (grams)", mealList.getTotalProtein()));
										nutriAnalysis.getData().add(dataSeries1);
										HBox information = new HBox();
										information.getChildren().add(nutriAnalysis);
										VBox amountSum = new VBox();
										amountSum.setPadding(new Insets(120,0,0,50));
										Label cals = new Label("Calories: " + mealList.getTotalCals());
										Label fat = new Label("Fat: " + mealList.getTotalFat());
										Label carbs = new Label("Carbs: " + mealList.getTotalCarbs());
										Label fiber = new Label("Fiber: " + mealList.getTotalFiber());
										Label protein = new Label("Protein: " + mealList.getTotalProtein());
										amountSum.getChildren().addAll(cals,fat,carbs,fiber,protein);
										information.getChildren().add(amountSum);
										popupPane.getChildren().addAll(information);
										Scene scene = new Scene(popupPane, 700, 400);
										popupStage.setScene(scene);
										popupStage.show();
									});

								}
							});
						}
					});
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
				List<String> filters = new ArrayList<>();
				for(int i = 1; i < forms.length; i++) {
					filters.add(forms[i].getText());
				}
				forms[0].filter(list);
				for (int i = 1; i < forms.length; i++) {
					forms[i].filter(list, filters);
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