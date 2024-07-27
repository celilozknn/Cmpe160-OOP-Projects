import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * An Angry Birds - like game
 * @author Celil Ozkan, Student ID: 2023400324
 * @since Date: 6 March 2024
 *
 */

public class AngryBullets {
    /**
     * Main method to run the game
     * @param args Command line arguments (not used).
     */

    public static void main(String[] args) {

        // Game Parameters
        int width = 1600; // screen width
        int height = 800; // screen height
        double gravity = 9.80665;
        double x0 = 120; // x and y coordinates of the bullet’s starting position on the platform
        double y0 = 120;
        double bulletVelocity = 180; // initial velocity
        double bulletAngle = 45.0; // initial angle

        // Box coordinates for obstacles and targets
        // Each row stores a box containing the following information:
        // x and y coordinates of the lower left rectangle corner, width, and height
        double[][] obstacleArray = {
                {1200, 0, 60, 220},
                {1000, 0, 60, 160},
                {600, 0, 60, 80},
                {600, 180, 60, 160},
                {220, 0, 120, 180}
        };
        double[][] targetArray = {
                {1160, 0, 30, 30},
                {730, 0, 30, 30},
                {150, 0, 20, 20},
                {1480, 0, 60, 60},
                {340, 80, 60, 30},
                {1500, 600, 60, 60}
        };


        StdDraw.enableDoubleBuffering();
        
        boolean quitProgram = false;
        boolean shootingDone = false;

        // While loop operates until the firing arrangements done
        int leftArrowCounter = 0, upperArrowCounter = 0, rightArrowCounter = 0, downArrowCounter = 0;

        while (!quitProgram) {

            drawBackground(width, height);

            while (!shootingDone) {
                // Arrangements for bullet line
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                    shootingDone = true;
                } else if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                    leftArrowCounter++;
                } else if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                    upperArrowCounter++;
                } else if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                    rightArrowCounter++;
                } else if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                    downArrowCounter++;
                }

                if (leftArrowCounter >= 10) {
                    leftArrowCounter -= 10;
                    if (bulletVelocity > 0)
                        bulletVelocity--;
                }
                if (upperArrowCounter >= 10) {
                    upperArrowCounter -= 10;
                    if (bulletAngle < 180)
                        bulletAngle++;
                }
                if (rightArrowCounter >= 10) {
                    rightArrowCounter -= 10;
                    bulletVelocity++;
                }
                if (downArrowCounter >= 10) {
                    downArrowCounter -= 10;
                    if (bulletAngle > -90)
                        bulletAngle--;

                }

                // Since my bullet function clears a bit thicker line than the bullet line
                // It may also clear some of the targets or obstacles as well as the shooting platform
                // Therefore I call them in each loop

                drawBulletLine(x0, y0, bulletAngle, bulletVelocity);
                drawShootingSquare(x0, y0, bulletAngle, bulletVelocity);
                drawTargets(targetArray);
                drawObstacles(obstacleArray);
                StdDraw.show();


            }

            // quitProgram gets the value false if 'r' is entered, which will start the loop again
            quitProgram = bulletTrajectory(x0, y0, bulletVelocity, bulletAngle, gravity, width, height,
                    obstacleArray, targetArray);
            shootingDone = quitProgram; // Also chang the shootingDone condition so that player sets them again
            bulletVelocity = 180; // Return to initial value
            bulletAngle = 45; // Return to initial value
        }
    }

    /**
     * Draws the trajectory of the bullet to the canvas.
     *
     * @param x0             Initial x-coordinate of the bullet.
     * @param y0             Initial y-coordinate of the bullet.
     * @param velocity       Initial velocity of the bullet.
     * @param angle          Initial angle of the bullet (in degrees).
     * @param gravity        Acceleration due to gravity.
     * @param width          Width of the game window.
     * @param height         Height of the game window.
     * @param obstacleArray  Array containing obstacle coordinates.
     * @param targetArray    Array containing target coordinates.
     * @return               True if the game should quit, false otherwise.
     */

    private static boolean bulletTrajectory(double x0, double y0, double velocity, double angle,
                                            double gravity, double width, double height,
                                            double[][] obstacleArray, double[][] targetArray) {
        // I introduced a multiplier to obtain a better gameplay
        velocity *= 0.578;

        double startingTime = System.currentTimeMillis();
        double angleRadian = Math.toRadians(angle);
        double vx = velocity * Math.cos(angleRadian);
        double vy = velocity * Math.sin(angleRadian);

        int bubbleCounter = 0; // A counter for 'bubbles' (Bigger circles)

        // Game ending conditions
        boolean minHeightReached = false;
        boolean maxWidthReached = false;
        boolean collisionOccured = false;
        boolean isObstacleHit;
        boolean isTargetHit;

        // Coordinate variables to draw the lines btw the bubbles
        double newX;
        double newY;
        double oldX = x0; // For initial conditions take the starting point, it will be updated for each bubble
        double oldY = y0; // For initial conditions take the starting point, it will be updated for each bubble

        while (true) {
            double currentTime = System.currentTimeMillis();
            double time = (currentTime - startingTime) / 160; // A time multiplier to match the video results

            double x = x0 + vx * time; // X coordinate for every time unit
            double y = y0 + vy * time - 0.5 * gravity * time * time; // Y coordinate for every time unit

            // Gets the value true if any corresponding collision occurred
            isObstacleHit = checkObstacleCollision(x, y, obstacleArray);
            isTargetHit = checkTargetCollision(x, y, targetArray);

            // Check if the bullet satisfies any game ending conditions and assign its cause to value true
            if (isObstacleHit || isTargetHit || y < 0 || x > width) {
                collisionOccured = true;
                if (y < 0) {
                    minHeightReached = true;
                }
                if (x > width){
                    maxWidthReached = true;
                }
            }

            StdDraw.setPenColor(StdDraw.BLACK);

            // If the counter is a multiple of 5 draw a bigger circle (which I call 'a bubble')
            // First give the bubble's coordinates to newX, newY which are the ends of the line
            // And use oldX, oldY for the start of the line which are the previous bubble coordinates
            // In case of first oldX, oldY they're not the previous bubble coordinates but simply x0 and y0
            // After drawing the line, set bubbles coordinates to oldX, oldY so that the next loop use them
            // Also check if collisionOccurred so that only the last bubble (inside the target or the obstacle) is drawn
            bubbleCounter++;
            if (bubbleCounter % 5 == 0) {
                StdDraw.filledCircle(x, y, 4);
                newX = x;
                newY = y;

                StdDraw.setPenRadius(0.001);
                StdDraw.line(oldX, oldY, newX, newY);

                oldX = newX;
                oldY = newY;

                if (collisionOccured) break;
            }

            StdDraw.show();
        }

        // After while loop is ended inform the user with the game ending cause
        printEndString(maxWidthReached, minHeightReached, isObstacleHit, isTargetHit, height);


        // Wait for the user if she wants to start again
        while (true){
            if (StdDraw.isKeyPressed(KeyEvent.VK_R)){
                StdDraw.clear();
                return false;}
        }

    }

    /**
     * Prints the end message based on the game result.
     *
     * @param maxWidthReached    True if the maximum X-coordinate is reached.
     * @param minHeightReached   True if the bullet hits the ground.
     * @param isObstacleHit      True if the bullet hits an obstacle.
     * @param isTargetHit        True if the bullet hits a target.
     * @param height             Height of the game window.
     */
    private static void printEndString(boolean maxWidthReached, boolean minHeightReached,
                                       boolean isObstacleHit, boolean isTargetHit, double height ){

        StdDraw.setFont(new Font("Helvetica Bold", Font.BOLD, 20));
        if (maxWidthReached) {
            StdDraw.textLeft(10, height - 30, "Max X reached. Press 'r' to shoot again.");
        }
        if (minHeightReached) {
            StdDraw.textLeft(10, height - 30, "Hit the ground. Press 'r' to shoot again.");
        }
        if (isObstacleHit) {
            StdDraw.textLeft(10, height - 30, "Hit an obstacle. Press 'r' to shoot again.");
        }
        if (isTargetHit) {
            StdDraw.textLeft(10, height - 30, "Congratulations: You hit the target!");
        }
        StdDraw.show();
    }

    /**
     * Draws the bullet trajectory line.
     *
     * @param x0        Initial x-coordinate of the bullet.
     * @param y0        Initial y-coordinate of the bullet.
     * @param angle     Angle of the bullet trajectory (in degrees).
     * @param velocity  Velocity of the bullet.
     */
    private static void drawBulletLine(double x0, double y0, double angle, double velocity) {

        // A function of velocity introduced to get a more dynamic bullet line
        // Observe that this will cause some drawing problems for any velocity < 150
        double lineLength = velocity * 2 - 300;

        // Find bulletLine's other end coordinates
        double angleRadian = Math.toRadians(angle);
        double x1 = x0 + lineLength * Math.cos(angleRadian);
        double y1 = y0 + lineLength * Math.sin(angleRadian);

        // If the angle is within the valid range (O/w intersects with the shooting platform)
        if (angle <= 180 && angle >= -90) {
            // Clear the previous bullet line by redrawing its shape with a thicker pen
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setPenRadius(0.2); // Choose a thicker pen radius for better erasing
            StdDraw.line(x0, y0, x1, y1); // Draw over the previous line with the background color

            // Draw the new bullet line
            StdDraw.setPenRadius(0.01);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.line(x0, y0, x1, y1);
        }
    }

    /**
     * Checks if the bullet collides with any obstacle.
     *
     * @param x              X-coordinate of the bullet.
     * @param y              Y-coordinate of the bullet.
     * @param obstacleArray  Array containing obstacle coordinates.
     * @return True if the bullet hits an obstacle, otherwise false.
     */
    private static boolean checkObstacleCollision(double x, double y, double[][] obstacleArray) {
        for (double[] obstacle : obstacleArray) {
            double obstacleLeftX = obstacle[0], obstacleLowerY = obstacle[1];
            double obstacleWidth = obstacle[2], obstacleHeight = obstacle[3];

            // If both x and y coordinates inside the obstacles area then bullet collide with an obstacle
            if (x >= obstacleLeftX && x <= obstacleLeftX + obstacleWidth
                    && y >= obstacleLowerY && y <= obstacleLowerY + obstacleHeight) {
                return true; // Bullet hit an obstacle
            }
        }
        return false; // Bullet misses all the obstacles at this x, y coordinate

    }

    /**
     * Checks if the bullet collides with any target.
     *
     * @param x            X-coordinate of the bullet.
     * @param y            Y-coordinate of the bullet.
     * @param targetArray  Array containing target coordinates.
     * @return True if the bullet hits a target, otherwise false.
     */
    private static boolean checkTargetCollision(double x, double y, double[][] targetArray) {
        for (double[] target : targetArray) {
            double targetLeftX = target[0], targetLowerY = target[1];
            double targetWidth = target[2], targetHeight = target[3];

            // If both x and y coordinates inside the targets area then bullet collide with a target
            if (x >= targetLeftX && x <= targetLeftX + targetWidth &&
                    y >= targetLowerY && y <= targetLowerY + targetHeight) {
                return true;
            }
        }

        return false; // Bullet misses all the targets at this x, y coordinate
    }

    /**
     * Sets the canvas, and scaling.
     *
     * @param width Width of the game window.
     * @param height Height of the game window.
     */
    private static void drawBackground(int width, int height){

        // Set the window size and scaling
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

    }

    /**
     * Draws the shooting platform with angle and velocity indicators.
     *
     * @param x0        Initial x-coordinate of the bullet.
     * @param y0        Initial y-coordinate of the bullet.
     * @param angle     Angle of the bullet trajectory (in degrees).
     * @param velocity  Velocity of the bullet.
     */

    private static void drawShootingSquare(double x0, double y0, double angle, double velocity){
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(x0 / 2, y0 / 2, x0 / 2, y0/2); // Draw the shooting platform

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Helvetica Bold", Font.BOLD, 15));

        String angleStr = String.format("a: %.1f", angle);
        String velocityStr = String.format("v: %.1f", velocity);

        StdDraw.text(x0 / 2, y0 / 2, angleStr); // Angle Specifier
        StdDraw.text(x0 / 2, y0 / 2 - 20, velocityStr); // Velocity Specifier

    }

    /**
     * Draws obstacles on the game screen.
     *
     * @param obstacleCoordinates  Array containing obstacle coordinates.
     */

    private static void drawObstacles(double[][] obstacleCoordinates){
        // Check every obstacle
        for (double[] obstacleCoordinate : obstacleCoordinates) {
            double lowerLeftX = obstacleCoordinate[0];
            double lowerLeftY = obstacleCoordinate[1];
            double obstacleWidth = obstacleCoordinate[2];
            double obstacleHeight = obstacleCoordinate[3];
            StdDraw.setPenColor(StdDraw.DARK_GRAY);

            // First two are the center coordinates, last two are the half width and half height
            StdDraw.filledRectangle(lowerLeftX + obstacleWidth / 2, lowerLeftY + obstacleHeight / 2,
                    obstacleWidth / 2, obstacleHeight / 2);
        }
    }

    /**
     * Draws targets on the game screen.
     *
     * @param targetCoordinates  Array containing target coordinates.
     */
    private static void drawTargets(double[][] targetCoordinates){
        // Check every target
        for (double[] targetCoordinate : targetCoordinates) {
            double lowerLeftX = targetCoordinate[0];
            double lowerLeftY = targetCoordinate[1];
            double targetWidth = targetCoordinate[2];
            double targetHeight = targetCoordinate[3];
            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);

            // First two are the center coordinates, last to are the half width and half height
            StdDraw.filledRectangle(lowerLeftX + targetWidth / 2, lowerLeftY + targetHeight / 2,
                    targetWidth / 2, targetHeight / 2);
        }
    }

}

