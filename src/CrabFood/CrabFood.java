package CrabFood;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Random;

public class CrabFood {

    private static char[][] map;

    public static void main(String[] args) throws InterruptedException {

//                                                      data arrangement for customers
        Customer[] cus = new Customer[calcCus()];
        for (int i = 0; i < cus.length; i++) {
            cus[i] = new Customer(String.valueOf(i + 1));
        }
        int currCus = 0;
        while (currCus < cus.length) {
            readCus(cus[currCus], currCus);
            currCus++;
        }

//                                                      data arrangement for restaurants
        Restaurant[] res = new Restaurant[calcRes()];
        for (int i = 0; i < res.length; i++) {
            res[i] = new Restaurant();
        }
        int currRes = 0;
        while (currRes < res.length) {
            readRes(res[currRes], currRes);
            currRes++;
        }

        createMap(res);

        takeOrderAndPrint(res, cus);

    }

    public static int arrivalOrder(Restaurant[] res, Customer[] cus, int time, int orderNum) {

        for (int i = 0; i < cus.length; i++) {
            if (cus[i].getArrTime() == time) {
                int restDishNum = 0;
                while (restDishNum < cus[i].getResDisSize()) {
                    int indexChoosenStore = 0;
                    int indexSelectedBranch = 0;

                    System.out.println(cus[i].getArrTime() + ": " + "\u001B[36m" + "Customer " + cus[i].getCustomerID() + "\u001B[0m" + " from "
                            + "\u001B[36m" + "(" + cus[i].getOrderCoorY() + ", " + cus[i].getOrderCoorX() + ")" + "\u001B[0m" + " wants to order "
                            + "\u001B[36m" + cus[i].getReqDis(restDishNum) + "\u001B[0m" + " from " + "\u001B[36m" + cus[i].getReqRes(restDishNum) + "\u001B[0m" + ".");

                    //select restaurant
                    for (int j = 0; j < res.length; j++) {
                        if (cus[i].getReqRes(restDishNum).equals(res[j].getResName())) {
                            indexChoosenStore = j;
                        }
                    }

                    //set distance based on customer's origin
                    int y = cus[i].getOrderCoorY();
                    int x = cus[i].getOrderCoorX();
                    for (int j = 0; j < res.length; j++) {
                        for (int k = 0; k < res[j].getCoorNum(); k++) {
                            res[j].setSelDis(y, x, k);
                        }
                    }

                    //variable for report
                    int orderTime = 0;
                    int finishedCookingTime = 0;
                    int startDelivery = 0;

                    //select branch
                    //get total time for every branch
                    int temp;
                    int Delay = delay();
                    int selDishesFinishedTime = res[indexChoosenStore].getSelDishesFinishedTime(cus[i].getReqDis(restDishNum));
                    int shortestTime = res[indexChoosenStore].getSelPrevTimeOrder(0)
                            + res[indexChoosenStore].getSelDishesFinishedTime(cus[i].getReqDis(restDishNum))
                            + res[indexChoosenStore].getSelDis(0);

                    for (int j = 0; j < res[indexChoosenStore].getCoorNum(); j++) {
                        boolean selPrev = false;
                        boolean deSelPrev = false;

                        //check if meal requested similar with previous order                            
                        if (res[indexChoosenStore].getSelBranch(j).contains(cus[i].getReqDis(restDishNum))) {
                            temp = res[indexChoosenStore].getSelPrevTimeOrder(j)
                                    + res[indexChoosenStore].getSelDishesFinishedTime(cus[i].getReqDis(restDishNum))
                                    + res[indexChoosenStore].getSelDis(j);
                            selPrev = true;
                        } else {
                            temp = res[indexChoosenStore].getSelDishesFinishedTime(cus[i].getReqDis(restDishNum))
                                    + res[indexChoosenStore].getSelDis(j);
                            deSelPrev = true;
                        }

                        //get the shortest time from all the branches
                        if (temp <= shortestTime) {
                            shortestTime = temp;
                            indexSelectedBranch = j;
                            if (selPrev) {
                                orderTime = cus[i].getArrTime() + res[indexChoosenStore].getSelPrevTimeOrder(indexSelectedBranch);
                                finishedCookingTime = cus[i].getArrTime() + res[indexChoosenStore].getSelPrevTimeOrder(indexSelectedBranch) + selDishesFinishedTime;
                            } else if (deSelPrev) {
                                orderTime = cus[i].getArrTime();
                                finishedCookingTime = cus[i].getArrTime() + selDishesFinishedTime;
                            }
                            startDelivery = finishedCookingTime;
                        }
                    }

                    //increase number of customer for selected branch
                    res[indexChoosenStore].getSelBranch(indexSelectedBranch).addCus();

                    //keep current dish handled by selected branch
                    res[indexChoosenStore].getSelBranch(indexSelectedBranch).addOccupiedDish(cus[i].getReqDis(restDishNum));

                    //take coordinate for selected branch into string
                    String selectedCoor = "("
                            + res[indexChoosenStore].getSelCoordinate(indexSelectedBranch).charAt(0) + ", "
                            + res[indexChoosenStore].getSelCoordinate(indexSelectedBranch).charAt(2)
                            + ")";

                    //store data into report
                    res[indexChoosenStore].addReport(cus[i].getCustomerID(), //customer number
                            res[indexChoosenStore].getResName(), //restaurant name
                            selectedCoor, //branch coordinate
                            String.valueOf(cus[i].getArrTime()), //arrival time of customer
                            String.valueOf(orderTime), //order time
                            String.valueOf(finishedCookingTime), //finished cooking time
                            String.valueOf(res[indexChoosenStore].getSelDis(indexSelectedBranch)), //delivery time
                            String.valueOf(shortestTime), //total time
                            cus[i].getReqDis(restDishNum), //requested dish
                            String.valueOf(Delay), //traffic jammed
                            String.valueOf(startDelivery), //start delivery time
                            String.valueOf(orderNum), //number of order
                            String.valueOf(selDishesFinishedTime));                                 //preparation time

                    //get maximum finished cooking time for same cusomer and same branch
                    for (int j = 0; j < res[indexChoosenStore].getReportSize(); j++) {
                        if (res[indexChoosenStore].getReport(j, 0).equals(cus[i].getCustomerID())) {
                            if (res[indexChoosenStore].getReport(j, 2).equals("(" + res[indexChoosenStore].getSelCoordinate(indexSelectedBranch).charAt(0) + ", " + res[indexChoosenStore].getSelCoordinate(indexSelectedBranch).charAt(2) + ")")) {
                                if (Integer.parseInt(res[indexChoosenStore].getReport(j, 5)) >= startDelivery) {
                                    startDelivery = Integer.parseInt(res[indexChoosenStore].getReport(j, 5));
                                }//get maximum delay for same cusomer and same branch
                                if (Integer.parseInt(res[indexChoosenStore].getReport(j, 9)) >= Delay) {
                                    Delay = Integer.parseInt(res[indexChoosenStore].getReport(j, 9));
                                }
                            }
                        }
                    }

                    //update report
                    updateReport(res, cus, indexChoosenStore, i, Delay, indexSelectedBranch, startDelivery);

                    //add waiting time for customer
                    res[indexChoosenStore].setSelPrevTimeOrder(indexSelectedBranch, selDishesFinishedTime, "add");

                    restDishNum++;
                    orderNum++;
                }
            }
        }
        return orderNum;
    }

