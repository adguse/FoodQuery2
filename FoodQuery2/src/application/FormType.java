package application;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * This class represents the backend for managing all the operations associated
 * with FoodItems
 * 
 * @author Alex E, Theo K, Ellie B, Will M
 */

public class FormType extends VBox {

	private CheckBox checkbox; // checkbox for name and nutrient filters 
	private TextField textField; // textfield for name and nutrient filters 
	
	/**
	 * Constructor for a FormType object. This method creates each filter's checkbox 
	 * and textfield. 
	 * 
	 * @param String field 
	 * @param greyedText
	 * 		  
	 */
	public FormType(String field, String greyedText) {
		super(10);
		this.getChildren().add(checkbox = new CheckBox(field) {
		{
			this.getStyleClass().add("filters");
		}
		});
		this.getChildren().add(textField = new TextField() {
		{
			this.setPromptText(greyedText); // sets example text in nutrient filters and name filter textfield 
			this.setFocusTraversable(false);
			this.getStyleClass().add("filters");
		}
		});
		// textfield becomes active after clicking checkbox 
		textField.setDisable(true);
		checkbox.setOnAction(e -> {
			textField.setDisable(!textField.isDisabled());
		});
	}

	/**
	 * 
	 * 		  
	 */
	public void filter(List<FoodItem> list, List<String> filters) {
		if (checkbox.isSelected()) {
			List<FoodItem> filtered = Main.foodData.filterByNutrients(filters);
			for (int i = 0; i < list.size(); i++) {
				if (!filtered.contains(list.get(i))) {
					list.remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * 
	 * 
	 * 		  
	 */
	public void filter(List<FoodItem> list) {
		if (checkbox.isSelected()) {
			List<FoodItem> filtered = Main.foodData.filterByName(textField.getText());
			for (int i = 0; i < list.size(); i++) {
				if (!filtered.contains(list.get(i))) {
					list.remove(i);
					i--;
				}
			}
		}
	}
	
	/**
	 * This method clears the text from the textfield and unchecks the checkbox. 
	 * 	  
	 */
	public void clear() {
		textField.clear();
		textField.setDisable(true);
		checkbox.setSelected(false);
	}

	/**
	 * This method returns true if the checkbox is selected, false if not. 
	 * 
	 * @return boolean 		  
	 */
	public boolean isSelected() {
		return checkbox.isSelected();
	}

	/**
	 * This method returns the text in the textfield.  
	 * 
	 * @return String textField text
	 * 		  
	 */
	public String getText() {
		return textField.getText().trim();
	}
	
	/**
	 * This method returns the text associated with the checkbox. 
	 * 
	 * @return String checkbox text
	 * 		  
	 */
	public String toString() {
		return checkbox.getText();
	}
}