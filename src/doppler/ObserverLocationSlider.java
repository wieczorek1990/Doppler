package doppler;

public class ObserverLocationSlider extends DopplerSlider {
	private static final long serialVersionUID = 6895781184605335948L;

	public ObserverLocationSlider(DopplerPanel dopplerPanel) {
		super(dopplerPanel);
	}

	@Override
	public void setValueFromDouble(double observerLocation) {
		setValue(dopplerPanel.getObserverLocationFromDouble(observerLocation));
	}
}
