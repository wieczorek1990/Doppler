package doppler;
import javax.swing.JSlider;


public abstract class DopplerSlider extends JSlider implements IDopplerSlider {
	private static final long serialVersionUID = 1624344153590545528L;
	protected DopplerPanel dopplerPanel;

	public DopplerSlider(DopplerPanel dopplerPanel) {
		this.dopplerPanel = dopplerPanel;
	}
}
