package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems
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
        
    }
    
    
    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
     */
    @Override
    public void loadFoodItems(String filePath) {
        File file = new File(filePath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            while((str = br.readLine()) != null) {
                str = str.toLowerCase().trim();
                String [] categories = str.split(",");
                if(categories.length != 12) {
                    continue;
                }
                for(int i = 0; i < categories.length; i++) {
                    if(categories[i] == "") {
                        continue;
                    }
                }
                FoodItem nextUp = new FoodItem(categories[0],categories[1].replaceAll("_", " "));
                nextUp.getNutrients().put(categories[2], Double.parseDouble(categories[3]));
                nextUp.getNutrients().put(categories[4], Double.parseDouble(categories[5]));
                nextUp.getNutrients().put(categories[6], Double.parseDouble(categories[7]));
                nextUp.getNutrients().put(categories[8], Double.parseDouble(categories[9]));
                nextUp.getNutrients().put(categories[10], Double.parseDouble(categories[11]));
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
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
        // TODO : Complete
        return null;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
        // TODO : Complete
        return null;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
        // TODO : Complete
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#getAllFoodItems()
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
        return foodItemList;
    }


	@Override
	public void saveFoodItems(String filename) {
		// TODO Auto-generated method stub
		
	}

}