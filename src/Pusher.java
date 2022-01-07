import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class Pusher {

    public final static EV3MediumRegulatedMotor pusher = new EV3MediumRegulatedMotor(MotorPort.D);
    public static int state = 0;
    public final static int[] states = {0, -1900, -3500};
    public static int speed = (int)pusher.getMaxSpeed();


    /**
     * Moves the Pusher to a given state
     * @param toState The state the pusher should move to
     */
    public static void rotateTo(int toState) {
        pusher.setSpeed(speed);
        pusher.rotateTo(states[toState]);

        state = toState;
    }

    /**
     * Moves the Pusher to a given state
     * @param toState The state the pusher should move to
     * @param immediateReturn Whether the function should instantly return
     */
    public static void rotateTo(int toState, boolean immediateReturn) {
        pusher.setSpeed(speed);
        pusher.rotateTo(states[toState], immediateReturn);

        state = toState;
    }


    /**
     * Extends the Pusher by a given Number of states
     * @param ex The amount of states the Pusher should extend
     */
    public static void extend(int ex) {
        if(ex < 0) return;
        rotateTo(Math.min(state + ex, states.length - 1));
    }

    /**
     * Extends the Pusher by a given Number of states
     * @param ex The amount of states the Pusher should extend
     * @param immediateReturn Whether the function should instantly return
     */
    public static void extend(int ex, boolean immediateReturn) {
        if(ex < 0) return;
        rotateTo(Math.max(state + ex, states.length - 1), immediateReturn);
    }

    /**
     * Retracts the Pusher by a given Number of states
     * @param re The amount of states the Pusher should extend
     */
    public static void retract(int re) {
        if(re < 0) return;
        rotateTo(Math.max(state - re, 0));
    }

    /**
     * Retracts the Pusher by a given Number of states
     * @param re The amount of states the Pusher should extend
     * @param immediateReturn Whether the function should instantly return
     */
    public static void retract(int re, boolean immediateReturn) {
        if(re < 0) return;
        rotateTo(Math.max(state - re, 0), immediateReturn);
    }


    /**
     * Tests if the Pusher is moving
     * @return Whether the Pusher moves
     */
    public static boolean isMoving() {
        return pusher.isMoving();
    }

    /**
     * Sets the Pusher speed
     * @param newSpeed The new speed value
     */
    public static void setSpeed(int newSpeed) {
        pusher.setSpeed(speed = newSpeed);
    }

    /**
     * Sets the pusher to maximum speed
     */
    public static void setMaxSpeed() {
        pusher.setSpeed(speed = (int) pusher.getMaxSpeed());
    }
}
