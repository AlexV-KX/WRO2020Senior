import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.Color;
import lejos.utility.Delay;

public class RunWRO {

    //ToDo: Test Wall Alignment
    //ToDo: Test rotateColor
    //ToDo: Test Action Parts and find correct values

    static final char greenHouse = Math.random() > .5 ? 'A': 'B';
    static final char blueHouse = Math.random() > .5 ? 'A': 'B';
    static final int startPosition = Math.random() > .5 ? 5: 6; //either 5 or 6
    static int loadedSandbagColor = Color.NONE;
    static int loadedDikePieceColor = Color.NONE;
    static int greenHouseDikeColor = Color.NONE;
    static int blueHouseDikeColor = Color.NONE;
    static char currentArea = 'E';

    public static void main(String[] args) {
        reset();
        Delay.msDelay(2500);
        //Actions.deliverSandbags();
        Actions.collectDikePieces();
   }

    private static void reset() {
        Pusher.setMaxSpeed();
        Lift.setMaxSpeed();
        while (!Button.ENTER.isDown()) {
            LCD.drawString("Lift" + Lift.lift.getTachoCount(), 1, 1);

            if (Button.RIGHT.isDown()) {
                Pusher.pusher.forward();
            } else if (Button.LEFT.isDown()) {
                Pusher.pusher.backward();
            } else {
                Pusher.pusher.stop(true);
            }
            if (Button.UP.isDown()) {
                Lift.lift.forward();
            } else if (Button.DOWN.isDown()) {
                Lift.lift.backward();
            } else {
                Lift.lift.stop(true);
            }
        }

        Pusher.pusher.resetTachoCount();
        Lift.lift.resetTachoCount();
    }

    private static void deliverEvacuations() {
        //Drive to first house and deliver first Evacuation
        Robot.align(false);
        Robot.driveTillHouseConnectionLine(true);
        Robot.rotate(startPosition == 5 && (greenHouse == 'A' || blueHouse == 'A') ||
                startPosition == 6 && (greenHouse == 'D' || blueHouse == 'D') ? 90 : -90, 300);
        Robot.lineFollowing(20, true);
        Robot.align(true);
        Robot.travel(3, 300);
        Robot.align(false);

        //Calculate at which house robot is standing
        char currentHouseColor = 'E';
        if (startPosition == 5) {
            if(greenHouse == 'A') {
                currentArea = 'A';
                currentHouseColor = 'G';
            } else if(blueHouse == 'A') {
                currentArea = 'A';
                currentHouseColor = 'B';
            } else if(greenHouse == 'B') {
                currentArea = 'B';
                currentHouseColor = 'G';
            } else if(blueHouse == 'B') {
                currentArea = 'B';
                currentHouseColor = 'B';
            }
        } else {
            if(greenHouse == 'D') {
                currentArea = 'D';
                currentHouseColor = 'G';
            } else if(blueHouse == 'D') {
                currentArea = 'D';
                currentHouseColor = 'B';
            } else if(greenHouse == 'B') {
                currentArea = 'C';
                currentHouseColor = 'G';
            } else if(blueHouse == 'C') {
                currentArea = 'C';
                currentHouseColor = 'B';
            }
        }

        //Scan Dike Status Card and drive to Area of second house
        int color = Actions.scanDikeColor();
        if(currentHouseColor == 'G') {
            greenHouseDikeColor = color;
            blueHouseDikeColor = getOppositeColor(color);
            currentArea = Actions.driveToArea(currentArea, blueHouse);
        } else {
            blueHouseDikeColor = color;
            greenHouseDikeColor = getOppositeColor(color);
            currentArea = Actions.driveToArea(currentArea, greenHouse);
        }

        //Deliver second Evacuation
        Lift.rotateTo(LiftStates.DOWN);
    }

    private static void startWait() {
        LCD.clear();
        LCD.drawString("Ready to start \n Waiting for \n Button press", 2, 2);
        Button.ENTER.waitForPress();
        LCD.clear();
        LCD.drawString("Driving", 2, 2);
        Sound.beep();
    }

    private static int getOppositeColor(int color) {
        return color == Color.RED ? Color.YELLOW:Color.RED;
    }

}