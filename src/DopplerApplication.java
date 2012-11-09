import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

// TODO player
public class DopplerApplication implements ChangeListener {

	private JFrame frame;
	private DopplerPanel dopplerPanel;
	private JSlider slider;
	private JSlider slider_1;
	private JSlider slider_2;
	private JSlider slider_3;

	// TODO playerThreads
	private double observerLocation;
	private double sourceFrequency;
	private double time;
	private double initialVelocity;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DopplerApplication window = new DopplerApplication();
					window.frame.setTitle("Doppler");
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DopplerApplication() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(0, 4, 0, 0));

		JLabel lblLokalizacjaObserwatora = new JLabel("Lokalizacja obserwatora");
		panel.add(lblLokalizacjaObserwatora);

		JLabel lblCzstotliworda = new JLabel("Częstotliwość źródła");
		panel.add(lblCzstotliworda);

		JLabel lblPrdkoPocztkowa = new JLabel("Prędkość początkowa");
		panel.add(lblPrdkoPocztkowa);

		JLabel lblCzas = new JLabel("Czas");
		panel.add(lblCzas);

		slider = new JSlider();
		slider.addChangeListener(this);
		slider.setMinimum(1);
		slider.setMaximum(40);
		slider.setValue(0);
		panel.add(slider);

		slider_1 = new JSlider();
		slider_1.addChangeListener(this);
		slider_1.setMinimum(200);
		slider_1.setMaximum(1250);
		slider_1.setValue(200);
		panel.add(slider_1);

		slider_2 = new JSlider();
		slider_2.addChangeListener(this);
		slider_2.setMinimum(10);
		slider_2.setMaximum(50);
		slider_2.setValue(10);
		panel.add(slider_2);

		slider_3 = new JSlider();
		slider_3.addChangeListener(this);
		slider_3.setMinimum(0);
		slider_3.setMaximum(20);
		slider_3.setValue(0);
		panel.add(slider_3);

		dopplerPanel = new DopplerPanel(slider.getValue(), slider_1.getValue(),
				slider_2.getValue(), slider_3.getValue(), slider_3.getMaximum());
		frame.getContentPane().add((JPanel) dopplerPanel, BorderLayout.CENTER);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting() && dopplerPanel != null) {
			if (source == slider) {
				observerLocation = (double) source.getValue();
				dopplerPanel.setObserverLocation(observerLocation);
				dopplerPanel.repaint();
			} else if (source == slider_1) {
				sourceFrequency = (double) source.getValue();
				dopplerPanel.setSourceFrequency(sourceFrequency);
				dopplerPanel.repaint();
			} else if (source == slider_2) {
				initialVelocity = (double) source.getValue();
				dopplerPanel.setInitialVelocity(initialVelocity);
				int timeMax = (int) (dopplerPanel.getDistance() / initialVelocity);
				dopplerPanel.setTimeMax(timeMax);
				slider_3.setMaximum(timeMax);
				dopplerPanel.repaint();
			} else if (source == slider_3) {
				time = (double) source.getValue();
				dopplerPanel.setTime(time);
				dopplerPanel.repaint();
			}
		}
	}
}
