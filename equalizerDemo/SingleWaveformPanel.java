import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class SingleWaveformPanel extends JPanel{
	
	public static int length = 0;
	
	public SingleWaveformPanel() {
		this.setBackground(Color.DARK_GRAY);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D graphics2D = (Graphics2D) g;
	    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    
	    graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    
	    graphics2D.setColor(Color.GREEN);
	    drawWave(graphics2D);
	    
	    graphics2D.setColor(Color.WHITE);
	    graphics2D.setFont(new Font("Consolas", Font.PLAIN, 12));
	    graphics2D.drawString("Audio Sample per Sec: " + length, 10, 10);
	}
	
	public void drawWave(Graphics2D g){
		double[] barData = getFrom16BitArray(MusicWrapper.out.toByteArray());
		length = barData.length;
		for (int i = 1; i < barData.length; i++) {
			g.drawLine(i-1, ((int)(barData[i-1]/2f)+(this.getHeight()/2)), i, ((int)(barData[i]/2f)+(this.getHeight()/2)));
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

}
