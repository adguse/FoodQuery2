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
 * @author sapan (sapan@cs.wisc.edu)
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
		this.foodItemList = new ArrayList<FoodItem>();
		this.indexes = new HashMap<String, BPTree<Double, FoodItem>>();
		indexes.put("Calories", new BPTree<Double, FoodItem>(4));
		indexes.put("Carb", new BPTree<Double, FoodItem>(4));
		indexes.put("Protein", new BPTree<Double, FoodItem>(4));
		indexes.put("Fiber", new BPTree<Double, FoodItem>(4));
		indexes.put("Fat", new BPTree<Double, FoodItem>(4));
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
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null) {
				str = str.toLowerCase().trim();
				String[] categories = str.split(",");
				if (categories.length != 12) {
					continue;
				}
				for (int i = 0; i < categories.length; i++) {
					if (categories[i] == "") {
						continue;
					}
				}
				FoodItem nextUp = new FoodItem(categories[0], categories[1].replaceAll("_", " "));
				nextUp.getNutrients().put(categories[2], Double.parseDouble(categories[3]));
				nextUp.getNutrients().put(categories[4], Double.parseDouble(categories[5]));
				nextUp.getNutrients().put(categories[6], Double.parseDouble(categories[7]));
				nextUp.getNutrients().put(categories[8], Double.parseDouble(categories[9]));
				nextUp.getNutrients().put(categories[10], Double.parseDouble(categories[11]));

				indexes.get("Calories").insert(nextUp.getNutrientValue(categories[2]), nextUp);
				indexes.get("Fat").insert(nextUp.getNutrientValue(categories[4]), nextUp);
				indexes.get("Carb").insert(nextUp.getNutrientValue(categories[6]), nextUp);
				indexes.get("Fiber").insert(nextUp.getNutrientValue(categories[8]), nextUp);
				indexes.get("Protein").insert(nextUp.getNutrientValue(categories[10]), nextUp);

				this.foodItemList.add(nextUp);
			}
			this.foodItemList.sort(new Comparator<FoodItem>() {

				@Override
				public int compare(FoodItem arg0, FoodItem arg1) {
					return arg0.getName().compareTo(arg1.getName());
				}

			});
		} catch (FileNotFoundException e) {
			System.out.println("FILE NOT FOUND.");
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
		for (int i = 0; i < foodItemList.size(); i++) {
			if (foodItemList.get(i).getName().contains(substring.toLowerCase())) {
				filterFoodItems.add(foodItemList.get(i));
			}
		}
		return filterFoodItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
	 */
	@Override
	public List<FoodItem> filterByNutrients(List<String> rules) {
		List<FoodItem> nutrientFilters = new ArrayList<FoodItem>();
		try {
			Object[] s = indexes.keySet().toArray();
			Arrays.sort(s);
			for (int i = 0; i < rules.size(); ++i) {
				if (!(rules.get(i).trim().equals(""))) {
					String[] arr = rules.get(i).trim().split(",");
					for (int k = 0; k < arr.length; k++) {
						String[] tokens = arr[k].trim().split(" ");
						String comparator = tokens[0];
						double key = Double.parseDouble(tokens[1]);
						if (key < 0) {
							throw new IllegalArgumentException();
						}
						List<FoodItem> nutrientRangeList = (indexes.get((s[i])).rangeSearch(key, comparator));
						if (nutrientFilters.isEmpty()) {
							nutrientFilters = nutrientRangeList;
						} else {
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
		} catch (Exception e) {
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
		return nutrientFilters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
	 */
	@Override
	public void addFoodItem(FoodItem foodItem) {
		foodItemList.add(foodItem);
		indexes.get("Calories").insert(foodItem.getNutrientValue("Calories"), foodItem);
		indexes.get("Fat").insert(foodItem.getNutrientValue("Fat"), foodItem);
		indexes.get("Carb").insert(foodItem.getNutrientValue("Carb"), foodItem);
		indexes.get("Fiber").insert(foodItem.getNutrientValue("Fiber"), foodItem);
		indexes.get("Protein").insert(foodItem.getNutrientValue("Protein"), foodItem);
		foodItemList.sort(Main.lexicographicOrder);
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

	@Override
	public void saveFoodItems(String filename) {
		File newFoodFile = null; // file for saving food list to
		PrintStream writer = null; // used to write food list to file

		try {
			newFoodFile = new File(filename);
			writer = new PrintStream(newFoodFile);

			for (int i = 0; i < foodItemList.size(); i++) {
				writer.print(foodItemList.get(i).getID()); // write the ID to the file
				writer.print(", ");
				writer.print(foodItemList.get(i).getName()); // write name to file
				writer.print((", calories, "));
				writer.print(foodItemList.get(i).getNutrientValue("calories"));
				writer.print((", fat, "));
				writer.print(foodItemList.get(i).getNutrientValue("fat"));
				writer.print((", carbohydrate, "));
				writer.print(foodItemList.get(i).getNutrientValue("carbohydrate"));
				writer.print((", fiber, "));
				writer.print(foodItemList.get(i).getNutrientValue("fiber"));
				writer.print((", protein, "));
				writer.println(foodItemList.get(i).getNutrientValue("protein"));
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
