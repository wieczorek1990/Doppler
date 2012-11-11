package doppler;

public class VelocityInitialSlider extends DopplerSlider {
	private static final long serialVersionUID = 6895781184605335948L;

	public VelocityInitialSlider(DopplerExperimentPanel dopplerPanel) {
		super(dopplerPanel);
	}

	@Override
	public void setValueFromDouble(double velocityInitial) {
		setValue(dopplerPanel.getVelocityInitialFromDouble(velocityInitial));
	}

	@Override
	public void setMaximum() {
		setMaximum(dopplerPanel.getVelocityInitialMaxForSlider());
	}
}
