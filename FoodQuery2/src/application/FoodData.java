package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * This class represents the backend for managing all the operations associated
 * with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu), Alex E, Theo K, Ellie B, Will M
 */
public class FoodData implements FoodDataADT<FoodItem> {

	// List of all the food items.
	private List<FoodItem> foodItemList;

	// Map of nutrients and their corresponding index
	private HashMap<String, BPTree<Double, FoodItem>> indexes;

	/**
	 * Public constructor
	 */
	public FoodData() {
		this.foodItemList = new ArrayList<FoodItem>(); //creates a new list of food items
		this.indexes = new HashMap<String, BPTree<Double, FoodItem>>();
		indexes.put("Calories", new BPTree<Double, FoodItem>(3)); //the BPTree for Calories
		indexes.put("Carb", new BPTree<Double, FoodItem>(3)); //the BPTree for Carbs
		indexes.put("Protein", new BPTree<Double, FoodItem>(3)); //The BPTree for protein
		indexes.put("Fiber", new BPTree<Double, FoodItem>(3)); //The BPTree for Fiber
		indexes.put("Fat", new BPTree<Double, FoodItem>(3)); //The BPTree for fat
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
	 */
	@Override
	public void loadFoodItems(String filePath) {
		File file = new File(filePath);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file)); //instantiates a new BR
			String str; //str will store each line of the file
			while ((str = br.readLine()) != null) { //iterates through the entire file
				str = str.toLowerCase().trim();
				String[] categories = str.split(","); //splits each line based on the commas
				if (categories.length != 12) {
					continue;
				}
				for (int i = 0; i < categories.length; i++) {
					if (categories[i] == "") {
						continue;
					}
				}
				//instanties a new food item for each line of the file, with the proper id and name
				FoodItem nextUp = new FoodItem(categories[0], categories[1].replaceAll("_", " ")); //replaces underscores with spaces
				nextUp.getNutrients().put(categories[2], Double.parseDouble(categories[3])); //stores the caloric content
				nextUp.getNutrients().put(categories[4], Double.parseDouble(categories[5])); //stores the fat
				nextUp.getNutrients().put(categories[6], Double.parseDouble(categories[7])); //stores the carbs
				nextUp.getNutrients().put(categories[8], Double.parseDouble(categories[9])); //stores the fiber
				nextUp.getNutrients().put(categories[10], Double.parseDouble(categories[11])); //stores the protein

				//adds each nutrition value to the respective BPTree for the food iteam
				indexes.get("Calories").insert(nextUp.getNutrientValue(categories[2]), nextUp);
				indexes.get("Fat").insert(nextUp.getNutrientValue(categories[4]), nextUp);
				indexes.get("Carb").insert(nextUp.getNutrientValue(categories[6]), nextUp);
				indexes.get("Fiber").insert(nextUp.getNutrientValue(categories[8]), nextUp);
				indexes.get("Protein").insert(nextUp.getNutrientValue(categories[10]), nextUp);

				this.foodItemList.add(nextUp); //adds the food item to the list
			}
			//lexicographic sort is then done on the food list
			this.foodItemList.sort(new Comparator<FoodItem>() {
				@Override
				public int compare(FoodItem arg0, FoodItem arg1) {
					return arg0.getName().compareTo(arg1.getName()); //compares two values to each other to determine ordering
				}

			});
		} catch (FileNotFoundException e) {
			System.out.println("FILE NOT FOUND."); //file not found exception
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Problem with the BR");
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see skeleton.FoodDataADT#filterByName(java.lang.String)
	 */
	@Override
	public List<FoodItem> filterByName(String substring) {
		List<FoodItem> filterFoodItems = new ArrayList<FoodItem>();
		for (int i = 0; i < foodItemList.size(); i++) { //iterates through the food list
			if (foodItemList.get(i).getName().contains(substring.toLowerCase())) {
				filterFoodItems.add(foodItemList.get(i)); //stores any food item that contains the substring
			}
		}
		return filterFoodItems; //returns the filtered list
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
	 */
	@Override
	public List<FoodItem> filterByNutrients(List<String> rules) {
		List<FoodItem> nutrientFilters = new ArrayList<FoodItem>(); // array to store newly filtered food list
		try {
			Object[] s = indexes.keySet().toArray();
			Arrays.sort(s);
			for (int i = 0; i < rules.size(); ++i) {
				if (!(rules.get(i).trim().equals(""))) {  
					String[] arr = rules.get(i).trim().split(","); // separate list of rules
					for (int k = 0; k < arr.length; k++) {
						String[] tokens = arr[k].trim().split(" "); // separate individual rule into comparator and double value 
						String comparator = tokens[0];
						double key = Double.parseDouble(tokens[1]);
						if (key < 0) { // throw exception with negative nutrient value
							throw new IllegalArgumentException();
						}
						List<FoodItem> nutrientRangeList = (indexes.get((s[i])).rangeSearch(key, comparator));
						if (nutrientFilters.isEmpty()) {
							nutrientFilters = nutrientRangeList;
						} else {
						    // if the food list contains items are that are not in the filtered foods lists, remove them 
							for (int j = 0; j < nutrientFilters.size(); j++) {
								if (!nutrientRangeList.contains(nutrientFilters.get(j))) {
									nutrientFilters.remove(nutrientFilters.get(j));
									j--;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) { //prints out an error message if the filter search string is not properly formatted
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Opps, looks like your search criteria isn't in the correct format");
			alert.setContentText("Remember: the input has 2 parts, each part separated by a space:\n"
					+ "<comparator>: One of the following comparison operators: <=, >=, ==\n"
					+ "<value>: a positive double value\n" + "Format of a rule: <comparator> <value>\n"
					+ "Multiple rules can contain the same nutrient.\n"
					+ "Example of a rule for filtering between 50 and 200:\n" + "\">= 50.0,<= 200.0\"\n");

			alert.showAndWait();
		}
		return nutrientFilters; //returns the new list of filtered items
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
	 */
	@Override
	public void addFoodItem(FoodItem foodItem) {
		foodItemList.add(foodItem); //adds the food item to the food list
		//adds each nutritional value of the food iteam into its respective BPTree
		indexes.get("Calories").insert(foodItem.getNutrientValue("Calories"), foodItem);
		indexes.get("Fat").insert(foodItem.getNutrientValue("Fat"), foodItem);
		indexes.get("Carb").insert(foodItem.getNutrientValue("Carb"), foodItem);
		indexes.get("Fiber").insert(foodItem.getNutrientValue("Fiber"), foodItem);
		indexes.get("Protein").insert(foodItem.getNutrientValue("Protein"), foodItem);
		foodItemList.sort(Main.lexicographicOrder); //resorts the list
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see skeleton.FoodDataADT#getAllFoodItems()
	 */
	@Override
	public List<FoodItem> getAllFoodItems() {
		return foodItemList;
	}

	/* (non-Javadoc)
	 * @see application.FoodDataADT#saveFoodItems(java.lang.String)
	 */
	@Override
	public void saveFoodItems(String filename) {
		File newFoodFile = null; // file for saving food list to
		PrintStream writer = null; // used to write food list to file

		try {
			newFoodFile = new File(filename);
			writer = new PrintStream(newFoodFile); //writer is instantiated to a Print Stream

			for (int i = 0; i < foodItemList.size(); i++) {
				writer.print(foodItemList.get(i).getID()); // write the ID to the file
				writer.print(", ");
				writer.print(foodItemList.get(i).getName()); // write name to file
				writer.print((", calories, "));
				writer.print(foodItemList.get(i).getNutrientValue("calories")); //writes the calories to the file
				writer.print((", fat, "));
				writer.print(foodItemList.get(i).getNutrientValue("fat")); //writes the fat value to the file
				writer.print((", carbohydrate, "));
				writer.print(foodItemList.get(i).getNutrientValue("carbohydrate")); //writes the carbohydrate value to the file
				writer.print((", fiber, "));
				writer.print(foodItemList.get(i).getNutrientValue("fiber")); //writes the fiber value to the file
				writer.print((", protein, "));
				writer.println(foodItemList.get(i).getNutrientValue("protein")); //writes protein value to the file
			}
		}

		catch (java.io.FileNotFoundException f) {
			System.out.println("WARNING: Could not save food items to file" + filename);
		} finally {
			if (writer != null) // if statement checks for null pointer
				writer.close(); // close the file
		}
	}

}
