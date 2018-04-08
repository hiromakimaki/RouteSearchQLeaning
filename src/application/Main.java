package application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


enum RewardColor {

	FIREBRICK(Color.FIREBRICK, Integer.MIN_VALUE, 1),
	YELLOWGREEN(Color.YELLOWGREEN, 1, 2),
	AQUAMARINE(Color.AQUAMARINE, 2, Integer.MAX_VALUE);

	private Color color;
	private Integer lowerBound; // inclusive
	private Integer upperBound; // exclusive

	RewardColor(Color color, Integer lowerBound, Integer upperBound){
		this.color = color;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	Color getColor() {
		return this.color;
	}

	Integer getLowerBound() {
		return this.lowerBound;
	}

	Integer getUpperBound() {
		return this.upperBound;
	}

	static RewardColor getRewardColor(Integer reward) {
		for (RewardColor col : RewardColor.values()) {
			if(col.getLowerBound() <= reward && reward < col.getUpperBound()) {
				return col;
			}
		}
		throw new IllegalArgumentException("The input value of `reward` is invalid...");
	}
}

enum Action {

	UP(0, "↑", 0, -1),
	RIGHT(1, "→", 1, 0),
	DOWN(2, "↓", 0, 1),
	LEFT(3, "←", -1, 0);

	private Integer num; // Action sequential number
	private String sign;
	private Integer x;
	private Integer y;

	Action(Integer num, String sign, Integer x, Integer y){
		this.num = num;
		this.sign = sign;
		this.x = x;
		this.y = y;
	}

	Integer getNum() {
		return this.num;
	}

	String getSign() {
		return this.sign;
	}

	Integer getX() {
		return this.x;
	}

	Integer getY() {
		return this.y;
	}

	static Action getAction(Integer num) {
		for (Action a : Action.values()) {
			if(num == a.getNum()) {
				return a;
			}
		}
		throw new IllegalArgumentException("The input value of `num` is invalid...");
	}
}

class QLearningAgent{

	// At least, the width and height must be larger than 3.
	public static final int FIELD[][] = {
			{1, 1, 1, 1, 1, 1, 0, 0, 1, 0},
			{1, 0, 1, 0, 0, 2, 0, 2, 1, 1},
			{1, 0, 1, 0, 0, 1, 0, 1, 0, 0},
			{2, 1, 2, 1, 1, 2, 0, 1, 0, 0},
			{0, 0, 1, 0, 0, 1, 1, 2, 1, 0},
			{1, 1, 1, 0, 0, 1, 1, 0, 1, 0},
			{0, 0, 1, 1, 1, 1, 1, 0, 1, 1}
	};
	public static final int HEIGHT = FIELD.length;
	public static final int WIDTH = FIELD[0].length;
	public static final int START[] = {0, 0}; // Top left(y, x)
	public static final int GOAL[] = {6, 9}; // Bottom right(y, x)
	public static final double EPSILON = 0.2;
	public static final int maxTurn = 200;

	private double qValue[][][] = new double[HEIGHT][WIDTH][Action.values().length];

	private int status[] = new int[2];
	private int beforeStatus[] = new int[2];
	private LinkedList<Action> actionList = new LinkedList<>();

	QLearningAgent(){
		this.status[0] = START[0];
		this.status[1] = START[1];
		this.beforeStatus[0] = START[0];
		this.beforeStatus[1] = START[1];
	}


	public void learnOneStep() {
		moveAgentToStart();
		double reward = 0;
		for (int i = 0; i < maxTurn; i++) {
			Action a = choiceActionByEpsilonGreedy();
			double r = actionAndGetReward(a);
			updateQValue(r);
			reward += r;
			if (isGoal()) {
				break;
			}
		}
		System.out.println("********************");
		System.out.println("The reward: " + reward);
		System.out.println("The last status -> x: " + this.status[1] + ", y: " + this.status[0] + ".");
		System.out.println(getActionListString());
	}

