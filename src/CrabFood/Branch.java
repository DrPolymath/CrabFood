package CrabFood;

import java.util.ArrayList;
import java.util.List;

public class Branch<E> extends Restaurant {

    private String coordinate;
    private ArrayList<E> occupiedDish = new ArrayList();
    private int numOfCustomer;
    private ArrayList<Integer> numOfDish = new ArrayList<>();

    public Branch(String coor) {
        coordinate = coor;
        numOfCustomer = 0;
    }

    public void addCus() {
        numOfCustomer++;
    }

    public int getNumOfCustomer() {
        return numOfCustomer;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public void addOccupiedDish(E occupiedDish) {
        this.occupiedDish.add(occupiedDish);
    }

    public int getNumOfDish(int i) {
        return numOfDish.get(i);
    }

    public int getNumOfDishSize() {
        return numOfDish.size();
    }

    public boolean contains(E occupiedDish) {
        return this.occupiedDish.contains(occupiedDish);
    }

    public boolean remove(E occupiedDish) {
        return this.occupiedDish.remove(occupiedDish);
    }

    public void setReport(ArrayList<String> dishes) {
        for (int i = 0; i < dishes.size(); i++) {
            numOfDish.add(0);
        }
    }

    public void makeReport(ArrayList<String> dishes, ArrayList<List<String>> report) {
        for (int i = 0; i < numOfDish.size(); i++) {
            int count = 0;
            for (int j = 0; j < report.size(); j++) {
                String coor = report.get(j).get(2).charAt(1) + " " + report.get(j).get(2).charAt(4);
                if (dishes.get(i).equals(report.get(j).get(8)) && coordinate.equals(coor)) {
                    count++;
                }
            }
            numOfDish.set(i, count);
        }
    }

}
