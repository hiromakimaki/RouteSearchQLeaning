package application;

public class QLearningEnvironment {

	// At least, the width and height must be larger than 3.
	public static final int FIELD[][] = {
			{1, 1, 1, 1, 1, 2, 1, 1, 1, 0},
			{1, 0, 1, 0, 0, 1, 0, 0, 0, 0},
			{1, 0, 1, 0, 0, 1, 1, 1, 0, 0},
			{1, 1, 2, 1, 1, 1, 0, 1, 1, 1},
			{1, 0, 0, 1, 0, 1, 0, 1, 0, 0},
			{1, 0, 0, 1, 0, 2, 1, 1, 0, 0},
			{1, 1, 1, 1, 1, 1, 0, 2, 1, 1},
			{0, 1, 0, 0, 0, 1, 0, 1, 0, 1},
			{0, 1, 0, 0, 0, 1, 0, 1, 0, 1},
			{0, 1, 1, 1, 1, 1, 1, 1, 1, 1}
	};
	public static final int WIDTH = FIELD[0].length;
	public static final int HEIGHT = FIELD.length;
	// Start: Top Left
	public static final int START_X = 0;
	public static final int START_Y = 0;
	// Goal: Bottom Right
	public static final int GOAL_X = WIDTH - 1;
	public static final int GOAL_Y = HEIGHT - 1;


	/**
	 * Calculate the reward, which does NOT contain that of the congestion.
	 *
	 * @param x: x of the location
	 * @param y: y of the location
	 * @param action: action which is choiced on (x, y)
	 *
	 * @return base reward
	 */
	public static double getReward(int x, int y, Action action) {
		int[] nextLocation = nextLocation(x, y, action);
		int nextX = nextLocation[0];
		int nextY = nextLocation[1];

		// negative reward for spending time
		double reward = -2;
		if(x == nextX & y == nextY) {
			// negative reward for staying the same location.
			reward -= 30;
		} else {
			// positive reward for the next location.
			reward += (FIELD[nextY][nextX] - 1) * 10;
			// goal reward
			if(nextX == GOAL_X & nextY == GOAL_Y) {
				reward += 70;
			}
		}
		return reward;
	}

	/**
	 * Check whether the location is outside of the field.
	 *
	 * @param x: x of the location
	 * @param y: y of the location
	 *
	 * @return {@code true} if the location is outside, else {@code false}
	 */
	public static boolean isOutOfRange(int x, int y) {
		return x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT;
	}

	/**
	 * Calculate the next location of the agent.
	 * If the agent will be outside the field, it stays on the same location.
	 *
	 * @param x: current x
	 * @param y: current y
	 * @param action: agents's action
	 *
	 * @return the result location of the action
	 */
	public static int[] nextLocation(int x, int y, Action action){
		int nextX = x + action.getX();
		int nextY = y + action.getY();
		int[] loc = {nextX, nextY};
		if (isOutOfRange(nextX, nextY)) {
			loc[0] = x;
			loc[1] = y;
		}
		return loc;
	}
}
