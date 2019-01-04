import java.io.IOException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.util.Console;

public class AlexColorFade {

	public static int c1 = 0;
	public static int c2 = 0;
	public static int c3 = 0;
	public static int c4 = 0;
	public static int rot1 = 0b00000000;
	public static int grun1 = 0b00000000;
	public static int blau1 = 0b00000000;
    public static SpiDevice spi = null;
    protected static final Console console = new Console();

    public static void main(String args[]) throws InterruptedException, IOException {

        console.title("LED-Cube Fade Programm");
        console.promptForExit();

       // create SPI object instance for SPI for communication
        //spi = SpiFactory.getInstance(SpiChannel.CS0,
        //        SpiDevice.DEFAULT_SPI_SPEED, // default spi speed 1 MHz
        //        SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0
 
		spi = SpiFactory.getInstance(SpiChannel.CS0,
                10000000, // default spi speed 1 MHz
                SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0

        // continue running program until user exits using CTRL-C
 
		// create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #01 as an output pin and turn on
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "MyLED", PinState.LOW);

        // set shutdown state for this pin
        pin.setShutdownOptions(true, PinState.LOW);
 
        while(console.isRunning()) {
			for (int i = 0; i < 8; i++) {
			   byte ebene = (byte) (1 << i);
				for (int j = 0; j < 8; j++) {
					buildSend(ebene, j);
					//Thread.sleep(1);
					pin.toggle();
					//Thread.sleep(1);
					pin.toggle(); 
				}
			}
			c2++;
			  
			if (c2>=100) {
				c2 = 0;
				c1++;
			}
			
			if(c1 >= 8) c1=1;
			  
			if(c4==100){
				rot1 = 0b11111111;
				grun1 = 0b00000000;
				blau1 = 0b00000000;
			} 
			  
			if(c4==800){
				rot1 = 0b11111111;
				grun1 = 0b11111111;
				blau1 = 0b00000000;
			}
			
			if(c4==1500){
				rot1 = 0b00000000;
				grun1 = 0b11111111;
				blau1 = 0b00000000;
			}
			
			if(c4==2200){
				rot1 = 0b00000000;
				grun1 = 0b11111111;
				blau1 = 0b11111111;
			}

			if(c4==2900){
				rot1 = 0b00000000;
				grun1 = 0b00000000;
				blau1 = 0b11111111;
			}

			if(c4==3600){
				rot1 = 0b11111111;
				grun1 = 0b00000000;
				blau1 = 0b11111111;
			}

			if(c4==4300){
				rot1 = 0b11111111;
				grun1 = 0b11111111;
				blau1 = 0b11111111;
				c4 = 100;
			}
		
			c4++;
		}
					
        console.emptyLine();
    }

	public static byte[] buildrot(int j) {
		byte rot[] = new byte[8];
			if (c1 >= j) {
				for (int i = 0; i < rot.length; i++) {
				rot[i] = (byte) (rot1);
			}
		}
		return rot;
	}

	public static byte[] buildgrun(int j) {
		byte rot[] = new byte[8];
			if (c1 >= j) {
				for (int i = 0; i < rot.length; i++) {
					rot[i] = (byte) (grun1);
				}
			}
		return rot;
	}

	public static byte[] buildblau(int j) {
		byte rot[] = new byte[8];
			if (c1 >= j) {
				for (int i = 0; i < rot.length; i++) {
					rot[i] = (byte) (blau1);
				}
			}
		return rot;
	}

	public static void buildSend(byte ebene, int j) throws IOException {
		byte rot[] = buildrot(j);
		byte blau[] = buildblau(j);
		byte grun[] = buildgrun(j);
 
		sendTest(ebene, rot, grun, blau);
	}

    public static void sendTest(byte ebene, byte[] rot, byte[] grun, byte[] blau) throws IOException {
        // create a data buffer
        spi.write(blau);
		spi.write(grun);
		spi.write(rot);
		spi.write(ebene);

        // send data via SPI channel
        //byte[] result = spi.write(data);
		//return result;
    }
}