package doppler;

public class VelocityInitialSlider extends DopplerSlider {
	private static final long serialVersionUID = 6895781184605335948L;

	public VelocityInitialSlider(DopplerPanel dopplerPanel) {
		super(dopplerPanel);
	}

	@Override
	public void setValueFromDouble(double velocityInitial) {
		setValue(dopplerPanel.getVelocityInitialFromDouble(velocityInitial));
	}
}
