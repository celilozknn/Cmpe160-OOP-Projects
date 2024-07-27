import java.util.ArrayList;
import java.util.Arrays;

public class Ant {

    private int antStartCityIndex;
    private ArrayList<Integer> unvisitedCities;

    private int[] path;

    /**
     * Constructor for the Ant class
     * @param numberOfCities the number of cities in the problem
     */
    public Ant(int numberOfCities){
        antStartCityIndex = (int) (Math.random() * numberOfCities);

        unvisitedCities = new ArrayList<>();
        for(int i = 0; i < numberOfCities; i++){
            if(i != antStartCityIndex){
                unvisitedCities.add(i);
            }
        }

        path = new int[numberOfCities];
        path[0] = antStartCityIndex;

    }

    /**
     * Getter for the unvisited cities
     * @return the unvisited cities
     */
    public ArrayList<Integer> getUnvisitedCities() {
        return unvisitedCities;
    }

    /**
     * Setter for the unvisited cities
     * @param unvisitedCityIndex the index of the unvisited city to remove
     */
    public void removeUnvisitedCity(int unvisitedCityIndex) {
        this.unvisitedCities.remove(unvisitedCityIndex);
    }

    /**
     * Getter for the path
     * @return the path
     */
    public int[] getPath() {
        return path;
    }

    /**
     * Setter for the path
     * @param path the path to set
     */
    public void setPath(int[] path) {
        this.path = path;
    }

    /**
     * Getter for the ant start city index
     * @return the ant start city index
     */
    public int getAntStartCityIndex() {
        return antStartCityIndex;
    }

    /**
     * Setter for the ant start city index
     * @param antStartCityIndex the ant start city index to set
     */
    public void setAntStartCityIndex(int antStartCityIndex) {
        this.antStartCityIndex = antStartCityIndex;
    }

    /**
     * Getter for the path city at the given index
     * @param index the index of the path city to get
     * @return the path city at the given index
     */
    public int getPathCity(int index){
        return path[index];
    }

    /**
     * Setter for the path city at the given index
     * @param index the index of the path city to set
     * @param cityIndex the city index to set
     */
    public void setPathCity(int index, int cityIndex){
        path[index] = cityIndex;
    }

    /**
     * ToString method for the Ant class to inform the user on the terminal
     * @return brief information about the Ant
     */
    public String toString(){
        String result = "P: ";
        for(int i = 0; i < path.length; i++){
            result += path[i];
            if (i < path.length - 1){
                result += ",";
            }
        }

        result += "; UV: ";
        for(int i = 0; i < unvisitedCities.size(); i++){
            result += unvisitedCities.get(i) + " ";
        }
        return result;
    }

    /**
     * Method to move the ant to the next city
     * @param coordinates the list of cities
     * @param pathPheromoneArray the array of paths
     * @param moveCounter the move counter
     * @param alpha the alpha value
     * @param beta the beta value
     */
    public void move(ArrayList<City> coordinates, Path[] pathPheromoneArray,
                                    int moveCounter, double alpha, double beta){

        // Find possible paths
        ArrayList<Path> ways = findPaths(pathPheromoneArray, moveCounter, alpha, beta);

        // Calculate the total edge value
        double totalEdgeValue = calculateTotalEdgeValue(ways, alpha, beta);

        // Calculate the probabilities of each path (Normalize Edge Values)
        ArrayList<Double> waysProbabilities = new ArrayList<>();
        fillPathProbabilities(waysProbabilities, ways, totalEdgeValue, alpha, beta);

        double random = Math.random();

        double currentProbability = 0.0;
        for (int i = 0; i < ways.size(); i++){

            currentProbability += waysProbabilities.get(i);
            if (currentProbability > random){
                Path chosenWay = ways.get(i);
                    path[moveCounter + 1] = chosenWay.getEndCityIndex();
                    unvisitedCities.remove(i);
                break;

            }
        }


    }

    /**
     * Method to fill the path probabilities
     * @param waysProbabilities the list of path probabilities
     * @param ways the list of possible paths
     * @param totalEdgeValue the total edge value
     * @param alpha the alpha value
     * @param beta the beta value
     */
    public void fillPathProbabilities(ArrayList<Double> waysProbabilities, ArrayList<Path> ways, double totalEdgeValue,
                                      double alpha, double beta){
        for (Path way: ways){
            waysProbabilities.add(way.getEdgeValue(alpha, beta) / totalEdgeValue);
        }
    }


    /**
     * Method to calculate the total edge value
     * @param possiblePaths the list of possible paths
     * @param alpha the alpha value
     * @param beta the beta value
     * @return the total edge value
     */
    public double calculateTotalEdgeValue(ArrayList<Path> possiblePaths, double alpha, double beta){
        double totalEdgeValue = 0.0;
        for (Path way: possiblePaths){
            totalEdgeValue += way.getEdgeValue(alpha, beta);
        }
        return totalEdgeValue;
    }

    /**
     * Method to find the possible paths
     * @param pathPheromoneArray the array of paths
     * @param moveCounter the move counter
     * @param alpha the alpha value
     * @param beta the beta value
     * @return the list of possible paths
     */
    public ArrayList<Path> findPaths(Path[] pathPheromoneArray, int moveCounter, double alpha, double beta){
        ArrayList<Path> possiblePaths = new ArrayList<>();
        for (Path way: pathPheromoneArray){

            // If the start city is the current city and the end city is in the unvisited cities
            // or if the end city is the current city and the start city is in the unvisited cities
            if (way.getStartCityIndex() == path[moveCounter] && unvisitedCities.contains(way.getEndCityIndex())){
                possiblePaths.add(way);
            }

        }

        return possiblePaths;
    }








}
