package doppler;

public class FrequencySourceSlider extends DopplerSlider {
	private static final long serialVersionUID = 6895781184605335948L;

	public FrequencySourceSlider(DopplerExperimentPanel dopplerPanel) {
		super(dopplerPanel);
	}

	@Override
	public void setMaximum() {
		setMaximum(dopplerPanel.getFrequencySourceMaxForSlider());
	}

	@Override
	public void setValueFromDouble(double frequencySource) {
		setValue(dopplerPanel.getFrequencySourceFromDouble(frequencySource));
	}
}
