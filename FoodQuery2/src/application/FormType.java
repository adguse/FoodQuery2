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

	public void filter(boolean type, List<FoodItem> list) {
		if (checkbox.isSelected()) {
			if (type) {
				List<FoodItem> filtered = Main.foodData.filterByName(textField.getText());
				
				for (int i = 0; i < list.size(); i++) {
					if (!filtered.contains(list.get(i))) {
						list.remove(i);
						i--;
					}
				}
			} else {

			}
		}
	}
	
	public boolean isSelected() {
		return checkbox.isSelected();
	}

	public String getText() {
		return textField.getText();
	}
}