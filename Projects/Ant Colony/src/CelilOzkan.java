import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This program reads the coordinates and also the cities information from a file
 * and finds the shortest cycle path that visits all the cities exactly once.
 * The program has two methods to find the shortest path:
 * 1. Brute-Force Method
 * 2. Ant Colony Optimization
 * The program also has two drawing methods:
 * 1. Displays the shortest path and the shortest distance
 * 2. Displays the pheromone map
 * Finally, visualizes the cities and the shortest path on a map with the StdDraw Library.
 *
 * @author Celil Ozkan, Student ID: 20234003234
 * @since Date: 12 May 2024
 */
public class CelilOzkan {

    /**
     * The main method of the program.
     * @param args The command line arguments
     * The program reads the coordinates from the input file and stores them in an arraylist.
     * Then, the program initializes the pheromone intensity array and the path array.
     * Depending on the chosen method, the program uses the brute-force method or the ant colony optimization method.
     */

    public static void main(String[] args) {

        // 1 Brute-Force Method
        // 2 Ant Colony Optimization
        int chosenMethod = 2;

        // 1 Displays the shortest path and the shortest distance
        // 2 Displays the pheromone map
        int drawMethod = 2;

        // If the chosen method is 1, the drawMethod must be 1
        // Otherwise throw an exception
        if (chosenMethod == 1 && drawMethod == 2){
            throw new IllegalArgumentException("Brute-Force Method cannot be used with the pheromone map");
        }

        double initialPheromone = 0.1; // The initial pheromone intensity
        double alpha = 1.7; // The importance of the pheromone
        double beta = 1.7; // The importance of the distance
        double q = 0.001; // The constant that is used to update the pheromone intensity
        double delta; // The change in the pheromone intensity
        double degradationFactor = 0.92; // The factor used to decrease the pheromone intensity for each iteration
        int numberOfAnts = 50;
        int numberOfIterations = 100;
        ArrayList<City> coordinates = new ArrayList<City>();
        int[] minCycle;


        // Get the coordinates of the cities
        try{
            getCoordinates(coordinates);
        }catch(Exception e){
            System.out.println("File not found");
            return;
        }

        // Create an array of paths to store the pheromone intensity of each path
        Path[] pathArray  = new Path[coordinates.size() * (coordinates.size() - 1)];

        // If the chosen method is 1, use the brute-force method
        if (chosenMethod == 1){
            double startTime = System.currentTimeMillis();

            minCycle = bruteForce(coordinates);

            System.out.println("Method: Brute-Force Method");

            // Print the shortest distance and the shortest path
            System.out.printf("Shortest Distance: %.5f\n", routeDistance(minCycle, coordinates));
            System.out.println("Shortest Path: " + Arrays.toString(incrementOneIndex(minCycle)));


            double endTime = System.currentTimeMillis();
            double executionTime = (endTime - startTime) / 1000;
            System.out.println("Time it takes to find the shortest path: " + executionTime + " seconds.");

            draw(coordinates, minCycle, drawMethod, pathArray);

        }

        // If the chosen method is 2, use the ant colony optimization method
        if (chosenMethod == 2){
            double startTime = System.currentTimeMillis();

            minCycle = antColonyOptimization(coordinates, alpha, beta, q, degradationFactor,
                    numberOfAnts, numberOfIterations, initialPheromone, pathArray);

            // Print the shortest distance and the shortest path
            System.out.println("Method: Ant Colony Method");
            System.out.printf("Shortest Distance: %.5f\n", routeDistance(minCycle, coordinates));
            System.out.println("Shortest Path: " + Arrays.toString(incrementOneIndex(minCycle)));


            double endTime = System.currentTimeMillis();
            double executionTime = (endTime - startTime) / 1000;
            System.out.println("Time it takes to find the shortest path: " + executionTime + " seconds.");

            draw(coordinates, minCycle, drawMethod, pathArray);

        }
    }

