package doppler;

public class TimeSlider extends DopplerSlider {
	private static final long serialVersionUID = 6895781184605335948L;

	public TimeSlider(DopplerExperimentPanel dopplerPanel) {
		super(dopplerPanel);
	}

	@Override
	public void setMaximum() {
		setMaximum(dopplerPanel.getTimeMaxForSlider());
	}

	@Override
	public void setValueFromDouble(double time) {
		setValue(dopplerPanel.getTimeFromDouble(time));
	}
}
