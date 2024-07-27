public class City {
    private double x;
    private double y;

    /**
     * Constructor for the City class
     * @param x the x coordinate of the city
     * @param y the y coordinate of the city
     */
    public City(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for the x coordinate
     * @return the x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Getter for the y coordinate
     * @return the y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Setter for the x coordinate
     * @param x the x coordinate to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Setter for the y coordinate
     * @param y the y coordinate to set
     */
    public void setY(double y) {
        this.y = y;
    }
}
