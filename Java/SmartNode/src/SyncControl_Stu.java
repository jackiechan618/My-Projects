import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;



public class SyncControl_Stu extends JFrame{
	int Sync_Switcher, connect_to_teacher_flag = 0;
	Socket stu_to_tea_socket;
	PrintWriter stu_to_tea_os;
	BufferedReader stu_to_tea_is;
	MyPaint mainPain;
	Sync_Stu_Thread mySyncStuThread;
	String teacher_ipaddr;
	int tea_port = 8887;
	String recvMessage, sendMessage;
	int sync_index, choice, start_x, start_y, end_x, end_y, width, height;
	String input;
	int R, G, B, thickness, index;
	float stroke;
	String my_ipaddr;
	Timer keeplive_timer;
	
	class keeplive_timertask extends TimerTask{    
		Sync_Stu_Thread mySyncStuThread;
		Socket stotSocket;
		
		public keeplive_timertask(Sync_Stu_Thread sst, Socket s_to_t_socket){
			mySyncStuThread = sst;
			stotSocket = s_to_t_socket;
		}		
		public void run(){
			if(connect_to_teacher_flag == 0){
				Sync_Switcher = 0;
				try{
					stotSocket.close();
				}catch(Exception e) {
		       		System.out.println("stotSocket cannot close: "+e); 
		       	}
				Object[] options = { "OK" };				
				int s = JOptionPane.showOptionDialog(null, "Cannot connect to the teacher, please make sure the teacher is online and try again later, thanks!", "Sync Information (Timeout)", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);				
				mySyncStuThread.stop();
			}
		}
	}
	
