import java.util.ArrayList;

public class Path {

    private int startCityIndex;
    private int endCityIndex;
    private double distance;
    private double pheromone;

    public Path(ArrayList<City> coordinates, int startCityIndex, int endCityIndex,
                double distance, double initialPheromone){
        this.startCityIndex = startCityIndex;
        this.endCityIndex = endCityIndex;
        this.distance = calculatePathDistance(coordinates);
        this.pheromone = initialPheromone;
    }

    public int getStartCityIndex() {
        return startCityIndex;
    }

    public void setStartCityIndex(int startCityIndex) {
        this.startCityIndex = startCityIndex;
    }

    public int getEndCityIndex() {
        return endCityIndex;
    }

    public void setEndCityIndex(int endCityIndex) {
        this.endCityIndex = endCityIndex;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPheromone() {
        return pheromone;
    }

    public void setPheromone(double pheromone) {
        this.pheromone = pheromone;
    }

    public double getEdgeValue(double alpha, double beta){
        return Math.pow(pheromone, alpha) * Math.pow(1.0 / distance, beta);
    }

    public String toString(){
        String distance4f = String.format("%.4f", distance);
        return startCityIndex + " -> " + endCityIndex
                + ", D: " +  distance4f + ", Ph: " + pheromone;
    }

    public void degradationPheromone(double degradationConstant){
        pheromone *= degradationConstant;
    }

    public void updatePheromone(double q, double cycleDistance){
        this.pheromone += q / cycleDistance;
    }

    private double calculatePathDistance(ArrayList<City> coordinates){
        City startCity = coordinates.get(startCityIndex);
        City endCity = coordinates.get(endCityIndex);
        double xDistance = Math.abs(startCity.getX() - endCity.getX());
        double yDistance = Math.abs(startCity.getY() - endCity.getY());
        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
    }


}
