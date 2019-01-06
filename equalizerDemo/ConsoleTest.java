import org.jtransforms.fft.DoubleFFT_1D;

public class ConsoleTest {
	
	public static double yFactor = 0.0002f;
	
	public ConsoleTest() {
	}
	
	public void draw64LEDs(){
		int[][] data = getData();
		
		
		for (int i = 1; i < 9; i++) {
			for (int j = 1; j < 9; j++) {
				if (data[i-1][j-1] != 0) {
					System.out.print("X");					
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();
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
