package doppler;

public enum PlayType {
	RIGHT, LEFT, LEFT_RIGHT;
	PlayType next() {
		return values()[(ordinal() + 1) % values().length];
	}
}