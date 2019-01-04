import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import org.jtransforms.fft.DoubleFFT_1D;

public class FrequencyPanel extends JPanel {

	public static final Color REFERENCE_LINE_COLOR = Color.RED;
	public static final Color FREQUENZ_COLOR = Color.BLUE;
	public static double yFactor = 0.001f;
	
	public static int minFrequency = 1;	//Minimum 1! (0 maybe okay?)
	public static int maxFrequency = 1000;
	public static int bars = 8;
	public static double logarithm = Math.pow(2, (double)(1f/12f));
	
	public static float fps = 0.0f;
	
	public static long lastPaint = System.nanoTime();
	
	public static int samplesInFrq = 0;
	
	public static double[] tops = new double[bars];

	public FrequencyPanel() {
		this.setBackground(Color.DARK_GRAY);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int lineHeight = this.getHeight()/2;
		
		Graphics2D graphics2D = (Graphics2D) g;
	    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    
	    graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 
		
	    drawFrequenzBarsWithTops(graphics2D);
	    drawXaxis(graphics2D);
	    
	    graphics2D.setColor(Color.WHITE);
	    graphics2D.setFont(new Font("Consolas", Font.PLAIN, 12));
	    graphics2D.drawString("Audio Sample per Sec: " + ((int)(MusicWrapper.sps*100)/100f) + " (~" + ((int)(MusicWrapper.spss*100)/100f) + ")", 10, 10);
	    graphics2D.drawString("FPS: " + ((int)(fps*10)/10f), 10, 30);
	    graphics2D.drawString("Samples in Frq range: " + samplesInFrq + " (~" + samplesInFrq/bars + " per frq. bar)", 10, 50);
	    
	    fps = 1000000000f / (float)(System.nanoTime() - lastPaint);
	    lastPaint = System.nanoTime();
	}
	
	private void drawFrequenzBars(Graphics2D g){
		g.setColor(FREQUENZ_COLOR);
		double[] barData = getFrequenzBarData(getFrom16BitArray(MusicWrapper.out.toByteArray()));
		double xPosMulti = getWidth() / barData.length + 1;
		for (int i = 0; i < barData.length; i++) {
			double scaledData = barData[i] * yFactor;
			float colorCode = (float)scaledData / (float)getHeight();
			float h = (1-colorCode)*0.3f;
			float s = 1.0f;
			float b = 0.77f;
			Color c = Color.getHSBColor(h, s, b);
			g.setColor(c);
			g.fillRect((int)(i*xPosMulti), (int)(getHeight()-(scaledData+10)), (int)(xPosMulti)-5, (int)scaledData+2);
		}
	}
	
	private void drawFrequenzBarsWithTops(Graphics2D g){
		g.setColor(FREQUENZ_COLOR);
		double[] barData = getFrequenzBarData(getFrom16BitArray(MusicWrapper.out.toByteArray()));
		double xPosMulti = getWidth() / barData.length + 1;
		for (int i = 0; i < barData.length; i++) {
			double scaledData = yFactor * barData[i];
			float colorCode = (float)scaledData / (float)(getHeight()/2);
			float h = (1-colorCode)*0.3f;
			float s = 1.0f;
			float b = 0.77f;
			Color c = Color.getHSBColor(h, s, b);
			g.setColor(c);
			g.fillRect((int)(i*xPosMulti), (int)(getHeight()-(scaledData+10)), (int)(xPosMulti)-5, (int)scaledData+2);
			
			colorCode = (float)tops[i] / (float)getHeight();
			h = (1-colorCode)*0.3f;
			c = Color.getHSBColor(h, s, b);
			c = Color.RED;
			g.setColor(c);
			
			if (tops[i] < scaledData) tops[i] = scaledData;
			g.fillRect((int)(i*xPosMulti), (int)(getHeight()-(tops[i]+25)), (int)(xPosMulti)-5, 10);
			tops[i]-=0.6f;
		}
	}
	
	private void drawXaxis(Graphics2D g){
		//Print x Axis
		g.setColor(Color.BLACK);
//		for (int i = 0; i < 20; i++) {
//			int x = i*getWidth()/20;
//			int hz = (int)(MusicWrapper.sampleRate * (i/10f) * samples.length);
//			
//			graphics2D.drawLine(x, lineHeight-5, x, lineHeight+5);
//			graphics2D.drawString(hz+"", x, lineHeight+16);
//		}
//		for (int i = 0; i < samples.length; i+=samples.length/10) {
//			int x = (int) (i*pix);
//			int hz = (int) (i * MusicWrapper.sampleRate / MusicWrapper.pointArraySize);
//			
//			g.drawLine(x, lineHeight-5, x, lineHeight+5);
//			g.drawString(hz+"", x, lineHeight+16);
//		}
		
		//double[] barData = getFrequenzBarData(getFrom16BitArray(MusicWrapper.out.toByteArray()));
		double xPosMulti = getWidth() / bars + 1;
		double[] areas = getfrqAreasSqr();
		for (int i = 0; i < bars; i++) {
			int a = minFrequency;
			if(i!=0) {
				a = (int) Math.round(areas[i-1]);
			}
			int b = (int) Math.round(areas[i]);
			String s = a + "-" + b + "hz";
			g.setFont(new Font("Consolas", Font.PLAIN, 12));
			g.drawString(s, (int) ((i*xPosMulti)+3), getHeight()-15);
		}
	}
	
	private double[] getFrom16BitArrayWithCorrection(byte[] dataIn){
		double[] dataOut = new double[dataIn.length/2];
		double lastY = 0;
		for (int i = 0; i < dataIn.length; i+=2) {
			if ((double)((int)(dataIn[i+1]<<8 | dataIn[i])) > 500 || (double)((int)(dataIn[i+1]<<8 | dataIn[i])) < -500){
				dataOut[i/2] = (double)((int)(dataIn[i+1]<<8 | dataIn[i]));				
				lastY = dataOut[i/2];
			} else {
				dataOut[i/2] = lastY;
			}
		}
		return dataOut;
	}
	
	private double[] getFrom16BitArray(byte[] dataIn){
		double[] dataOut = new double[dataIn.length/2];
		for (int i = 0; i < dataIn.length; i+=2) {
			dataOut[i/2] = (double)((int)(dataIn[i+1]<<8 | dataIn[i]));
			//System.out.println(dataOut[i/2]);
		}
		return dataOut;
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
	
	private double[] getfrqAreasLog(){
		double[] frqAreas = new double[bars];
		double maxX = logOfBase(logarithm, maxFrequency-minFrequency+1);
		double steps = maxX / bars;
		for (int i = 0; i < frqAreas.length; i++) {
			frqAreas[i] = Math.pow(logarithm, steps*(i+1)) + minFrequency;
			//System.out.println(i + ": bis " + frqAreas[i] + "hz");
		}
		return frqAreas;
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
	
	private double[] getfrqAreasLinear(){
		double[] frqAreas = new double[bars];
		double steps = (maxFrequency-minFrequency) / bars;
		for (int i = 0; i < frqAreas.length; i++) {
			frqAreas[i] = steps*(i+1) + minFrequency;
		}
		return frqAreas;
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
