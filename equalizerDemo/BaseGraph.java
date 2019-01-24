import java.io.IOException;

import org.jtransforms.fft.DoubleFFT_1D;

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

public class BaseGraph {
	
	public static double yFactor = 0.00002f;
	
	public static SpiDevice spi = null;
    protected static final Console console = new Console();
    
    public static final int SPI_SPEED = 10000000; // default spi speed 1 MHz
    public static final Pin LATCH_PIN_MAP = RaspiPin.GPIO_00;
    
    public final GpioController gpio = GpioFactory.getInstance();
    public final GpioPinDigitalOutput pinLatch = gpio.provisionDigitalOutputPin(LATCH_PIN_MAP, "Latch_Pin", PinState.LOW);

	private long lastFpsTime = 0;
	private int fps = 0;
	
	private boolean[][][] red = new boolean[8][8][8];
	private boolean[][][] green = new boolean[8][8][8];
	private boolean[][][] blue = new boolean[8][8][8];
	
	private byte[] raw_red = new byte[8];
	private byte[] raw_green = new byte[8];
	private byte[] raw_blue = new byte[8];
	private byte raw_level = 0b00000001;
	private int level = 0;
	
	public static void main(String[] args) throws IOException {
		MusicWrapper.start();
		BaseGraph bg = new BaseGraph();
		bg.init();
		bg.mainLoop();
	}
	
	public void init() throws IOException {
		console.title("LED-Cube Fade Programm");
        console.promptForExit();
        spi = SpiFactory.getInstance(SpiChannel.CS0, SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE);
        pinLatch.setShutdownOptions(true, PinState.LOW);
        
        for (int i = 0; i < red.length; i++) {
			for (int j = 0; j < red[i].length; j++) {
				for (int j2 = 0; j2 < red[i][j].length; j2++) {
					red[i][j][j2] = false;
					green[i][j][j2] = false;
					blue[i][j][j2] = false;
				}
			}
		}
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
			
//			long time = (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000;
//			if (time > 0) {
//				try {
//					Thread.sleep(time);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			} else System.out.println("Sleeptime: " + time);

		}
		console.emptyLine();
	}
	
	private void calcRaw(int level) {
		for (int i = 0; i < green[level].length; i++) {
			raw_green[i] = 0b00000000;
			for (int j = 0; j < green[level][i].length; j++) {
				if (green[level][i][j] = true) raw_green[i] |= (0b00000001 << j);
			}
		}
	}

	private void render() throws IOException {
		for (int i = 0; i < 8; i++) {
			calcRaw(level);
			spi.write(raw_blue);
			spi.write(raw_green);
			spi.write(raw_red);
			spi.write(raw_level);
			pinLatch.toggle();
			pinLatch.toggle();
			level++;
			if (level >= 8) level = 0;
			raw_level = (byte) (0b00000001 << level);
		}
		
	}

	private void doAnimationUpdates(double delta) {
		int[][] data = getData();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (data[i][j] != 0) {
					green[i][j][i] = true;				
				} else {
					green[i][j][i] = false;
				}
			}
		}
	}
	
	public int[][] getData(){
		int[][] data = new int[8][8];
		double[] volumeData = getFrom16BitArray(MusicWrapper.out.toByteArray());
		double sum = 0;
		for (int i = 0; i < volumeData.length; i++) {
			sum+=Math.abs(volumeData[i]);
		}
		sum/=data.length;
		int sumI = (int)(sum*yFactor);
		
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				if(sumI > 3){
					data[i][j] = 1;
				} else if (sumI > 2) {
					if (i > 0 && i < 7 && j > 0 && j < 7) {
						data[i][j] = 1;
					} else {
						data[i][j] = 0;
					}
				} else if (sumI > 1) {
					if (i > 1 && i < 6 && j > 1 && j < 6) {
						data[i][j] = 1;
					} else {
						data[i][j] = 0;
					}
				} else if (sumI > 0) {
					if (i > 2 && i < 5 && j > 2 && j < 5) {
						data[i][j] = 1;
					} else {
						data[i][j] = 0;
					}
				} else {
					data[i][j] = 0;
				}
			}
		}
		
		return data;
	}
	
	private double[] getFrom16BitArray(byte[] dataIn){
		double[] dataOut = new double[dataIn.length/2];
		for (int i = 0; i < dataIn.length; i+=2) {
			dataOut[i/2] = (double)((int)(dataIn[i+1]<<8 | dataIn[i]));
			//System.out.println(dataOut[i/2]);
		}
		return dataOut;
	}

}
