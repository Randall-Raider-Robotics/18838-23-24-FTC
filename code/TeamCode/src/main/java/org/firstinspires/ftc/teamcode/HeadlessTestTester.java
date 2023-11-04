import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class HeadlessTestTester {

    @Mock
    private BNO055IMU imu;
    @Mock
    private Telemetry telemetry;

    private HeadlessCode headlessCode;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        headlessCode = new HeadlessCode(imu, telemetry);
    }

    @Test
    public void testCorrectHeadlessCode() {
        when(imu.getRotation()).thenReturn(45.0);
        when(headlessCode.getLeftStickY()).thenReturn(0.5);
        when(headlessCode.getLeftFrontPower()).thenReturn(0.5);
        when(headlessCode.getRightFrontPower()).thenReturn(0.5);
        when(headlessCode.getLeftBackPower()).thenReturn(0.5);
        when(headlessCode.getRightBackPower()).thenReturn(0.5);

        headlessCode.checkHeadlessCode();

        verify(telemetry).addData("Using headless code", " ");
        verify(telemetry).addData("Correct headless code", " ");
        verify(telemetry, never()).addData("Incorrect headless code", " ");
        verify(telemetry, never()).addData("Not using headless code", " ");
    }

    @Test
    public void testIncorrectHeadlessCode() {
        when(imu.getRotation()).thenReturn(45.0);
        when(headlessCode.getLeftStickY()).thenReturn(0.5);
        when(headlessCode.getLeftFrontPower()).thenReturn(0.0);
        when(headlessCode.getRightFrontPower()).thenReturn(0.5);
        when(headlessCode.getLeftBackPower()).thenReturn(0.5);
        when(headlessCode.getRightBackPower()).thenReturn(0.5);

        headlessCode.checkHeadlessCode();

        verify(telemetry).addData("Using headless code", " ");
        verify(telemetry, never()).addData("Correct headless code", " ");
        verify(telemetry).addData("Incorrect headless code", " ");
        verify(telemetry, never()).addData("Not using headless code", " ");
    }

    @Test
    public void testNotUsingHeadlessCode() {
        when(imu.getRotation()).thenReturn(0.0);

        headlessCode.checkHeadlessCode();

        verify(telemetry, never()).addData("Using headless code", " ");
        verify(telemetry, never()).addData("Correct headless code", " ");
        verify(telemetry, never()).addData("Incorrect headless code", " ");
        verify(telemetry).addData("Not using headless code", " ");
    }
}