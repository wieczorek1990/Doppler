package doppler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DopplerApplication implements ChangeListener {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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

	private DopplerExperimentPanel dopplerPanel;
	private DopplerPlayer dopplerPlayer;
	private DopplerPlayer dopplerPlayer_1;
	private DopplerPlayer dopplerPlayer_2;
	private DopplerPlayer dopplerPlayer_3;
	private JFrame frame;
	private int frequencySource;
	private int observerLocation;
	private ObserverLocationSlider slider;
	private FrequencySourceSlider slider_1;
	private VelocityInitialSlider slider_2;
	private TimeSlider slider_3;
	private int time;
	private int velocityInitial;

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
		dopplerPanel = new DopplerExperimentPanel();

		frame = new JFrame();
		frame.setMinimumSize(new Dimension(850, 0));
		frame.setBounds(100, 100, 850, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				dopplerPanel.invalidatePlot();
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}
		});

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);

		panel.setLayout(new GridLayout(0, 4, 0, 0));

		JLabel lblLokalizacjaObserwatora = new JLabel(
				"Lokalizacja obserwatora [m]");
		panel.add(lblLokalizacjaObserwatora);

		JLabel lblCzstotliworda = new JLabel("Częstotliwość źródła [Hz]");
		panel.add(lblCzstotliworda);

		JLabel lblPrdkoPocztkowa = new JLabel("Prędkość początkowa [m/s]");
		panel.add(lblPrdkoPocztkowa);

		JLabel lblCzas = new JLabel("Czas [s]");
		panel.add(lblCzas);

		slider = new ObserverLocationSlider(dopplerPanel);
		slider.addChangeListener(this);
		slider.setMinimum(0);
		slider.setMaximum();
		slider.setValue(0);
		panel.add(slider);

		slider_1 = new FrequencySourceSlider(dopplerPanel);
		slider_1.addChangeListener(this);
		slider_1.setMinimum(0);
		slider_1.setMaximum();
		slider_1.setValue(0);
		panel.add(slider_1);

		slider_2 = new VelocityInitialSlider(dopplerPanel);
		slider_2.addChangeListener(this);
		slider_2.setMinimum(0);
		slider_2.setMaximum();
		slider_2.setValue(0);
		panel.add(slider_2);

		slider_3 = new TimeSlider(dopplerPanel);
		slider_3.addChangeListener(this);
		slider_3.setMinimum(0);
		slider_3.setMaximum();
		slider_3.setValue(0);
		panel.add(slider_3);

		dopplerPlayer = new DopplerPlayer(slider);
		panel.add(dopplerPlayer);

		dopplerPlayer_1 = new DopplerPlayer(slider_1);
		panel.add(dopplerPlayer_1);

		dopplerPlayer_2 = new DopplerPlayer(slider_2);
		panel.add(dopplerPlayer_2);

		dopplerPlayer_3 = new DopplerPlayer(slider_3);
		panel.add(dopplerPlayer_3);

		dopplerPlayer.setValue(dopplerPanel
				.getObserverLocationFromSlider(observerLocation));
		dopplerPlayer_1.setValue(dopplerPanel
				.getFrequencySourceFromSlider(frequencySource));
		dopplerPlayer_2.setValue(dopplerPanel
				.getVelocityInitialFromSlider(velocityInitial));
		dopplerPlayer_3.setValue(dopplerPanel.getTimeFromSlider(time));

		frame.getContentPane().add(dopplerPanel, BorderLayout.CENTER);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting() && dopplerPanel != null
				&& dopplerPlayer != null && dopplerPlayer_1 != null
				&& dopplerPlayer_2 != null && dopplerPlayer_3 != null) {
			if (source == slider) {
				observerLocation = source.getValue();
				dopplerPanel.setObserverLocationFromSlider(observerLocation);
				dopplerPanel.repaint();
				dopplerPlayer.setValue(dopplerPanel
						.getObserverLocationFromSlider(observerLocation));
			} else if (source == slider_1) {
				frequencySource = source.getValue();
				dopplerPanel.setFrequencySourceFromSlider(frequencySource);
				dopplerPanel.repaint();
				dopplerPlayer_1.setValue(dopplerPanel
						.getFrequencySourceFromSlider(frequencySource));
			} else if (source == slider_2) {
				velocityInitial = source.getValue();
				dopplerPanel.setVelocityInitialFromSlider(velocityInitial);
				slider_3.setMaximum();
				dopplerPanel.repaint();
				dopplerPlayer_2.setValue(dopplerPanel
						.getVelocityInitialFromSlider(velocityInitial));
			} else if (source == slider_3) {
				time = source.getValue();
				dopplerPanel.setTimeFromSlider(time);
				dopplerPanel.repaint();
				dopplerPlayer_3.setValue(dopplerPanel.getTimeFromSlider(time));
			}
		}
	}
}
