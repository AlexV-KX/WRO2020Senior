import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class Robot {

    public final static Wheel wheelLeft = WheeledChassis.modelWheel(new EV3LargeRegulatedMotor(MotorPort.B), 5.6).offset(-7.8);
    public final static Wheel wheelRight = WheeledChassis.modelWheel(new EV3LargeRegulatedMotor(MotorPort.C), 5.6).offset(7.8);
    public final static MovePilot pilot = new MovePilot(new WheeledChassis(new Wheel[] { wheelRight, wheelLeft }, WheeledChassis.TYPE_DIFFERENTIAL));

    public final static EV3ColorSensor colorSLeft = new EV3ColorSensor(SensorPort.S2);
    public final static EV3ColorSensor colorSRight = new EV3ColorSensor(SensorPort.S3);

    public final static EV3ColorSensor colorSSandbags = new EV3ColorSensor(SensorPort.S1);
    public final static EV3ColorSensor colorSDike = new EV3ColorSensor(SensorPort.S4);


    /**
     * Drives the given Distance
     * @param distance The distance in cm
     * @param speed The speed the robot should drive with in cm/s
     */
    public static void travel(double distance, int speed){
        pilot.setLinearSpeed(speed);
        pilot.travel(distance);
    }

    /**
     * Drives the given Distance with optional stop
     * @param distance The distance in cm
     * @param speed The speed the robot should drive with in cm/s
     * @param stop Whether the robot should stop at the end or keep on turning
     */
    public static void travel(double distance, int speed, boolean stop){
        pilot.setLinearSpeed(speed);
        if(distance > 0)
            pilot.forward();
        else
            pilot.backward();
        distance = Math.abs(distance);
        while (pilot.getMovement().getDistanceTraveled() < distance);
        if (stop)
            pilot.stop();
    }


    /**
     * The Robot turns counterclockwise
     * @param angle The angle the robot should rotate
     * @param speed The speed at which the Robot rotates
     */
    public static void rotate(int angle, int speed) {
        pilot.setAngularSpeed(speed);
        pilot.rotate(angle);
    }

    //ToDo: Implement new type of rotation
    /**
     * The Robots turns counterclockwise and than adjusts so both sensor have the same light value
     * @param angle The angle the robot should rotate
     * @param speed The speed at which the Robot rotates
     */
    public static void rotateColor(int angle, int speed) {
        driveTillDarkest(speed/2);
        rotate(angle,speed);

        float[] vl = {10};
        float[] vr = {1};
        while (Math.abs(vl[0]-vr[0]) < 0.01) {
            colorSLeft.getRedMode().fetchSample(vl, 0);
            colorSRight.getRedMode().fetchSample(vr, 0);
            if(vl[0] > vr[0]) {
                wheelLeft.getMotor().forward();
                wheelRight.getMotor().backward();
            } else {

                wheelLeft.getMotor().backward();
                wheelRight.getMotor().forward();
            }
        }
    }


    /**
     * The Robot squares up with a line it drives towards
     * @param forward The direction in which the Robot drives
     */
    public static void align(boolean forward) {
            Sound.beep();
//        LCD.drawInt(colorSLeft.getColorID(), 0, 0);
//        findLine(forward, Color.BLACK);
//        wheelRight.getMotor().setSpeed(100);
//        wheelLeft.getMotor().setSpeed(100);
//        float[] vl = {1};
//        float[] vr = {1};
//        wheelRight.getMotor().backward();
//        wheelRight.getMotor().backward();
//        while(vl[0] >= 0.5 || vr[0] >= 0.5) {
//            colorSLeft.getRedMode().fetchSample(vl, 0);
//            colorSRight.getRedMode().fetchSample(vr, 0);
//        }
//        wheelLeft.getMotor().stop(true);
//        wheelRight.getMotor().stop(true);
//        while (Math.abs(vl[0]-vr[0]) < 0.02) {
//            colorSLeft.getRedMode().fetchSample(vl, 0);
//            colorSRight.getRedMode().fetchSample(vr, 0);
//            if(vl[0] > vr[0]) {
//                wheelLeft.getMotor().forward();
//                wheelRight.getMotor().backward();
//            } else {
//
//                wheelLeft.getMotor().backward();
//                wheelRight.getMotor().forward();
//            }
//        }
//        wheelLeft.getMotor().stop(true);
//        wheelRight.getMotor().stop(true);

//        findLine(forward, Color.BLACK);
        wheelRight.getMotor().setSpeed(300);
        wheelLeft.getMotor().setSpeed(300);
        float[] vl = {10};
        float[] vr = {1};
        if(forward) {
            wheelRight.getMotor().forward();
            wheelLeft.getMotor().forward();
        } else {
            wheelRight.getMotor().backward();
            wheelLeft.getMotor().backward();
        }
        char finishedFirst = ' ';
        while (finishedFirst == ' ') {
            if (colorSRight.getColorID() == Color.BLACK) {
                finishedFirst = 'r';
            } else if (colorSLeft.getColorID() == Color.BLACK) {
                finishedFirst = 'l';
            }
        }
        wheelRight.getMotor().resetTachoCount();
        wheelLeft.getMotor().resetTachoCount();
        wheelRight.getMotor().setSpeed(200);
        wheelLeft.getMotor().setSpeed(200);
        boolean finishedAll = false;
        while (!finishedAll) {
            if (finishedFirst == 'r' && colorSLeft.getColorID() == Color.BLACK) {
                finishedAll = true;
            } else if (finishedFirst == 'l' && colorSRight.getColorID() == Color.BLACK) {
                finishedAll = true;
            }
        }

        double distCm = wheelLeft.getMotor().getTachoCount() * 0.0488 / 3.7;
        pilot.setAngularSpeed(100);
        pilot.rotate(Math.toDegrees(Math.atan(distCm) * (finishedFirst == 'r' ? 1 : -1)), false);
       while (Math.abs(vl[0]-vr[0]) < 0.01) {
            colorSLeft.getRedMode().fetchSample(vl, 0);
            colorSRight.getRedMode().fetchSample(vr, 0);
            if(vl[0] > vr[0]) {
                wheelLeft.getMotor().forward();
                wheelRight.getMotor().backward();
            } else {

                wheelLeft.getMotor().backward();
                wheelRight.getMotor().forward();
            }
        }
    }

    /**
     * The Robot squares up with a line it drives towards
     * @param forward The direction in which the Robot drives
     * @param speed The speed at which the Robot moves
     */
    public static void align(int speed, boolean forward) {
        Sound.beep();
//        LCD.drawInt(colorSLeft.getColorID(), 0, 0);
//        findLine(forward, Color.BLACK);
//        wheelRight.getMotor().setSpeed(100);
//        wheelLeft.getMotor().setSpeed(100);
//        float[] vl = {1};
//        float[] vr = {1};
//        wheelRight.getMotor().backward();
//        wheelRight.getMotor().backward();
//        while(vl[0] >= 0.5 || vr[0] >= 0.5) {
//            colorSLeft.getRedMode().fetchSample(vl, 0);
//            colorSRight.getRedMode().fetchSample(vr, 0);
//        }
//        wheelLeft.getMotor().stop(true);
//        wheelRight.getMotor().stop(true);
//        while (Math.abs(vl[0]-vr[0]) < 0.02) {
//            colorSLeft.getRedMode().fetchSample(vl, 0);
//            colorSRight.getRedMode().fetchSample(vr, 0);
//            if(vl[0] > vr[0]) {
//                wheelLeft.getMotor().forward();
//                wheelRight.getMotor().backward();
//            } else {
//
//                wheelLeft.getMotor().backward();
//                wheelRight.getMotor().forward();
//            }
//        }
//        wheelLeft.getMotor().stop(true);
//        wheelRight.getMotor().stop(true);

//        findLine(forward, Color.BLACK);
        wheelRight.getMotor().setSpeed(speed);
        wheelLeft.getMotor().setSpeed(speed);
        float[] vl = {10};
        float[] vr = {1};
        if(forward) {
            wheelRight.getMotor().forward();
            wheelLeft.getMotor().forward();
        } else {
            wheelRight.getMotor().backward();
            wheelLeft.getMotor().backward();
        }
        char finishedFirst = ' ';
        while (finishedFirst == ' ') {
            if (colorSRight.getColorID() == Color.BLACK) {
                finishedFirst = 'r';
            } else if (colorSLeft.getColorID() == Color.BLACK) {
                finishedFirst = 'l';
            }
        }
        wheelRight.getMotor().resetTachoCount();
        wheelLeft.getMotor().resetTachoCount();
        wheelRight.getMotor().setSpeed(speed/(2*3));
        wheelLeft.getMotor().setSpeed(speed/(2*3));
        boolean finishedAll = false;
        while (!finishedAll) {
            if (finishedFirst == 'r' && colorSLeft.getColorID() == Color.BLACK) {
                finishedAll = true;
            } else if (finishedFirst == 'l' && colorSRight.getColorID() == Color.BLACK) {
                finishedAll = true;
            }
        }

        double distCm = wheelLeft.getMotor().getTachoCount() * 0.0488 / 3.7;
        pilot.setAngularSpeed(speed/3);
        pilot.rotate(Math.toDegrees(Math.atan(distCm) * (finishedFirst == 'r' ? 1 : -1)), false);
        while (Math.abs(vl[0]-vr[0]) < 0.01) {
            colorSLeft.getRedMode().fetchSample(vl, 0);
            colorSRight.getRedMode().fetchSample(vr, 0);
            if(vl[0] > vr[0]) {
                wheelLeft.getMotor().forward();
                wheelRight.getMotor().backward();
            } else {

                wheelLeft.getMotor().backward();
                wheelRight.getMotor().forward();
            }
        }
    }

    /**
     * The robot drives backwards against a wall to stand straight
     * @param speed The speed at which the Robot moves
     */
    public static void alignWall(int speed) {
        pilot.setLinearSpeed(speed);
        pilot.backward();
        float prevDis = 0;
        Delay.msDelay(100);
        while (pilot.getMovement().getDistanceTraveled() - prevDis > 1) {
            prevDis = pilot.getMovement().getDistanceTraveled();
            Delay.msDelay(10);
        }
        pilot.stop();
    }


    /**
     * The Robot follows a line of any color
     * @param distance The distance it should drive along the line in cm
     * @param forward Direction the Robot should drive
     */
    public static void lineFollowing(double distance, boolean forward) {
        float[] vl = {1};
        float[] vr = {1};

        wheelLeft.getMotor().resetTachoCount();
        wheelRight.getMotor().resetTachoCount();
        if(forward) {
            wheelLeft.getMotor().forward();
            wheelRight.getMotor().forward();
        } else {
            wheelLeft.getMotor().backward();
            wheelRight.getMotor().backward();
        }
        while (Math.abs(0.0488 * wheelLeft.getMotor().getTachoCount()) < distance) {
            LCD.drawString(wheelLeft.getMotor().getTachoCount()+"", 2, 2);
            colorSLeft.getRedMode().fetchSample(vl, 0);
            colorSRight.getRedMode().fetchSample(vr, 0);

            float diff = vl[0] - vr[0];

            wheelLeft.getMotor().setSpeed(Math.round(300 + diff * 100));
            wheelRight.getMotor().setSpeed(Math.round(300 - diff * 100));
        }
        wheelLeft.getMotor().stop(true);
        wheelRight.getMotor().stop(true);

        Delay.msDelay(5000);
    }

    /**
     * The Robot follows a line of any color with speed parameter
     * @param distance The distance it should drive along the line in cm
     * @param speed The speed the Robot should have
     * @param forward Direction the Robot should drive
     */
    public static void lineFollowing(double distance, int speed, boolean forward) {
        float[] vl = {1};
        float[] vr = {1};

        wheelLeft.getMotor().resetTachoCount();
        wheelRight.getMotor().resetTachoCount();
        if(forward) {
            wheelLeft.getMotor().forward();
            wheelRight.getMotor().forward();
        } else {
            wheelLeft.getMotor().backward();
            wheelRight.getMotor().backward();
        }
        while (Math.abs(0.0488 * wheelLeft.getMotor().getTachoCount()) < distance) {
            LCD.drawString(wheelLeft.getMotor().getTachoCount()+"", 2, 2);
            colorSLeft.getRedMode().fetchSample(vl, 0);
            colorSRight.getRedMode().fetchSample(vr, 0);

            float diff = vl[0] - vr[0];

            wheelLeft.getMotor().setSpeed(Math.round(speed + diff * speed/3));
            wheelRight.getMotor().setSpeed(Math.round(speed - diff * speed/3));
        }
        wheelLeft.getMotor().stop(true);
        wheelRight.getMotor().stop(true);

        Delay.msDelay(5000);
    }


    /**
     * Drives until both bottom sensors see a different color than green or white
     * @param forward The direction it should drive
     */
    public static void driveTillHouseConnectionLine(boolean forward) {
        wheelLeft.getMotor().setSpeed(500);
        wheelRight.getMotor().setSpeed(500);
        if(forward) {
            wheelLeft.getMotor().forward();
            wheelRight.getMotor().forward();
        }else {
            wheelLeft.getMotor().backward();
            wheelRight.getMotor().backward();
        }

        boolean alignedLeft = false;
        boolean alignedRight = false;

        while (!(alignedLeft && alignedRight)) {
            int cL = colorSLeft.getColorID();
            if (cL != Color.GREEN && cL != Color.WHITE) {
                wheelLeft.getMotor().stop(true);
                alignedLeft = true;
            }
            int cR = colorSRight.getColorID();
            if (cR != Color.GREEN && cR != Color.WHITE) {
                wheelRight.getMotor().stop(true);
                alignedRight = true;
            }
        }
    }

    /**
     * Drives until both bottom sensors see a different color than green or white with speed parameter
     * @param speed The speed the Robot should drive with
     * @param forward The direction it should drive
     */
    public static void driveTillHouseConnectionLine(int speed, boolean forward) {
        wheelLeft.getMotor().setSpeed(speed);
        wheelRight.getMotor().setSpeed(speed);
        if (forward) {
            wheelLeft.getMotor().forward();
            wheelRight.getMotor().forward();
        }else {
            wheelLeft.getMotor().backward();
            wheelRight.getMotor().backward();
        }

        boolean alignedLeft = false;
        boolean alignedRight = false;

        while (!(alignedLeft && alignedRight)) {
            int cL = colorSLeft.getColorID();
            if (cL != Color.GREEN && cL != Color.WHITE) {
                wheelLeft.getMotor().stop(true);
                alignedLeft = true;
            }
            int cR = colorSRight.getColorID();
            if (cR != Color.GREEN && cR != Color.WHITE) {
                wheelRight.getMotor().stop(true);
                alignedRight = true;
            }
        }
    }

    //TODO: Test this
    /**
     * Drives until the sensor reaches its darkest point (center of the line)
     */
    public static void driveTillDarkest() {
        pilot.setLinearSpeed(100);
        float[] vl = {1};
        colorSLeft.getRedMode().fetchSample(vl, 0);
        pilot.forward();
        Delay.msDelay(50);
        float prev = vl[0];
        colorSLeft.getRedMode().fetchSample(vl, 0);
        if(prev > vl[0]) {
            while(prev > vl[0]);
            pilot.stop();
        } else {
            pilot.backward();
            while(prev < vl[0]);
            pilot.stop();
        }
    }

    /**
     * Drives until the sensor reaches its darkest point (center of the line) with speed parameter
     * @param speed The speed at which the Robot moves
     */
    public static void driveTillDarkest(int speed) {
        pilot.setLinearSpeed(speed);
        float[] vl = {1};
        colorSLeft.getRedMode().fetchSample(vl, 0);
        pilot.forward();
        Delay.msDelay(50);
        float prev = vl[0];
        colorSLeft.getRedMode().fetchSample(vl, 0);
        if(prev > vl[0]) {
            while(prev > vl[0]);
            pilot.stop();
        } else {
            pilot.backward();
            while(prev < vl[0]);
            pilot.stop();
        }
    }
}
