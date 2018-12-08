package application;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class FormType extends VBox {

	private CheckBox checkbox;
	private TextField textField;

	public FormType(String field) {
		super(10);
		this.getChildren().add(checkbox = new CheckBox(field));
		this.getChildren().add(textField = new TextField());
		textField.setDisable(true);
		checkbox.setOnAction(e -> {
			textField.setDisable(!textField.isDisabled());
		});
		textField.setOnAction(e -> {
			if (Main.listOfFoods.getItems().isEmpty())
				return;
			List<FoodItem> filtered = Main.foodData.filterByName(textField.getText());
			
			ObservableList<FoodItem> items = FXCollections
					.observableArrayList(Main.foodData.filterByName(textField.getText()));
			Main.listOfFoods.setItems(items);
			Main.numberLabel.setText("# of items in Food List: " + Main.listOfFoods.getItems().size());
		});
	}

	public String getText() {
		return textField.getText();
	}
}