    /**
     * Method to read city information from file and create City objects
     *
     * @param coordinates The arraylist that stores the city coordinates
     *                    The coordinates are stored in City objects
     * @param alpha The importance of the pheromone
     * @param beta The importance of the distance
     * @param q The constant that is used to update the pheromone intensity
     * @param degradationFactor The factor used to decrease the pheromone intensity for each iteration
     * @param numberOfAnts The number of ants
     * @param numberOfIterations The number of iterations
     * @param initialPheromone The initial pheromone intensity
     * @param pathArray The array that stores the pheromone intensity of each path
     * @throws FileNotFoundException If the file is not found
     * @return void
     */

    private static int[] antColonyOptimization(ArrayList<City> coordinates, double alpha, double beta, double q,
                                              double degradationFactor, int numberOfAnts, int numberOfIterations,
                                              double initialPheromone, Path[] pathArray){


        int numberOfCities = coordinates.size();

        initializePaths(coordinates, initialPheromone, pathArray);

        // Initialize the best distance
        int[] minCycle = new int[coordinates.size()];
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < numberOfIterations; i++) {

            // Create an array of ants
            Ant[] ants = new Ant[numberOfAnts];

            // Initialize the ants
            // Implicitly creates two arrays for each ant namely unvisitedCities and path taken by the ant
            for (int j = 0; j < numberOfAnts; j++){
                ants[j] = new Ant(coordinates.size());
            }

            int moveCounter = 0;
            // For each movement (Ants didn't finish a cycle yet)
            for (int j = 0; j < numberOfCities - 1; j++) {
                // Move each ant
                for (int k = 0; k < numberOfAnts; k++) {
                    ants[k].move(coordinates, pathArray, moveCounter, alpha, beta);
                }
                // After all ants move one edge, update the counter that keeps track of the moves
                moveCounter++;
            }
            // All the ants finished a cycle

            for (int j = 0; j < numberOfAnts; j++){

                int[] antCycle = createCycleForACO(ants[j].getPath());
                double antCycleDistance = routeDistance(antCycle, coordinates);

                // Update the pheromone intensity of the paths
                for (int node = 0; node < antCycle.length - 1; node++){
                    int startCityIndex = antCycle[node];
                    int endCityIndex = antCycle[node + 1];
                    for (Path path : pathArray){
                        if (path.getStartCityIndex() == startCityIndex && path.getEndCityIndex() == endCityIndex){
                            path.updatePheromone(q, antCycleDistance);
                            break;
                        }
                    }
                }


                if (antCycleDistance < minDistance){
                    minDistance = antCycleDistance;
                    minCycle = antCycle;
                }
            }

            // Degrade the pheromone intensity array (multiplies the intensity by the given factor)
            for (Path path : pathArray){
                path.degradationPheromone(degradationFactor);
            }
        }

