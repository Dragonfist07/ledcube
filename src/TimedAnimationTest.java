import java.io.IOException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.util.Console;

public class TimedAnimationTest {
	
    public static SpiDevice spi = null;
    protected static final Console console = new Console();
    
    public static final int SPI_SPEED = 10000000; // default spi speed 1 MHz
    public static final Pin LATCH_PIN_MAP = RaspiPin.GPIO_00;
    
    public final GpioController gpio = GpioFactory.getInstance();
    public final GpioPinDigitalOutput pinLatch = gpio.provisionDigitalOutputPin(LATCH_PIN_MAP, "Latch_Pin", PinState.LOW);

	private long lastFpsTime = 0;
	private int fps = 0;
	
	private byte[] red = new byte[8];
	private byte[] green = new byte[8];
	private byte[] blue = new byte[8];
	private byte plane;
	
	public static void main(String[] args) throws IOException {
		TimedAnimationTest tat = new TimedAnimationTest();
		tat.init();
		tat.mainLoop();
	}
	
	public void init() throws IOException {
		console.title("LED-Cube Fade Programm");
        console.promptForExit();
        spi = SpiFactory.getInstance(SpiChannel.CS0, SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE);
        pinLatch.setShutdownOptions(true, PinState.LOW);
	}

	public void mainLoop() throws IOException {
		long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

		while (console.isRunning()) {
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / ((double) OPTIMAL_TIME);

			lastFpsTime += updateLength;
			fps++;

			if (lastFpsTime >= 1000000000) {
				System.out.println("(FPS: " + fps + ")");
				lastFpsTime = 0;
				fps = 0;
			}

			doAnimationUpdates(delta);

			render();
			
			long time = (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000;
	    	try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				System.out.println("Sleeptime: " + time);
				e.printStackTrace();
			}

		}
		console.emptyLine();
	}

	private void render() throws IOException {
		spi.write(blue);
		spi.write(green);
		spi.write(red);
		spi.write(plane);
		
		pinLatch.toggle();
		pinLatch.toggle();
	}

	private void doAnimationUpdates(double delta) {
//		for (int i = 0; i < stuff.size(); i++) {
//			// all time-related values must be multiplied by delta!
//			Stuff s = stuff.get(i);
//			s.velocity += Gravity.VELOCITY * delta;
//			s.position += s.velocity * delta;
//
//			// stuff that isn't time-related doesn't care about delta...
//			if (s.velocity >= 1000) {
//				s.color = Color.RED;
//			} else {
//				s.color = Color.BLUE;
//			}
//		}
	}

}
