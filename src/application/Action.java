package application;

public enum Action {

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

	public Integer getNum() {
		return this.num;
	}

	public String getSign() {
		return this.sign;
	}

	public Integer getX() {
		return this.x;
	}

	public Integer getY() {
		return this.y;
	}

	public static Action getAction(Integer num) {
		for (Action a : Action.values()) {
			if(num == a.getNum()) {
				return a;
			}
		}
		throw new IllegalArgumentException("The input value of `num` is invalid...");
	}
}
