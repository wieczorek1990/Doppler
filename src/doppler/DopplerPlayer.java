package doppler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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

	public boolean playing = false;
	public PlayType playType = PlayType.RIGHT;

	private DopplerApplication dopplerApplication;

	public DopplerPlayer(DopplerApplication dopplerApplication) {
		this.dopplerApplication = dopplerApplication;

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

				}
			}
		});
		textField.setColumns(5);

		button = new JButton("");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
				} else {
					button_1.setIcon(new ImageIcon(DopplerApplication.class
							.getResource("/res/play.png")));
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

			}
		});
		button_2.setIcon(new ImageIcon(DopplerApplication.class
				.getResource("/res/forward.png")));
		button_2.setContentAreaFilled(false);
		button_2.setBorder(new EmptyBorder(0, 0, 0, 0));

		button_3 = new JButton("");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		button_3.setIcon(new ImageIcon(DopplerApplication.class
				.getResource("/res/slower.png")));
		button_3.setContentAreaFilled(false);
		button_3.setBorder(new EmptyBorder(0, 0, 0, 0));

		button_4 = new JButton("");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
					button_5.setIcon(new ImageIcon(DopplerApplication.class
							.getResource("/res/left.png")));
				} else if (playType == PlayType.RIGHT) {
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
}