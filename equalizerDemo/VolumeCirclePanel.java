import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class VolumeCirclePanel extends JPanel {
	
	public double lastmax = 3;
	
	public VolumeCirclePanel() {
		this.setBackground(Color.DARK_GRAY);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D graphics2D = (Graphics2D) g;
	    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    
	    graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    
	    drawMaxCircle(graphics2D);
	    
	    graphics2D.setColor(Color.BLUE);
	    drawVolumeCircle(graphics2D);
	}
	
	public void drawVolumeCircle(Graphics2D g){
		double[] data = getFrom16BitArray(MusicWrapper.out.toByteArray());
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum+=Math.abs(data[i]);
		}
		sum/=data.length;
		if (sum<3) sum=3;
		if (lastmax<sum) lastmax=sum;
		g.fillOval(this.getWidth()/2-(int)(sum/2), this.getHeight()/2-(int)(sum/2), (int)sum, (int)sum);
	}
	
	public void drawMaxCircle(Graphics2D g){
		g.setColor(Color.RED);
		g.fillOval(this.getWidth()/2-(int)(lastmax/2), this.getHeight()/2-(int)(lastmax/2), (int)lastmax, (int)lastmax);
		g.setColor(Color.DARK_GRAY);
		g.fillOval(this.getWidth()/2-(int)((lastmax-3)/2), this.getHeight()/2-(int)((lastmax-3)/2), (int)(lastmax-3), (int)(lastmax-3));
		lastmax-=0.2f;
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
