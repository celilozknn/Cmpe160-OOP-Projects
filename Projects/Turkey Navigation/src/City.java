import java.util.ArrayList;
import java.util.Locale;

/**
 * City class represents a city in Turkey.
 * Each city has a name, x and y coordinates,
 * an index and a list of adjacent cities.
 */
public class City {

    public String cityName;
    public int x;
    public int y;
    public int index;

    public City previousCity;
    public ArrayList<City> adjacentCities;

    /**
     * Constructor for the City objects
     *
     * @param cityName The name of the city
     * @param x The x coordinate of the city
     * @param y The y coordinate of the city
     * @param index The index of the city
     * @param adjacentCities The list of adjacent cities
     */

    public City(String cityName, int x, int y, int index, ArrayList<City> adjacentCities){
        // Initialize the city object with the given parameters
        // Store city names in lowercase when I need to print it to somewhere
        // I will simply change it to capitalized version
        // Storing it in lowercase will make it easier to handle errors stems from case sensitivity
        this.cityName = cityName.toLowerCase(Locale.US);
        this.x = x;
        this.y = y;
        this.index = index;
        this.previousCity = null;
        if (adjacentCities != null) {
            this.adjacentCities = adjacentCities;
        } else {
            this.adjacentCities = new ArrayList<>();
        }
    }


    /**
     * Method to add an adjacent city to the list of a city's adjacent cities
     * @param otherCity The city to be added to the list of adjacent cities
     *                  of the city object
     */
    public void addAdjacentCity(City otherCity){
        adjacentCities.add(otherCity);
    }

    /**
     * Method to calculate the distance between two cities
     * @param otherCity The city to calculate the distance to
     * @return The distance between the two cities as a double
     */
    public double distanceBetweenCities(City otherCity){
        return Math.sqrt(Math.pow(x - otherCity.x, 2) + Math.pow(y - otherCity.y, 2));
    }



}
