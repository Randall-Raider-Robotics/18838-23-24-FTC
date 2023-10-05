package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;


/*
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * It has been modified to work with omni wheels.
 *
 * If you are a new programmer but understand java, take a look at the
 * example OpModes in the FtcRobotController > java > org.firstinspires.ftc.robotcontroller
 */

@TeleOp(name = "Headless OpMode", group = "Iterative OpMode")
public class ExperimentalOmniHeadless_Iterative extends OpMode {

    private final ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;

    // IMU Variables
    static RevHubOrientationOnRobot.LogoFacingDirection[] logoFacingDirections
            = RevHubOrientationOnRobot.LogoFacingDirection.values();
    static RevHubOrientationOnRobot.UsbFacingDirection[] usbFacingDirections
            = RevHubOrientationOnRobot.UsbFacingDirection.values();
    static int LAST_DIRECTION = logoFacingDirections.length - 1;
    static float TRIGGER_THRESHOLD = 0.2f;
    double imuYaw_initial = 0.0;

    IMU imu;
    int logoFacingDirectionPosition;
    int usbFacingDirectionPosition;
    boolean orientationIsValid = true;

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");


        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");

        // Flip it if the wheel is going an unexpected direction.
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        // Start IMU
        imu = hardwareMap.get(IMU.class, "imu");
        logoFacingDirectionPosition = 2; // Forward
        usbFacingDirectionPosition = 0; // Up

        updateOrientation();

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {
    }

    // Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {
        runtime.reset();
        imuYaw_initial = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        double leftFrontPower;
        double rightFrontPower;
        double leftBackPower;
        double rightBackPower;

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        double forwardBackward = gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double yaw = -gamepad1.right_stick_x;

        double imuYaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        double difference;

        leftFrontPower = forwardBackward + lateral + yaw;
        rightFrontPower = forwardBackward - lateral - yaw;
        leftBackPower = forwardBackward - lateral + yaw;
        rightBackPower = forwardBackward + lateral - yaw;

        if(Math.abs(lateral)>0.1 || Math.abs(yaw)>0.1){
            difference = Math.abs(imuYaw - imuYaw_initial);
        }
        else {
            difference = 0;
        }

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime);
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);

        if (gamepad1.y) {
            telemetry.addData("Yaw", "Resetting\n");
            imu.resetYaw();
        } else {
            telemetry.addData("Yaw", "Press Y (triangle) on Gamepad to reset.\n");
        }

        // Check to see if new Logo Direction is requested
        if (gamepad1.left_bumper || gamepad1.right_bumper) {
            if (gamepad1.left_bumper) {
                logoFacingDirectionPosition--;
                if (logoFacingDirectionPosition < 0) {
                    logoFacingDirectionPosition = LAST_DIRECTION;
                }
            } else {
                logoFacingDirectionPosition++;
                if (logoFacingDirectionPosition > LAST_DIRECTION) {
                    logoFacingDirectionPosition = 0;
                }
            }
            updateOrientation();
        }

        // Check to see if new USB Direction is requested
        if (gamepad1.left_trigger > TRIGGER_THRESHOLD || gamepad1.right_trigger > TRIGGER_THRESHOLD) {
            if (gamepad1.left_trigger > TRIGGER_THRESHOLD) {
                usbFacingDirectionPosition--;
                if (usbFacingDirectionPosition < 0) {
                    usbFacingDirectionPosition = LAST_DIRECTION;
                }
            } else {
                usbFacingDirectionPosition++;
                if (usbFacingDirectionPosition > LAST_DIRECTION) {
                    usbFacingDirectionPosition = 0;
                }
            }
            updateOrientation();
        }

        // Display User instructions and IMU data
        telemetry.addData("logo Direction (set with bumpers)", logoFacingDirections[logoFacingDirectionPosition]);
        telemetry.addData("usb Direction (set with triggers)", usbFacingDirections[usbFacingDirectionPosition] + "\n");


        if (orientationIsValid) {
            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
            AngularVelocity angularVelocity = imu.getRobotAngularVelocity(AngleUnit.DEGREES);

            telemetry.addData("Yaw (Z)", "%.2f Deg. (Heading)", imuYaw);
            telemetry.addData("Yaw (Z)", "%.2f Deg. (Heading)", imuYaw);
            telemetry.addData("Pitch (X)", "%.2f Deg.", orientation.getPitch(AngleUnit.DEGREES));
            telemetry.addData("Roll (Y)", "%.2f Deg.\n", orientation.getRoll(AngleUnit.DEGREES));
            telemetry.addData("Yaw (Z) velocity", "%.2f Deg/Sec", angularVelocity.zRotationRate);
            telemetry.addData("Pitch (X) velocity", "%.2f Deg/Sec", angularVelocity.xRotationRate);
            telemetry.addData("Roll (Y) velocity", "%.2f Deg/Sec", angularVelocity.yRotationRate);
        } else {
            telemetry.addData("Error", "Selected orientation on robot is invalid");
        }

        // Send calculated power to wheels
        leftFrontDrive.setPower(leftFrontPower+difference);
        rightFrontDrive.setPower(rightFrontPower+difference);
        leftBackDrive.setPower(leftBackPower+difference);
        rightBackDrive.setPower(rightBackPower+difference);

        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

    void updateOrientation() {
        RevHubOrientationOnRobot.LogoFacingDirection logo = logoFacingDirections[logoFacingDirectionPosition];
        RevHubOrientationOnRobot.UsbFacingDirection usb = usbFacingDirections[usbFacingDirectionPosition];
        try {
            RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logo, usb);
            imu.initialize(new IMU.Parameters(orientationOnRobot));
            orientationIsValid = true;
        } catch (IllegalArgumentException e) {
            orientationIsValid = false;
        }
    }

}
