import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class Lift {

    public final static EV3MediumRegulatedMotor lift = new EV3MediumRegulatedMotor(MotorPort.A);
    public static int state = 0;
    public final static int[] states = {-4100, -3100, -2945, -1200};
    public static int speed = (int)lift.getMaxSpeed();


    /**
     * Moves the Lift to a given state
     * @param toState The state the Lift should move to
     */
    public static void rotateTo(int toState) {
        lift.setSpeed(speed);
        lift.rotateTo(states[toState], true);
        while(!lift.isStalled() && lift.isMoving());
        lift.stop();

        state = toState;
    }

    /**
     * Moves the Lift to a given state
     * @param toState The state the Lift should move to
     * @param immediateReturn Whether the function should instantly return
     */
//    public static void rotateTo(int toState, boolean immediateReturn) {
//        lift.setSpeed(speed);
//        lift.rotateTo(states[toState], immediateReturn);
//
//        state = toState;
//    }


    /**
     * Lowers the Lift by a given Number of states
     * @param ex The amount of states the Lift should extend
     */
    public static void lower(int ex) {
        if(ex < 0) return;
        rotateTo(Math.min(state + ex, states.length - 1));
    }

    /**
     * Lowers the Lift by a given Number of states
     * @param ex The amount of states the Lift should extend
     * @param immediateReturn Whether the function should instantly return
     */
//    public static void lower(int ex, boolean immediateReturn) {
//        if(ex < 0) return;
//        rotateTo(Math.min(state + ex, states.length - 1), immediateReturn);
//    }

    /**
     * Raises the Lift by a given Number of states
     * @param re The amount of states the Lift should extend
     */
    public static void raise(int re) {
        if(re < 0) return;
        rotateTo(Math.max(state - re, 0));
    }

    /**
     * Raises the Lift by a given Number of states
     * @param re The amount of states the Lift should extend
     * @param immediateReturn Whether the function should instantly return
     */
//    public static void raise(int re, boolean immediateReturn) {
//        if(re < 0) return;
//        rotateTo(Math.max(state - re, 0), immediateReturn);
//    }


    /**
     * Tests if the Lift is moving
     * @return Whether the Lift moves
     */
    public static boolean isMoving() {
        return lift.isMoving();
    }

    /**
     * Sets the Lift speed
     * @param newSpeed The new speed value
     */
    public static void setSpeed(int newSpeed) {
        lift.setSpeed(speed = newSpeed);
    }

    /**
     * Sets the Lift to maximum speed
     */
    public static void setMaxSpeed() {
        lift.setSpeed(speed = (int) lift.getMaxSpeed());
    }
}
