
public class TimedAnimationTest {

	private boolean appRunning = true;
	private long lastFpsTime = 0;
	private int fps = 0;
	
	public static void main(String[] args) {
		TimedAnimationTest tat = new TimedAnimationTest();
		tat.mainLoop();
	}

	public void mainLoop() {
		long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

		while (appRunning) {
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / ((double) OPTIMAL_TIME);

			lastFpsTime += updateLength;
			fps++;

			if (lastFpsTime >= 1000000000) {
				System.out.println("(FPS: " + fps + ")");
				lastFpsTime = 0;
				fps = 0;
			}

			doAnimationUpdates(delta);

			render();

	    	try {
				Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void render() {
		// TODO Auto-generated method stub
	}

	private void doAnimationUpdates(double delta) {
//		for (int i = 0; i < stuff.size(); i++) {
//			// all time-related values must be multiplied by delta!
//			Stuff s = stuff.get(i);
//			s.velocity += Gravity.VELOCITY * delta;
//			s.position += s.velocity * delta;
//
//			// stuff that isn't time-related doesn't care about delta...
//			if (s.velocity >= 1000) {
//				s.color = Color.RED;
//			} else {
//				s.color = Color.BLUE;
//			}
//		}
	}

}
