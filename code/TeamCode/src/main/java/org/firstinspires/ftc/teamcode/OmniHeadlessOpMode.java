package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

// TODO: Test this OpMode

/*
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * It has been modified to work with omni wheels.
 *
 * If you are a new programmer but understand java, take a look at the
 * example OpModes in the FtcRobotController > java > org.firstinspires.ftc.robotcontroller
 */
@SuppressWarnings("unused")
@TeleOp(name = "Omni Iterative OpMode", group = "Iterative OpMode")
public class OmniHeadlessOpMode extends OpMode {
    private final boolean RECORD = false; // DO NOT RECORD AT COMPETITION YOU WILL BE DISQUALIFIED!

    private final ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;

    public double prevLeftStickX = 0.00;
    public double prevLeftStickY = 0.00;
    public double prevRightStickX = 0.00;

    public double leftStickX = 0.00;
    public double leftStickY = 0.00;
    public double rightStickX = 0.00;

    public int count = 0;

    @Override
    public void init() {
        // Set motors to hardware map
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");

        // Set motor directions
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        // Initialize IMU
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(parameters);
        double imuYaw = 0.00;
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {
    }

    // Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {
        runtime.reset();
        count = 0;
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        imuYaw = imu.getRotation();

        // Setup a variable for each drive wheel to save power level for telemetry
        double leftFrontPower;
        double rightFrontPower;
        double leftBackPower;
        double rightBackPower;

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.

        leftStickX = -gamepad1.left_stick_x;
        leftStickY = gamepad1.left_stick_y;
        rightStickX = -gamepad1.right_stick_x;

        double axial = leftStickY;
        double lateral = leftStickX;
        double yaw = rightStickX;

        // Cut power in half if stick change is greater than 0.2,
        // reduces drifting. (Doesn't work yet. Probably divide power instead of
        // yaw/axial/lateral
        // if (Math.abs(leftStickX - prevLeftStickX) > 0.2) {
        // lateral = lateral / 2;
        // }

        leftFrontPower = axial + lateral + yaw;
        rightFrontPower = axial - lateral - yaw;
        leftBackPower = axial - lateral + yaw;
        rightBackPower = axial + lateral - yaw;

        // Send calculated power to wheels
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime);
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.update();

        prevLeftStickX = gamepad1.left_stick_x;
        prevLeftStickY = gamepad1.left_stick_y;
        prevRightStickX = gamepad1.right_stick_x;

        /*
         * Using the imu rotation, below the codes will display whether or not the robot is
         * moving as it should, forward will always be the direction the use is playing,
         * and the direction the robot starts in will always match that direction.
         * 
         * When the robot is matching the movement it needs to EX: while it's facing left it's
         * still going right since that's the direction it that appears to be forward to the drive,
         * it will display "Correct headless code" and when it's not it will display "Incorrect headless code"
         * 
         * Once this code is uneeded, it will be commented out, but kept in case needed later
         */
        if (imu.getRotation() >= 0 && imu.getRotation() <= 90) {
            // When it's movement direction is forward (from the user) no matter what the user is facing say "Correct headless code"
            
            // Only test if it's using the headless code and not just facing forward
            if(imuYaw != 0.00) {
                telemetry.addData("Using headless code", " ");
                //if velocity is forward while the left stick is pushed forward say "corred headless code"
                if (leftStickY > 0 && leftFrontPower > 0 && rightFrontPower > 0 && leftBackPower > 0 && rightBackPower > 0) {
                    telemetry.addData("Correct headless code", " ");
                }
                else {
                    telemetry.addData("Incorrect headless code", " ");
                }
            }
            else {
                telemetry.addData("Not using headless code", " ");
            }
        }
        

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
