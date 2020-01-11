package negotiator;

import org.junit.Test;

public class PausableTimelineTest {

	@Test
	public void testTimeline() {
		PausableContinuousTimeline timeline = new PausableContinuousTimeline(3);
		System.out.println("Elapsed: " + timeline.getElapsedSeconds() + " seconds");
		try {
			timeline.pause();
			Thread.sleep(2000);
			timeline.resume();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Elapsed: " + timeline.getElapsedSeconds() + " seconds");
		try {
			timeline.pause();
			Thread.sleep(2000);
			timeline.resume();
		} catch (Exception e) {
			e.printStackTrace();
		}
		timeline.printElapsedSeconds();
		while (!timeline.isDeadlineReached()) {
			System.out.println("Elapsed: " + timeline.getElapsedSeconds() + " seconds");
		}
	}

}
