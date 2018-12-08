package application;

import java.util.ArrayList;
import java.util.List;

public class MealBuilder {
    private List<FoodItem> mealList;
    private double totalCals;
    public double getTotalCals() {
        return totalCals;
    }

    public void setTotalCals(double totalCals) {
        this.totalCals = totalCals;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(double totalFat) {
        this.totalFat = totalFat;
    }

    public double getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(double totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public double getTotalFiber() {
        return totalFiber;
    }

    public void setTotalFiber(double totalFiber) {
        this.totalFiber = totalFiber;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }

    private double totalFat;
    private double totalCarbs;
    private double totalFiber;
    private double totalProtein;
    
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
    
    public void clearNutri() {
        this.totalCals = 0;
        this.totalFat = 0;
        this.totalCarbs = 0;
        this.totalFiber = 0;
        this.totalProtein = 0;
    }
    
    public void nutriAnalysis() {
        clearNutri();
        for(FoodItem f: this.mealList) {
            this.totalCals += f.getNutrientValue("calories");
            this.totalFat += f.getNutrientValue("fat");
            this.totalCarbs += f.getNutrientValue("carbohydrate");
            this.totalFiber += f.getNutrientValue("fiber");
            this.totalProtein += f.getNutrientValue("protein");
        }
    }
    
}