    public static void updateReport(Restaurant[] res, Customer[] cus, int indexChoosenStore, int i, int Delay, int indexSelectedBranch, int startDelivery) {
        for (int j = 0; j < res[indexChoosenStore].getReportSize(); j++) {
            if (res[indexChoosenStore].getReport(j, 0).equals(cus[i].getCustomerID())) {
                if (res[indexChoosenStore].getReport(j, 2).equals("(" + res[indexChoosenStore].getSelCoordinate(indexSelectedBranch).charAt(0) + ", " + res[indexChoosenStore].getSelCoordinate(indexSelectedBranch).charAt(2) + ")")) {
                    res[indexChoosenStore].setReport(j, res[indexChoosenStore].getReport(j, 0), res[indexChoosenStore].getReport(j, 1), res[indexChoosenStore].getReport(j, 2), res[indexChoosenStore].getReport(j, 3),
                            res[indexChoosenStore].getReport(j, 4), res[indexChoosenStore].getReport(j, 5), res[indexChoosenStore].getReport(j, 6), res[indexChoosenStore].getReport(j, 7),
                            res[indexChoosenStore].getReport(j, 8), String.valueOf(Delay), String.valueOf(startDelivery), res[indexChoosenStore].getReport(j, 11), res[indexChoosenStore].getReport(j, 12));
                }
            }
        }
    }

