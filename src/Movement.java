import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.Sound;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.BaseSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.filter.OffsetCorrectionFilter;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;


public class Movement {
    private MovePilot pilot;

    RegulatedMotor left, right;
    private EV3ColorSensor cLeft, cRight;

    private final float DIAMETER = 5.6f;

    private float kp = 1, ki = 1, kd = 1;

    private float targetValue = 35;
    private float integral, lastError;

    private final float OFFSET = 7.8f;

    public Movement (RegulatedMotor l, RegulatedMotor r, Port sl, Port sr) {
        left = l;
        right = r;
        cLeft = new EV3ColorSensor(sl);
        cRight = new EV3ColorSensor(sr);
        Wheel wL = WheeledChassis.modelWheel(l,5.6).offset(-OFFSET);
        Wheel wR = WheeledChassis.modelWheel(r, 5.6).offset(OFFSET);
        Chassis c = new WheeledChassis(new Wheel[]{wL, wR},WheeledChassis.TYPE_DIFFERENTIAL);
        pilot = new MovePilot(c);
    }

   /* public void driveCM(float cm, int speed) {
        int tacho = 0;
        left.setSpeed(speed);
        right.setSpeed(speed * OFFSET);

        left.resetTachoCount();
        right.resetTachoCount();

//        left.rotateTo((int)(cm / (3.142f * DIAMETER) * 360));
//        right.rotateTo((int)(cm / (3.142f * DIAMETER) * 360));

        left.forward();
        right.forward();

        float dist = 0;
        while (dist < Math.abs(cm)) {
            dist = DIAMETER * Math.abs(left.getTachoCount()) * 3.1415f / 360;
            LCD.drawString(left.getTachoCount() + "", 2, 2);
//            if (dist > Math.abs(cm)-5) {
//                left.setSpeed(left.getSpeed() * (Math.abs(cm) - dist) / 10 + 0.5f);
//                right.setSpeed(right.getSpeed() * (Math.abs(cm) - dist) / 10 + 0.5f);
//            }
        }


        left.stop(true);
        right.stop();
    } */

   public void drivecm (double cm, int speed) {
       pilot.setAngularSpeed(speed);
       pilot.travel(cm);
   }

    public void turnDegrees(double angle, int speed) {
        pilot.setAngularSpeed(speed);
        pilot.rotate(angle);
    }
    public void align(int colA, int colB, int speed) {
        int finished = 0;
        pilot.setLinearSpeed(speed);
        pilot.forward();
        int cL = -1;
        int cR = -1;
        cRight.setCurrentMode("RGB");
        cLeft.setCurrentMode("RGB");
        while (finished != 2) {
            cL = cLeft.getColorID();
            cR = cRight.getColorID();

            if (cL== 7 || cR == 7) {
                LCD.drawString("STOP!", 3, 3);
                left.stop(true);
                right.stop(true);
                Delay.msDelay(1000);
//                left.setSpeed(0);
//                right.setSpeed(0);
//                left.setAcceleration(0);
//                right.setAcceleration(0);
                finished = 2;

            }
        }
    }
//    public void pid (boolean left) {
//        float[] value = new float[1];
//        (left ? cleft:cright).getredmode().fetchsample(value, 0);
//        //lcd.drawstring(arr[0]+"", 2, 2);
//        float error = targetvalue - value[0];
//        integral += error;
//        float derivative = error - lasterror;
//
//        motor.a.setspeed(50);
//        motor.b.setspeed(50);
//
//        motor.a.forward();
//        motor.b.forward();
//    }

    public void test() {
       pilot.forward();
       Delay.msDelay(1000);
       LCD.drawString("STOP!", 2, 2);
       left.stop(true);
       right.stop(true);
       Delay.msDelay(1000);
    }
}
