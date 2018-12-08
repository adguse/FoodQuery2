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
	}

	public void filter(boolean name) {
		if (checkbox.isSelected()) {
			if (name) {
				List<FoodItem> filtered = Main.foodData.filterByName(textField.getText());
				for (int i = 0; i < filtered.size(); i++) {
					if (!Main.listOfFoods.getItems().contains(filtered.get(i))) {
						filtered.remove(i);
						i--;
					}
				}
				ObservableList<FoodItem> items = FXCollections.observableArrayList(filtered);
				Main.listOfFoods.setItems(items);
				Main.numberLabel.setText("# of items in Food List: " + Main.listOfFoods.getItems().size());
			} else {

			}
		}
	}

	public String getText() {
		return textField.getText();
	}
}