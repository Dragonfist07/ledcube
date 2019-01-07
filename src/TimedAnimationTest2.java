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

public class TimedAnimationTest2 {
	
    public static SpiDevice spi = null;
    protected static final Console console = new Console();
    
    public static final int SPI_SPEED = 10000000; // default spi speed 1 MHz
    public static final Pin LATCH_PIN_MAP = RaspiPin.GPIO_00;
    
    public final GpioController gpio = GpioFactory.getInstance();
    public final GpioPinDigitalOutput pinLatch = gpio.provisionDigitalOutputPin(LATCH_PIN_MAP, "Latch_Pin", PinState.LOW);

	private long lastFpsTime = 0;
	private int fps = 0;
	
	private byte[][][] red = new byte[8][8][8];
	private byte[][][] green = new byte[8][8][8];
	private byte[][][] blue = new byte[8][8][8];
	
	private byte[] raw_red = new byte[8];
	private byte[] raw_green = new byte[8];
	private byte[] raw_blue = new byte[8];
	private byte raw_level;
	
	public static void main(String[] args) throws IOException {
		TimedAnimationTest2 tat = new TimedAnimationTest2();
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
		final int TARGET_FPS = 480;
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
			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else System.out.println("Sleeptime: " + time);

		}
		console.emptyLine();
	}
	
	private void multiplexing(int stage) {
		for (int i = 0; i < red[raw_level].length; i++) {
			raw_red[i] = 0b00000000;
			for (int j = 0; j < red[raw_level][i].length; j++) {
				if (red[raw_level][i][j] > stage) raw_red[i] &= (0b00000001 << j);
			}
		}
	}

	private void render() throws IOException {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				multiplexing(i);
				spi.write(raw_blue);
				spi.write(raw_green);
				spi.write(raw_red);
				spi.write(raw_level);
			}
			raw_level = (byte) (0b00000001 << i);
		}
		
		pinLatch.toggle();
		pinLatch.toggle();
	}

	private void doAnimationUpdates(double delta) {
		for (int i = 0; i < red.length; i++) {
			for (int j = 0; j < red.length; j++) {
				for (int j2 = 0; j2 < red.length; j2++) {
					red[i][j][j2] = 7;
				}
			}
		}
	}

}
