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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;


public class SyncControl_Tea extends JFrame{
	int Sync_Switcher;
	ServerSocket tea_listen_socket;
	Socket tea_send_socket;
	PrintWriter tea_send_os;
	ArrayList<Socket> stuSocket_List;
	ArrayList<PrintWriter> stuSocket_PrintWriter_List;
	ArrayList<String> stuIpaddr_List;
	MyPaint mainPain;
	Sync_Tea_Thread mySyncTeaThread;
	ExecutorService exec;
	int sync_count = 0;
	
	class Task implements Runnable{
		Socket tea_and_stu_socket;
		BufferedReader tea_recive_is;
		PrintWriter tea_send_os;
		int socketSwitcher = 0;   //turn off
		int index;
		String sendMess, recvMess, stu_ipaddr;
				
		public Task(Socket tea_s_socket) throws IOException{
			tea_and_stu_socket = tea_s_socket;
		}
		
		public void run() {
			try{
				tea_recive_is = new BufferedReader(new InputStreamReader(tea_and_stu_socket.getInputStream()));		
				tea_send_os = new PrintWriter(tea_send_socket.getOutputStream());	
				stuSocket_PrintWriter_List.add(tea_send_os);
				recvMess = tea_recive_is.readLine();
				if(recvMess.substring(0, 4).equals("@11:")){
					stu_ipaddr = "";
					for(int i = 4; i < recvMess.length(); i++){
						stu_ipaddr += recvMess.charAt(i);
					}
					stuIpaddr_List.add(stu_ipaddr);
					sendMess = "@12:y";			
					tea_send_os.println(sendMess);
					tea_send_os.flush();
					
					for(int i = 0; i <= mainPain.index; i++){
						Sync_Send_drawShape(i, mainPain.itemList.get(i).type, mainPain.itemList.get(i).x1, mainPain.itemList.get(i).y1, mainPain.itemList.get(i).x2, mainPain.itemList.get(i).y2, mainPain.itemList.get(i).R, mainPain.itemList.get(i).G, mainPain.itemList.get(i).B, (int)mainPain.itemList.get(i).stroke, mainPain.itemList.get(i).thickness);
						Sync_Send_Repaint();
					}
					
		
				}
				
				while(true){		
					if(tea_recive_is.read() < 0){
						tea_and_stu_socket.close();
						socketSwitcher = 1;
						stuSocket_List.remove(index);
						stuSocket_PrintWriter_List.remove(index);
						break;
					}
				}
			}catch(Exception e){
				System.out.println("Thread error: " + e);
			}
		}		
	}
		
	class Sync_Tea_Thread extends Thread {
		public void run(){		
			try {
				try {
					tea_listen_socket = new ServerSocket(8887);
					exec = Executors.newCachedThreadPool();
				} catch (Exception e) {
					System.out.println("cannot listen to:" + e);
				}

				while (true) {
					tea_send_socket = null;
					try {
						tea_send_socket = tea_listen_socket.accept(); 
						stuSocket_List.add(tea_send_socket);
						exec.execute(new Task(tea_send_socket));	
					} catch (Exception e) {
						System.out.println("Error." + e);
					}
					Thread.sleep(300);
				}
			} catch (Exception e) {
				System.out.println("Error." + e);
			}
		}
	}
	
	public SyncControl_Tea(MyPaint m){
		Sync_Switcher = 0;	
		mainPain = m;
		stuSocket_List = new ArrayList<Socket>(100);
		stuSocket_PrintWriter_List = new ArrayList<PrintWriter>(100);
		stuIpaddr_List = new ArrayList<String>(100);
	}

	public void Sync_Send_drawShape(int count, int choice, int start_x, int start_y, int end_x, int end_y, int r, int g, int b, int stroke, int thickness){
		String sendshape_message="", co="", ch="", sx="", sy="", ex="", ey="", R="", G="", B="", st="", th="";
		if(count == 0) co = "0";
		if(choice == 0) ch = "0";
		if(start_x == 0) sx = "0";
		if(start_y == 0) sy = "0";
		if(end_x == 0) ex = "0";
		if(end_y == 0) ey = "0";
		if(r == 0) R = "0";
		if(g == 0) G = "0";
		if(b == 0) B = "0";
		if(stroke == 0) st = "0";
		if(thickness == 0) th = "0";
		
		while(count != 0){		
			co = (char)((count % 10) + '0') + co; 
			count = count / 10;
		}
		while(choice != 0){		
			ch = (char)((choice % 10) + '0') + ch; 
			choice = choice / 10;
		}
		while(start_x != 0){		
			sx = (char)((start_x % 10) + '0') + sx; 
			start_x = start_x / 10;
		}
		while(start_y != 0){		
			sy = (char)((start_y % 10) + '0') + sy; 
			start_y = start_y / 10;
		}
		while(end_x != 0){		
			ex = (char)((end_x % 10) + '0') + ex; 
			end_x = end_x / 10;
		}
		while(end_y != 0){		
			ey = (char)((end_y % 10) + '0') + ey; 
			end_y = end_y / 10;
		}
		while(r != 0){		
			R = (char)((r % 10) + '0') + R; 
			r = r / 10;
		}
		while(g != 0){		
			G = (char)((g % 10) + '0') + G; 
			g = g / 10;
		}
		while(b != 0){		
			B = (char)((b % 10) + '0') + B; 
			b = b / 10;
		}
		while(stroke != 0){		
			st = (char)((stroke % 10) + '0') + st; 
			stroke = stroke / 10;
		}
		while(thickness != 0){		
			th = (char)((thickness % 10) + '0') + th; 
			thickness = thickness / 10;
		}
				
		sendshape_message = "";
		sendshape_message = "@21:" + co + ":" + ch + ":" + sx + ":" + sy + ":" + ex + ":" + ey + ":" + R + ":" + G + ":" + B + ":" + st + ":" + th;
		for(int i = 0; i < stuSocket_PrintWriter_List.size(); i++){
			stuSocket_PrintWriter_List.get(i).println(sendshape_message);
			stuSocket_PrintWriter_List.get(i).flush();
		}
	}
	