	class Sync_Stu_Thread extends Thread {
		public void run(){
			try{				
				keeplive_timer = new Timer();
				keeplive_timer.schedule(new keeplive_timertask(this, stu_to_tea_socket), 3 * 1000);
				stu_to_tea_socket = new Socket(teacher_ipaddr,tea_port);
				stu_to_tea_os = new PrintWriter(stu_to_tea_socket.getOutputStream());	
				stu_to_tea_is = new BufferedReader(new InputStreamReader(stu_to_tea_socket.getInputStream()));								
				sendMessage = "@11:" + my_ipaddr;
				stu_to_tea_os.println(sendMessage);
				stu_to_tea_os.flush();
				mainPain.newFile();
				mainPain.sync_itemList.clear();
				mainPain.drawingArea.repaint();
				
				while(true){
					if(stu_to_tea_is.read() > 0){
						connect_to_teacher_flag = 1;
						recvMessage = "";
						recvMessage = stu_to_tea_is.readLine();
						
						if(recvMessage.substring(0, 3).equals("12:")){ 
							if(recvMessage.charAt(3) == 'y'){
								Object[] options = { "OK" };
								int s = JOptionPane.showOptionDialog(null, "Connect to teacher and sync starts!", "SmartNote Information", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
							}
						}
						else if(recvMessage.substring(0, 3).equals("21:")){      //draw shape
							sync_index = choice = start_x = start_y = end_x = end_y = R = G = B  = thickness = 0;
							stroke = 0.0f;
							int i = 3;
							
							for (; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									sync_index *= 10;
									sync_index += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}

							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									choice *= 10;
									choice += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									start_x *= 10;
									start_x += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									start_y *= 10;
									start_y += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									end_x *= 10;
									end_x += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									end_y *= 10;
									end_y += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									R *= 10;
									R += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									G *= 10;
									G += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									B *= 10;
									B += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									stroke *= 10;
									stroke += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									thickness *= 10;
									thickness += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}


							mainPain.sync_index++;
							mainPain.createSyncItem(choice, R, G, B, stroke,
									thickness);
							mainPain.sync_itemList.get(mainPain.sync_index).x1 = start_x;
							mainPain.sync_itemList.get(mainPain.sync_index).y1 = start_y;
							mainPain.sync_itemList.get(mainPain.sync_index).x2 = end_x;
							mainPain.sync_itemList.get(mainPain.sync_index).y2 = end_y;
						}
						else if(recvMessage.substring(0, 3).equals("22:")){      //delete
							sync_index = 0;
							for (int i = 3; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									sync_index *= 10;
									sync_index += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							if(mainPain.sync_index > 0){
								mainPain.sync_itemList.remove(sync_index);
								mainPain.sync_index--;
								mainPain.drawingArea.repaint();
							}
						}
						else if(recvMessage.substring(0, 3).equals("23:")){      //draw word						
							start_x = start_y = end_x = end_y = width = height = 0;
							input = "";
							int i = 3;
														
							for (; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									start_x *= 10;
									start_x += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									start_y *= 10;
									start_y += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									end_x *= 10;
									end_x += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									end_y *= 10;
									end_y += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									width *= 10;
									width += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									height *= 10;
									height += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								input += recvMessage.charAt(i);
							}
							
							mainPain.sync_index++;
							mainPain.createSyncItem(17, R, G, B, stroke, thickness);
							mainPain.sync_itemList.get(mainPain.sync_index).x1 = start_x;
							mainPain.sync_itemList.get(mainPain.sync_index).y1 = start_y;
							mainPain.sync_itemList.get(mainPain.sync_index).x2 = end_x;
							mainPain.sync_itemList.get(mainPain.sync_index).y2 = end_y;
							mainPain.sync_itemList.get(mainPain.sync_index).width = width;
							mainPain.sync_itemList.get(mainPain.sync_index).height = height;
							mainPain.sync_itemList.get(mainPain.sync_index).s1 = input;
						}
						else if(recvMessage.substring(0, 3).equals("24:")){      //changeshape
							index = start_x = start_y = end_x = end_y = 0;
							input = "";
							int i = 3;
							
							for (; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									index *= 10;
									index += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}							
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									start_x *= 10;
									start_x += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									start_y *= 10;
									start_y += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									end_x *= 10;
									end_x += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							for (++i; i < recvMessage.length(); i++) {
								if (recvMessage.charAt(i) != ':') {
									end_y *= 10;
									end_y += (int) (recvMessage.charAt(i) - '0');
								} else
									break;
							}
							
							mainPain.sync_itemList.get(index).x1 = start_x;
							mainPain.sync_itemList.get(index).y1 = start_y;
							mainPain.sync_itemList.get(index).x2 = end_x;
							mainPain.sync_itemList.get(index).y2 = end_y;
						}
						else if(recvMessage.substring(0, 3).equals("25:")){      //repaint
							mainPain.drawingArea.repaint();
						}
					}
				}
			}catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				Object[] options = { "OK" };
				int s = JOptionPane.showOptionDialog(null, "Teacher has exited!", "Sync Information", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);								
				Sync_Shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public SyncControl_Stu(MyPaint m, String tea_addr){
		Sync_Switcher = 0;	
		mainPain = m;
		teacher_ipaddr = tea_addr;
		try{
			my_ipaddr = InetAddress.getLocalHost().getHostAddress();
		}catch(Exception e1){
       		System.out.println("cannot get my_ipaddr: "+e1);
       	}
	}

	public void Sync_Function_Stu(){
		mySyncStuThread = new Sync_Stu_Thread();
		mySyncStuThread.start();
		Sync_Switcher = 1;
	}
	
	public void Sync_Shutdown(){
		try {
			try {
				stu_to_tea_socket.close();
			} catch (Exception e1) {
				System.out.println("stu_to_tea_socket cannot be closed: " + e1);
			}
			mySyncStuThread.stop();
			Sync_Switcher = connect_to_teacher_flag = 0;
			mainPain.sync_status = "Sync Function: OFF";
		} catch (Exception e) {
			System.out.println("cannot showdown" + e);
		}
	}
	
}
