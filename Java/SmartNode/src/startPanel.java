import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class startPanel extends JFrame{
	Toolkit kit;
	Dimension screenSize;
	int screenWidth, screenHeight, startpanelWidth, startpanelHeight;
	int startpanel_x, startpanel_y;
	char userMode = 'n';
	String label_str;
	
	public startPanel(){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		screenWidth = screenSize.width;
		screenHeight = screenSize.height;
		startpanelWidth = 400;
		startpanelHeight = 260;
		startpanel_x = (screenWidth - startpanelWidth) / 2;
		startpanel_y = (screenHeight - startpanelHeight) / 2;

		Container con = getContentPane();
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(4, 1));
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(1, 1));
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(2, 1));
		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(1, 2));
		JPanel p4 = new JPanel();
		p4.setLayout(new GridLayout(1, 2));

		JLabel label1 = new JLabel(" Please choose your mode: ");
		label1.setFont(new Font("Helvetica", Font.PLAIN, 16));
		p1.add(label1, BorderLayout.CENTER);

		JButton b_tea = new JButton("Teacher Mode");
		b_tea.setFont(new Font("Helvetica", Font.PLAIN, 16));
		JButton b_stu = new JButton("Student Mode");
		b_stu.setFont(new Font("Helvetica", Font.PLAIN, 16));
		p2.add(b_tea, BorderLayout.WEST);
		p2.add(b_stu, BorderLayout.EAST);

		JLabel label2 = new JLabel(" Input teacher's ip address: ");
		label2.setFont(new Font("Helvetica", Font.PLAIN, 16));
		JLabel label3 = new JLabel("(only for student Mode)");
		label3.setFont(new Font("Helvetica", Font.PLAIN, 16));
		p3.add(label2, BorderLayout.NORTH);
		p3.add(label3, BorderLayout.SOUTH);

		JTextField tf = new JTextField("");
		tf.setFont(new Font("Helvetica", Font.PLAIN, 16));
		tf.setEditable(false);
		tf.setBackground(Color.BLACK);
		JButton b_ok = new JButton("OK");
		b_ok.setFont(new Font("Helvetica", Font.PLAIN, 16));
		p4.add(tf, BorderLayout.WEST);
		p4.add(b_ok, BorderLayout.EAST);

		p.add(p1, BorderLayout.NORTH);
		p.add(p2, BorderLayout.SOUTH);
		p.add(p3, BorderLayout.SOUTH);
		p.add(p4, BorderLayout.SOUTH);
		con.add(p);

		b_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (userMode == 's') {
					String temp = tf.getText();
					if (temp.length() > 0) {
						new MyPaint(tf.getText(), userMode);
						setVisible(false);
					} else {
						Object[] options = { "OK" };
						int s = JOptionPane.showOptionDialog(null,
								"Please input the teacher's ip address!",
								"SmartNote Information",
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE, null, options,
								options[0]);
					}
				}
				else{
					Object[] options = { "OK" };
					int s = JOptionPane.showOptionDialog(null,
							"Please choose your mode first!",
							"SmartNote Information",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options,
							options[0]);
				}
			}
		});
		b_tea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userMode = 't';
				new MyPaint("", userMode);
				setVisible(false);
			}
		});
		b_stu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userMode = 's';
				tf.setEditable(true);
				tf.setBackground(Color.WHITE);
				String temp = tf.getText();
				if(temp.length() == 0){
					Object[] options = { "OK" };
					int s = JOptionPane.showOptionDialog(null, "Please input the teacher's ip address!", "SmartNote Information", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[0]);
				}
				else{
					Object[] options = { "OK" };
					int s = JOptionPane.showOptionDialog(null, "Please click the \"OK\" buttom to start!", "SmartNote Information", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[0]);
				}
			}
		});

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		}); 

		this.setTitle("Thanks for using SmartNote");
		setLocation(startpanel_x, startpanel_y);
		setSize(startpanelWidth, startpanelHeight);
		setVisible(true);
	}
}
