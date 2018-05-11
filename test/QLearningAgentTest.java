import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;

import application.Action;
import application.QLearningAgent;
import application.QLearningEnvironment;

public class QLearningAgentTest {

	@Test
	public void testChoiceActionByEpsilonGreedy(){
		QLearningAgent agent = new QLearningAgent(QLearningEnvironment.WIDTH, QLearningEnvironment.HEIGHT);
		Action choicedAction = agent.choiceActionByEpsilonGreedy();
		// TODO: implement the test (for random case).
	}

	@Test
	public void testUpdateQValue() throws NoSuchFieldException, IllegalAccessException {
		QLearningAgent agent = new QLearningAgent(QLearningEnvironment.WIDTH, QLearningEnvironment.HEIGHT);
		Action choicedAction = agent.choiceActionByEpsilonGreedy();
		agent.addAction(choicedAction);
		// Positive reward is given on the initial location and the choicedAction.
		agent.updateQValue(10.0);
		Field field = agent.getClass().getDeclaredField("qValue");
		field.setAccessible(true);
		double[][][] qValue = (double[][][]) field.get(agent);
		// Positive reward is given on the initial location and the choiced action.
		assertTrue(qValue[0][0][choicedAction.getNum()] > 0);
		// No reward is given on the other locations and actions.
		assertTrue(qValue[1][1][choicedAction.getNum()] == 0);
	}

	@Test
	public void testUpdateLocation(){
		int beforeX = 0;
		int beforeY = 1;
		int newX = 1;
		int newY = 2;
		QLearningAgent agent = new QLearningAgent(QLearningEnvironment.WIDTH, QLearningEnvironment.HEIGHT);
		agent.updateLocation(beforeX, beforeY);
		agent.updateLocation(newX, newY);
		assertEquals(agent.getX(), newX);
		assertEquals(agent.getY(), newY);
		assertEquals(agent.getBeforeX(), beforeX);
		assertEquals(agent.getBeforeY(), beforeY);
	}
}
