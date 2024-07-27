import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * This program reads the coordinates and also the cities information from a file
 * and finds the shortest path between two cities which are given by the user.
 * Finally, visualizes the cities and the shortest path on a map with the StdDraw Library.
 *
 * @author Celil Ozkan, Student ID: 20234003234
 * @since Date: 23 March 2024
 */


public class CelilOzkan {

    /**
     * The main method of the program.
     * It reads city coordinates and information, gets user input for cities of travel,
     * finds the shortest path, and visualizes the cities and shortest path on a map.
     *
     * @param args The command-line arguments (not used).
     * @throws IOException If an I/O error occurs.
     */

    public static void main(String[] args) throws IOException {

        // One should change paths below before running the code!!!

        // Set the file paths to the relevant files
        String coordinateFilePath = "/Users/celil/Desktop/Cmpe160/Projects/Turkey Navigation/city_coordinates.txt";
        String connectionFilePath = "/Users/celil/Desktop/Cmpe160/Projects/Turkey Navigation/city_connections.txt";
        String mapFilePath = "/Users/celil/Desktop/Cmpe160/Projects/Turkey Navigation/map.png";

        // Read the cities from the file
        // And fulfill the relevant arrays
        ArrayList<City> cities = getCityCoordinates(coordinateFilePath);
        ArrayList<String> cityNames = getCityNames(cities);

        // Get the cities of travel from the user
        String[] userCityNames = getCitiesOfTravelFromUser(cityNames);
        String startCityName = userCityNames[0];
        String endCityName = userCityNames[1];

        // Adds all connections to relevant cities' data fields
        getCityConnections(connectionFilePath, cities);

        // Get the City objects corresponding to the city names
        City startCity = getCity(startCityName, cities);
        City endCity = getCity(endCityName, cities);

        // If there is no path between the cities, inform the user on the console
        if (!isTravelPossible(startCity, endCity, cities)) {
            System.out.println("No path could be found.");
        }
        else{
            // Inform the user about the shortest path in the terminal
            informUser(startCity, endCity, cities);

            // Now start Drawing Operations
            // Set required variables
            int mapPngWidth = 2377;
            int mapPngHeight = 1055;
            int width = 1000;
            int height = 600;

            // General settings
            // Set the canvas size and scale
            // Use the map image as the background
            StdDraw.setCanvasSize(width, height);
            StdDraw.setXscale(0, mapPngWidth);
            StdDraw.setYscale(0, mapPngHeight);
            StdDraw.picture(mapPngWidth / 2.0, mapPngHeight / 2.0, mapFilePath, mapPngWidth, mapPngHeight);

            // Get the shortest path and store it in the shortestPath array
            ArrayList<City> shortestPath = findShortestPath(startCity, endCity, cities);

            // Draw all the cities
            // Cities in the shortest path are drawn in blue
            // The other cities are drawn in gray
            // The connections between the cities are drawn in gray
            drawCityAndConnections(cities, shortestPath);

        }
    }


    /**
     * Method to read city information from file and create City objects
     *
     * @param coordinateFilePath A string representing the file path of the city coordinates file
     * @return An ArrayList of City objects
     * @throws IOException If an I/O error occurs
     */
    // Method to read city information from file and create City objects
    public static ArrayList<City> getCityCoordinates(String coordinateFilePath) throws IOException {

        // Set a cities array that consist of City objects
        ArrayList<City> cities = new ArrayList<>();

        // Build a scanner object to get city information from coordinate file
        Scanner sc = new Scanner(new FileInputStream(coordinateFilePath));

        // Set a city index indicator
        int cityIndex = 0;
        // If the file has a next line
        while (sc.hasNextLine()) {
            // Read the next line
            String nextLine = sc.nextLine();
            // Split the line by ", " to get the city name, x and y coordinates
            String[] lineParts = nextLine.split(", ");
            String cityName = lineParts[0];
            // Parse the x and y coordinates to integers
            int cityX = Integer.parseInt(lineParts[1]);
            int cityY = Integer.parseInt(lineParts[2]);

            // Create a City object and add it to the cities array
            City city = new City(cityName, cityX, cityY, cityIndex, null);
            cities.add(city);
            // Increment the city index
            cityIndex++;
        }
        // Close the scanner
        sc.close();

        // Return the cities array
        return cities;
    }

