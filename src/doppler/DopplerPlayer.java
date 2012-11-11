package doppler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public final class DopplerPlayer extends JPanel {
	private static final long serialVersionUID = -8590676196574889728L;

	private JTextField textField;
	private JButton button;
	private JButton button_1;
	private JButton button_2;
	private JButton button_3;
	private JButton button_4;
	private JButton button_5;
	private DopplerSlider slider;
	private Thread playerThread;

	public boolean playing = false;
	public Direction direction = Direction.FORWARD;
	public PlayType playType = PlayType.RIGHT;
	public Position position = Position.LEFT_END;
	private double value;
	private int step;
	private double stepScaler = 1.0;

	private static final double stepPercentOfMaximum = 0.02;
	private static final double stepScaleDown = 0.1;
	private static final double stepScaleUp = 0.1;
	private static final double stepScalerMin = 0.1;
	private static final double stepScalerMax = 2.0;
	private static final long sleepMillis = 100;

	private NumberFormat numberFormat = NumberFormat.getInstance(new Locale(
			"pl", "PL"));

	public DopplerPlayer(final DopplerSlider slider) {
		this.slider = slider;

		calculateStep();

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						String text = textField.getText();
						Number number = numberFormat.parse(text);
						value = number.doubleValue();
					} catch (ParseException e1) {
						;
					}
					slider.setValueFromDouble(value);
				}
			}
		});
		textField.setColumns(5);

		button = new JButton("");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextPosition(Direction.BACKWARD);
			}
		});
		button.setIcon(new ImageIcon(DopplerApplication.class
				.getResource("/res/backward.png")));
		button.setContentAreaFilled(false);
		button.setBorder(new EmptyBorder(0, 0, 0, 0));

		button_1 = new JButton("");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playing = !playing;
				if (playing) {
					button_1.setIcon(new ImageIcon(DopplerApplication.class
							.getResource("/res/pause.png")));
					play();
				} else {
					button_1.setIcon(new ImageIcon(DopplerApplication.class
							.getResource("/res/play.png")));
					pause();
				}
			}
		});
		button_1.setIcon(new ImageIcon(DopplerApplication.class
				.getResource("/res/play.png")));
		button_1.setContentAreaFilled(false);
		button_1.setBorder(new EmptyBorder(0, 0, 0, 0));

		button_2 = new JButton("");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextPosition(Direction.FORWARD);
			}
		});
		button_2.setIcon(new ImageIcon(DopplerApplication.class
				.getResource("/res/forward.png")));
		button_2.setContentAreaFilled(false);
		button_2.setBorder(new EmptyBorder(0, 0, 0, 0));

		button_3 = new JButton("");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (stepScaler <= 1.0) {
					stepScaler -= stepScaleDown;
				} else {
					stepScaler -= stepScaleUp;
				}
				if (stepScaler < stepScalerMin) {
					stepScaler = stepScalerMin;
				}
				calculateStep();
			}
		});
		button_3.setIcon(new ImageIcon(DopplerApplication.class
				.getResource("/res/slower.png")));
		button_3.setContentAreaFilled(false);
		button_3.setBorder(new EmptyBorder(0, 0, 0, 0));

		button_4 = new JButton("");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (stepScaler < 1.0) {
					stepScaler += stepScaleDown;
				} else {
					stepScaler += stepScaleUp;
				}
				if (stepScaler > stepScalerMax) {
					stepScaler = stepScalerMax;
				}
				calculateStep();
			}
		});
		button_4.setIcon(new ImageIcon(DopplerApplication.class
				.getResource("/res/faster.png")));
		button_4.setContentAreaFilled(false);
		button_4.setBorder(new EmptyBorder(0, 0, 0, 0));

		button_5 = new JButton("");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playType = playType.next();
				if (playType == PlayType.LEFT) {
					direction = Direction.BACKWARD;
					button_5.setIcon(new ImageIcon(DopplerApplication.class
							.getResource("/res/left.png")));
				} else if (playType == PlayType.RIGHT) {
					direction = Direction.FORWARD;
					button_5.setIcon(new ImageIcon(DopplerApplication.class
							.getResource("/res/right.png")));
				} else if (playType == PlayType.LEFT_RIGHT) {
					button_5.setIcon(new ImageIcon(DopplerApplication.class
							.getResource("/res/left_right.png")));
				}
			}
		});
		button_5.setIcon(new ImageIcon(DopplerApplication.class
				.getResource("/res/right.png")));
		button_5.setContentAreaFilled(false);
		button_5.setBorder(new EmptyBorder(0, 0, 0, 0));

		add(textField);
		add(button);
		add(button_1);
		add(button_2);
		add(button_3);
		add(button_4);
		add(button_5);
	}

	private void calculateStep() {
		step = (int) (stepPercentOfMaximum * stepScaler * slider.getMaximum());
	}

	public void setValue(double value) {
		this.value = value;
		textField.setText(DopplerExperimentPanel.formatDouble(value));
	}

	private void nextPosition(Direction direction) {
		int value = slider.getValue();
		if (direction == Direction.FORWARD) {
			value += step;
		} else {
			value -= step;
		}
		if (value < 0) {
			value = 0;
			position = Position.LEFT_END;
		} else if (value > slider.getMaximum()) {
			value = slider.getMaximum();
			position = Position.RIGHT_END;
		} else {
			position = Position.MIDDLE;
		}
		slider.setValue(value);
	}

	private void play() {
		playerThread = new Thread() {
			public void run() {
				while (true) {
					try {
						nextPosition(direction);
						switch (playType) {
						case RIGHT:
							if (position == Position.RIGHT_END) {
								slider.setValue(0);
							}
							break;
						case LEFT:
							if (position == Position.LEFT_END) {
								slider.setValue(slider.getMaximum());
							}
							break;
						case LEFT_RIGHT:
							if (position == Position.LEFT_END
									|| position == Position.RIGHT_END) {
								direction = direction.next();
							}
							break;
						}
						sleep(sleepMillis);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		};
		playerThread.start();
	}

	private void pause() {
		playerThread.interrupt();
	}
}