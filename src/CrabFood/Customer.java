package CrabFood;

import java.util.ArrayList;

public class Customer {

    private String customerID = "";
    private int arrTime;
    private ArrayList<String> reqRes = new ArrayList();
    private ArrayList<String> reqDis = new ArrayList();
    private int orderCoorY;
    private int orderCoorX;

    public Customer(String customerID) {
        this.customerID = customerID;
    }

    //add data from text file into Customer class variable
    public void add(String input) {
        if (input.matches("[0-9]+")) {
            arrTime = Integer.parseInt(input);                                  //store arrival time of customer
        } else if (reqRes.size() == reqDis.size() && input.charAt(1) != ' ') {
            reqRes.add(input);                                                  //store requested restarant name
        } else if (reqRes.size() > reqDis.size()) {
            reqDis.add(input);                                                  //store requested dish name
        } else if (input.charAt(1) == ' ') {
            orderCoorY = Character.getNumericValue(input.charAt(0));            //store customer's origin coordinate (row)
            orderCoorX = Character.getNumericValue(input.charAt(2));            //store customer's origin coordinate (column)
        }
    }

    public int getOrderCoorY() {
        return orderCoorY;
    }

    public int getOrderCoorX() {
        return orderCoorX;
    }

    public String getCustomerID() {
        return customerID;
    }

    public int getArrTime() {
        return arrTime;
    }

    public String getReqRes(int i) {
        return reqRes.get(i);
    }

    public String getReqDis(int i) {
        return reqDis.get(i);
    }

    public int getResDisSize() {
        return reqRes.size();
    }

}
