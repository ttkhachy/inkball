package inkball;

import java.util.Random;
import java.util.HashMap;
import java.util.Arrays;

import processing.core.PImage;

public class Ball {
    private int xPosition;
    private int yPosition;
    private float xVelocity;
    private float yVelocity;
    private String ballType;
    private String ballColour;
    private int radius;

    Random rand = new Random();

    // can handle collisions and things

    public Ball(String ballType, int xPosition, int yPosition, String ballColour) {
        this.ballType = ballType;
        this.ballColour = ballColour;
        this.xPosition = xPosition * App.CELLSIZE;
        this.yPosition = yPosition * App.CELLSIZE + App.TOPBAR;
        this.xVelocity = (float) rand.nextInt(7) - 3; // generates number from 0-4 and then subtracts two to get to the
                                                      // range of [-2,2]
        this.yVelocity = (float) rand.nextInt(7) - 3;
        this.radius = 12; /// just gonna hard code it for now and then actually get it later
        System.out.println(ballColour);
    }

    public void draw(App app) {
        PImage ball = App.levelFileSymbolSprites.get(ballType);
        app.image(ball, xPosition + 4, yPosition + 4);
        move(); // need to fix this
    }

    public void move() {
        xPosition += xVelocity;
        yPosition += yVelocity;
    }

    public float[] getVelocityVector() {
        return new float[] { xVelocity, yVelocity };
    }

    public void handleBallVelocities(App app, Wall wall) {
        // if (collision) {
        // handle collision velocities
        // }

    }

    public boolean checkCollisionWithWall(Wall wall) { // i suppose this could become a generic? for line as well in
                                                       // future
        float nextX = xPosition + xVelocity;
        float nextY = yPosition + yVelocity;
        int[][] wallSegments = wall.getWallSegments();

        double[] distances = new double[wallSegments.length];
        String wallColour = wall.getWallColour();

        for (int i = 0; i < wallSegments.length; i++) {
            distances[i] = getDistance(wallSegments[i][0], wallSegments[i][1], nextX, nextY);
        }

        for (int i = 0; i < distances.length; i++) {
            int nextIndex = (i + 1) % distances.length; // this ensures the index wraps around to the start (for the
                                                        // last segment).
            if (isCollidingWithLineSegment(distances[i], distances[nextIndex])) {
                int[] P1 = wallSegments[i];
                int[] P2 = wallSegments[nextIndex];
                float dx = P2[0] - P1[0];
                float dy = P2[1] - P1[1];

                float[] normal1 = { -dy, dx };
                float[] normal2 = { dy, -dx };

                // step 3
                normalise(normal1);
                normalise(normal2);

                int wallMidpointX = (int) dx / 2;
                int wallMidpointY = (int) dy / 2;

                // step 4
                float[] closestNormal = getCloserNormalVector(normal1, normal2, wallMidpointX, wallMidpointY);

                applyNewVelocities(closestNormal);
                return true;

            }
        }
        return false;
    }

    public boolean isCollidingWithLineSegment(double d1, double d2) {
        return d1 + d2 < App.CELLSIZE + radius;
    }

    public boolean isCollidingWithEdge() {
        // TODO check this
        return false;
    }

    public double getDistance(int aX, int aY, double bX, double bY) {
        // bruh PApplet literally had a dist function saddd
        double d = Math.sqrt(Math.pow(bX - aX, 2) + Math.pow(bY - aY, 2));
        return d;
    }

    public void normalise(float[] vec) {
        float divisor = (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1]);

        vec[0] = vec[0] / divisor;
        vec[1] = vec[1] / divisor;
    }

    public float[] getCloserNormalVector(float[] n1, float[] n2, int wallMidpointX, int wallMidPointY) {
        double n1Distance = getDistance(wallMidPointY, wallMidPointY, n1[0], n1[1]);
        double n2Distance = getDistance(wallMidPointY, wallMidPointY, n2[0], n2[1]);
        if (n1Distance < n2Distance) {
            return n1;
        } else {
            return n2;
        }
    }

    public void applyNewVelocities(float[] norm) {
        // compute dot product v · n
        float dotProduct = (xVelocity * norm[0] + yVelocity * norm[1]);

        // Reflect velocity using the formula u = v - 2(v · n)n
        xVelocity = xVelocity - 2 * dotProduct * norm[0];
        yVelocity = yVelocity - 2 * dotProduct * norm[1];
    }

    public void collisionChangeBallColour(String wallColour) {

    }

}
