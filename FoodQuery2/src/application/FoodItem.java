package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a food item with all its properties.
 * 
 * @author aka
 */
public class FoodItem {
	// The name of the food item.
	private String name;

	// The id of the food item.
	private String id;

	// Map of nutrients and value.
	private HashMap<String, Double> nutrients;

	/**
	 * Constructor
	 * 
	 * @param name name of the food item
	 * @param id   unique id of the food item
	 */
	public FoodItem(String id, String name) {
		this.id = id;
		this.name = name;
		this.nutrients = new HashMap<String, Double>();
	}

	/**
	 * Gets the name of the food item
	 * 
	 * @return name of the food item
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the unique id of the food item
	 * 
	 * @return id of the food item
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * Gets the nutrients of the food item
	 * 
	 * @return nutrients of the food item
	 */
	public HashMap<String, Double> getNutrients() {
		return this.nutrients;
	}

	/**
	 * Adds a nutrient and its value to this food. If nutrient already exists,
	 * updates its value.
	 */
	public void addNutrient(String name, double value) {
		// checks if the nutrient already exits
		if (this.nutrients.containsKey(name)) {
			this.nutrients.remove(name); // updates its value
			this.nutrients.put(name, value);
		} else {
			this.nutrients.put(name, value);
		}
	}

	/**
	 * Returns the value of the given nutrient for this food item. If not present,
	 * then returns 0.
	 */
	public double getNutrientValue(String name) {
		if (this.nutrients.containsKey(name)) {
			return this.nutrients.get(name);
		}
		return 0; // returns 0 if the nutrient is not present
	}

	public String toString() {
		return this.getName();
	}
}
