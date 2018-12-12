package application;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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

/**
 * This class represents the backend for managing all the operations associated
 * with FoodItems
 * 
 * @author Alex E, Theo K, Ellie B, Will M, Alex G
 */
public class Main extends Application {
	public File file = null; //will store the file that is imported into the program
	public File saveFile = null; //will store the file that will save food items into the program
	public static FoodData foodData = null; //FoodData stores the list of food as it is filtered
	public MealBuilder mealList = new MealBuilder(); //mealList will store instances of created meals
	public ListView<FoodItem> listOfFoods; //will display the list of foods
	public Label numberLabel; //number of food elements
	public ListView<FoodItem> mealViewer; //will display the meal list
	public BarChart nutriAnalysis; //will display the nutritional analysis as a bar graph
	public final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //Stores screen size
	public FormType[] forms = new FormType[6]; //Array holding all the filter fields
	public TextField[] fields = new TextField[7]; //Array holding all the fields to add a fooddata object to list
	public Button apply = new Button("Apply"); // apply button, defined globally for use in lambda expressions
	private final double WR = (screenSize.getWidth() / 1920); //Width ratio to make program look the same on every screen
	private final double HR = (screenSize.getHeight() / 1080); //Height ratio to make program look the same on every screen
	public TextField fileName = new TextField(); //filename textfield
	public static final Comparator<FoodItem> lexicographicOrder = (e, e1) -> { //Static comparator method to sort lexicographically
		return e.getName().compareTo(e1.getName());
	};
	private static final EventHandler<ActionEvent> HelpDialog = e -> { // Static EventHandler that makes a new help dialog with information
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("What is Choose to Lose?");
		alert.setContentText(
				"Welcome to Choose to Lose! Choose to lose is program that allows you to view the nutritional facts of a meal "
						+ "created by you. To begin, upload a file from your computer to create a list of foods or individually add food items using "
						+ "the add to food list button. To search for certain foods in your list, use the name and nutritional filters to the right of the food list. "
						+ "Once you find a food you like, add it to your meal list. Finally, to view the "
						+ "nutritional facts of a food item or your meal, use the Analyze Nutritional Analysis button below the meal list. Let's get started!");

		alert.showAndWait();
	};
	public Stage popupStage = new Stage(); //instantiates a stage

