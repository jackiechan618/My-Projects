import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;




public class questionRaise_Tea extends JFrame{
	ServerSocket tea_listen_socket;
	Socket stu_socket, tea_send_socket;
	String stu_addr, tea_addr;
	int stu_port, tea_port;
	MyPaint mainPaint;	
	ExecutorService exec;
	String statusStr;
	receiveQuestionsThread myRecvQuestion;
	ArrayList<Socket> stuSocket_List;
	ArrayList<PrintWriter> stuSocket_PrintWriter_List;
	ArrayList<String> stu_addr_List;
	ArrayList<String> question_List;
	int questionCount = 0;
	int questionraiseSwitch = 0;
	int initial_flag;
	
	class Task implements Runnable{
		Socket tea_and_stu_socket;
		JTextArea questionArea;
		JTextArea statusArea;
		BufferedReader tea_recive_is;
		PrintWriter tea_send_os;
		String sendMessage, recvMessage;
		int socketSwitcher = 0;   //turn off
		int index;
				
		public Task(Socket tea_s_socket, JTextArea qArea, JTextArea sArea, int index) throws IOException{
			tea_and_stu_socket = tea_s_socket;
			questionArea = qArea;
			statusArea = sArea;
			this.index = index;
		}
		
		public void run() {
			try{
				tea_recive_is = new BufferedReader(new InputStreamReader(tea_and_stu_socket.getInputStream()));	
				tea_send_os = new PrintWriter(tea_send_socket.getOutputStream());	
							
				while(true){		
					if(tea_recive_is.read() <= 0){
						tea_and_stu_socket.close();
						socketSwitcher = 1;
						stuSocket_List.remove(index);
						stuSocket_PrintWriter_List.remove(index);
						statusStr = "";
						statusStr = "Online students' number: " + stuSocket_List.size();
						statusArea.setText(statusStr);
						break;
					}
					else{
						recvMessage = "";
						recvMessage = tea_recive_is.readLine();
						if(recvMessage.substring(0, 3).equals("11:")){   //student connect"
							stuSocket_List.add(tea_send_socket);
							statusStr = "";
							statusStr = "Online students' number: " + stuSocket_List.size();
							statusArea.setText(statusStr);
							stu_addr = "";
							stu_addr = recvMessage.substring(3, recvMessage.length());
							stu_addr_List.add(stu_addr);
							sendMessage = "";
							sendMessage = "@12:y";
							tea_send_os.println(sendMessage);
							tea_send_os.flush();
							
							for(int i = 0; i < question_List.size(); i++){
								sendMessage = "";
								sendMessage = "@22:" + question_List.get(i);
								tea_send_os.println(sendMessage);
								tea_send_os.flush();
							}							
						}
						else if(recvMessage.substring(0, 3).equals("31:")){   //student's question update"
							recvMessage = recvMessage.substring(3, recvMessage.length());
							questionCount++;
							recvMessage = "question" + questionCount + ": " + recvMessage;
							
							for(int i = 0; i < stuSocket_PrintWriter_List.size(); i++){
								stuSocket_PrintWriter_List.get(i).println("@32:" + recvMessage);
								stuSocket_PrintWriter_List.get(i).flush();
							}
							
							question_List.add(recvMessage);
							recvMessage += '\n';
							questionArea.append(recvMessage);
							setVisible(true);
							questionraiseSwitch = 1;
											
						}
					}
				}
			}catch(Exception e){
				System.out.println("Thread error: " + e);
			}
		}		
	}	
	
	class receiveQuestionsThread extends Thread {
		public void run(){
			Container con = getContentPane();		
			JTextArea questionArea = new JTextArea();
			questionArea.setPreferredSize(new Dimension(250,550));
			questionArea.setLineWrap(true);         
			questionArea.setWrapStyleWord(true);
			questionArea.setFont(new Font("Helvetica", Font.PLAIN, 16));
			questionArea.setEditable(false);
			JTextArea statusArea = new JTextArea();
			statusArea.setBackground(Color.BLACK);
			statusArea.setForeground(Color.GREEN);
			statusArea.setPreferredSize(new Dimension(250,50));
			statusArea.setLineWrap(true);         
			statusArea.setWrapStyleWord(true);
			statusArea.setFont(new Font("Helvetica", Font.PLAIN, 16));
			statusArea.setEditable(false);
			
			JScrollPane scroll_1 = new JScrollPane(questionArea);
			scroll_1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
			scroll_1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			con.add(scroll_1,BorderLayout.NORTH);
			JScrollPane scroll_2 = new JScrollPane(statusArea);
			scroll_2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
			scroll_2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			con.add(scroll_2,BorderLayout.SOUTH);
			
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {			
					setVisible(false);
					questionraiseSwitch = 0;
				}
			});	
			
			setSize(250, 650);
			setLocation(mainPaint.noteWidth, 0);
			setVisible(true);
			
			statusStr = "";
			statusStr = "Online students' number: 0";
			statusArea.setText(statusStr);
			
			try{
				try{
					tea_listen_socket = new ServerSocket(8888);
					exec = Executors.newCachedThreadPool();
				}catch(Exception e) {
					System.out.println("cannot listen to:" + e);
				}	
				
				while(true){
					tea_send_socket = null;
					try{
						tea_send_socket = tea_listen_socket.accept();  
						exec.execute(new Task(tea_send_socket, questionArea, statusArea, stuSocket_List.size())); 			
					}catch(Exception e) {
						System.out.println("Error."+e);
					}			
					Thread.sleep(300);
				}
			}catch(Exception e) {
				System.out.println("Error."+e);
			}
		}
	}
	
	public questionRaise_Tea(MyPaint mpaint){
		mainPaint = mpaint;
		stuSocket_List = new ArrayList<Socket>(100);
		stu_addr_List = new ArrayList<String>(100);
		stuSocket_PrintWriter_List = new ArrayList<PrintWriter>(100);
		question_List = new ArrayList<String>(100);

		try{
			tea_addr = InetAddress.getLocalHost().getHostAddress();
			tea_port = 8888;
		}catch(Exception e) {
			System.out.println("cannot get the tea addr" + e);
		}
	}
	
	public void showQuestion(){
		questionraiseSwitch = 1;
		initial_flag = 1;
		myRecvQuestion = new receiveQuestionsThread();
		myRecvQuestion.start();
	}
	
}