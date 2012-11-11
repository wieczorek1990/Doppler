package doppler;

public enum Direction {
	BACKWARD, FORWARD;
	Direction next() {
		return values()[(ordinal() + 1) % values().length];
	}
}
