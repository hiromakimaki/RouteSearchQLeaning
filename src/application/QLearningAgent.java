package application;

import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;


public class QLearningAgent {

	public static final double EPSILON = 0.2;

	private double qValue[][][];// = new double[HEIGHT][WIDTH][Action.values().length];
	private int width;
	private int height;

	private int x;
	private int y;
	private int beforeX;
	private int beforeY;
	private LinkedList<Action> actionList;

	public QLearningAgent(int width, int height, int startX, int startY){
		this.qValue = new double[height][width][Action.values().length];
		this.width = width;
		this.height = height;
		this.initializeLocationAndActionList(startX, startY);
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getBeforeX() {
		return this.beforeX;
	}

	public int getBeforeY() {
		return this.beforeY;
	}

	public void addAction(Action action) {
		this.actionList.add(action);
	}

	/**
	 * Initialize the location and action list of the agent
	 *
	 * @param x: initial x
	 * @param y: initial y
	 */
	public void initializeLocationAndActionList(int startX, int startY) {
		this.x = startX;
		this.y = startY;
		this.beforeX = startX;
		this.beforeY = startY;
		this.actionList = new LinkedList<>();
	}

	/**
	 * Update the agents location.
	 *
	 * @param x: x of the new location
	 * @param y: y of the new location
	 */
	public void updateLocation(int x, int y) {
		this.beforeX = this.x;
		this.beforeY = this.y;
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the optimal actions for each locations.
	 *
	 * @return optimal locations
	 */
	public Action[][] getOptimalAction(){
		Action actions[][] = new Action[this.height][this.width];
		for (int y = 0; y < this.height; y++) {
			for(int x = 0; x < this.width; x++) {
				double qMax = Double.NEGATIVE_INFINITY;
				for(Action a : Action.values()) {
					if(qMax < this.qValue[y][x][a.getNum()]) {
						qMax = this.qValue[y][x][a.getNum()];
						actions[y][x] = a;
					}
				}
			}
		}
		return actions;
	}

	/**
	 * Get the string of the action list.
	 * This method may be useful for debugging.
	 *
	 * @return action list string
	 */
	public String getActionListString() {
		return this.actionList
				.stream()
				.map((a)->(a.getSign()))
				.collect(Collectors.joining(","));
	}

	/**
	 * Updating q-value based on the reward.
	 *
	 * @param reward: reward for the last action
	 */
	public void updateQValue(double reward) {
		Action lastAction = this.actionList.getLast();
		double alpha = 0.1;
		double gamma = 0.99;
		double qMax = Double.NEGATIVE_INFINITY;
		for (Action a : Action.values()) {
			if (qMax < this.qValue[this.y][this.x][a.getNum()]) {
				qMax = this.qValue[this.y][this.x][a.getNum()];
			}
		}
		this.qValue[this.beforeY][this.beforeX][lastAction.getNum()] =
				(1 - alpha) * this.qValue[this.beforeY][this.beforeX][lastAction.getNum()]
						+ alpha * (reward + gamma * qMax);
	}

	/**
	 * Get the action based on the epsilon-greedy method.
	 *
	 * @return choiced action
	 */
	public Action choiceActionByEpsilonGreedy(){
		Action choicedAction = null;
		Random r = new Random();
		double randomDouble = r.nextDouble();
		if(randomDouble < EPSILON) {
			// epsilon: random action
			int randomInt = r.nextInt(Action.values().length);
			choicedAction = Action.getAction(randomInt);
		} else {
			// 1 - epsilon: optimal action based on q-values
			double maxQ = Double.NEGATIVE_INFINITY;
			for (Action a : Action.values()) {
				double q = this.qValue[this.y][this.x][a.getNum()];
				if (maxQ < q) {
					maxQ = q;
					choicedAction = a;
				}
			}
		}
		return choicedAction;
	}
}
