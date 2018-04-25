import static org.junit.Assert.*;

import org.junit.Test;

import application.Action;
import application.QLearningEnvironment;

public class QLearningEnvironmentTest {

	@Test
	public void testGetBaseReward() {
		QLearningEnvironment env = new QLearningEnvironment();
		// outside the field
		assertTrue(env.getReward(0, 0, Action.UP) < -10);
		// inside the field, but not goal
		assertTrue(env.getReward(0, 0, Action.DOWN) > -10);
		assertTrue(env.getReward(0, 0, Action.DOWN) < 0);
		// goal
		assertTrue(env.getReward(QLearningEnvironment.GOAL_X - 1, QLearningEnvironment.GOAL_Y, Action.RIGHT) > 10);
	}

	@Test
	public void testIsOutOfRange() {
		QLearningEnvironment env = new QLearningEnvironment();
		// Start location
		assertTrue(env.isOutOfRange(QLearningEnvironment.START_X - 1, QLearningEnvironment.START_Y));
		assertTrue(env.isOutOfRange(QLearningEnvironment.START_X, QLearningEnvironment.START_Y - 1));
		assertFalse(env.isOutOfRange(QLearningEnvironment.START_X, QLearningEnvironment.START_Y));
		// Goal location
		assertFalse(env.isOutOfRange(QLearningEnvironment.GOAL_X, QLearningEnvironment.GOAL_Y));
		assertTrue(env.isOutOfRange(QLearningEnvironment.GOAL_X + 1, QLearningEnvironment.GOAL_Y));
		assertTrue(env.isOutOfRange(QLearningEnvironment.GOAL_X, QLearningEnvironment.GOAL_Y + 1));
	}

	@Test
	public void testNextLocation() {
		QLearningEnvironment env = new QLearningEnvironment();
		int locX, locY;
		int[] nextLoc = new int[2];
		// Ordinal
		locX = 1;
		locY = 1;
		nextLoc = env.nextLocation(locX, locY, Action.UP);
		assertEquals(nextLoc[0], locX);
		assertEquals(nextLoc[1], locY - 1);
		nextLoc = env.nextLocation(locX, locY, Action.DOWN);
		assertEquals(nextLoc[0], locX);
		assertEquals(nextLoc[1], locY + 1);
		nextLoc = env.nextLocation(locX, locY, Action.LEFT);
		assertEquals(nextLoc[0], locX - 1);
		assertEquals(nextLoc[1], locY);
		nextLoc = env.nextLocation(locX, locY, Action.RIGHT);
		assertEquals(nextLoc[0], locX + 1);
		assertEquals(nextLoc[1], locY);
		// Boundary
		locX = 0;
		locY = 0;
		nextLoc = env.nextLocation(locX, locY, Action.UP);
		assertEquals(nextLoc[0], locX);
		assertEquals(nextLoc[1], locY);
		nextLoc = env.nextLocation(locX, locY, Action.LEFT);
		assertEquals(nextLoc[0], locX);
		assertEquals(nextLoc[1], locY);
		locX = QLearningEnvironment.WIDTH - 1;
		locY = QLearningEnvironment.HEIGHT - 1;
		nextLoc = env.nextLocation(locX, locY, Action.DOWN);
		assertEquals(nextLoc[0], locX);
		assertEquals(nextLoc[1], locY);
		nextLoc = env.nextLocation(locX, locY, Action.RIGHT);
		assertEquals(nextLoc[0], locX);
		assertEquals(nextLoc[1], locY);
	}
}
