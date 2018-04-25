package application;

public class QLearningEnvironment {

	// At least, the width and height must be larger than 3.
	public static final int FIELD[][] = {
			{1, 1, 1, 1, 1, 1, 0, 0, 1, 0},
			{1, 0, 1, 0, 0, 1, 0, 1, 1, 1},
			{1, 0, 1, 0, 0, 1, 0, 1, 0, 0},
			{2, 1, 2, 1, 1, 2, 0, 1, 0, 0},
			{0, 0, 1, 0, 0, 1, 1, 2, 1, 0},
			{1, 1, 1, 0, 0, 1, 0, 0, 1, 0},
			{0, 0, 1, 1, 1, 1, 1, 1, 1, 1}
	};
	public static final int WIDTH = FIELD[0].length;
	public static final int HEIGHT = FIELD.length;
	// Start: Top Left
	public static final int START_X = 0;
	public static final int START_Y = 0;
	// Goal: Bottom Right
	public static final int GOAL_X = WIDTH - 1;
	public static final int GOAL_Y = HEIGHT - 1;

	private QLearningAgent agent;


	public QLearningEnvironment(){
		this.agent = new QLearningAgent(WIDTH, HEIGHT, START_X, START_Y);
	}

	/**
	 * Get the optimal actions of the agent
	 *
	 * @return optimal actions
	 */
	public Action[][] getAgentOptimalAction(){
		return this.agent.getOptimalAction();
	}

	/**
	 * Execute one learning step.
	 *
	 */
	public void learnOneStep() {
		int maxTurn = 200;
		boolean reachGoal = false;
		double reward = 0.0;
		this.agent.initializeLocationAndActionList(START_X, START_Y);
		for (int i = 0; i < maxTurn; i++) {
			if(reachGoal) {
				break;
			}
			int x = this.agent.getX();
			int y = this.agent.getY();
			Action action = this.agent.choiceActionByEpsilonGreedy();

			int[] nextLoc = this.nextLocation(x, y, action);
			int nextX = nextLoc[0];
			int nextY = nextLoc[1];

			reward += this.getReward(x, y, action);

			this.agent.updateLocation(nextX, nextY);
			this.agent.addAction(action);
			this.agent.updateQValue(reward);

			reachGoal = nextX == GOAL_X & nextY == GOAL_Y;
		}
		// for debugging
		System.out.println("***** RESULT *****");
		if(reachGoal) {
			System.out.println(reward);
			System.out.println(agent.getActionListString());
		}
	}

	/**
	 * Calculate the reward, which does NOT contain that of the congestion.
	 *
	 * @param x: x of the location
	 * @param y: y of the location
	 * @param action: action which is choiced on (x, y)
	 *
	 * @return base reward
	 */
	public double getReward(int x, int y, Action action) {
		int[] nextLocation = this.nextLocation(x, y, action);
		int nextX = nextLocation[0];
		int nextY = nextLocation[1];

		// negative reward for spending time
		double reward = -2;
		if(x == nextX & y == nextY) {
			// negative reward for staying the same location.
			reward -= 15;
		} else {
			// positive reward for the next location.
			reward += FIELD[nextY][nextX];
			// goal reward
			if(nextX == GOAL_X & nextY == GOAL_Y) {
				reward += 50;
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
	public boolean isOutOfRange(int x, int y) {
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
	public int[] nextLocation(int x, int y, Action action){
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