    /**
     * Method to read the connections between the cities from the connections file
     *
     * @param connectionFilePath A string representing the file path of the city connections file
     * @param cities An ArrayList of City objects
     * @throws IOException If an I/O error occurs
     */
    // Method to read the connections between the cities from the connections file
    public static void getCityConnections(String connectionFilePath, ArrayList<City> cities) throws IOException{

        // Build a scanner object to get city connections from connection file
        Scanner sc = new Scanner(new FileInputStream(connectionFilePath));

        // If the file has a next line
        while (sc.hasNextLine()) {
            // Read the next line
            String nextLine = sc.nextLine();
            // Split the line by ", " to get the city names
            String[] lineParts = nextLine.split(",");
            // No need to parse anything we need String values
            String cityName = lineParts[0].toLowerCase(Locale.US);
            String otherCityName = lineParts[1].toLowerCase(Locale.US);

            // Find the City objects corresponding to given city names
            City city = getCity(cityName, cities);
            City otherCity = getCity(otherCityName, cities);

            // Add the other city to the adjacent cities of the city
            // and also the city to the adjacent cities of the other city
            // Since the connections are bidirectional
            city.addAdjacentCity(otherCity);
            otherCity.addAdjacentCity(city);
        }
        // Close the scanner
        sc.close();

        // No need to return anything since the connections are added to the cities array
    }

    /**
     * Method to get the city names array from the cities array of City objects
     *
     * @param cities An ArrayList of City objects
     * @return An ArrayList of Strings representing the city names
     */
    // A simple method to get the city names from the cities array
    // And create an array list consists of city names
    public static ArrayList<String> getCityNames(ArrayList<City> cities) {
        // Create an array list to store city names
        ArrayList<String> cityNames = new ArrayList<>();

        // Create a for loop to iterate over the cities array
        // And add the city names to the cityNames array list
        // Use toLowerCase(Locale.US) to make the city names case-insensitive

        for (City city : cities) {

            String cityNameLowered = city.cityName.toLowerCase(Locale.US);
            cityNames.add(cityNameLowered);
        }
        return cityNames;
    }

    /**
     * Method to get the City object corresponding to the given city name
     *
     * @param cityName A string representing the city name
     * @param cities An ArrayList of City objects
     * @return A City object corresponding to the city name
     */
    // A simple method to get the City object corresponding to the city name
    public static City getCity(String cityName, ArrayList<City> cities) {
        // Iterate over the cities array
        for (City city : cities) {
            // If a given name is equal to any object's city name
            // Return the object
            if (city.cityName.equals(cityName)) {
                return city;
            }
        }
        return null;
    }

    /**
     * Method to find the shortest path between two cities
     *
     * @param startCity A City object representing the starting city
     * @param endCity A City object representing the ending city
     * @param cities An ArrayList of City objects
     * @return An ArrayList of City objects representing the shortest path
     */
    public static ArrayList<City> findShortestPath(City startCity, City endCity, ArrayList<City> cities) {

        // Initialize shortestPath to store the shortest path
        // Initialize array to keep track of visited cities all values are false by default
        // Initialize array to store distances all values are 0.0 by default

        ArrayList<City> shortestPath = new ArrayList<>();
        boolean[] visited = new boolean[cities.size()];
        double[] distances = new double[cities.size()];

        // Set all the distances to very high values, I at first set them to Double.MaxValue
        // But it caused some problems
        for (int i = 0; i < distances.length; i++) {
            distances[i] = Integer.MAX_VALUE;
        }

        // Set the distance of the start city to 0
        // Obviously the distance of the start city to itself is 0
        distances[startCity.index] = 0;

        // Iterate over the cities array
        for (int i = 0; i < cities.size(); i++){

            // Find the city with the minimum distance
            // Mark the city as visited
            // For the first iteration, the start city will be the closest city
            // Because the distance of the start city to itself is 0
            City currentCity = findClosestCity(cities, distances, visited);
            visited[currentCity.index] = true;


            // Iterate over the neighbors of the current city
            for (City neighbor: currentCity.adjacentCities){
                // If the neighbor is not visited
                if (!visited[neighbor.index]){
                    // Find the distance between the current city and the neighbor
                    double distance = currentCity.distanceBetweenCities(neighbor);
                    // If the distance plus distance from starting city to current city is
                    // less than the neighbor's distance
                    if (distances[currentCity.index] + distance < distances[neighbor.index]){
                        // Update the neighbor's distance
                        // Update the previous city of the neighbor
                        // There previous city denotes the city that is before the neighbor in the shortest path
                        // Starting from the start city to the neighbor
                        // Similarly distance denotes the distance from the start city to the neighbor

                        distances[neighbor.index] = distances[currentCity.index] + distance;
                        neighbor.previousCity = currentCity;
                    }
                }
            }
        }


        // This part of the method is to get the shortest path
        // Just above we checked for each neighbor if the distance is less than the neighbor's distance
        // If it is less, we updated the neighbor's distance and the previous city
        // That's why we have the previous city of each city
        // Start from the end city and iterate over the previous cities
        // At the end we will get to the point where the previous city is the start city
        // And we know that the start city's previous city is null
        // At this point we have the shortest path, and the while loop below will be broken

        City iteratedCity = endCity;
        while (iteratedCity != null){
            shortestPath.add(0, iteratedCity);
            iteratedCity = iteratedCity.previousCity;
        }


        return shortestPath;
    }

