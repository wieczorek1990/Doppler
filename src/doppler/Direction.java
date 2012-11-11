package doppler;

public enum Direction {
	FORWARD, BACKWARD;
	Direction next() {
		return values()[(ordinal() + 1) % values().length];
	}
}
