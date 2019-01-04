import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.jtransforms.fft.DoubleFFT_1D;

public class MusicWrapper implements Runnable {
	
	public static boolean stopped = false;
	
	public static MusicWrapper musicWrapper = new MusicWrapper();
	
	public static ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	public static int pointArraySize = 1600;
	
	public static float sampleRate = 192000.0f;
	
	public static float sps = 0.0f;
	public static float spss = 0.0f;
	
	public static void start(){
		Thread t = new Thread(MusicWrapper.musicWrapper);
		t.setName("MusicWrapper");
		t.start();
	}
	
	@Override
	public void run() {
		
		
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		
		TargetDataLine line;
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("Audio Format not Supported");
		}
		// Obtain and open the line.
		try {
		    line = (TargetDataLine) AudioSystem.getLine(info);
		    line.open(format);
		    
		    // Assume that the TargetDataLine, line, has already
		    // been obtained and opened.
		    int numBytesRead;
		    byte[] data = new byte[line.getBufferSize() / (int)(sampleRate/(pointArraySize*2))];
		    // Begin audio capture.
		    line.start();
		    
		    // Here, stopped is a global boolean set by another thread.
		    long lastRead = System.nanoTime();
		    long lastReads = System.nanoTime();
		    int c = 0;
		    while (!stopped) {
		    	c++;
		    	// Read the next chunk of data from the TargetDataLine.
		    	numBytesRead =  line.read(data, 0, data.length);
		    	// Save this chunk of data.
		    	if(c >= 100){
		    		spss = 100000000000f / (float)(System.nanoTime() - lastReads);
		    		lastReads = System.nanoTime();
		    		c = 0;
		    	}
	    		sps = 1000000000f / (float)(System.nanoTime() - lastRead);
	    		lastRead = System.nanoTime();
		    	
		    	out.reset();
		    	out.write(data, 0, numBytesRead);
		    }     
		} catch (LineUnavailableException ex) {
		    // Handle the error ... 
		}
		
	}

}