    /**
     * Method to find the closest city to the current city
     *
     * @param cities An ArrayList of City objects representing the cities
     * @param distances An array of doubles representing the distances
     * @param visited An array of booleans representing the visited cities
     * @return A City object representing the closest city
     */
    private static City findClosestCity(ArrayList<City> cities, double[] distances, boolean[] visited) {

        // Set the minimum distance to a very high value
        // Set the closest city to null

        double minDistance = Double.MAX_VALUE;
        City closestCity = null;

        // Iterate over the distances array
        // For the first iteration, the minimum distance will be the first element of the distances array
        // Which is the distance of the first city to itself that's why it's 0, and it's the starting point
        for (int i = 0; i < distances.length; i++){

            // If the distance is less than the minimum distance and the city is not visited
            if (distances[i] < minDistance && !visited[i]){

                // Update the minimum distance
                // Update the closest city

                minDistance = distances[i];
                closestCity = cities.get(i);
            }
        }

        // Return the closest city
        return closestCity;
    }


    /**
     * Method to draw the cities and connections
     *
     * @param cities An ArrayList of City objects representing the cities
     * @param shortestPath An ArrayList of City objects that are in the shortest possible path
     */
    // Main method to draw the cities and connections
    public static void drawCityAndConnections(ArrayList<City> cities, ArrayList<City> shortestPath) {

        // For any city in the cities array if city is in the shortest path
        // Change the color of the pen to Blue and draw the city and write its name
        // Otherwise, change the color of the pen to Gray and draw the city and write its name
        for (City city : cities) {
            if (shortestPath.contains(city)) {
                StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
            } else {
                StdDraw.setPenColor(StdDraw.GRAY);
            }
            StdDraw.filledCircle(city.x, city.y, 5);

            StdDraw.setFont(new Font("Helvetica", Font.PLAIN, 13));

            String cityNameCapitalized = city.cityName.substring(0, 1).toUpperCase(Locale.US) + city.cityName.substring(1);

            StdDraw.text(city.x, city.y + 12, cityNameCapitalized);
        }

        // After all the cities are drawn
        // Draw the connections between the cities unless it's a part of the shortest path
        // First change the color of the pen to Gray and draw the connections
        // Since they are not in the shortest path they will be drawn in Gray
        for (City city : cities) {
            for (City otherCity : city.adjacentCities) {
                if (!shortestPath.contains(otherCity)){
                    StdDraw.setPenRadius(0.002);
                    StdDraw.setPenColor(StdDraw.GRAY);
                    StdDraw.line(city.x, city.y, otherCity.x, otherCity.y);
                }
            }
        }

        // After all the cities and connections are drawn except the shortest path
        // Now draw the shortest path in blue
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
            StdDraw.setPenRadius(0.01);
            // Get first city's x and y and also the second city's x and y in the shortest path array
            // Recall that ShortestPath array consists of City objects
            StdDraw.line(shortestPath.get(i).x, shortestPath.get(i).y,
                    shortestPath.get(i + 1).x, shortestPath.get(i + 1).y);
        }


    }

    /**
     * Method to get the cities of travel from the user
     *
     * @param cityNames An ArrayList of Strings representing the city names
     * @return A String array that's filled with the start and end city names
     */
    // Method to get the cities of travel from the user
    public static String[] getCitiesOfTravelFromUser(ArrayList<String> cityNames) {
        // Create a scanner object to get input from the user
        Scanner sc = new Scanner(System.in);
        // Initialize the city names
        String startCityName;
        String endCityName;

        // Get the starting city from the user in this while loop
        while (true) {

            // Inform the user to give a starting city
            // Read the input from the user
            // If city is not in the cityNames array, inform the user and ask for a valid city name again
            // If a valid city name is entered, break the loop
            // Use strip the input to remove leading and trailing whitespaces

            System.out.println("Enter starting city: ");
            String givenStartCityName = sc.nextLine();
            startCityName = givenStartCityName.strip().toLowerCase(Locale.US);
            if (!cityNames.contains(startCityName)) {
                System.out.printf("City named '%s' not found. Please enter a valid city name.\n", givenStartCityName);
            } else {
                break; // Exit the loop if a valid city name is entered
            }
        }

        // Almost the same while loop with the previous one
        // To get the ending city from the user
        // Use strip the input to remove leading and trailing whitespaces


        while (true) {
            System.out.println("Enter destination city: ");
            String givenEndCityName = sc.nextLine();
            endCityName = givenEndCityName.strip().toLowerCase(Locale.US);
            if (!cityNames.contains(endCityName)) {
                System.out.printf("City named '%s' not found. Please enter a valid city name.\n", givenEndCityName);
            } else {
                break; // Exit the loop if a valid city name is entered
            }
        }

        // Close the scanner
        sc.close();

        // Return the start and end city names as a String array
        return new String[]{startCityName, endCityName};
    }

    /**
     * Method to inform the user about the shortest path in the console
     *
     * @param startCity A City object representing the starting city
     * @param endCity A City object representing the ending city
     * @param cities An ArrayList of City objects
     */
    // Method to inform the user about the shortest path in the console
    public static void informUser(City startCity, City endCity, ArrayList<City> cities) {

        // Get the shortest path array with the help of the findShortestPath method
        ArrayList<City> shortestPath = findShortestPath(startCity, endCity, cities);

        // Set a variable to store the total distance
        double totalDistance = 0;

        // Iterate over the shortest path array
        // And calculate the total distance between each consecutive cities
        // Then add these distances to the total distance
        // Observe that there will be n-1 distances between n cities
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            totalDistance += shortestPath.get(i).distanceBetweenCities(shortestPath.get(i + 1));
        }

        // Print the total distance and the path in the console with a formatted output
        System.out.printf("Total Distance: %.2f. Path: ", totalDistance);

        // Iterate over the shortest path array
        for (int i = 0; i < shortestPath.size(); i++) {

            // Get the city object at index i
            City city = shortestPath.get(i);
            String cityNameCapitalized = city.cityName.substring(0, 1).toUpperCase(Locale.US) + city.cityName.substring(1);

            // Print the city name with an arrow if it is not the last city
            if (i != shortestPath.size() - 1){
                System.out.print(cityNameCapitalized + " -> ");
            }

            // Print the city name without an arrow if it is the last city
            else {
                System.out.print(cityNameCapitalized);
            }
        }


    }

    /*
    I could also use this terrifically simple way to check whether travel is possible or not.
    However, It would be kind of abuse of the method I wrote. So, I decided to write the validator below this method.

    public static boolean isTravelPossible (City startCity, City endCity, ArrayList<City> cities) {
        ArrayList<City> shortestPath = findShortestPath(startCity, endCity, cities);
        return shortestPath.size() > 1;
    }
     */

    /**
     * Method to check whether travel is possible between two cities
     *
     * @param startCity A City object representing the starting city
     * @param endCity A City object representing the ending city
     * @param cities An ArrayList of City objects
     * @return A boolean that indicates if the travel between the given cities is possible
     */
    public static boolean isTravelPossible (City startCity, City endCity, ArrayList<City> cities) {

        // Initialize the isVisited array that will keep track of visited cities
        // Observe all are false at the beginning by default
        boolean[] isVisited = new boolean[cities.size()];

        // Mark the start city as visited
        isVisited[startCity.index] = true;

        // Search for travel possibilities starting with the start city's neighbors
        for (City neighbor: startCity.adjacentCities){

            // Unless a city visited call the recursive function named searchTravelPossibilities
            if (!isVisited[neighbor.index]){
                searchTravelPossibilities(neighbor, isVisited);
            }
        }

        // At the end of the search,
        // All possible cities that can be visited are marked as true in the isVisited array
        // If the end city is visited, then there is a path between the start and end cities
        // Then return true
        return isVisited[endCity.index];
    }

    /**
     * Recursive method to search for travel possibilities between inputCities
     * Takes any city as input and marks the city as visited and calls the function for the neighbors of that input city
     *
     * @param inputCity A City object representing any arbitrary city
     * @param isVisited A boolean array that keeps track of visited cities which is need for isTravelPossible method
     */
    public static void searchTravelPossibilities(City inputCity, boolean[] isVisited){
        // Mark the given city as visited
        isVisited[inputCity.index] = true;

        // Call the function for the neighbors of the input city recursively
        // So that every possible city that can be visited is marked as visited
        for (City inputCityNeighbor: inputCity.adjacentCities){
            if (!isVisited[inputCityNeighbor.index]){
                searchTravelPossibilities(inputCityNeighbor, isVisited);
            }
        }
    }
}