	/**
	 * This start method sets up the entire GUI.  The WR and HR variables contain ratios that are multiplied by all
	 * pixel numbers to make the GUI look the same on all screens.  The original testing screen was 1920 by 1080 and so
	 * the ratios are based off of those numbers.  The main border pane contains all the sub elements, including a custom element,
	 * FormType, which extends VBox and ties together the checkboxes and textfields in the filtering aspect of the GUI.
	 * The GUI utilizes anonymous classes and initialization blocks to save extra variable declarations and lines of code.
	 * The GUI is built to be userfriendly and intuitive.
	 * 
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			

			BorderPane root = new BorderPane();
			
			// menubar at the top of the screen including a file button (includes save, load and close) and a help button (includes about)
			MenuBar bar = new MenuBar();
			Menu fileMenu = new Menu("File");
			fileMenu.getItems().add(new MenuItem("Load") {
				{
					this.setOnAction(e -> loadFile()); //On click load file
				}
			});
			
			// save current food list 
			fileMenu.getItems().add(new MenuItem("Save") {
				{
					this.setOnAction(e -> {
						FileChooser fileChooser = new FileChooser(); //Create new fileChooser object
						fileChooser.setTitle("Save Resource File");
						if (saveFile != null) {
							fileChooser.setInitialDirectory(saveFile.getParentFile());
						}
						File choosenFile = fileChooser.showSaveDialog(null);
						if (choosenFile != null) {
							foodData.saveFoodItems(
									choosenFile.getAbsolutePath().contains(".") ? choosenFile.getAbsolutePath()
											: choosenFile.getAbsolutePath() + ".csv"); //If the choosen file doesn't define an extension, add csv extension
							saveFile = choosenFile; //Set the saveFile
						}

					});
				}
			});
			fileMenu.getItems().add(new MenuItem("Close") {
				{
					this.setOnAction(e -> {
						Platform.exit(); //On close exit platform
					});
				}
			});
			foodData = new FoodData();
			Menu helpMenu = new Menu("Help");
			helpMenu.getItems().add(new CustomMenuItem(new Label("About")) {
				{
					this.setOnAction(HelpDialog); //On click display helpdialog
				}
			});
			bar.getMenus().add(fileMenu);
			bar.getMenus().add(helpMenu);
			
			// top of page including the select a file feature and the title  
			HBox h = new HBox(10 * WR); //Multiply 10 by WR to scale on other screens
			h.setAlignment(Pos.CENTER_LEFT);
			h.getChildren().add(new Label("Select a File:") {
				{ 
					this.getStyleClass().add("filters"); // Add css style class to anonymous class
				}
			});
			h.getChildren().add(fileName);
			h.getChildren().add(new Button("Browse") {
				{
					this.setOnAction(e -> loadFile()); //oncliclk load filexs
				}
			});
			//code that creates the title of the program
			HBox title = new HBox(10 * WR);
			VBox name = new VBox(10 * HR);
			Label titleOfApp = new Label("Choose to Lose"); //title of app
			titleOfApp.getStyleClass().add("title");
			name.getChildren().add(titleOfApp);
			Label slogan = new Label("   The first step towards a happier and healthier life...");
			slogan.getStyleClass().add("slogan");// add css class to slogan
			name.getChildren().add(slogan);
			title.getChildren().add(name);
			title.setPadding(new Insets(0, 0, 0, 125 * WR));
			h.getChildren().add(title);
			h.setPadding(new Insets(15 * HR, 20 * WR, 0, 40 * WR));
			VBox v = new VBox(); //stores all of the components that wil;l be part of the title
			v.setSpacing(40 * HR);
			v.getChildren().add(bar);
			v.getChildren().add(h);
			v.getChildren().add(new Separator(Orientation.HORIZONTAL));
			
			// primary functioning area of the program 
			HBox main = new HBox(10 * WR);
			main.setPadding(new Insets(10 * HR, 0, 0, 50 * WR));
			main.setMaxHeight(400 * HR);
			main.setMaxWidth(400 * WR);
			
			// left side of main area including list of foods list and associated buttons 
			main.getChildren().add(new VBox(10 * HR) { 
				{
					Label smallTitle = new Label("List Of Foods");
					smallTitle.setPadding(new Insets(0, 0, 0, 50 * WR));
					smallTitle.getStyleClass().add("white-labels");
					this.getChildren().add(smallTitle);
					listOfFoods = new ListView<FoodItem>();
//					listOfFoods.setCellFactory(new Callback<ListView<FoodItem>, ListCell<FoodItem>>() { //Set a cellfactory callback
//						/**
//							This callback will display a tooltip popup on every element in the listOfFoods that displays nutritional
//							information for that element
//						*/
//			          public ListCell<FoodItem> call(ListView<FoodItem> param) {
//			            final Label leadLbl = new Label();
//			            final Tooltip tooltip = new Tooltip();
//			            final ListCell<FoodItem> cell = new ListCell<FoodItem>() {
//			              @Override
//			              public void updateItem(FoodItem foodItem, boolean empty) {
//			                super.updateItem(foodItem, empty);
//			                if (foodItem != null) {
//			                  leadLbl.setText(foodItem.getName());
//			                  setText(foodItem.getName());
//			                  tooltip.setText("Calories: " + foodItem.getNutrientValue("calories") + "\n"
//			                		  + "Carbs: " + foodItem.getNutrientValue("carbohydrate") + "\n"
//			                		  + "Fat: " + foodItem.getNutrientValue("fat") + "\n"
//			                		  + "Fiber: " + foodItem.getNutrientValue("fiber") + "\n"
//			                		  + "Protein: " + foodItem.getNutrientValue("protein") + "\n");
//			                  setTooltip(tooltip);
//			                }
//			              }
//			            }; // ListCell
//			            return cell;
//			          }
//			        }); // setCellFactory
					listOfFoods.setMinHeight(400 * HR);
					listOfFoods.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
					this.getChildren()
							.add(numberLabel = new Label("# of items in Food List: " + listOfFoods.getItems().size()));
					numberLabel.setPadding(new Insets(0, 0, 5 * HR, 22 * WR));
					numberLabel.getStyleClass().add("number-of-foods");
					this.getChildren().add(listOfFoods);
					this.getChildren().add(new Button("Add Selected Foods to Meal List") {
						{
							this.setOnAction(e -> {
								for (FoodItem f : listOfFoods.getSelectionModel().getSelectedItems()) {
									mealList.addToMeal(f);
								}
								mealViewer.setItems(FXCollections.observableArrayList(mealList.getMeal())); //Get a meal, make sure there are no duplicates
							});
						}
					});
					
