import lejos.hardware.Brick;
import lejos.hardware.Sound;
import lejos.robotics.Color;
import lejos.utility.Delay;

public class Actions {

    /**
     * Builds the structure of Dike Pieces.
     * Needs to start at the line in parallel to the house facing the house.
     * Finishes at the line connecting the Dike Pieces.
     * Lift is in state UP at the end.
     * @return Returns the ColorID of the Sandbags.
     */
    public static int collectSandbags() {
        alignCollectionArea(true);
        Lift.rotateTo(LiftStates.DOWN);
        Robot.travel(8,200);
        Lift.rotateTo(LiftStates.UP);
        int color = Robot.colorSSandbags.getColorID();
        Robot.travel(-5,500);
        Robot.align(false);
        return color;
    }

    /**
     * Collects Dike pieces
     * Needs to start at the line connecting the Dike Pieces.
     * Finishes in same position as started.
     * Lift is in state DOWN_CLOSED at the end.
     * @return Returns the ColorID of the Dike Pieces.
     */
    public static int collectDikePieces() {
        int pieceColor = Color.NONE;
        int pieceCount = 0;
        final int SPEED = 250;
        final int ROTSPEED = 100;

        Lift.rotateTo(LiftStates.UP);

        Robot.travel(-1.75, SPEED);
        //Collect 1-2 Dike Pieces on right side
        Robot.rotate(90,ROTSPEED);
        Delay.msDelay(1000);
        Robot.lineFollowing(5.5,SPEED, false);
        if(Robot.colorSDike.getColorID() != Color.NONE) {
            pieceColor = Robot.colorSDike.getColorID();
            pieceCount++;
            Sound.beep();
        }

        Robot.lineFollowing(7,SPEED, false);
        if(Robot.colorSDike.getColorID() != Color.NONE) {
            pieceColor = Robot.colorSDike.getColorID();
            pieceCount++;
            Sound.beep();
        }
        Lift.rotateTo(LiftStates.DOWN);
        Robot.rotate(180, ROTSPEED);
        if(pieceCount != 2)
            Lift.rotateTo(LiftStates.UP);
        Robot.lineFollowing(15, false);
        Delay.msDelay(2000);

        //If 2 Pieces were collected, the robot drives through because their can only be one
        if (pieceCount != 2) {
            Lift.rotateTo(LiftStates.DOWN);
        }
        Robot.lineFollowing(7,SPEED, false);
        Lift.rotateTo(LiftStates.DOWN_CLOSED);
        Robot.travel(14, 400);
        Robot.rotate(90, ROTSPEED);
        Robot.align(true);

        return pieceColor;
    }

    /**
     * Collects both Sandbags and Dike Pieces
     * @return First Array value is Sandbag, Second Array value is Dike Piece
     */
    public static int[] collectAll() {
        int[] colors = new int[2];
        colors[0] = collectSandbags();
        colors[1] = collectDikePieces();
        return colors;
    }

    //ToDO: Implement Shift to side, so Sensor can see the Dike Status Card
    /**
     * Scans the Dike Status Card.
     * Needs to start aligned with the line parallel to the house facing towards the house.
     * Ends on starting position
     * @return Returns the ColorID of the Dike status Card
     */
    public static int scanDikeColor() {
        alignCollectionArea(false);
        Robot.alignWall(300);
        Robot.travel(1,200);
        int color = Robot.colorSDike.getColorID();
        Robot.travel(10,500);
        Robot.align(true);
        Robot.lineFollowing(15,true);
        Robot.rotate(-90,300);
        Robot.align(false);

        return color;
    }


    /**
     * Places the Sandbags in front of the house
     * Needs to start at the line parallel to the house facing the house.
     * Ends in the same position as started.
     */
    public static void deliverSandbags() {
        int ANGLE = 15;
        Lift.rotateTo(LiftStates.DOWN);
        Robot.travel(2.5,400);
        Robot.rotate(ANGLE,200);
        Pusher.extend(1);
        Robot.rotate(-2*ANGLE,200);
        Pusher.extend(2);
        Robot.rotate(ANGLE,200);
        Pusher.rotateTo(PusherStates.NOT,true);
        Robot.align(false);
        while (Pusher.isMoving());
        Lift.rotateTo(LiftStates.UP);
    }

    //ToDo: Build Dike function
    /**
     * Builds the structure of Dike Pieces.
     * Needs to start at the line parallel to the house facing the house.
     * Ends in the same position as started
     */
    public static void deliverDikePieces() {
        alignCollectionArea(false);

        //Build Dike

        Robot.align(false);
        Robot.lineFollowing(15,false);
        Robot.rotate(90,300);
        Robot.align(false);
    }

    /**
     * Places both Dike Pieces and Sandbags.
     * Dike Pieces get placed first.
     */
    public static void deliverAll() {
        deliverDikePieces();
        deliverSandbags();
    }


    /**
     * Travels from one Field Area to another.
     * Needs to start at the line parallel to the house facing the house.
     * Ends on the line parallel to the house facing the house.
     * @param startArea Area the robot starts in
     * @param goalArea Area the robot tries to reach
     * @return Returns parameter goalArea
     */
    public static char driveToArea(char startArea, char goalArea) {
        startArea = Character.toUpperCase(startArea);
        goalArea = Character.toUpperCase(goalArea);
        if(startArea == goalArea) return goalArea;

        if(startArea == 'A' && goalArea == 'B' || startArea == 'B' && goalArea == 'A' ||
           startArea == 'C' && goalArea == 'D' || startArea == 'D' && goalArea == 'C') {
            Robot.rotate(180, 300);
            Robot.lineFollowing(40, true);
            Robot.align(true);
        } else {
            Robot.lineFollowing(20, false);
            Robot.rotate(90, 300);
            //Guessed the distances
            Robot.travel(150, 1000);
            Robot.align(true);
            Robot.driveTillHouseConnectionLine(true);
            Robot.rotate((goalArea == 'A' || goalArea == 'D') ? 90:-90,300);
            Robot.lineFollowing(20, true);
        }
        Robot.align(true);
        return goalArea;
    }

    /**
     * Needs to start aligned with the line parallel to the house facing towards the house.
     * Ends standing on the line connecting the Dike Pieces, facing the End Zone.
     * @param endForward gives the orientation it faces at the end
     */
    private static void alignCollectionArea(boolean endForward) {
        Robot.rotate((endForward ? -90 : 90),300);
        Robot.lineFollowing(15,endForward);
        Robot.align(endForward);
    }

}