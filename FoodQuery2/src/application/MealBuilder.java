package application;

import java.util.ArrayList;
import java.util.List;

public class MealBuilder {
    private List<FoodItem> mealList;
    private double totalCals;
    private double totalFat;
    private double totalCarbs;
    private double totalFiber;
    private double totalProtein;
    
    public MealBuilder() {
        this.mealList = new ArrayList<FoodItem>();
        
    }
    
    public List<FoodItem> getMeal(){
        return this.mealList;
    }
    
    public void addToMeal(FoodItem foodStuff) {
        if(mealList.contains(foodStuff)) {
            return;
        }
 
        this.mealList.add(foodStuff);
    }
    
    public void removeFromMeal(FoodItem foodStuff) {
        if(!mealList.contains(foodStuff)) {
            return;
        }
        
        this.mealList.remove(foodStuff);
    }
    
    public void nutriAnalysis() {
        
    }
    
}