package doppler;

public class TimeSlider extends DopplerSlider {
	private static final long serialVersionUID = 6895781184605335948L;

	public TimeSlider(DopplerPanel dopplerPanel) {
		super(dopplerPanel);
	}

	@Override
	public void setValueFromDouble(double time) {
		setValue(dopplerPanel.getTimeFromDouble(time));
	}
}
