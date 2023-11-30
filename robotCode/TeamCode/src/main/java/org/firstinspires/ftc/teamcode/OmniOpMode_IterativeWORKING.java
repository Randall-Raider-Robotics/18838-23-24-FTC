package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * It has been modified to work with omni wheels.
 *
 * If you are a new programmer but understand java, take a look at the
 * example OpModes in the FtcRobotController > java > org.firstinspires.ftc.robotcontroller
 */
@SuppressWarnings("unused, SpellCheckingInspection")
@TeleOp(name = "Omni Iterative OpMode", group = "Iterative OpMode")
public class OmniOpMode_IterativeWORKING extends OpMode {

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
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");

        // Flip it if the wheel is going an unexpected direction.
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

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
        count = 0;
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        count += 1;

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
    }

    @Override
    public void stop() {
    }

}