    public static void printDelivery(Restaurant[] res, int time) {
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].getReportSize(); j++) {
                if (time == Integer.parseInt(res[i].getReport(j, 10))) {
                    System.out.println(time + ": " + "\u001B[35m" + res[i].getReport(j, 8) + "\u001B[0m" + " is on delivery from " + "\u001B[35m" + res[i].getReport(j, 1)
                            + "\u001B[0m" + " to " + "\u001B[35m" + "Customer " + res[i].getReport(j, 0) + "\u001B[0m");
                    if (!res[i].getReport(j, 9).equals("0")) {
                        System.out.println(time + ": There's a " + "\u001B[31m" + "delay" + "\u001B[0m" + " of "
                                + "\u001B[31m" + res[i].getReport(j, 9) + " unit" + "\u001B[0m" + " to deliver the food to "
                                + "\u001B[31m" + "Customer " + res[i].getReport(j, 0) + "\u001B[0m"
                                + " due to " + "\u001B[31m" + "traffic jam." + "\u001B[0m");
                    }
                }
            }
        }
    }

    public static int printFinishDelivery(Restaurant[] res, Customer[] cus, int time, int orderCompleted) {
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].getReportSize(); j++) {
                int orderTime = Integer.parseInt(res[i].getReport(j, 5));
                int deliverTime = Integer.parseInt(res[i].getReport(j, 6));
                if (time == (orderTime + deliverTime + Integer.parseInt(res[i].getReport(j, 9)))) {
                    System.out.println(time + ": " + "\u001B[32m" + res[i].getReport(j, 8) + "\u001B[0m" + " from " + "\u001B[32m" + res[i].getReport(j, 1)
                            + "\u001B[0m" + " has been delivered to " + "\u001B[32m" + "Customer " + res[i].getReport(j, 0) + "\u001B[0m" + ".");
                    orderCompleted++;
                }
            }
        }
        return orderCompleted;
    }

    public static void printFinishOrder(Restaurant[] res, Customer[] cus, int time) {
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].getReportSize(); j++) {
                if (time == Integer.parseInt(res[i].getReport(j, 5))) {
                    System.out.println(time + ": " + "\u001B[34m" + res[i].getReport(j, 8) + "\u001B[0m" + " from " + "\u001B[34m" + res[i].getReport(j, 1)
                            + "\u001B[0m" + "'s branch at " + "\u001B[34m" + res[i].getReport(j, 2) + "\u001B[0m" + " is ready to deliver to " + "\u001B[34m" + "Customer " + res[i].getReport(j, 0) + "\u001B[0m");

                    String coor = res[i].getReport(j, 2).charAt(1) + " " + res[i].getReport(j, 2).charAt(4);
                    for (int k = 0; k < res[i].getSizeList(); k++) {
                        if (res[i].getSelBranch(k).getCoordinate().equals(coor)) {
                            res[i].getSelBranch(k).remove(res[i].getReport(j, 8));
                        }
                    }
                }
            }
        }
    }

    public static void printBranchTakeOrder(Restaurant[] res, int time) {
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].getReportSize(); j++) {
                if (time == Integer.parseInt(res[i].getReport(j, 4))) {
                    System.out.println(res[i].getReport(j, 4) + ": Branch of " + "\u001B[33m" + res[i].getReport(j, 1) + "\u001B[0m" + " at " + "\u001B[33m"
                            + res[i].getReport(j, 2) + "\u001B[0m" + " take the " + "\u001B[33m" + "Customer " + res[i].getReport(j, 0)
                            + "\u001B[0m" + " order. " + "\u001B[33m" + "(" + res[i].getReport(j, 8) + ")" + "\u001B[0m");
                }
            }
        }
    }

    public static void printLog(Restaurant[] res, int orderCompleted) {
        try {
            FileWriter fileWriter = new FileWriter("log.txt", true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for (int i = 0; i <= orderCompleted; i++) {
                if (i == 0) {
                    System.out.println(" -----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                    System.out.println("|Customer\t|Arrival\t|Order Time\t|Preparation Time\t|Finished Cooking Time\t|Start Delivery Time\t|Delivery Time\t|Received Time\t|Total Time\t|");
                    System.out.println(" -----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                    printWriter.println(" -----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                    printWriter.println("|Customer\t|Arrival\t|Order Time\t|Preparation Time\t|Finished Cooking Time\t|Start Delivery Time\t|Delivery Time\t|Received Time\t|Total Time\t|");
                    printWriter.println(" -----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                }
                for (int j = 0; j < res.length; j++) {
                    for (int k = 0; k < res[j].getReportSize(); k++) {
                        if (res[j].getReport(k, 11).equals(String.valueOf(i))) {
                            System.out.println("| " + res[j].getReport(k, 0) + "\t\t| "
                                    + res[j].getReport(k, 3) + "\t\t| "
                                    + res[j].getReport(k, 4) + "\t\t| "
                                    + res[j].getReport(k, 12) + "\t\t\t| "
                                    + res[j].getReport(k, 5) + "\t\t\t| "
                                    + res[j].getReport(k, 10) + "\t\t\t| "
                                    + res[j].getReport(k, 6) + " + " + res[j].getReport(k, 9) + "\t\t| "
                                    + (Integer.parseInt(res[j].getReport(k, 10)) + Integer.parseInt(res[j].getReport(k, 9)) + Integer.parseInt(res[j].getReport(k, 6))) + "\t\t| "
                                    + (Integer.parseInt(res[j].getReport(k, 7)) + Integer.parseInt(res[j].getReport(k, 9))) + "\t\t|");
                            String input = "| " + res[j].getReport(k, 0) + "\t\t| "
                                    + res[j].getReport(k, 3) + "\t\t| "
                                    + res[j].getReport(k, 4) + "\t\t| "
                                    + res[j].getReport(k, 12) + "\t\t\t| "
                                    + res[j].getReport(k, 5) + "\t\t\t| "
                                    + res[j].getReport(k, 10) + "\t\t\t| "
                                    + res[j].getReport(k, 6) + " + " + res[j].getReport(k, 9) + "\t\t| "
                                    + (Integer.parseInt(res[j].getReport(k, 10)) + Integer.parseInt(res[j].getReport(k, 9)) + Integer.parseInt(res[j].getReport(k, 6))) + "\t\t| "
                                    + (Integer.parseInt(res[j].getReport(k, 7)) + Integer.parseInt(res[j].getReport(k, 9))) + "\t\t|";
                            printWriter.println(input);

                        }
                    }
                }
                if (i == orderCompleted) {
                    System.out.println(" -----------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
                    printWriter.println(" -----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                }
            }
            printWriter.close();
        } catch (IOException a) {
            System.out.println("Problem with file.");
        }
    }

    public static void printRestaurantReport(Restaurant[] res) {
        try {
            for (int i = 0; i < res.length; i++) {
                String filename = res[i].getResName() + ".txt";
                FileWriter fileWriter = new FileWriter(filename, true);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println("------------------------------------");
                printWriter.println("Summary of " + res[i].getResName() + " restaurant");
                printWriter.println("------------------------------------");
                printWriter.println();
                for (int j = 0; j < res[i].getSizeList(); j++) {
                    res[i].getSelBranch(j).setReport(res[i].getDishes());
                    res[i].getSelBranch(j).makeReport(res[i].getDishes(), res[i].getWholeReport());
                    printWriter.println("------------------------------------");
                    printWriter.println("Branch at " + res[i].getSelBranch(j).getCoordinate());
                    printWriter.println("------------------------------------");
                    printWriter.println("Number of customer: " + res[i].getSelBranch(j).getNumOfCustomer());
                    printWriter.println();
                    for (int k = 0; k < res[i].getSelBranch(j).getNumOfDishSize(); k++) {
                        printWriter.println(res[i].getSelDish(k) + ": " + res[i].getSelBranch(j).getNumOfDish(k));
                    }
                    printWriter.println();
                }
                printWriter.println("------------------------------------");
                printWriter.println("Overall");
                printWriter.println("------------------------------------");
                int countCust = 0;
                for (int j = 0; j < res[i].getReportSize(); j++) {
                    if (res[i].getResName().equals(res[i].getReport(j, 1))) {
                        countCust++;
                    }
                }

                printWriter.println("Number of customers: " + countCust);
                printWriter.println();

                for (int j = 0; j < res[i].getDishSize(); j++) {
                    int count = 0;
                    for (int k = 0; k < res[i].getReportSize(); k++) {
                        if (res[i].getSelDish(j).equals(res[i].getReport(k, 8))) {
                            count++;
                        }
                    }
                    printWriter.println(res[i].getSelDish(j) + ": " + count);
                }
                printWriter.println("------------------------------------");
                printWriter.close();
            }
        } catch (IOException a) {
            System.out.println("Problem with file.");
        }
    }

    public static void readReportRestaurant() {
        Scanner read = new Scanner(System.in);
        while (true) {
            System.out.println("Please enter restaurant's name to read its report and enter Exit to exit.");
            String response = read.nextLine();
            if (response.toUpperCase().charAt(0) == 'E') {
                break;
            }
            System.out.println("");
            response += ".txt";
            try {
                Scanner scan = new Scanner(new FileInputStream(response));
                while (scan.hasNextLine()) {
                    System.out.println(scan.nextLine());
                }
                scan.close();
            } catch (IOException b) {
                System.out.println("File not found. Please enter the right name.");
            }
            System.out.println("");
        }
    }

    public static void takeOrderAndPrint(Restaurant[] res, Customer[] cus) throws InterruptedException {
        int orderCompleted = 0;

        System.out.println("");

        int time = 0;
        int orderNum = 1;
        int TotalOrder = cus.length;

        for (int i = 0; i < cus.length; i++) {
            TotalOrder += cus[i].getResDisSize() - 1;
        }

        while (true) {

            Thread.sleep(150);

            if (time == 0) {
                System.out.println("0: A new day has started.");
            }

            //decrement time prev            
            for (int i = 0; i < res.length; i++) {
                int holdPTO;
                for (int j = 0; j < res[i].getCoorNum(); j++) {
                    if (res[i].getSelPrevTimeOrder(j) != 0) {
                        holdPTO = res[i].getSelPrevTimeOrder(j);
                        holdPTO--;
                        res[i].setSelPrevTimeOrder(j, holdPTO, "decre");
                    }
                }
            }

            //arrival and order
            orderNum = arrivalOrder(res, cus, time, orderNum);

            //finished deliver
            orderCompleted = printFinishDelivery(res, cus, time, orderCompleted);

            //finished order 
            printFinishOrder(res, cus, time);

            //start deliver
            printDelivery(res, time);

            //branch take order
            printBranchTakeOrder(res, time);

            time++;

            //closing
            if (orderCompleted == TotalOrder) {
                System.out.println(time - 1 + ": All customers served and shops are closed.\n");
                break;
            }
        }

        //print log to console and text file
        printLog(res, orderCompleted);

        //report by restaurant and its branch
        printRestaurantReport(res);

        //read report by restaurant
        readReportRestaurant();
    }

    public static int calcRes() {
        int numRes = 1;
        try {
            Scanner read = new Scanner(new FileInputStream("Input.txt"));
            while (read.hasNextLine()) {
                if (read.nextLine().isEmpty()) {
                    numRes++;
                }
            }
        } catch (IOException e) {
            System.out.println("The file cannot be read.");
        }
        return numRes;
    }                           //calculate number of restaurant

    public static void readRes(Restaurant res, int j) {
        int k = 0;
        try {
            Scanner read = new Scanner(new FileInputStream("Input.txt"));
            while (read.hasNextLine()) {
                String string = read.nextLine();
                //transition between restaurants
                if (string.equals("")) {
                    k++;
                    continue;
                }
                //input value based on restaurant
                if (k == j) {
                    res.add(string);
                }
            }
        } catch (IOException e) {
            System.out.println("The file cannot be read.");
        }

    }     //read restaurant data

    public static int calcCus() {
        int numCus = 1;
        try {
            Scanner read = new Scanner(new FileInputStream("Customer.txt"));
            while (read.hasNextLine()) {
                if (read.nextLine().isEmpty()) {
                    numCus++;
                }
            }
        } catch (IOException e) {
            System.out.println("The file cannot be read.");
        }
        return numCus;
    }                           //calculate number of customers

    public static void readCus(Customer cus, int j) {
        int k = 0;
        try {
            Scanner read = new Scanner(new FileInputStream("Customer.txt"));
            while (read.hasNextLine()) {
                String string = read.nextLine();
                //transition between customers
                if (string.equals("")) {
                    k++;
                    continue;
                }
                //input value based on customer
                if (k == j) {
                    cus.add(string);
                }
            }
        } catch (IOException e) {
            System.out.println("The file cannot be read.");
        }
    }       //read customers' data

    public static void createMap(Restaurant[] res) {
        calcMapArea(res);
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].getCoorNum(); j++) {
                map[Character.getNumericValue(res[i].getSelCoordinate(j).charAt(0))][Character.getNumericValue(res[i].getSelCoordinate(j).charAt(2))] = res[i].getResName().charAt(0);
            }
        }
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 0) {
                    map[i][j] = '0';
                }
            }
        }
        System.out.println("\t\t\t\t" + "\u001B[32m" + "    CRABFOOD MAP\n" + "\u001B[0m");

        for (int i = 0; i < map.length; i++) {

            System.out.print("\t\t\t\t");
            for (int j = 0; j < map[i].length; j++) {
                printMap(map[i][j]);
            }
            System.out.println("");
        }

        System.out.println("\n");
    }

    public static void calcMapArea(Restaurant[] res) {
        int maxRow = Character.getNumericValue(res[0].getSelCoordinate(0).charAt(0));
        int maxCol = Character.getNumericValue(res[0].getSelCoordinate(0).charAt(2));

        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].getCoorNum(); j++) {
                if (Character.getNumericValue(res[i].getSelCoordinate(j).charAt(0)) > maxRow) {
                    maxRow = Character.getNumericValue(res[i].getSelCoordinate(j).charAt(0));
                }
                if (Character.getNumericValue(res[i].getSelCoordinate(j).charAt(2)) > maxCol) {
                    maxCol = Character.getNumericValue(res[i].getSelCoordinate(j).charAt(2));
                }
            }
        }

        map = new char[maxRow + 1][maxCol + 1];
    }

    public static void printMap(char map) {

        switch (map) {
            case '0':
                System.out.print("\u001B[47m" + " " + "\u001B[0m" + "\u001B[47m" + "\u001B[34m" + "-" + "\u001B[0m" + "\u001B[0m" + "\u001B[47m" + " " + "\u001B[0m");
                break;
            case 'B':
                System.out.print("\u001B[41m" + " " + "\u001B[0m" + "\u001B[41m" + "\u001B[33m" + map + "\u001B[0m" + "\u001B[0m" + "\u001B[41m" + " " + "\u001B[0m");
                break;
            case 'C':
                System.out.print("\u001B[43m" + " " + "\u001B[0m" + "\u001B[43m" + "\u001B[31m" + map + "\u001B[0m" + "\u001B[0m" + "\u001B[43m" + " " + "\u001B[0m");
                break;
            case 'J':
                System.out.print("\u001B[42m" + " " + "\u001B[0m" + "\u001B[42m" + "\u001B[34m" + map + "\u001B[0m" + "\u001B[0m" + "\u001B[42m" + " " + "\u001B[0m");
                break;
            case 'P':
                System.out.print("\u001B[44m" + " " + "\u001B[0m" + "\u001B[44m" + "\u001B[32m" + map + "\u001B[0m" + "\u001B[0m" + "\u001B[44m" + " " + "\u001B[0m");
                break;
            case 'S':
                System.out.print("\u001B[45m" + " " + "\u001B[0m" + "\u001B[45m" + "\u001B[36m" + map + "\u001B[0m" + "\u001B[0m" + "\u001B[45m" + " " + "\u001B[0m");
                break;
            default:
                System.out.print("\u001B[46m" + " " + "\u001B[0m" + "\u001B[46m" + "\u001B[31m" + map + "\u001B[0m" + "\u001B[0m" + "\u001B[46m" + " " + "\u001B[0m");
                break;
        }

    }

    public static int delay() {

        Random random = new Random();
        int jam = random.nextInt(3);

        if (jam == 0 || jam == 1) {
            return 0;
        } else {
            return 1 + random.nextInt(5);
        }
    }
}
