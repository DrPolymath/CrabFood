package CrabFood;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Restaurant {

    private String resName = "";
    private ArrayList<String> coordinate = new ArrayList();
    private ArrayList<Integer> storeDis = new ArrayList();
    private ArrayList<String> dishes = new ArrayList();
    private ArrayList<Integer> dishesFinishedTime = new ArrayList();
    private ArrayList<Integer> prevTimeOrder = new ArrayList();
    private ArrayList<List<String>> report = new ArrayList();
    private LinkedList<Branch<String>> list = new LinkedList();

    public Restaurant() {
    }

    public String getResName() {
        return resName;
    }

    public ArrayList<String> getDishes() {
        return dishes;
    }

    public String getSelDish(int i) {
        return dishes.get(i);
    }

    public int getDishSize() {
        return dishes.size();
    }

    public int getStoreNumber() {
        return storeDis.size();
    }

    public int getCoorNum() {
        return coordinate.size();
    }

    public String getSelCoordinate(int i) {
        return coordinate.get(i);
    }

    public int getSelDis(int i) {
        return storeDis.get(i);
    }

    public void setSelDis(int y, int x, int index) {
        int newY = -Character.getNumericValue(coordinate.get(index).charAt(0)) - (-y);
        int newX = Character.getNumericValue(coordinate.get(index).charAt(2)) - x;
        if (newY < 0) {
            newY *= -1;
        }
        if (newX < 0) {
            newX *= -1;
        }
        storeDis.set(index, newY + newX);
    }

    public int getSelDishesFinishedTime(String dish) {
        for (int i = 0; i < dishes.size(); i++) {
            if (dish.equals(dishes.get(i))) {
                return dishesFinishedTime.get(i);
            }
        }
        return 0;
    }

    public int getSelPrevTimeOrder(int i) {
        return prevTimeOrder.get(i);
    }

    public void setSelPrevTimeOrder(int i, int time, String choice) {
        if (choice.equals("decre")) {
            prevTimeOrder.set(i, time);
        } else if (choice.equals("add")) {
            int current = prevTimeOrder.get(i);
            prevTimeOrder.set(i, current + time);
        }
    }

    public Branch<String> getSelBranch(int i) {
        return list.get(i);
    }

    public int getSizeList() {
        return list.size();
    }

    public void addReport(String custID, String restaurant, String coordinate, String arrivalTime, String orderTime, String finishedCookingTime, String deliveryTime, String totalTime, String dish, String traffic, String startDelivery, String orderID, String prepTime) {
        report.add(Arrays.asList(custID, restaurant, coordinate, arrivalTime, orderTime, finishedCookingTime, deliveryTime, totalTime, dish, traffic, startDelivery, orderID, prepTime));
    }

    public void setReport(int i, String custID, String restaurant, String coordinate, String arrivalTime, String orderTime, String finishedCookingTime, String deliveryTime, String totalTime, String dish, String traffic, String startDelivery, String orderID, String prepTime) {
        report.set(i, Arrays.asList(custID, restaurant, coordinate, arrivalTime, orderTime, finishedCookingTime, deliveryTime, totalTime, dish, traffic, startDelivery, orderID, prepTime));
    }

    public String getReport(int i, int j) {
        return report.get(i).get(j);
    }

    public int getReportSize() {
        return report.size();
    }

    public ArrayList<List<String>> getWholeReport() {
        return report;
    }
    
//add data from text file into Restaurant class variable
    public void add(String input) {
        if (resName.equals("")) {
            resName = input;                                                                                                //store restaurant name
        } else if (input.charAt(1) == ' ' && Character.isDigit(input.charAt(0)) && Character.isDigit(input.charAt(2))) {
            coordinate.add(input);                                                                                          //store coordinate of the branch
            Branch<String> branch = new Branch(input);
            list.add(branch);                                                                                               //store branch's data
            storeDis.add(Character.getNumericValue(input.charAt(0)) + Character.getNumericValue(input.charAt(2)));          //store distance from origin to branch
            prevTimeOrder.add(0);                                                                                           //set previous time order to 0
        } else if (input.matches("[0-9]+")) {
            dishesFinishedTime.add(Integer.parseInt(input));                                                                //store preparation time
        } else {
            dishes.add(input);                                                                                              //store dish name
        }
    }
}