	public void Sync_Send_deleteShape(int index){
		String sendshape_message="", in="";
		while(index != 0){		
			in = (char)((index % 10) + '0') + in; 
			index = index / 10;
		}
				
		sendshape_message = "";
		sendshape_message = "@22:" + in;
		for(int i = 0; i < stuSocket_PrintWriter_List.size(); i++){
			stuSocket_PrintWriter_List.get(i).println(sendshape_message);
			stuSocket_PrintWriter_List.get(i).flush();
		}
	}
	
	public void Sync_Send_drawword(int start_x, int start_y, int end_x, int end_y, int width, int height, String input){
		String sendshape_message="", sx="", sy="", ex="", ey="", w="", h="";
		if(start_x == 0) sx = "0";
		if(start_y == 0) sy = "0";
		if(end_x == 0) ex = "0";
		if(end_y == 0) ey = "0";
		if(width == 0) w = "0";
		if(height == 0) h = "0";
		
		while(start_x != 0){		
			sx = (char)((start_x % 10) + '0') + sx; 
			start_x = start_x / 10;
		}
		while(start_y != 0){		
			sy = (char)((start_y % 10) + '0') + sy; 
			start_y = start_y / 10;
		}
		while(end_x != 0){		
			ex = (char)((end_x % 10) + '0') + ex; 
			end_x = end_x / 10;
		}
		while(end_y != 0){		
			ey = (char)((end_y % 10) + '0') + ey; 
			end_y = end_y / 10;
		}
		while(width != 0){		
			w = (char)((width % 10) + '0') + w; 
			width = width / 10;
		}
		while(height != 0){		
			h = (char)((height % 10) + '0') + h; 
			height = height / 10;
		}
		
		sendshape_message = "";
		sendshape_message = "@23:" + sx + ":" + sy + ":" + ex + ":" + ey + ":" + w + ":" + h + ":" + input;
		for(int i = 0; i < stuSocket_PrintWriter_List.size(); i++){
			stuSocket_PrintWriter_List.get(i).println(sendshape_message);
			stuSocket_PrintWriter_List.get(i).flush();
		}
		
	}

	public void Sync_Send_changeShape(int index, int start_x, int start_y, int end_x, int end_y){
		String sendshape_message="", in="", sx="", sy="", ex="", ey="";
		if(index == 0) in = "0";
		if(start_x == 0) sx = "0";
		if(start_y == 0) sy = "0";
		if(end_x == 0) ex = "0";
		if(end_x == 0) ey = "0";
			
		while(index != 0){		
			in = (char)((index % 10) + '0') + in; 
			index = index / 10;
		}
		while(start_x != 0){		
			sx = (char)((start_x % 10) + '0') + sx; 
			start_x = start_x / 10;
		}
		while(start_y != 0){		
			sy = (char)((start_y % 10) + '0') + sy; 
			start_y = start_y / 10;
		}
		while(end_x != 0){		
			ex = (char)((end_x % 10) + '0') + ex; 
			end_x = end_x / 10;
		}
		while(end_y != 0){		
			ey = (char)((end_y % 10) + '0') + ey; 
			end_y = end_y / 10;
		}

		sendshape_message = "";
		sendshape_message = "@24:" + in + ":" + sx + ":" + sy + ":" + ex + ":" + ey;
		for(int i = 0; i < stuSocket_PrintWriter_List.size(); i++){
			stuSocket_PrintWriter_List.get(i).println(sendshape_message);
			stuSocket_PrintWriter_List.get(i).flush();
		}
	}
	
	public void Sync_Send_Repaint(){
		String sendshape_message = "";
		sendshape_message = "@25:";
		for(int i = 0; i < stuSocket_PrintWriter_List.size(); i++){
			stuSocket_PrintWriter_List.get(i).println(sendshape_message);
			stuSocket_PrintWriter_List.get(i).flush();
		}
	}
	
	public void Sync_Function_Tea(){
		mySyncTeaThread = new Sync_Tea_Thread();
		mySyncTeaThread.start();
		Sync_Switcher = 1;
		
	}
}
