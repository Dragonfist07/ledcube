import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import org.jtransforms.fft.DoubleFFT_1D;

public class CubeSim extends JPanel {
	
	public static final Color REFERENCE_LINE_COLOR = Color.RED;
	public static final Color FREQUENZ_COLOR = Color.BLUE;
	public static double yFactor = 0.00005f;
	
	public static int samplesInFrq = 0;
	
	public static int minFrequency = 1;	//Minimum 1! (0 maybe okay?)
	public static int maxFrequency = 1000;
	public static int bars = 8;
	public static double logarithm = Math.pow(2, (double)(1f/12f));
	
	public CubeSim() {
		this.setBackground(Color.BLACK);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D graphics2D = (Graphics2D) g;
	    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    
	    graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    
	    graphics2D.setColor(Color.WHITE);
	    draw64LEDs(graphics2D);
	}
	
	public void draw64LEDs(Graphics2D g){
		int[][] data = getData();
		
		int spaceX = this.getWidth()/9;
		int spaceY = this.getHeight()/9;
		
		for (int i = 1; i < 9; i++) {
			for (int j = 1; j < 9; j++) {
				if (data[i-1][j-1] != 0) {
					g.setColor(new Color(255-(i*25), (i*25), 0));
					g.fillOval(j*spaceX, i*spaceY, spaceX/3, spaceY/3);					
				}
			}
		}
	}
	
	public int[][] getData(){
		int[][] data = new int[8][8];
		double[] barData = getFrequenzBarData(getFrom16BitArray(MusicWrapper.out.toByteArray()));
		for (int i = 0; i < barData.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				if((barData[i]*yFactor)-1>j){
					data[data[i].length-j-1][i] = 1;
				} else {
					data[data[i].length-j-1][i] = 0;
				}
			}
		}
		
		return data;
	}
	
	private void drawFrequenzBars(Graphics2D g){
		double[] barData = getFrequenzBarData(getFrom16BitArray(MusicWrapper.out.toByteArray()));
		double xPosMulti = getWidth() / barData.length + 1;
		for (int i = 0; i < barData.length; i++) {
			double scaledData = barData[i] * 3;
			float colorCode = (float)scaledData / (float)getHeight();
			float h = (1-colorCode)*0.3f;
			float s = 1.0f;
			float b = 0.77f;
			Color c = Color.getHSBColor(h, s, b);
			g.setColor(c);
			g.fillRect((int)(i*xPosMulti), (int)(getHeight()-(scaledData+10)), (int)(xPosMulti)-5, (int)scaledData+2);
		}
	}
	
	private double[] getFrom16BitArray(byte[] dataIn){
		double[] dataOut = new double[dataIn.length/2];
		for (int i = 0; i < dataIn.length; i+=2) {
			dataOut[i/2] = (double)((int)(dataIn[i+1]<<8 | dataIn[i]));
			//System.out.println(dataOut[i/2]);
		}
		return dataOut;
	}
	
	private double[] getfrqAreasSqr(){
		double[] frqAreas = new double[bars];
		double maxX = Math.sqrt(maxFrequency-minFrequency);
		double steps = maxX / bars;
		for (int i = 0; i < frqAreas.length; i++) {
			frqAreas[i] = Math.pow(steps*(i+1), 2) + minFrequency;
			//System.out.println(i + ": bis " + frqAreas[i] + "hz");
		}
		return frqAreas;
	}
	
	private double[] getFrequenzBarData(double[] inData){
		double[] fftData = getFFT(inData);
		samplesInFrq = fftData.length;
		double[] frqAreas = getfrqAreasSqr();
		double[] ret = new double[frqAreas.length];
		int min = 0;
		for (int i = 0; i < ret.length; i++) {
			double sum = 0;
			int j = 0;
			float hz = 0;
			int c = 0;
			for (j = min; j < fftData.length; j++) {
				c++;
				hz = ((j) * MusicWrapper.sampleRate / MusicWrapper.pointArraySize);
				sum += fftData[j];
				if (hz > frqAreas[i]) {
					//System.out.println(i + ": break by " + c + " runs");
					//sum/=c;
					break;
				}
			}
			min = j;
			ret[i] = sum;
		}
		return ret;
	}
	
	private double[] getFFT(double[] inData){
		DoubleFFT_1D fft = new DoubleFFT_1D(inData.length);

        double[] fftData = new double[inData.length*2];
        for (int i = 0; i < inData.length; i++) {
            // copying audio data to the fft data buffer, imaginary part is 0
            fftData[2 * i] = inData[i];
            fftData[2 * i + 1] = 0;
        }

        // calculating the fft of the data, so we will have spectral power of each frequency component
        // fft resolution (number of bins) is samplesNum, because we initialized with that value
        fft.complexForward(fftData);
        double[] spectrum = new double[inData.length];
        
        for (int i = 0; i < spectrum.length; i++) {
        	spectrum[i] = Math.sqrt(Math.pow((fftData[2*i]), 2) + Math.pow((fftData[2*i+1]), 2));
		}
        
        //cut spectrum
        int cutLength = 0;
        for (int i = 0; i < spectrum.length; i++) {
        	int hz = (int) (i * MusicWrapper.sampleRate / MusicWrapper.pointArraySize);
        	if(hz >= minFrequency && hz <= maxFrequency){
        		cutLength++;
        	} 
		}
        double[] cuttedSpectrum = new double[cutLength];
        for (int i = 0; i < cuttedSpectrum.length; i++) {
        	cuttedSpectrum[i] = spectrum[i + minFrequency];
		}
        
        return cuttedSpectrum;
	}
	
	private double logOfBase(double base, double num) {
	    return Math.log(num) / Math.log(base);
	}

}
