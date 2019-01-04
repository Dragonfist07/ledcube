import javax.swing.JFrame;

public class musicTest {
	
	public static void main(String[] args) {
		MusicWrapper.start();
		JFrame jf = new JFrame("CS2");
		SingleWaveformPanel swp = new SingleWaveformPanel();
		swp.setSize(700, 500);
		
		FrequencyPanel fp = new FrequencyPanel();
		fp.setSize(700, 500);
		
		VolumeCirclePanel vcp = new VolumeCirclePanel();
		vcp.setSize(700, 500);
		
		CubeSim cs = new CubeSim();
		cs.setSize(700, 500);
		
		CubeSim2 cs2 = new CubeSim2();
		cs2.setSize(700, 500);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(800, 600);
		jf.setLocationRelativeTo(null);
		
		jf.getContentPane().add(cs2);	
		jf.setVisible(true);
		
		while (true) {
			cs2.repaint();				
		}
	}

}
//Hallo Jonas