				    // 
					this.getChildren().add(new Button("Add To Food List") {
						{
							// popup allowing the user to add a individual food item with name, id and nutritional facts
							popupStage = new Stage();
							this.setOnAction(e -> { //when "Add To Food List" is clicked, the code below will be executed
								apply.setOnAction(ee -> {
									try {
									FoodItem f = new FoodItem(fields[1].getText(), fields[0].getText().toLowerCase()); //instantiates a new foodItem to add to the list
									//based on information that the user inputs
									//each nutrient user input is added to the corresponding nutritional value
									if(Double.parseDouble(fields[2].getText()) >= 0 && 
											Double.parseDouble(fields[3].getText()) >= 0 && 
											Double.parseDouble(fields[4].getText()) >= 0 && 
											Double.parseDouble(fields[5].getText()) >= 0 &&
											Double.parseDouble(fields[6].getText()) >= 0) {
										f.addNutrient("calories", Double.parseDouble(fields[2].getText())); 
										f.addNutrient("carbohydrate", Double.parseDouble(fields[3].getText()));
										f.addNutrient("protein", Double.parseDouble(fields[4].getText()));
										f.addNutrient("fiber", Double.parseDouble(fields[5].getText()));
										f.addNutrient("fat", Double.parseDouble(fields[6].getText()));
										listOfFoods.getItems().add(f);
										listOfFoods.getItems().sort(lexicographicOrder);
										foodData.addFoodItem(f);
										numberLabel.setText("# of items in Food List: " + listOfFoods.getItems().size());
										popupStage.close();
									}else throw new NumberFormatException();
									// catch error if user enters input incorrectly 
									}catch(NumberFormatException ne) {
										Alert alert = new Alert(AlertType.ERROR);
										alert.setTitle("Error Dialog");
										alert.setHeaderText("Opps, looks like your nutrition values aren't in the correct format");
										alert.setContentText("All nutrient values must exist and be numeric");

										alert.showAndWait();
									}
								});
								
								AnchorPane popupPane = new AnchorPane();
								popupPane.setMaxHeight(400 * HR);
								popupPane.setMaxWidth(400 * WR);
								popupPane.getChildren().add(new VBox(10 * HR) {
									{
										//All GUI code for adding food popup
										this.getChildren().add(new HBox(10 * WR) {
											{
												this.setPadding(new Insets(25 * HR, 25 * WR, 25 * HR, 25 * WR));
												this.getChildren().add(new Label("Name:"));
												this.getChildren().add(fields[0] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10 * WR) {
											{
												this.setPadding(new Insets(0, 25 * WR, 0, 25 * WR));
												this.getChildren().add(new Label("ID:"));
												this.getChildren().add(fields[1] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10 * WR) {
											{
												this.setPadding(new Insets(0, 25 * WR, 0, 25 * WR));
												this.getChildren().add(new Label("Calories:"));
												this.getChildren().add(fields[2] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10 * WR) {
											{
												this.setPadding(new Insets(0, 25 * WR, 0, 25 * WR));
												this.getChildren().add(new Label("Carbs:"));
												this.getChildren().add(fields[3] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10 * WR) {
											{
												this.setPadding(new Insets(0, 25 * WR, 0, 25 * WR));
												this.getChildren().add(new Label("Protein:"));
												this.getChildren().add(fields[4] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10 * WR) {
											{
												this.setPadding(new Insets(0, 25 * WR, 0, 25 * WR));
												this.getChildren().add(new Label("Fiber:"));
												this.getChildren().add(fields[5] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10 * WR) {
											{
												this.setPadding(new Insets(0, 25 * WR, 0, 25 * WR));
												this.getChildren().add(new Label("Fat:"));
												this.getChildren().add(fields[6] = new TextField());
											}
										});
										this.getChildren().add(new HBox(10 * WR) {
											{
												this.setPadding(new Insets(0, 25 * WR, 0, 25 * WR));
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
								Scene scene = new Scene(popupPane, 400 * WR, 400 * HR);
								popupStage.setScene(scene);
								popupStage.show();
							});
						}
					});
				}
			});
			root.setLeft(main); //Set left
			root.setTop(v);
			HBox meal = new HBox(10 * WR);
			meal.setPadding(new Insets(10 * HR, 50 * WR, 0, 0));
			meal.setMaxHeight(600 * HR);
			meal.setMaxWidth(400 * WR);
			
			// right side of main area containing list of foods in meal and nutritional analysis 
			meal.getChildren().add(new VBox(10 * HR) {
				{
					Label selectedFoods = new Label("Selected Foods for Meal");
					selectedFoods.getStyleClass().add("white-labels");
					selectedFoods.setPadding(new Insets(20 * HR, 0, 0, 20 * WR));
					this.getChildren().add(selectedFoods);
					mealViewer = new ListView<FoodItem>(); 
					mealViewer.setPrefHeight(425 * HR);
					mealViewer.setPrefWidth(400 * WR);
					mealViewer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
					this.getChildren().add(mealViewer);

					this.getChildren().add(new VBox(10 * HR) {
						{
							this.getChildren().add(new HBox(10*WR) {
								{
									this.getChildren().add(new Button("Remove From Meal") {
										{
											this.setOnAction(e -> {
		
												for (FoodItem f : mealViewer.getSelectionModel().getSelectedItems()) {
													mealList.removeFromMeal(f);
												}
												mealViewer.setItems(FXCollections.observableArrayList(mealList.getMeal())); //Remove from meal and make sure no duplicates
											});
										}
									});	
									this.getChildren().add(new Button("Clear") {
										{
											this.setOnAction(e -> {
												mealViewer.getSelectionModel().selectAll();
												for (FoodItem f : mealViewer.getSelectionModel().getSelectedItems()) {
													mealList.removeFromMeal(f);
												}
												mealViewer.setItems(FXCollections.observableArrayList(mealList.getMeal())); //Remove from meal and make sure no duplicates
											});
										}
									});	
								};
							});
							
							// graph containing nutritional facts 
							this.getChildren().add(new Button("Analyze Nutritional Analysis") {
								{
									this.setOnAction(e -> { //when "Analyze Nutritional Analysis" is clicked, the following code is executed
										Stage popupStage = new Stage(); //new popup is instantiated
										AnchorPane popupPane = new AnchorPane();
										popupPane.setMaxHeight(400 * HR);
										popupPane.setMaxWidth(900 * WR);
										CategoryAxis xAxis = new CategoryAxis();
										xAxis.setLabel("Nutrient"); 
										NumberAxis yAxis = new NumberAxis();
										yAxis.setLabel("Total");
										BarChart<String, Double> nutriAnalysis = new BarChart(xAxis, yAxis); //creates a new barChart with an x axis and y axis
										nutriAnalysis.setPrefHeight(400 * HR);
										nutriAnalysis.setPrefWidth(500 * WR);
										XYChart.Series<String, Double> dataSeries1 = new XYChart.Series();
										dataSeries1.setName("Nutrients within Selected Meal List");
										mealList.nutriAnalysis(); //calls the nutritional analysis method on the meal list
										//five columns are created for the corresponding nutritinal facts of the list
										dataSeries1.getData().add(
												new XYChart.Data<String, Double>("Calories", mealList.getTotalCals()));
										dataSeries1.getData().add(new XYChart.Data<String, Double>("Fat (grams)",
												mealList.getTotalFat()));
										dataSeries1.getData().add(new XYChart.Data<String, Double>("Carbs (grams)",
												mealList.getTotalCarbs()));
										dataSeries1.getData().add(new XYChart.Data<String, Double>("Fiber (grams)",
												mealList.getTotalFiber()));
										dataSeries1.getData().add(new XYChart.Data<String, Double>("Protein (grams)",
												mealList.getTotalProtein()));
										nutriAnalysis.getData().add(dataSeries1); 
										HBox information = new HBox(); //Hbox is added to store the graph
										information.getChildren().add(nutriAnalysis); //graph is added to the Hbox
										VBox amountSum = new VBox(); //a Vbox is created to output all of the numbers of the meal list
										amountSum.setPadding(new Insets(120 * HR, 0, 0, 50 * WR));
										Label cals = new Label("Calories: " + mealList.getTotalCals());
										Label fat = new Label("Fat: " + mealList.getTotalFat());
										Label carbs = new Label("Carbs: " + mealList.getTotalCarbs());
										Label fiber = new Label("Fiber: " + mealList.getTotalFiber());
										Label protein = new Label("Protein: " + mealList.getTotalProtein());
										amountSum.getChildren().addAll(cals, fat, carbs, fiber, protein); //all labels are added to amountSum
										//the vbox filled with labels is added to the HBox, and then added to the popup
										information.getChildren().add(amountSum);
										popupPane.getChildren().addAll(information);
										Scene scene = new Scene(popupPane, 700 * WR, 400 * HR);
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
			VBox all = new VBox(10 * HR);
			
			// middle area of main including filters 
			HBox middle = new HBox(10 * WR);
			VBox middleLeft = new VBox(10 * HR);
			VBox middleRight = new VBox(10 * HR);
			middleLeft.setPadding(new Insets(10 * HR, 50 * WR, 0, 0));
			middleLeft.setMaxHeight(400 * HR);
			middleLeft.setMaxWidth(200 * WR);
			middleLeft.getChildren().add(new VBox(10 * HR) {
				{
					this.getChildren().add(forms[0] = new FormType("Name Filter", "Apple"));
					this.getChildren().add(forms[1] = new FormType("Calorie Filter", "<= 500"));
					this.getChildren().add(forms[2] = new FormType("Carb Filter", "== 28"));
				}
			});
			middleRight.setPadding(new Insets(10 * HR, 50 * WR, 0, 0));
			middleRight.setMaxHeight(400 * HR);
			middleRight.setMaxWidth(200 * WR);
			middleRight.getChildren().add(new VBox(10 * HR) {
				{
					this.getChildren().add(forms[5] = new FormType("Protein Filter", ">= 5"));
					this.getChildren().add(forms[4] = new FormType("Fiber Filter", "== 1"));
					this.getChildren().add(forms[3] = new FormType("Fat Filter", "<= 10"));
				}
			});

			middle.setPadding(new Insets(100 * HR, 0, 0, 75 * WR));
			middle.getChildren().add(middleLeft);
			middle.getChildren().add(middleRight);
			all.getChildren().add(middle);
			HBox button = new HBox(10 * WR);
			Button na = new Button("Apply");
			na.setOnAction(e -> {
				List<FoodItem> list = new ArrayList<>(foodData.getAllFoodItems());
				List<String> filters = new ArrayList<>();
				//Get all filters and add them to filters
				for (int i = 1; i < forms.length; i++) {
					if (forms[i].isSelected())
						filters.add(forms[i].getText());
					else
						filters.add("");
				}
				forms[0].filter(list);//Filter by name
				for (int i = 1; i < forms.length; i++) { //Filter by nutrients
					if (forms[i].isSelected()) { //Only call filterByNutrients once because it works on all filters
						forms[i].filter(list, filters);
						break;
					}
				}
				ObservableList<FoodItem> items = FXCollections.observableArrayList(list); //Update list
				listOfFoods.setItems(items);
				numberLabel.setText("# of items in Food List: " + listOfFoods.getItems().size());
			});
			button.getChildren().addAll(na);
			Button help = new Button("Clear");
			help.setOnAction(e->{
				for(FormType f : forms) {
					f.clear(); //Clear filters on clear clicked
				}
				ObservableList<FoodItem> items = FXCollections.observableArrayList(foodData.getAllFoodItems());
				listOfFoods.setItems(items);
				numberLabel.setText("# of items in Food List: " + listOfFoods.getItems().size());
			});
			button.getChildren().add(help);
			button.setPadding(new Insets(10 * HR, 0, 0, 175 * WR));

			all.getChildren().add(button);
			root.setCenter(all);

			Scene scene = new Scene(root, 1280 * WR, 810 * HR);

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

	private void loadFile() {
		FileChooser fileChooser = new FileChooser();//Open FileChooser
		fileChooser.setTitle("Open Resource File");
		if (file != null) {
			fileChooser.setInitialDirectory(file.getParentFile());
		}
		File choosenFile = fileChooser.showOpenDialog(null);
		if (choosenFile != null) { //If file isnt null
			file = choosenFile;
			fileName.setText(file.getName());
			foodData = new FoodData(); //Make new food data object
			foodData.loadFoodItems(file.getAbsolutePath()); //Populate foodData
			ObservableList<FoodItem> items = FXCollections.observableArrayList(foodData.getAllFoodItems());
			listOfFoods.setItems(items);
			numberLabel.setText("# of items in Food List: " + listOfFoods.getItems().size());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}