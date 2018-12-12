package application;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates a "Meal" object, which can then have nutritional
 * analysis performed on it.
 * @author Alex E, Will M, Theo K
 *
 */
public class MealBuilder {
    private List<FoodItem> mealList; //contains the list of all foods in the meal
    private double totalCals; //stores all cals of the meal
    private double totalFat; //stores all fat of the meal
    private double totalCarbs; //stores all carbs of the meal
    private double totalFiber; //stores all fiber of the meal
    private double totalProtein; //stores all protein of the meal
       
    //the constructor sets all data to 0 at the start
    public MealBuilder() {
        this.mealList = new ArrayList<FoodItem>();
        this.totalCals = 0;
        this.totalFat = 0;
        this.totalCarbs = 0;
        this.totalFiber = 0;
        this.totalProtein = 0;
    }
    
    public List<FoodItem> getMeal(){
        return this.mealList;
    }
    
    public void addToMeal(FoodItem foodStuff) {
        if(mealList.contains(foodStuff)) { //checks if the food item is already in the meal list
            return;
        }
 
        this.mealList.add(foodStuff);
    }
    
    public void removeFromMeal(FoodItem foodStuff) {
        if(!mealList.contains(foodStuff)) {
            return;
        }
        //removes the food from the list
        this.mealList.remove(foodStuff);
    }
    
    //resets all nutritional informatin to 0 for the meal
    public void clearNutri() {
        this.totalCals = 0;
        this.totalFat = 0;
        this.totalCarbs = 0;
        this.totalFiber = 0;
        this.totalProtein = 0;
    }
    
    public void nutriAnalysis() {
        clearNutri(); //clears the meal information
        for(FoodItem f: this.mealList) { //iterates through all items in the meal list
            //continually adds all of the items' nutritional information together
            this.totalCals += f.getNutrientValue("calories");
            this.totalFat += f.getNutrientValue("fat");
            this.totalCarbs += f.getNutrientValue("carbohydrate");
            this.totalFiber += f.getNutrientValue("fiber");
            this.totalProtein += f.getNutrientValue("protein");
        }
    }
    
    public double getTotalCals() {
        return totalCals;
    }


    public double getTotalFat() {
        return totalFat;
    }


    public double getTotalCarbs() {
        return totalCarbs;
    }



    public double getTotalFiber() {
        return totalFiber;
    }


    public double getTotalProtein() {
        return totalProtein;
    }

    
}