        return minCycle;
    }


    /**
     * Method to increment the indexes of the path by one
     * @param route The path in zeroIndex format
     * @return The path in oneIndex format
     */

    private static int[] incrementOneIndex(int[] route){

        // Increments all the indexes by one to output the path in oneIndex  format
        int[] newRoute = new int[route.length];
        for (int i = 0; i < route.length; i++){
            newRoute[i] = route[i] + 1;
        }
        return newRoute;
    }

    /**
     * Method to draw the cities and the shortest path on a map
     * @param coordinates The arraylist that stores the city coordinates
     * @param minRoute The shortest path
     * @param drawingMethod The drawing method
     * @param pathArray The array that stores the pheromone intensity of each path
     */

    private static void draw(ArrayList<City> coordinates, int[] minRoute, int drawingMethod, Path[] pathArray){
        StdDraw.enableDoubleBuffering();
        int canvasWidth = 800;
        int canvasHeight = 800;
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        double circleRadius = 0.02;
        StdDraw.clear(StdDraw.WHITE);
        StdDraw.setFont(new Font("Helvetica", Font.BOLD, 16));

        // If drawingMethod is 1, draw the shortest path
        if (drawingMethod == 1){
            for (int i = 0; i < minRoute.length - 1; i++){
                StdDraw.setPenRadius(0.005);
                StdDraw.setPenColor(StdDraw.BLACK);
                double x1 = coordinates.get(minRoute[i]).getX();
                double y1 = coordinates.get(minRoute[i]).getY();
                double x2 = coordinates.get(minRoute[i + 1]).getX();
                double y2 = coordinates.get(minRoute[i + 1]).getY();
                StdDraw.line(x1, y1, x2, y2);
            }
        }

        // If drawingMethod is 2, draw the pheromone map
        if (drawingMethod == 2){
            for (City city : coordinates){
                for (City city2 : coordinates){
                    for (Path path : pathArray){
                        if (path.getStartCityIndex() == coordinates.indexOf(city) &&
                                path.getEndCityIndex() == coordinates.indexOf(city2)){
                            StdDraw.setPenRadius(0.075 * path.getPheromone());
                            StdDraw.setPenColor(StdDraw.BLACK);
                            double x1 = city.getX();
                            double y1 = city.getY();
                            double x2 = city2.getX();
                            double y2 = city2.getY();
                            StdDraw.line(x1, y1, x2, y2);
                            break;
                        }
                    }
                }
            }
        }

        // Draw the cities
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        for (int i = 0; i < coordinates.size(); i++){
            if (i == 0 && drawingMethod == 1){
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            }
            else if (i == 0 && drawingMethod == 2){
                StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            }
            City city = coordinates.get(i);
            StdDraw.filledCircle(city.getX(), city.getY(), circleRadius);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(city.getX(), city.getY(), Integer.toString(coordinates.indexOf(city) + 1));
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        }

        StdDraw.show();

    }

    /**
     * Method to find the shortest path using the brute-force method
     * @param coordinates The arraylist that stores the city coordinates
     * @return The shortest path
     */

    private static int[] bruteForce(ArrayList<City> coordinates){

        // Create an array of numbers from 1 to n - 1, where n is the number of cities
        // This array will be permuted to find the shortest path
        // The first city is Migros, so it is not included in the array
        int[] nums = new int[coordinates.size() - 1];
        for(int i = 0; i < coordinates.size() - 1; i++){
            nums[i] = i + 1;
        }

        int[] minRoute = nums.clone();

        // Here with the createCycle method, we add Migros to the beginning and the end of the cycle
        int[] minCycle = createCycle(minRoute);

        // Then we permute all the possible arrays to find the shortest path
        minCycleFinder(nums, 0, minCycle, coordinates);

        return minCycle;
    }

    /**
     * Method to create a cycle by adding Migros to the beginning and the end of the cycle
     * @param route The route in zeroIndex format
     * @return The route where Migros is added to the beginning and the end of the cycle
     */

    private static int[] createCycle(int[] route){

        // Create a new array with the length of the route + 2 to add Migros to the beginning and the end of the cycle
        int[] reformedCycle = new int[route.length + 2];

        // Copy the route to the new array that we will create
        // First and last positions will be Migros
        // That's why we don't copy the route starting from the first index but the second index
        System.arraycopy(route, 0, reformedCycle, 1, route.length);

        // Add Migros to the beginning and the end of the cycle
        // Both of the indexes are 0 by default, but I added these lines to make it more clear
        reformedCycle[0] = 0;
        reformedCycle[reformedCycle.length - 1] = 0;

        return reformedCycle;
    }

    /**
     * Method to create a cycle for the ant colony optimization method
     * @param route The route in zeroIndex format
     * @return The route where Migros is the both starting point and also the end point of the cycle
     */

    private static int[] createCycleForACO(int[] route){
        // Shifts the cycle to the right by one index until the first element is 0 (Migros)
        while (route[0] != 0){
            turnCycle(route);
        }

        // Create a new array with the length of the route + 1 to add Migros to the end of the cycle
        // Copy the route to the new array
        // Add Migros to the end of the cycle
        int[] newRoute = new int[route.length + 1];
        System.arraycopy(route, 0, newRoute, 0, route.length);
        newRoute[newRoute.length - 1] = route[0];
        return newRoute;
    }

    /**
     * Method to turn the cycle to the right by one index
     * @param cycle The cycle that will be turned
     */

    private static void turnCycle(int[] cycle){
        // Shifts the cycle to the right by one index
        // The last element becomes the first element
        int temp = cycle[0];
        for (int i = 0; i < cycle.length - 1; i++){
            cycle[i] = cycle[i + 1];
        }
        cycle[cycle.length - 1] = temp;
    }

    /**
     * Main helper method to find the shortest path using the brute-force method
     * @param nums The array of numbers that will be permuted
     * @param start The start index of the array
     * @param minCycle The array that stores the shortest path
     * @param coordinates The arraylist that stores the city coordinates
     */
    public static void minCycleFinder(int[] nums, int start, int[] minCycle, ArrayList<City> coordinates) {

        // Main permutation method
        // If the start index is equal to the length of the array, we have found a new permutation
        // We calculate the distance of the new permutation and compare it with the minimum distance
        if (start == nums.length - 1) {

            int[] currentCycle = createCycle(nums);

            double currentDistance = routeDistance(currentCycle, coordinates);
            double minDistance = routeDistance(minCycle, coordinates);

            if (currentDistance < minDistance) {
                System.arraycopy(currentCycle, 0, minCycle, 0, currentCycle.length);
            }

        } else {
            for (int i = start; i < nums.length; i++) {
                swap(nums, start, i); // Swap elements
                minCycleFinder(nums, start + 1, minCycle, coordinates); // Recursively permute the rest of the array
                swap(nums, start, i); // Backtrack
            }
        }
    }

    /**
     * Method to swap two elements in an array
     * @param nums The array that stores the elements
     * @param i The first index
     * @param j The second index
     */
    public static void swap(int[] nums, int i, int j) {
        // Simple swap method
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }


    /**
     * Method to calculate the distance of a route
     * @param route The route in zeroIndex format
     * @param coordinates The arraylist that stores the city coordinates
     * @return The distance of the route
     */

    private static double routeDistance(int[] route, ArrayList<City> coordinates){

        // Initialize the distance
        double distance = 0;

        // Calculate the distance between each city in the route
        // Add the distances to find the total distance
        for(int i = 0; i < route.length - 1; i++){
            // Get city coordinates
            double x1 = coordinates.get(route[i]).getX();
            double y1 = coordinates.get(route[i]).getY();
            double x2 = coordinates.get(route[i + 1]).getX();
            double y2 = coordinates.get(route[i + 1]).getY();

            distance += Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        }
        return distance;
    }


    /**
     * Method to initialize the paths
     * @param coordinates The arraylist that stores the city coordinates
     * @param initialPheromone The initial pheromone intensity
     * @param pathArray The array that stores the pheromone intensity of each path
     */
    private static void initializePaths(ArrayList<City> coordinates, double initialPheromone,
                                        Path[] pathArray){

        // Initialize the pheromone intensity array
        int numberOfCities = coordinates.size();

        // Create a path for each pair of cities
        // There is a two-way roads between each pair of cities
        for (int i = 0, pathIndex = 0; i < numberOfCities; i++){
            for (int j = 0; j < numberOfCities; j++){
                if (i != j){
                    pathArray[pathIndex] = new Path(coordinates, i, j, routeDistance(new int[]{i, j},
                            coordinates), initialPheromone);
                    pathIndex++;
                }

            }
        }
    }

    /**
     * Method to get the coordinates of the cities from the input file
     * @param coordinates The arraylist that stores the city coordinates
     * @throws FileNotFoundException If the file is not found
     */
    private static void getCoordinates(ArrayList<City> coordinates) throws FileNotFoundException {

        // Create a file object from the input file
        File coordinateFile = new File("/Users/celil/Desktop/Cmpe160/Projects/Ant Colony/inputs/input04.txt");
        Scanner sc = new Scanner(coordinateFile);

        // Read the coordinates from the file and add them to the coordinates arraylist
        // The coordinates are in the format x,y and will be stored in the City object
        // So the coordinates array is an arraylist of City objects
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] lineArray = line.split(",");
            double x = Double.parseDouble(lineArray[0]);
            double y = Double.parseDouble(lineArray[1]);
            City city = new City(x, y);
            coordinates.add(city);
        }
        sc.close();


    }
}
