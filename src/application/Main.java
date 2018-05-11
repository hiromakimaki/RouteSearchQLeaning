package application;

import java.util.ArrayList;
import java.util.List;

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

public class Main extends Application {

	private final int UNIT_SIZE = 50;
	private final int RECT_SIZE = UNIT_SIZE - 10;
	private final int CIRCLE_RADIUS = RECT_SIZE / 2;

	private Button button;
	private QLearningAgent agent;
	private BorderPane root;
	private Scene scene;

	@Override
	public void start(Stage primaryStage) {
		try {
			this.button = new Button("Learn!");

			this.button.setOnAction((actionEvent) -> {
				this.root.getChildren().clear();
				for(int i = 0; i < 50; i++) {
					this.agent.learnOneStep();
				}
				this.root.getChildren().addAll(getRectangles(QLearningEnvironment.FIELD));
				this.root.getChildren().addAll(getOptimalActionTexts(this.agent.getOptimalAction()));
				this.root.getChildren().addAll(getCircle(QLearningEnvironment.GOAL_X, QLearningEnvironment.GOAL_Y));
				this.root.setBottom(this.button);
			});

			this.agent = new QLearningAgent(
				QLearningEnvironment.WIDTH,
				QLearningEnvironment.HEIGHT
			);
			this.root = new BorderPane();
			this.root.getChildren().addAll(getRectangles(QLearningEnvironment.FIELD));
			this.root.getChildren().addAll(getOptimalActionTexts(this.agent.getOptimalAction()));
			this.root.getChildren().add(getCircle(QLearningEnvironment.GOAL_X, QLearningEnvironment.GOAL_Y));
			this.root.setBottom(button);

			final int sceneHeight = QLearningEnvironment.HEIGHT * UNIT_SIZE + 50; // plus button
			final int sceneWidth = QLearningEnvironment.WIDTH * UNIT_SIZE;

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

	public Circle getCircle(int x, int y) {
		return new Circle(x * UNIT_SIZE + CIRCLE_RADIUS, y * UNIT_SIZE + CIRCLE_RADIUS, CIRCLE_RADIUS, Color.WHITE);
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