	public Action[][] getOptimalAction(){
		Action actions[][] = new Action[HEIGHT][WIDTH];
		for (int y = 0; y < HEIGHT; y++) {
			for(int x = 0; x < WIDTH; x++) {
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

	public void moveAgentToStart() {
		this.status[0] = START[0];
		this.status[1] = START[1];
		this.beforeStatus[0] = START[0];
		this.beforeStatus[1] = START[1];
		this.actionList = new LinkedList<>();
	}

	public boolean isGoal() {
		return this.status[1] == GOAL[1] && this.status[0] == GOAL[0];
	}

	public String getActionListString() {
		return this.actionList
				.stream()
				.map((a)->(a.getSign()))
				.collect(Collectors.joining(","));
	}

	public void updateQValue(double reward) {
		Action lastAction = this.actionList.getLast();
		double alpha = 0.1;
		double gamma = 0.99;
		double qMax = Double.NEGATIVE_INFINITY;
		for (Action a : Action.values()) {
			if (qMax < this.qValue[this.status[0]][this.status[1]][a.getNum()]) {
				qMax = this.qValue[this.status[0]][this.status[1]][a.getNum()];
			}
		}
		this.qValue[this.beforeStatus[0]][this.beforeStatus[1]][lastAction.getNum()] =
				(1 - alpha) * this.qValue[this.beforeStatus[0]][this.beforeStatus[1]][lastAction.getNum()]
						+ alpha * (reward + gamma * qMax);
	}

	public double actionAndGetReward(Action action) {
		this.beforeStatus[0] = this.status[0];
		this.beforeStatus[1] = this.status[1];
		this.actionList.add(action);
		double reward = -2; // minus reward for moving
		int nextX = this.status[1] + action.getX();
		int nextY = this.status[0] + action.getY();
		if (nextX < 0 || nextX >= WIDTH || nextY < 0 || nextY >= HEIGHT) {
			reward -= 15; // go outside the field
		} else {
			this.status[1] = nextX;
			this.status[0] = nextY;
			reward += FIELD[nextY][nextX]; // target cell reward
			if (nextX == GOAL[1] && nextY == GOAL[0]) {
				// goal reward
				reward += 50;
			}
		}
		return reward;
	}

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
				double q = this.qValue[this.status[0]][this.status[1]][a.getNum()];
				if (maxQ < q) {
					maxQ = q;
					choicedAction = a;
				}
			}
		}
		return choicedAction;
	}
}

public class Main extends Application {

	private final int UNIT_SIZE = 50;
	private final int RECT_SIZE = UNIT_SIZE - 10;
	private final int CIRCLE_RADIUS = RECT_SIZE / 2;

	private Button button;
	private QLearningAgent qLearningAgent;
	private BorderPane root;
	private Scene scene;

	@Override
	public void start(Stage primaryStage) {
		try {
			this.button = new Button("Learn!");

			this.button.setOnAction((actionEvent) -> {
				this.root.getChildren().clear();
				for(int i = 0; i < 5; i++) {
					this.qLearningAgent.learnOneStep();
				}
				this.root.getChildren().addAll(getRectangles(QLearningAgent.FIELD));
				this.root.getChildren().addAll(getOptimalActionTexts(this.qLearningAgent.getOptimalAction()));
				this.root.getChildren().addAll(getCircle(QLearningAgent.GOAL));
				this.root.setBottom(this.button);
			});

			this.qLearningAgent = new QLearningAgent();
			this.root = new BorderPane();
			this.root.getChildren().addAll(getRectangles(QLearningAgent.FIELD));
			this.root.getChildren().addAll(getOptimalActionTexts(this.qLearningAgent.getOptimalAction()));
			this.root.getChildren().add(getCircle(QLearningAgent.GOAL));
			this.root.setBottom(button);

			final int sceneHeight = QLearningAgent.HEIGHT * UNIT_SIZE + 50; // plus button
			final int sceneWidth = QLearningAgent.WIDTH * UNIT_SIZE;

			this.scene = new Scene(this.root,sceneWidth,sceneHeight);
			this.scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(this.scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public Circle getCircle(int[] status) {
		return new Circle(status[1] * UNIT_SIZE + CIRCLE_RADIUS, status[0] * UNIT_SIZE + CIRCLE_RADIUS, CIRCLE_RADIUS, Color.WHITE);
	}

	private List<Rectangle> getRectangles(int[][] field){
		List<Rectangle> result = new ArrayList<>();
		int height = field.length;
		int width = field[0].length;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Rectangle rect = new Rectangle(RECT_SIZE, RECT_SIZE, RewardColor.getRewardColor(field[y][x]).getColor());
				rect.setX(UNIT_SIZE*x);
				rect.setY(UNIT_SIZE*y);
				result.add(rect);
			}
		}
		return result;
	}

	private List<Text> getOptimalActionTexts(Action[][] actions){
		List<Text> result = new ArrayList<>();
		int height = actions.length;
		int width = actions[0].length;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Text text = new Text(UNIT_SIZE*x+12, UNIT_SIZE*y+25, actions[y][x].getSign());
				text.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
				text.setFill(Color.WHITE);
				result.add(text);
			}
		}
		return result;
	}

}
