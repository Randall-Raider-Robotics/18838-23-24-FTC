import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

/*
 * This file tests if controller input is properly translated to motor power.
 */

public class OmniHeadlessOpModeTest {

    @Mock
    private DcMotor leftFrontDrive;
    @Mock
    private DcMotor rightFrontDrive;
    @Mock
    private DcMotor leftBackDrive;
    @Mock
    private DcMotor rightBackDrive;
    @Mock
    private Gamepad gamepad1;
    @Mock
    private Telemetry telemetry;

    private OmniHeadlessOpMode opMode;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        opMode = new OmniHeadlessOpMode();
        opMode.leftFrontDrive = leftFrontDrive;
        opMode.rightFrontDrive = rightFrontDrive;
        opMode.leftBackDrive = leftBackDrive;
        opMode.rightBackDrive = rightBackDrive;
        opMode.gamepad1 = gamepad1;
        opMode.telemetry = telemetry;
    }

    @Test
    public void testLoop() {
        when(gamepad1.left_stick_x).thenReturn(0.0);
        when(gamepad1.left_stick_y).thenReturn(0.0);
        when(gamepad1.right_stick_x).thenReturn(0.0);

        opMode.loop();

        verify(leftFrontDrive).setPower(0.0);
        verify(rightFrontDrive).setPower(0.0);
        verify(leftBackDrive).setPower(0.0);
        verify(rightBackDrive).setPower(0.0);
        verify(telemetry).addData("Status", "Run Time: " + opMode.getRuntime());
        verify(telemetry).addData("Front left/Right", "%4.2f, %4.2f", 0.0, 0.0);
        verify(telemetry).addData("Back  left/Right", "%4.2f, %4.2f", 0.0, 0.0);
        verify(telemetry).update();
    }

    @Test
    public void testLoopWithJoystickInput() {
        when(gamepad1.left_stick_x).thenReturn(0.5);
        when(gamepad1.left_stick_y).thenReturn(0.5);
        when(gamepad1.right_stick_x).thenReturn(-0.5);

        opMode.loop();

        verify(leftFrontDrive).setPower(0.5);
        verify(rightFrontDrive).setPower(-1.0);
        verify(leftBackDrive).setPower(0.0);
        verify(rightBackDrive).setPower(0.5);
        verify(telemetry).addData("Status", "Run Time: " + opMode.getRuntime());
        verify(telemetry).addData("Front left/Right", "%4.2f, %4.2f", 0.5, -1.0);
        verify(telemetry).addData("Back  left/Right", "%4.2f, %4.2f", 0.0, 0.5);
        verify(telemetry).update();
    }
}