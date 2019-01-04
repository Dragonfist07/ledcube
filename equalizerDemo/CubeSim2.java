import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import org.jtransforms.fft.DoubleFFT_1D;

public class CubeSim2 extends JPanel {
	
	public static double yFactor = 0.0002f;
	
	public CubeSim2() {
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
		
		g.setColor(Color.BLUE);
		for (int i = 1; i < 9; i++) {
			for (int j = 1; j < 9; j++) {
				if (data[i-1][j-1] != 0) {
					g.fillOval(j*spaceX, i*spaceY, spaceX/3, spaceY/3);					
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
