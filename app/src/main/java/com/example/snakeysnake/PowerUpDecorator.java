package com.example.snakeysnake;

import android.graphics.Point;

import java.util.Random;

public class PowerUpDecorator implements PowerUp {
    private WaterBottle waterBottle;
    private Point location = new Point();
    private Point mSpawnRange;

    public PowerUpDecorator(WaterBottle waterBottle) {
        this.waterBottle = waterBottle;
    }

    @Override
    public void spawn() {
        Random random = new Random();
        do {
            location.x = random.nextInt(mSpawnRange.x) + 1;
            location.y = random.nextInt(mSpawnRange.y - 1) + 1;
        } while(locationIsTooCloseToObstacle());
    }

    private boolean locationIsTooCloseToObstacle() {
        for (Point obstacleLoc : GameUI.mObstacle.getLocations()) {
            // Calculate the distance between the ball and the obstacle
            int distanceX = Math.abs(location.x - obstacleLoc.x);
            int distanceY = Math.abs(location.y - obstacleLoc.y);
            int distance = Math.max(distanceX, distanceY);

            // Check if the distance is less than a certain threshold (5 pixels)
            if (distance < 5) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Point getLocation() {
        return waterBottle.getLocation();
    }
}