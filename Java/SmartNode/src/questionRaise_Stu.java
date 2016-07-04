import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class questionRaise_Stu extends JFrame{
	Socket stu_socket;	
	PrintWriter stu_os;
	BufferedReader stu_is;
	MyPaint mainPain;
	String stu_addr, tea_addr, boardcast_addr;
	ArrayList<String> question_List;
	connect_to_tea_Task myconnect_to_tea_task;
	receiveQuestions myRecvQuestion;
	int visible_flag;
	int stu_port, tea_port, boardcast_port, stu_keeplive_port;
	int get_server_flag = 0;
	int timeout_flag = 0;
	int questionraiseSwitch = 0;
		
	class keeplive_timertask extends TimerTask{    
		connect_to_tea_Task myc_to_t_task;
		
		public keeplive_timertask(connect_to_tea_Task ct){
			myc_to_t_task = ct;
		}		
		
		public void run(){
			if(get_server_flag == 0){
				questionraiseSwitch = 0;
				timeout_flag = 1;
				
				Object[] options = { "OK" };				
				int s = JOptionPane.showOptionDialog(null, "Cannot get the response from the teacher, please make sure the teacher is online and try again later, thanks!", "Question-Raise Information (Timeout)", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);				
				myc_to_t_task.stop();
			}
		}
	}
		
	class connect_to_tea_Task extends Thread{
		Timer keeplive_timer = new Timer();
		String sendMessage = "", recvMessage = "";
						
		public void run(){
			sendMessage = "@11:" + stu_addr; 
			try {
				keeplive_timer.schedule(new keeplive_timertask(this), 5 * 1000);
				stu_socket = new Socket(tea_addr, tea_port);
				stu_os = new PrintWriter(stu_socket.getOutputStream());	
				stu_is = new BufferedReader(new InputStreamReader(stu_socket.getInputStream()));								
				stu_os.println(sendMessage);
				stu_os.flush();
				
				while (true) {
					if (stu_is.read() > 0) {
						recvMessage = "";
						recvMessage = stu_is.readLine();
						if (recvMessage.substring(0, 3).equals("12:")) { // response
							if (recvMessage.charAt(3) == 'y') {
								get_server_flag = 1;
								myRecvQuestion = new receiveQuestions();
								myRecvQuestion.start();
							}
						} 
						else if (recvMessage.substring(0, 3).equals("22:")) { // update"
							String update_questionList = recvMessage.substring(3, recvMessage.length());
							if (update_questionList.charAt(update_questionList.length() - 1) != '\n') {
								update_questionList += '\n';
							}
							question_List.add(update_questionList);
						}
						else if (recvMessage.substring(0, 3).equals("32:")){
							recvMessage = recvMessage.substring(3, recvMessage.length());
							question_List.add(recvMessage);
							if(recvMessage.charAt(recvMessage.length()-1) != '\n')
								recvMessage += '\n';
							myRecvQuestion.questionArea.append(recvMessage);
						}
					} else
						break;
				}
				stu_socket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				Object[] options = { "OK" };
				int s = JOptionPane.showOptionDialog(null, "Teacher has exited!", "QuestionRaise Information", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);								
				QuestionRaise_Shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class receiveQuestions extends Thread {
		JTextArea questionArea;
		
		public void run(){
			Container con = getContentPane();		
			questionArea = new JTextArea();
			questionArea.setPreferredSize(new Dimension(250,450));
			questionArea.setLineWrap(true);         
			questionArea.setWrapStyleWord(true);
			questionArea.setFont(new Font("Helvetica", Font.PLAIN, 16));
			questionArea.setEditable(false);
			JScrollPane scroll_1 = new JScrollPane(questionArea);
			scroll_1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
			scroll_1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			con.add(scroll_1,BorderLayout.NORTH);
				
			JPanel p = new JPanel();
			p.setLayout(new GridLayout(3,1));
			JLabel label1 = new JLabel("  Input your questions: ");
			label1.setPreferredSize(new Dimension(250,35));
			label1.setFont(new Font("Helvetica", Font.PLAIN, 16));
			p.add(label1, BorderLayout.NORTH);
			JTextArea Message = new JTextArea();
			Message.setFont(new Font("Helvetica", Font.PLAIN, 16));
			Message.setLineWrap(true);       
			Message.setWrapStyleWord(true); 
			JScrollPane scroll_2 = new JScrollPane(Message);
			scroll_2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
			scroll_2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
			JButton sendButton  = new JButton("Raise Questions");
			sendButton.setFont(new Font("Helvetica", Font.PLAIN, 16));
			scroll_2.setPreferredSize(new Dimension(250,230));
			sendButton.setPreferredSize(new Dimension(250,100));
			p.add(scroll_2);
			p.add(sendButton);
			
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {			
					setVisible(false);
					try{
						stu_socket.close();
					}catch(Exception e1){
			       		System.out.println("stu_socket cannot be closed: "+e1);
			       	}	
					question_List.clear();
					myconnect_to_tea_task.stop();
					myRecvQuestion.stop();
					questionraiseSwitch = 0;
				}
			});
			
			con.add(p, BorderLayout.EAST);
			setSize(260, 650);
			setLocation(mainPain.noteWidth, 0);
			setVisible(true);
			
			for(int i = 0; i < question_List.size(); i++){
				questionArea.append(question_List.get(i));
			}
			
			try{							
				sendButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e){
						String myQuestion = Message.getText();
						if(myQuestion.equals(""));
						else{
							myQuestion = "@31:" + myQuestion;
							Message.setText("");
							stu_os.println(myQuestion);
							stu_os.flush();
						}
					}		
				});
			}catch(Exception e) {
					try{
						stu_socket.close();
					}catch(Exception e1) {
						System.out.println("cannot close stu_socket and serve :" + e1);
					}
					question_List.clear();
					questionArea.setText("");
					questionraiseSwitch = 0;
					setVisible(false);
					System.out.println("cannot listen to:" + e);
			}
		}	
	}
	
	public questionRaise_Stu(MyPaint mpaint, String teacher_ipaddr){
		mainPain = mpaint;
		tea_addr = teacher_ipaddr;
		boardcast_addr = "255.255.255.255";
		boardcast_port = 9999;
		stu_keeplive_port = 8889;
		question_List = new ArrayList<String>(100);
		
		try{
			stu_addr = InetAddress.getLocalHost().getHostAddress();
			stu_port = 8888;
			tea_port = 8888;
		}catch(Exception e) {
			System.out.println("cannot get the stu addr" + e);
		}
	}
	
	public void showQuestion(){	
		questionraiseSwitch = 1;
		myconnect_to_tea_task = new connect_to_tea_Task();
		myconnect_to_tea_task.start();		
	}
	
	public void QuestionRaise_Shutdown(){
		try{
			setVisible(false);
			try{
			stu_socket.close();
			}catch(Exception e1){
	       		System.out.println("cannot be closed: "+e1);
	       	}	
			question_List.clear();
			myconnect_to_tea_task.stop();
			myRecvQuestion.stop();
			questionraiseSwitch = 0;
			mainPain.questionraise_status = "QuestionRaise Function: OFF";
		}catch(Exception e) {
			System.out.println("cannot showdown" + e);
		}
	}

}