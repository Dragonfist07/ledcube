
public class Test {
	
	public static void main(String[] args) {
		byte b1 = 0b00000010;
		while(true) {
			b1 = (byte)Integer.rotateLeft(b1, 1);
			String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
			System.out.println(s1); // 10000001
		}
	}

}
