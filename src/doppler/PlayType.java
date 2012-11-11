package doppler;

public enum PlayType {
	LEFT, LEFT_RIGHT, RIGHT;
	PlayType next() {
		return values()[(ordinal() + 1) % values().length];
	}
}