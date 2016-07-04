import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;


public class MainPaint{ 
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.
					getSystemLookAndFeelClassName());
	    }			
	    catch (Exception e){
	    }
		new startPanel();
	}
}

class MyPaint extends JFrame{
	JMenuBar jmenuBar;
	ObjectInputStream  input;
	ObjectOutputStream output; 
	private JButton choices[];         
	private String names[]={
			"Select",       
			"Undo",
			"Redo",
			"Pencil",		
			"Line",			
			"Rect",			
			"fRect",		
			"Oval",			
			"fOval",		
			"Circle",		
			"fCircle",		
			"RoundRect",	
			"fRect",		
			"3DRect",		
			"f3DRect",		
			"Cube",			
			"Eraser",		
			"bgColor",		
			"Color",		
			"Stroke",		
			"Word"			
		};
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	String styleNames[] = ge.getAvailableFontFamilyNames();  
	private Icon items[];
	private String tipText[]={"Select",  "Undo", "Redo", "Pencil","Line","Hollow Rectangle",
			"Filled Rectangle","Hollow Oval","Filled Oval","Circle","Filled Circle","Hollow RoundRectangle",
			"Filled RoundRectangle","3D Rectangle","Filled 3D Rectangle","3D Cuboid","Eraser", "Setting Backgound Color","Brush Color",
			"Brush Thickness","Adding Text"};			
	private JToolBar buttonPanel;			
	private JToolBar simpleButtonPanel;
	private JLabel statusBar;				
	DrawPanel drawingArea;			
	
	int index = -1;							
	int start_index = -1;
	ArrayList<drawings> itemList = new ArrayList<drawings>(2000);	
	int sync_index = -1;
	ArrayList<drawings> sync_itemList = new ArrayList<drawings>(2000);
	private selectItems selectitem;
	private UndoManager myUndomanager;
	private int currentChoice = 3;			
	private int previousChoice = -1;
	private Color color=Color.black;		
	int R,G,B;								
	int f1,f2;								
	int copy_count = 0;
	String style1;							
	private float stroke = 2.0f;		   
	static int thickness = 10;				
	JCheckBox bold,italic;					     
	JComboBox styles;						
	int screenWidth;
	int screenHeight;
	int noteWidth;
    int noteHeight;
    char userMode; 
	private HandWrite myHandWrite;
	private Modifier myModifier;
	private SyncControl_Tea mySyncControllor_tea;
	private SyncControl_Stu mySyncControllor_stu;
	private savetoJPG_PDF myJPG_PDF;
	private questionRaise_Stu myQuestionRaise_stu;
	private questionRaise_Tea myQuestionRaise_tea;
	String teacher_ipaddr;
	String sync_status = "Sync Function: OFF";
	String handwrite_status = "Handwrite Function: OFF";
	String questionraise_status = "QuestionRaise Function: OFF";
	
	public MyPaint(String tea_ipaddr, char mode){
		userMode = mode;
		if(userMode == 's'){
			teacher_ipaddr = tea_ipaddr;
		}
		else if(userMode == 't'){
			try{
				teacher_ipaddr = InetAddress.getLocalHost().getHostAddress();
			}catch(Exception e1){
	       		System.out.println("cannot get local ipaddr: "+e1);
	       	}
		}
		Toolkit kit = Toolkit.getDefaultToolkit();              
        Dimension screenSize = kit.getScreenSize();             
        screenWidth = screenSize.width;                     
        screenHeight = screenSize.height;
        noteWidth = 1000;
        noteHeight = 650;
        
        this.addKeyListener(new KeyAdapter(){    
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_DELETE){
					for(int i = selectitem.selectIndex; i >= 0; i--){
						for(int j = myUndomanager.drawList.get(i).startPos; j <= myUndomanager.drawList.get(i).endPos; j++){
							itemList.remove(myUndomanager.drawList.get(i).startPos);
							index--;
						}
						myUndomanager.drawList.remove(selectitem.selectitemList[i]);
						myUndomanager.draw_count--;
						for(int j = i; j < selectitem.selectIndex; j++){
							selectitem.selectitemList[j] = selectitem.selectitemList[j+1];
						}
						selectitem.selectitemList[selectitem.selectIndex] = 0;
						selectitem.selectIndex--;
					}
					drawingArea.repaint();
				}
		    }  
		});
        
        this.addComponentListener(new ComponentAdapter(){
        	public void componentResized(ComponentEvent e){
        		noteWidth = (int) e.getComponent().getSize().getWidth();
        		noteHeight = (int) e.getComponent().getSize().getHeight();
        	}
        });
        
        if(userMode == 't'){
        	setTitle("SmartNote (My ip address: " + teacher_ipaddr + ")");
    		mySyncControllor_tea = new SyncControl_Tea(this);
        }
        else{
        	setTitle("SmartNote (Teacher's ip address: " + teacher_ipaddr + ")");
        	mySyncControllor_stu = new SyncControl_Stu(this, teacher_ipaddr);
        }      
		setLocation(0,  0);
		setSize(noteWidth,noteHeight);					
		setVisible(true);
		setCursor(new Cursor(Cursor.HAND_CURSOR));		
		drawingArea = new DrawPanel();
		myUndomanager = new UndoManager();
		myHandWrite = new HandWrite();
		myModifier = new Modifier();
		myJPG_PDF = new savetoJPG_PDF(this);
		
		if(userMode == 's')
			myQuestionRaise_stu = new questionRaise_Stu(this, teacher_ipaddr);
		else if(userMode == 't')
			myQuestionRaise_tea = new questionRaise_Tea(this);
				
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {			
				Object[] options = { " No(N) ", " Yes(Y) " };
				int s = JOptionPane.showOptionDialog(null, "Are you sure to exit？", "Exit Information", JOptionPane.DEFAULT_OPTION,
						JOptionPane.WARNING_MESSAGE, null, options, options[1]);
				switch (s) {
				case 1:
					if(userMode == 's'){
						if(myQuestionRaise_stu.questionraiseSwitch == 1)
							myQuestionRaise_stu.QuestionRaise_Shutdown();
						if(mySyncControllor_stu.Sync_Switcher == 1)
							mySyncControllor_stu.Sync_Shutdown();
					}
					System.exit(0);	
				}
			}
		});					
		
		getJMenuBar();      
		items=new ImageIcon[names.length];
		
		
		choices=new JButton[names.length];
		buttonPanel = new JToolBar( JToolBar.VERTICAL);
		buttonPanel = new JToolBar( JToolBar.HORIZONTAL);
		simpleButtonPanel = new JToolBar(JToolBar.VERTICAL);
		ButtonHandler handler=new ButtonHandler();
		ButtonHandler1 handler1=new ButtonHandler1();
		ButtonHandler_Select handler_select= new ButtonHandler_Select();
		ButtonHandler_Undo handler_undo = new ButtonHandler_Undo();
		ButtonHandler_Redo handler_redo = new ButtonHandler_Redo();
		selectitem = new selectItems();
		buttonPanel.setBackground(new Color(255,255,255));	 
		buttonPanel.setVisible(false);
		simpleButtonPanel.setVisible(true);	

		
		for(int i = 0; i < choices.length; i++){
			choices[i] = new JButton(names[i]);
			buttonPanel.add(choices[i]);
		}
		
		ToolMenu(); 		 
		
		
		for(int i = 3; i < choices.length-4; i++){
			choices[i].addActionListener(handler);
		}
		choices[0].addActionListener(handler_select);
		choices[1].addActionListener(handler_undo);
		choices[2].addActionListener(handler_redo);
		choices[choices.length-4].addActionListener(handler1); 
		choices[choices.length-3].addActionListener(handler1); 
		choices[choices.length-2].addActionListener(handler1); 
		choices[choices.length-1].addActionListener(handler1); 
		
		
		styles=new JComboBox(styleNames);
		styles.setMaximumRowCount(10);
		styles.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				style1=styleNames[styles.getSelectedIndex()];
			}
		});
		
		
		bold=new JCheckBox("Overstriking");
		italic=new JCheckBox("Tilt");
		checkBoxHandler cHandler=new checkBoxHandler();
		bold.addItemListener(cHandler);
		italic.addItemListener(cHandler);
		bold.setBackground(new Color(0,255,0));
		italic.setBackground(new Color(0,255,0));
		buttonPanel.setLayout(new GridLayout(2, names.length));
		buttonPanel.add(bold);
		buttonPanel.add(italic);
		buttonPanel.addSeparator();
		buttonPanel.add(new JLabel("Font:"));
		buttonPanel.add(styles);
		buttonPanel.setFloatable(false);
		styles.setMinimumSize(new Dimension(100,20));		
		styles.setMaximumSize(new Dimension(120,20));
		Container c = getContentPane();
		c.add(buttonPanel,BorderLayout.NORTH);
		
		simpleButtonPanel.setLayout(new GridLayout(8, 1));
		JButton def_pencil = new JButton("Pencil");
		JButton def_eraser = new JButton("Eraser");
		JButton def_undo = new JButton("Undo");
		JButton def_redo = new JButton("Redo");
		JButton def_select = new JButton("Select");
		JButton def_modify = new JButton("Modify");
		JButton def_copy = new JButton("Copy");
		JButton def_delete = new JButton("Delete");
		simpleButtonPanel.add(def_pencil);
		simpleButtonPanel.add(def_eraser);
		simpleButtonPanel.add(def_undo);
		simpleButtonPanel.add(def_redo);
		simpleButtonPanel.add(def_select);
		simpleButtonPanel.add(def_modify);
		simpleButtonPanel.add(def_copy);
		simpleButtonPanel.add(def_delete);
		
		def_pencil.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {                                            
					drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
					previousChoice = currentChoice = 3;
				} catch (CannotRedoException ex) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Cannot use pencil！","Edit Information",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		def_eraser.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {                                            
					if(myHandWrite.handwrite_switcher == 0){
						drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
						previousChoice = currentChoice = 16;
					}
					else{
						Object[] options = { "OK"};
						int s = JOptionPane.showOptionDialog(null, "Eraser cannot be used when handwrite function is working. Please turn off the handwrite function before using eraser, thanks", "Handwrite Information", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
					}
				} catch (CannotRedoException ex) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Cannot use eraser！","Edit Information",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		
		def_undo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {                                            
					drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					if(index >= 0){
						myUndomanager.undoOperation(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, myUndomanager.drawList.get(myUndomanager.draw_count).type, itemList);	
						for(int i = myUndomanager.drawList.get(myUndomanager.draw_count).startPos; i <= myUndomanager.drawList.get(myUndomanager.draw_count).endPos; i++){							
							if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
								mySyncControllor_tea.Sync_Send_deleteShape(itemList.size()-1);
								mySyncControllor_tea.sync_count--;
							}
							itemList.remove(itemList.size()-1);	
						}
						if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
							mySyncControllor_tea.Sync_Send_Repaint();
						}
						drawingArea.repaint();
						index = myUndomanager.drawList.get(myUndomanager.draw_count).startPos-1;
						myUndomanager.drawList.remove(myUndomanager.draw_count);
						myUndomanager.draw_count--;
					}
				} catch (CannotUndoException ex) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Cannot undo！","Edit Information",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		
		def_redo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {                                            
					drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					if(myUndomanager.redo_count >= 0){
						if(myUndomanager.write_flag == 0){
							myUndomanager.drawList.add(myUndomanager.redoList.get(myUndomanager.redo_count));
							myUndomanager.draw_count++;
							for(int i = myUndomanager.redoList.get(myUndomanager.redo_count).startPos; i <= myUndomanager.redoList.get(myUndomanager.redo_count).endPos; i++){
								index++;
								currentChoice = myUndomanager.redoList.get(myUndomanager.redo_count).currentChoice;
								createNewItem(currentChoice);
								
								if(currentChoice != 17 && userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
									mySyncControllor_tea.Sync_Send_drawShape(
											i, currentChoice,
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).x1,
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).y1,
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).x2,
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).y2,
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).R,
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).G,
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).B,
											(int) myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).stroke,
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).thickness);
									mySyncControllor_tea.sync_count++;
								}
								else if(currentChoice == 17 && userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
									mySyncControllor_tea.Sync_Send_drawword(
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).x1, 
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).y1,
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).x2, 
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).y2, 
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).width,
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).height, 
											myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).s1);
									mySyncControllor_tea.sync_count++;
								}
							}
							myUndomanager.redoOperation(itemList);
							if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
								mySyncControllor_tea.Sync_Send_Repaint();
							}
							repaint();
						}
						else
							myUndomanager.redoOperation(itemList);
					}						
				} catch (CannotRedoException ex) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Cannot redo！","Edit Information",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		
		def_select.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {                                            
					drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					currentChoice = 0;
				} catch (CannotRedoException ex) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Cannot select！","Edit Information",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		
		def_modify.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					previousChoice = currentChoice;
					currentChoice = -1;
					myModifier.modify_switcher = 1;
				} catch (CannotRedoException ex) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Cannot modify！","Edit Information",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		
		def_copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				copy_count++;
				int temp_start_index = 0;
				for (int i = 0; i <= selectitem.selectIndex; i++) {
					temp_start_index = index + 1;
					selectitem.Check_max_min_border(myUndomanager.drawList, itemList);
					
					if (myUndomanager.drawList.get(selectitem.selectitemList[i]).currentChoice != 17) {
						for (int j = myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos; j <= myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos; j++) {
							index++;
							createNewItem(myUndomanager.drawList.get(selectitem.selectitemList[i]).currentChoice);
							itemList.get(index).x1 = itemList.get(j).x1 + selectitem.max_width * copy_count;
							itemList.get(index).y1 = itemList.get(j).y1 + selectitem.max_height * copy_count;
							itemList.get(index).x2 = itemList.get(j).x2 + selectitem.max_width * copy_count;
							itemList.get(index).y2 = itemList.get(j).y2 + selectitem.max_height * copy_count;
							itemList.get(index).R = itemList.get(j).R_backup;
							itemList.get(index).G = itemList.get(j).G;
							itemList.get(index).B = itemList.get(j).B;
							itemList.get(index).stroke = itemList.get(j).stroke;
							itemList.get(index).type = itemList.get(j).type;
							itemList.get(index).thickness = itemList.get(j).thickness;
							itemList.get(index).shapeType = itemList.get(j).shapeType;	
						}		
						
						myUndomanager.drawList.add(new drawing_info());
						myUndomanager.draw_count++;
						myUndomanager.drawList.get(myUndomanager.draw_count).startPos = temp_start_index;
						myUndomanager.drawList.get(myUndomanager.draw_count).endPos = temp_start_index + myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos - myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos;
						myUndomanager.drawList.get(myUndomanager.draw_count).type = myUndomanager.drawList.get(selectitem.selectitemList[i]).currentChoice;
						myUndomanager.drawList.get(myUndomanager.draw_count).copy_drawings(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, myUndomanager.drawList.get(myUndomanager.draw_count).type, itemList);									
						myUndomanager.write_flag = 1;
					}
					else{
						index++;
						createNewItem(myUndomanager.drawList.get(selectitem.selectitemList[i]).currentChoice);
						itemList.get(index).x1 = itemList.get(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).x1 + selectitem.max_width * copy_count;
						itemList.get(index).y1 = itemList.get(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).y1 + selectitem.max_height * copy_count;
						itemList.get(index).x2 = itemList.get(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).x2 + selectitem.max_width * copy_count;
						itemList.get(index).y2 = itemList.get(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).y2 + selectitem.max_height * copy_count;
						itemList.get(index).R = itemList.get(selectitem.selectitemList[i]).R_backup;
						itemList.get(index).G = itemList.get(selectitem.selectitemList[i]).G;
						itemList.get(index).B = itemList.get(selectitem.selectitemList[i]).B;
						itemList.get(index).stroke = itemList.get(selectitem.selectitemList[i]).stroke;
						itemList.get(index).type = itemList.get(selectitem.selectitemList[i]).type;
						itemList.get(index).thickness = itemList.get(selectitem.selectitemList[i]).thickness;
						itemList.get(index).shapeType = itemList.get(selectitem.selectitemList[i]).shapeType;
						itemList.get(index).s1 = itemList.get(selectitem.selectitemList[i]).s1;
	                    itemList.get(index).x2 = itemList.get(selectitem.selectitemList[i]).x2;
	                    itemList.get(index).y2 = itemList.get(selectitem.selectitemList[i]).y2;
	                    itemList.get(index).width = itemList.get(selectitem.selectitemList[i]).width;
	                    itemList.get(index).height = itemList.get(selectitem.selectitemList[i]).height;
	                    itemList.get(index).s2 = itemList.get(selectitem.selectitemList[i]).s2;
	                    myUndomanager.drawList.add(new drawing_info());
						myUndomanager.draw_count++;
						myUndomanager.drawList.get(myUndomanager.draw_count).startPos = index;
						myUndomanager.drawList.get(myUndomanager.draw_count).endPos = index;
						myUndomanager.drawList.get(myUndomanager.draw_count).type = myUndomanager.drawList.get(selectitem.selectitemList[i]).currentChoice;
						myUndomanager.drawList.get(myUndomanager.draw_count).copy_drawings(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, myUndomanager.drawList.get(myUndomanager.draw_count).type, itemList);												
					}
				}
				drawingArea.repaint();
                
				if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
					for( ; mySyncControllor_tea.sync_count <= index; mySyncControllor_tea.sync_count++){
						mySyncControllor_tea.Sync_Send_drawShape(mySyncControllor_tea.sync_count, itemList.get(mySyncControllor_tea.sync_count).type, itemList.get(mySyncControllor_tea.sync_count).x1, itemList.get(mySyncControllor_tea.sync_count).y1, itemList.get(mySyncControllor_tea.sync_count).x2, itemList.get(mySyncControllor_tea.sync_count).y2, itemList.get(mySyncControllor_tea.sync_count).R, itemList.get(mySyncControllor_tea.sync_count).G, itemList.get(mySyncControllor_tea.sync_count).B, (int)itemList.get(mySyncControllor_tea.sync_count).stroke, itemList.get(mySyncControllor_tea.sync_count).thickness);
					}
					mySyncControllor_tea.Sync_Send_Repaint();
				}
			}
		});	
		
		
		def_delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {                                           
					for(int i = selectitem.selectIndex; i >= 0; i--){																				
						for(int j = myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos; j <= myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos; j++){
							if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
								mySyncControllor_tea.Sync_Send_deleteShape(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos);
								mySyncControllor_tea.sync_count--;
							}
							itemList.remove(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos);
							index--;
						}
						int delete_count = myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos - myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos + 1;
						myUndomanager.drawList.remove(selectitem.selectitemList[i]);
						myUndomanager.draw_count--;
						for(int j = selectitem.selectitemList[i]; j < myUndomanager.drawList.size(); j++){
							myUndomanager.drawList.get(j).startPos -= delete_count;
							myUndomanager.drawList.get(j).endPos -= delete_count;
						}
						
						for(int j = i; j < selectitem.selectIndex; j++){
							selectitem.selectitemList[j] = selectitem.selectitemList[j+1];
						}
						selectitem.selectitemList[selectitem.selectIndex] = 0;
						selectitem.selectIndex--;
					}
					drawingArea.repaint();
					if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
						mySyncControllor_tea.Sync_Send_Repaint();
					}
				} catch (CannotRedoException ex) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Cannot delete！","Edit Information",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		c.add(simpleButtonPanel,BorderLayout.WEST);
		c.add(drawingArea,BorderLayout.CENTER);
		statusBar=new JLabel();
		c.add(statusBar,BorderLayout.SOUTH);
		setSize(noteWidth,noteHeight);
		setVisible(true);

	}
	
	
	public class ButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){
			for(int j = 3; j < choices.length-4; j++){
				if(e.getSource() == choices[j]){
					if(j >= 3) {previousChoice = currentChoice;}
					currentChoice = j;
				}
			}
		}
	}
	
	
	public class ButtonHandler1 implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(e.getSource()==choices[choices.length-4]){
				SetbgColor();
			}
			if(e.getSource()==choices[choices.length-3]){
				chooseColor();
			}
			if(e.getSource()==choices[choices.length-2]){
				setStroke();
			}
			if(e.getSource()==choices[choices.length-1]){				
				Object[] options = { " OK " };
				int s = JOptionPane.showOptionDialog(null, "Add the text in the position where mouse clicks",
						"Add Text", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				currentChoice = 17;
			}
		}
	}
	
	public class ButtonHandler_Select implements ActionListener{
		public void actionPerformed(ActionEvent e){
			currentChoice = 0;
			drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public class ButtonHandler_Undo implements ActionListener{
		public void actionPerformed(ActionEvent e){
			drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			if(index >= 0){
				myUndomanager.undoOperation(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, myUndomanager.drawList.get(myUndomanager.draw_count).type, itemList);	
				for(int i = myUndomanager.drawList.get(myUndomanager.draw_count).startPos; i <= myUndomanager.drawList.get(myUndomanager.draw_count).endPos; i++){
					if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
						mySyncControllor_tea.Sync_Send_deleteShape(itemList.size()-1);
						mySyncControllor_tea.sync_count--;
					}
					itemList.remove(itemList.size()-1);	
				}
				if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
					mySyncControllor_tea.Sync_Send_Repaint();
				}
				drawingArea.repaint();
				index = myUndomanager.drawList.get(myUndomanager.draw_count).startPos-1;
				myUndomanager.drawList.remove(myUndomanager.draw_count--);
			}
		}
	}
	
	public class ButtonHandler_Redo implements ActionListener{
		public void actionPerformed(ActionEvent e){
			drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			if(myUndomanager.redo_count >= 0){
				if(myUndomanager.write_flag == 0){
					myUndomanager.drawList.add(myUndomanager.redoList.get(myUndomanager.redo_count));
					myUndomanager.draw_count++;
					for(int i = myUndomanager.redoList.get(myUndomanager.redo_count).startPos; i <= myUndomanager.redoList.get(myUndomanager.redo_count).endPos; i++){
						index++;
						currentChoice = myUndomanager.redoList.get(myUndomanager.redo_count).currentChoice;
						createNewItem(currentChoice);
						
						if(currentChoice != 17 && userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
							mySyncControllor_tea.Sync_Send_drawShape(
									i, currentChoice,
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).x1,
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).y1,
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).x2,
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).y2,
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).R,
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).G,
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).B,
									(int) myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).stroke,
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).thickness);
							mySyncControllor_tea.sync_count++;
						}
						else if(currentChoice == 17 && userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
							mySyncControllor_tea.Sync_Send_drawword(
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).x1, 
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).y1,
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).x2, 
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).y2, 
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).width,
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).height, 
									myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).s1);
							mySyncControllor_tea.sync_count++;
						}
					}
					myUndomanager.redoOperation(itemList);
					if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
						mySyncControllor_tea.Sync_Send_Repaint();
					}					
					repaint();
				}
				else
					myUndomanager.redoOperation(itemList);
			}
		}
	}
	
	
	class mouseA extends MouseAdapter{
		public void mousePressed(MouseEvent e){
			statusBar.setText("Mouse clicks:["+e.getX()+","+e.getY()+"], " + sync_status + ", " + handwrite_status + ", " + questionraise_status);	//设置状态提示
			
			if(currentChoice >= 3){
				start_index = index;
				index++;
				createNewItem(currentChoice);
				itemList.get(index).x1=itemList.get(index).x2=e.getX();
				itemList.get(index).y1=itemList.get(index).y2=e.getY();
				myUndomanager.drawList.add(new drawing_info());
				myUndomanager.draw_count++;
				myUndomanager.drawList.get(myUndomanager.draw_count).startPos = index;
				myUndomanager.drawList.get(myUndomanager.draw_count).endPos = index;
				myUndomanager.drawList.get(myUndomanager.draw_count).type = currentChoice;
				myUndomanager.write_flag = 1;
				
				if(myHandWrite.handwrite_switcher == 1){
					myHandWrite.Add_to_pointList(itemList.get(index).x1, itemList.get(index).y1, index);
					myHandWrite.turn_point_list.get(0).index = index;
				}
				
				
				if(currentChoice == 3 || currentChoice == 16){
					index++;
					createNewItem(currentChoice);
					itemList.get(index).x1=itemList.get(index).x2=e.getX();
					itemList.get(index).y1=itemList.get(index).y2=e.getY();
				}
				
				if(currentChoice == 17){
					itemList.get(index).x1=e.getX();
					itemList.get(index).y1=e.getY();
					String input;
					input=JOptionPane.showInputDialog("Input the content: ");
					
                    if(input != null){
                        itemList.get(index).s1=input;
                        itemList.get(index).x2=f1;
                        itemList.get(index).y2=f2;
                        itemList.get(index).width = input.length()*10;
                        itemList.get(index).height = 20;
                        itemList.get(index).s2=style1;
                        myUndomanager.drawList.get(myUndomanager.draw_count).copy_drawings(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, currentChoice, itemList);
                        
                        if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
    						for( ; mySyncControllor_tea.sync_count <= index; mySyncControllor_tea.sync_count++){
    							mySyncControllor_tea.Sync_Send_drawword(itemList.get(index).x1, itemList.get(index).y1, itemList.get(index).x2, itemList.get(index).y2, itemList.get(index).width, itemList.get(index).height, itemList.get(index).s1);
    						}
    						mySyncControllor_tea.Sync_Send_Repaint();
    					}                       
                        drawingArea.repaint();
                    }
				}
			}
			
			else if(currentChoice == 0){
				if(selectitem.select_flag == 0){          
					selectitem.selectRange_x1 = selectitem.selectRange_x2 = e.getX();
					selectitem.selectRange_y1 = selectitem.selectRange_y2 = e.getY();
				}
				else if(selectitem.select_flag == 1){     
					int press_pointX = e.getX();
					int press_pointY = e.getY();
										
					for(int i = 0; i <= selectitem.selectIndex; i++){
						if(myUndomanager.drawList.get(selectitem.selectitemList[i]).max_x >= press_pointX && myUndomanager.drawList.get(selectitem.selectitemList[i]).min_x <= press_pointX && myUndomanager.drawList.get(selectitem.selectitemList[i]).max_y >= press_pointY && myUndomanager.drawList.get(selectitem.selectitemList[i]).min_y <= press_pointY){
							selectitem.move_x1 = selectitem.move_x2 = press_pointX;
							selectitem.move_y1 = selectitem.move_y2 = press_pointY;
							selectitem.move_flag = 1;
							break;
						}
					}

					if(selectitem.move_flag == 0){       
						selectitem.select_flag = 0;
						selectitem.move_x1 = selectitem.move_x2 = selectitem.move_y1 = selectitem.move_y2 = 0;
						selectitem.move_height = selectitem.move_width = 0;
						selectitem.selectRange_x1 = selectitem.selectRange_x2 = e.getX();
						selectitem.selectRange_y1 = selectitem.selectRange_y2 = e.getY();
						copy_count = 0;
						
						for(int i = 0; i <= selectitem.selectIndex; i++){
							for(int j = myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos; j <= myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos; j++){
								itemList.get(j).R = itemList.get(j).R_backup;
							}
							selectitem.selectitemList[i] = 0;
						}
						repaint();
						selectitem.selectIndex = -1;
					}
					selectitem.move_flag = 0;
				}
			}
			
			else if(myModifier.modify_switcher == 1){
				int press_pointX = e.getX();
				int press_pointY = e.getY();
				for(int i = 0; i < myUndomanager.drawList.size(); i++){
					int stop_flag = 0;
					if(myUndomanager.drawList.get(i).currentChoice == 3){
						for(int j = myUndomanager.drawList.get(i).startPos; j <= myUndomanager.drawList.get(i).endPos; j++){
							itemList.get(j).set_max_min_value();
							if(itemList.get(j).max_x_drawings >= press_pointX && itemList.get(j).min_x_drawings <= press_pointX && itemList.get(j).max_y_drawings >= press_pointY && itemList.get(j).min_y_drawings <= press_pointY){
								Object[] options = { " OK " };
								int s = JOptionPane.showOptionDialog(null, "This curve cannot be modified!", "Modification Information", JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE, null, options, options[0]);
								myModifier.clear_modifier();
								stop_flag = 1;
								break;
							}							
						}
						if(stop_flag == 1) break;
					}
					else if(myUndomanager.drawList.get(i).currentChoice == 17){
						myUndomanager.drawList.get(i).item.get(0).set_max_min_value();
						if(myUndomanager.drawList.get(i).item.get(0).max_x_drawings >= press_pointX && myUndomanager.drawList.get(i).item.get(0).min_x_drawings <= press_pointX && myUndomanager.drawList.get(i).item.get(0).max_y_drawings >= press_pointY && myUndomanager.drawList.get(i).item.get(0).min_y_drawings <= press_pointY){
							Object[] options = { " OK " };
							int s = JOptionPane.showOptionDialog(null, "This word cannot be modified!", "Modification Information", JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE, null, options, options[0]);
							myModifier.clear_modifier();
							break;
						}		
					}
					else{
						myUndomanager.drawList.get(i).item.get(0).set_max_min_value();
						if(myUndomanager.drawList.get(i).item.get(0).max_x_drawings >= press_pointX && myUndomanager.drawList.get(i).item.get(0).min_x_drawings <= press_pointX && myUndomanager.drawList.get(i).item.get(0).max_y_drawings >= press_pointY && myUndomanager.drawList.get(i).item.get(0).min_y_drawings <= press_pointY){
							itemList.get(myUndomanager.drawList.get(i).startPos).set_max_min_value();
							myModifier.modify_flag = 1;
							myModifier.modify_switcher = 1;
							myModifier.press_x = press_pointX;
							myModifier.press_y = press_pointY;
							myModifier.start_x1 = itemList.get(myUndomanager.drawList.get(i).startPos).x1;
							myModifier.start_y1 = itemList.get(myUndomanager.drawList.get(i).startPos).y1;
							myModifier.start_x2 = itemList.get(myUndomanager.drawList.get(i).startPos).x2;
							myModifier.start_y2 = itemList.get(myUndomanager.drawList.get(i).startPos).y2;
							myModifier.index = i;
							break;
						}
					}
				}
			}			
		}
		
		public void mouseReleased(MouseEvent e){
			
			if(currentChoice >= 3){
				statusBar.setText("Mouse clicks:["+e.getX()+","+e.getY()+"], " + sync_status + ", " + handwrite_status + ", " + questionraise_status);	//设置状态提示
				
				if(myHandWrite.handwrite_switcher == 0){
					if(currentChoice == 3 || currentChoice == 16){
						index++;
						createNewItem(currentChoice);
						itemList.get(index).x1=e.getX();
						itemList.get(index).y1=e.getY();
						myUndomanager.drawList.get(myUndomanager.draw_count).endPos = index;
					}    
					itemList.get(index).x2=e.getX();
					itemList.get(index).y2=e.getY();
					repaint();
					myUndomanager.drawList.get(myUndomanager.draw_count).copy_drawings(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, currentChoice, itemList);				
					
					if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
						for( ; mySyncControllor_tea.sync_count <= index; mySyncControllor_tea.sync_count++){
							mySyncControllor_tea.Sync_Send_drawShape(mySyncControllor_tea.sync_count, currentChoice, itemList.get(mySyncControllor_tea.sync_count).x1, itemList.get(mySyncControllor_tea.sync_count).y1, itemList.get(mySyncControllor_tea.sync_count).x2, itemList.get(mySyncControllor_tea.sync_count).y2, itemList.get(mySyncControllor_tea.sync_count).R, itemList.get(mySyncControllor_tea.sync_count).G, itemList.get(mySyncControllor_tea.sync_count).B, (int)itemList.get(mySyncControllor_tea.sync_count).stroke, itemList.get(mySyncControllor_tea.sync_count).thickness);
						}
						mySyncControllor_tea.Sync_Send_Repaint();
					}
				}
				
				else{
					myHandWrite.TurnPointRecognization();				
					myHandWrite.Add_to_pointList(e.getX(), e.getY(), index);
					int temp_index = index;
					for(int i = start_index+1; i <= temp_index; i++){
						itemList.remove(itemList.size()-1);
					}
					index = myHandWrite.turn_point_list.get(0).index-1;
					drawingArea.repaint();
					myHandWrite.ShapeRecognization();
//					
					if(myHandWrite.shape_flag == 'l' && myHandWrite.line_flag == 1){	   //line
						currentChoice = 4;
						index++;
						createNewItem(currentChoice);
						itemList.get(index).x1 = myHandWrite.turn_point_list.get(0).x;
						itemList.get(index).y1 = myHandWrite.turn_point_list.get(0).y;
						itemList.get(index).x2 = myHandWrite.turn_point_list.get(myHandWrite.turn_point_count).x;
						itemList.get(index).y2 = myHandWrite.turn_point_list.get(myHandWrite.turn_point_count).y;
						drawingArea.repaint();
						if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
							mySyncControllor_tea.Sync_Send_drawShape(index, currentChoice, itemList.get(index).x1, itemList.get(index).y1, itemList.get(index).x2, itemList.get(index).y2, itemList.get(index).R, itemList.get(index).G, itemList.get(index).B, (int)itemList.get(index).stroke, itemList.get(index).thickness);
							mySyncControllor_tea.Sync_Send_Repaint();
							mySyncControllor_tea.sync_count++;
						}
						myUndomanager.drawList.get(myUndomanager.draw_count).startPos = myUndomanager.drawList.get(myUndomanager.draw_count).endPos = index;
						myUndomanager.drawList.get(myUndomanager.draw_count).copy_drawings(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, currentChoice, itemList);											
					}
					
					else if(myHandWrite.shape_flag == 'c'){    //circle
						index++;
						currentChoice = 9;
						createNewItem(currentChoice);						
						index = myHandWrite.turn_point_list.get(0).index;
						itemList.get(index).x1 = myHandWrite.turn_point_list.get(0).x;
						itemList.get(index).y1 = myHandWrite.turn_point_list.get(0).y;
						itemList.get(index).x2 = myHandWrite.max_x_for_circle;
						itemList.get(index).y2 = myHandWrite.max_y_for_circle;	
						drawingArea.repaint();
						if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
							mySyncControllor_tea.Sync_Send_drawShape(index, currentChoice, itemList.get(index).x1, itemList.get(index).y1, itemList.get(index).x2, itemList.get(index).y2, itemList.get(index).R, itemList.get(index).G, itemList.get(index).B, (int)itemList.get(index).stroke, itemList.get(index).thickness);
							mySyncControllor_tea.Sync_Send_Repaint();
							mySyncControllor_tea.sync_count++;
						}
						myUndomanager.drawList.get(myUndomanager.draw_count).startPos = myUndomanager.drawList.get(myUndomanager.draw_count).endPos = index;
						myUndomanager.drawList.get(myUndomanager.draw_count).copy_drawings(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, currentChoice, itemList);											
					}
					
					else if(myHandWrite.shape_flag == 'r'){     //rectangle
						index++;
						currentChoice = 5;
						createNewItem(currentChoice);						
						index = myHandWrite.turn_point_list.get(0).index;
						itemList.get(index).x1 = myHandWrite.turn_point_list.get(0).x;
						itemList.get(index).y1 = myHandWrite.turn_point_list.get(0).y;
						itemList.get(index).x2 = myHandWrite.turn_point_list.get(2).x;
						itemList.get(index).y2 = myHandWrite.turn_point_list.get(2).y;	
						drawingArea.repaint();
						if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
							mySyncControllor_tea.Sync_Send_drawShape(index, currentChoice, itemList.get(index).x1, itemList.get(index).y1, itemList.get(index).x2, itemList.get(index).y2, itemList.get(index).R, itemList.get(index).G, itemList.get(index).B, (int)itemList.get(index).stroke, itemList.get(index).thickness);
							mySyncControllor_tea.Sync_Send_Repaint();
							mySyncControllor_tea.sync_count++;
						}
						myUndomanager.drawList.get(myUndomanager.draw_count).startPos = myUndomanager.drawList.get(myUndomanager.draw_count).endPos = index;
						myUndomanager.drawList.get(myUndomanager.draw_count).copy_drawings(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, currentChoice, itemList);											
					}
					
					if(myHandWrite.shape_flag != 'l' && myHandWrite.shape_flag != 'c' && myHandWrite.shape_flag != 'r'){
						myUndomanager.drawList.remove(myUndomanager.draw_count);
						myUndomanager.draw_count--;
					}
					myHandWrite.Handwrite_Reset();
					currentChoice = 3;				
				}
			}
			
			else if(currentChoice == 0){
				if(selectitem.select_flag == 0){
					int temp_max_x, temp_max_y, temp_min_x, temp_min_y;
					Graphics g = drawingArea.getGraphics();
					Graphics2D g2d = (Graphics2D)g;
					g2d.setXORMode(Color.WHITE);
					selectitem.draw(g2d);
					selectitem.selectRange_x2 = e.getX();
					selectitem.selectRange_y2 = e.getY();
					g2d.setXORMode(Color.WHITE);					
					selectitem.set_max_min_value();
					
					for(int i = 0; i <= myUndomanager.draw_count; i++){                   
						if(myUndomanager.drawList.get(i).item.get(0).shapeType == 'w'){   //word
							myUndomanager.drawList.get(i).min_x = myUndomanager.drawList.get(i).item.get(0).x1;
							myUndomanager.drawList.get(i).min_y = myUndomanager.drawList.get(i).item.get(0).y1 - myUndomanager.drawList.get(i).item.get(0).height;
							myUndomanager.drawList.get(i).max_x = myUndomanager.drawList.get(i).item.get(0).x1 + myUndomanager.drawList.get(i).item.get(0).width;
							myUndomanager.drawList.get(i).max_y = myUndomanager.drawList.get(i).item.get(0).y1;
						}
						else{                                                             //shape
							myUndomanager.drawList.get(i).max_x = (myUndomanager.drawList.get(i).item.get(0).x1 > myUndomanager.drawList.get(i).item.get(0).x2) ? myUndomanager.drawList.get(i).item.get(0).x1 : myUndomanager.drawList.get(i).item.get(0).x2;
							myUndomanager.drawList.get(i).max_y = (myUndomanager.drawList.get(i).item.get(0).y1 > myUndomanager.drawList.get(i).item.get(0).y2) ? myUndomanager.drawList.get(i).item.get(0).y1 : myUndomanager.drawList.get(i).item.get(0).y2;
							myUndomanager.drawList.get(i).min_x = myUndomanager.drawList.get(i).item.get(0).x1 + myUndomanager.drawList.get(i).item.get(0).x2 - myUndomanager.drawList.get(i).max_x;
							myUndomanager.drawList.get(i).min_y = myUndomanager.drawList.get(i).item.get(0).y1 + myUndomanager.drawList.get(i).item.get(0).y2 - myUndomanager.drawList.get(i).max_y;
					
							for(int j = 1; j < myUndomanager.drawList.get(i).item.size(); j++){
								temp_max_x = (myUndomanager.drawList.get(i).item.get(j).x1 > myUndomanager.drawList.get(i).item.get(j).x2) ? myUndomanager.drawList.get(i).item.get(j).x1 : myUndomanager.drawList.get(i).item.get(j).x2;
								temp_max_y = (myUndomanager.drawList.get(i).item.get(j).y1 > myUndomanager.drawList.get(i).item.get(j).y2) ? myUndomanager.drawList.get(i).item.get(j).y1 : myUndomanager.drawList.get(i).item.get(j).y2;
								temp_min_x = myUndomanager.drawList.get(i).item.get(j).x1 + myUndomanager.drawList.get(i).item.get(j).x2 - temp_max_x;
								temp_min_y = myUndomanager.drawList.get(i).item.get(j).y1 + myUndomanager.drawList.get(i).item.get(j).y2 - temp_max_y;
								myUndomanager.drawList.get(i).max_x = (myUndomanager.drawList.get(i).max_x > temp_max_x) ? myUndomanager.drawList.get(i).max_x : temp_max_x;
								myUndomanager.drawList.get(i).max_y = (myUndomanager.drawList.get(i).max_y > temp_max_y) ? myUndomanager.drawList.get(i).max_y : temp_max_y;
								myUndomanager.drawList.get(i).min_x = (myUndomanager.drawList.get(i).min_x < temp_min_x) ? myUndomanager.drawList.get(i).min_x : temp_min_x;
								myUndomanager.drawList.get(i).min_y = (myUndomanager.drawList.get(i).min_y < temp_min_y) ? myUndomanager.drawList.get(i).min_y : temp_min_y;
							}
						}
						
						if(selectitem.max_x_select >= myUndomanager.drawList.get(i).max_x && selectitem.min_x_select <= myUndomanager.drawList.get(i).min_x && selectitem.max_y_select >= myUndomanager.drawList.get(i).max_y && selectitem.min_y_select <= myUndomanager.drawList.get(i).min_y){
							selectitem.selectitemList[++selectitem.selectIndex] = i;
							for(int j = myUndomanager.drawList.get(i).startPos; j <= myUndomanager.drawList.get(i).endPos; j++){
								itemList.get(j).R_backup = itemList.get(j).R;
								itemList.get(j).R = 255;
							}							
							repaint();
						}
					}			
					selectitem.select_flag = 1;     
				}
					
				else if(selectitem.select_flag == 1){
					selectitem.move_x1 = selectitem.move_x2;
					selectitem.move_y1 = selectitem.move_y2;
					selectitem.move_x2 = e.getX();
					selectitem.move_y2 = e.getY();	
					selectitem.move_width = selectitem.move_x1 - selectitem.move_x2;
					selectitem.move_height = selectitem.move_y1 - selectitem.move_y2;
							
					for(int i = 0; i <= selectitem.selectIndex; i++){
						for(int j = myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos; j <= myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos; j++){						
							itemList.get(j).x1 -= selectitem.move_width;
							itemList.get(j).x2 -= selectitem.move_width;
							itemList.get(j).y1 -= selectitem.move_height;
							itemList.get(j).y2 -= selectitem.move_height;
							if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
								mySyncControllor_tea.Sync_Send_changeShape(j, itemList.get(j).x1, itemList.get(j).y1, itemList.get(j).x2, itemList.get(j).y2);
							}
							mySyncControllor_tea.Sync_Send_Repaint();
							myUndomanager.drawList.get(selectitem.selectitemList[i]).item.get(j-myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).x1 = itemList.get(j).x1;
							myUndomanager.drawList.get(selectitem.selectitemList[i]).item.get(j-myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).x2 = itemList.get(j).x2;
							myUndomanager.drawList.get(selectitem.selectitemList[i]).item.get(j-myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).y1 = itemList.get(j).y1;
							myUndomanager.drawList.get(selectitem.selectitemList[i]).item.get(j-myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).y2 = itemList.get(j).y2;
						}						
					}
					drawingArea.repaint();
					if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
						mySyncControllor_tea.Sync_Send_Repaint();
					}
				}
			}
			else if(myModifier.modify_switcher == 1){
				if(myModifier.modify_flag == 1){
					int move_x = e.getX() - myModifier.press_x;
					int move_y = e.getY() - myModifier.press_y;
					itemList.get(myUndomanager.drawList.get(myModifier.index).startPos).x2 = myModifier.start_x2 + move_x;
					itemList.get(myUndomanager.drawList.get(myModifier.index).startPos).y2 = myModifier.start_y2 + move_y;
					drawingArea.repaint();
					
					if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
						mySyncControllor_tea.Sync_Send_changeShape(myUndomanager.drawList.get(myModifier.index).startPos,
										itemList.get(myUndomanager.drawList.get(myModifier.index).startPos).x1, 
										itemList.get(myUndomanager.drawList.get(myModifier.index).startPos).y1,
										itemList.get(myUndomanager.drawList.get(myModifier.index).startPos).x2, 
										itemList.get(myUndomanager.drawList.get(myModifier.index).startPos).y2);
					}
					mySyncControllor_tea.Sync_Send_Repaint();
					
					myUndomanager.drawList.get(myModifier.index).item.get(0).x2 = itemList.get(myUndomanager.drawList.get(myModifier.index).startPos).x2;
					myUndomanager.drawList.get(myModifier.index).item.get(0).y2 = itemList.get(myUndomanager.drawList.get(myModifier.index).startPos).y2;
				}
				myModifier.clear_modifier();
				currentChoice = previousChoice;
			}
		}
		
		public void mouseEntered(MouseEvent e){
			statusBar.setText("Mouse clicks:["+e.getX()+","+e.getY()+"], " + sync_status + ", " + handwrite_status + ", " + questionraise_status);	
		}
		public void mouseExited(MouseEvent e){
			statusBar.setText("Mouse clicks:["+e.getX()+","+e.getY()+"], " + sync_status + ", " + handwrite_status + ", " + questionraise_status);			
		}
	}
	
	
	class mouseB extends MouseMotionAdapter{
		public void mouseDragged(MouseEvent e){
			statusBar.setText("Mouse clicks:["+e.getX()+","+e.getY()+"], " + sync_status + ", " + handwrite_status + ", " + questionraise_status);	
			if(currentChoice >= 3){
				if(currentChoice == 3 || currentChoice == 16){
					index++;
					createNewItem(currentChoice);
					itemList.get(index-1).x1=itemList.get(index).x2=itemList.get(index).x1=e.getX();
					itemList.get(index-1).y1=itemList.get(index).y2=itemList.get(index).y1=e.getY();
					
					if(myHandWrite.handwrite_switcher == 1){
						myHandWrite.line_list.add(new Handwrite_Drawing_Info(itemList.get(index-1).x2, itemList.get(index-1).y2, itemList.get(index).x1, itemList.get(index).y1, index));
						myHandWrite.line_flag = 1;
					}					
				}
				else{
					itemList.get(index).x2 = e.getX();
					itemList.get(index).y2 = e.getY();
				}
				repaint();
			}
			
			else if(currentChoice == 0){
				if(selectitem.select_flag == 0){           
					Graphics g = drawingArea.getGraphics();
					Graphics2D g2d = (Graphics2D)g;
					g2d.setXORMode(Color.WHITE);     
					selectitem.draw(g2d);
					selectitem.selectRange_x2 = e.getX();
					selectitem.selectRange_y2 = e.getY();
					selectitem.draw(g2d);
				}
				else if(selectitem.select_flag == 1){      
					Graphics g = drawingArea.getGraphics();
					Graphics2D g2d = (Graphics2D)g;
					g2d.setXORMode(Color.WHITE);
					selectitem.draw(g2d);
					selectitem.move_x1 = selectitem.move_x2;
					selectitem.move_y1 = selectitem.move_y2;
					selectitem.move_x2 = e.getX();
					selectitem.move_y2 = e.getY();	
					selectitem.move_width = selectitem.move_x1 - selectitem.move_x2;
					selectitem.move_height = selectitem.move_y1 - selectitem.move_y2;
					
					for(int i = 0; i <= selectitem.selectIndex; i++){
						for(int j = myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos; j <= myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos; j++){
							itemList.get(j).x1 -= selectitem.move_width;
							itemList.get(j).x2 -= selectitem.move_width;
							itemList.get(j).y1 -= selectitem.move_height;
							itemList.get(j).y2 -= selectitem.move_height;
						}
						for(int j = 0; j < myUndomanager.drawList.size(); j++){
							myUndomanager.drawList.get(j).max_x -= selectitem.move_width;
							myUndomanager.drawList.get(j).max_y -= selectitem.move_height;
							myUndomanager.drawList.get(j).min_x -= selectitem.move_width;
							myUndomanager.drawList.get(j).min_y -= selectitem.move_height;
						}
					}
					drawingArea.repaint();
				}
			}
			
			else if(myModifier.modify_switcher == 1){
				if(myModifier.modify_flag == 1){				
					int move_x = e.getX() - myModifier.press_x;
					int move_y = e.getY() - myModifier.press_y;
					itemList.get(myUndomanager.drawList.get(myModifier.index).endPos).x2= myModifier.start_x2 + move_x;
					itemList.get(myUndomanager.drawList.get(myModifier.index).endPos).y2 = myModifier.start_y2 + move_y;
					drawingArea.repaint();
				}
			}
		}
		
		public void mouseMoved(MouseEvent e){
			statusBar.setText("Mouse clicks:["+e.getX()+","+e.getY()+"], " + sync_status + ", " + handwrite_status + ", " + questionraise_status);		
		}
	}
	
	
	private class checkBoxHandler implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			if(e.getSource()==bold)			
				if(e.getStateChange()==ItemEvent.SELECTED)
					f1=Font.BOLD;
				else
					f1=Font.PLAIN;
			if(e.getSource()==italic)		
				if(e.getStateChange()==ItemEvent.SELECTED)
					f2=Font.ITALIC;
				else
					f2=Font.PLAIN;
		}
	}
	
	
	class DrawPanel extends JPanel{
		public DrawPanel(){
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			setBackground(Color.white);		
			addMouseListener(new mouseA());
			addMouseMotionListener(new mouseB());
		}
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d=(Graphics2D)g;
			
			int j = 0;
			while(j < sync_itemList.size()){
				draw(g2d, sync_itemList.get(j));
				j++;
			}
			
			j = 0;
			while (j < itemList.size()){
				draw(g2d, itemList.get(j));
				j++;
			}	
		}
		void draw(Graphics2D g2d,drawings i){
			i.draw(g2d);		
		}
	}
	
	
	void createNewItem(int currentChoice){
		if(currentChoice == 17)
			drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		else				
			drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		switch (currentChoice){
			case 3:itemList.add(new Pencil()); 
				   	itemList.get(index).shapeType = 's';
				   	break;
			case 4:itemList.add(new Line()); 
					itemList.get(index).shapeType = 's';
					break;
			case 5:itemList.add(new Rect()); 
					itemList.get(index).shapeType = 's';
					break;
			case 6:itemList.add(new fillRect()); 
					itemList.get(index).shapeType = 's';
					break;
			case 7:itemList.add(new Oval()); 
					itemList.get(index).shapeType = 's';
					break;
			case 8:itemList.add(new fillOval()); 
					itemList.get(index).shapeType = 's';
					break;
			case 9:itemList.add(new Circle()); 
					itemList.get(index).shapeType = 's';
					break;
			case 10:itemList.add(new fillCircle()); 
					itemList.get(index).shapeType = 's';
					break;
			case 11:itemList.add(new RoundRect()); 
					itemList.get(index).shapeType = 's';
					break;
			case 12:itemList.add(new fillRoundRect()); 
					itemList.get(index).shapeType = 's';
					break;
			case 13:itemList.add(new Rect3D()); 
					itemList.get(index).shapeType = 's';
					break;
			case 14:itemList.add(new fillRect3D()); 
					itemList.get(index).shapeType = 's';
					break;
			case 15:itemList.add(new Cube()); 
					itemList.get(index).shapeType = 's';
					break;
			case 16:itemList.add(new Rubber()); 
					itemList.get(index).shapeType = 's';
					break;
			case 17:itemList.add(new Word()); 
					itemList.get(index).shapeType = 'w';
					break;
		}
			itemList.get(index).type=currentChoice;
			itemList.get(index).R=R;
			itemList.get(index).G=G;
			itemList.get(index).B=B;
			itemList.get(index).stroke=stroke;
			itemList.get(index).thickness=thickness;
	}
	
	void createSyncItem(int SyncChoice, int r, int g, int b, float s, int t){
		switch (SyncChoice){
			case 3:sync_itemList.add(new Pencil()); 
				   	break;
			case 4:sync_itemList.add(new Line()); 
					break;
			case 5:sync_itemList.add(new Rect()); 
					break;
			case 6:sync_itemList.add(new fillRect()); 
					break;
			case 7:sync_itemList.add(new Oval()); 
					break;
			case 8:sync_itemList.add(new fillOval()); 
					break;
			case 9:sync_itemList.add(new Circle()); 
					break;
			case 10:sync_itemList.add(new fillCircle()); 
					break;
			case 11:sync_itemList.add(new RoundRect()); 
					break;
			case 12:sync_itemList.add(new fillRoundRect()); 
					break;
			case 13:sync_itemList.add(new Rect3D()); 
					break;
			case 14:sync_itemList.add(new fillRect3D()); 
					break;
			case 15:sync_itemList.add(new Cube()); 
					break;
			case 16:sync_itemList.add(new Rubber()); 
					break;
			case 17:sync_itemList.add(new Word()); 
					break;
		}
		sync_itemList.get(sync_index).type = SyncChoice;
		sync_itemList.get(sync_index).R = r;
		sync_itemList.get(sync_index).G = g;
		sync_itemList.get(sync_index).B = b;
		sync_itemList.get(sync_index).stroke = s;
		sync_itemList.get(sync_index).thickness = t;
	}
	
	
	public void chooseColor(){
		color=JColorChooser.showDialog(MyPaint.this,"Choose brush color",color);
		R=color.getRed();
		G=color.getGreen();
		B=color.getBlue();
		itemList.get(index).R=R;
		itemList.get(index).G=G;
		itemList.get(index).B=B;
	}
	
	
	public void SetbgColor(){
		color=JColorChooser.showDialog(MyPaint.this,"Choose background color",color);
		R=color.getRed();
		G=color.getGreen();
		B=color.getBlue();
		drawingArea.setBackground(new Color(R,G,B));
	}
	
	
	public void setStroke(){
		String input;
		input=JOptionPane.showInputDialog("Input the thickness of brush：");
		stroke=Float.parseFloat(input);
		itemList.get(index).stroke=stroke;
	}
	
	
	public void setthickness(){
		String input;
		input=JOptionPane.showInputDialog("Input the width of the cube：");
		thickness=(int) Float.parseFloat(input);
		itemList.get(index).thickness=thickness;
		createNewItem(currentChoice);
		repaint();
	}
	
	
	public void newFile(){
		if(myUndomanager.draw_count >= 0 || sync_itemList.size() > 0){
			Object[] options = { " Cancel ", " No(N) ", " Yes(Y) " };
			int s = JOptionPane.showOptionDialog(null,
							"Do you want to save the current document before creating a new one?",
							"Information", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
			if(s != 0){
				if(s == 2){
					JFileChooser fileChooser=new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int result =fileChooser.showSaveDialog(this);
					if(result == JFileChooser.CANCEL_OPTION) 
						return;
					File fileName = fileChooser.getSelectedFile();
					fileName.canWrite();
					if(fileName == null || fileName.getName().equals(""))
						JOptionPane.showMessageDialog(fileChooser,"Invalid file name", "Invalid file name",JOptionPane.ERROR_MESSAGE);
					else{
						try{
							fileName.delete();
							FileOutputStream fos = new FileOutputStream(fileName);
							output=new ObjectOutputStream(fos);
							output.writeInt(index+sync_itemList.size()-1);
							for(int i = 0; i <= index; i++){
								drawings p = itemList.get(i);
								output.writeObject(p);
								output.flush();       
							}
							for(int i = 0; i < sync_itemList.size(); i++){
								drawings p = sync_itemList.get(i);
								output.writeObject(p);
								output.flush();       
							}	
							output.flush();
							output.close();
							fos.close();
						}
						catch(IOException ioe){
							ioe.printStackTrace();
						}
					}
				}
			
				currentChoice = 3;
				color=Color.black;
				previousChoice = -1;
				index = -1;						
				drawingArea.setBackground(Color.white);
				stroke = 2.0f;
				myHandWrite.Handwrite_Reset();
				selectitem.Selectfunc_Reset();
				myUndomanager.Undomanager_Reset();
				sync_itemList.clear();
				sync_index = -1;
				repaint();				
			}
		}
		
		else{
			currentChoice = 3;
			color=Color.black;
			previousChoice = -1;
			index = -1;						
			drawingArea.setBackground(Color.white);
			stroke = 2.0f;
			myHandWrite.Handwrite_Reset();
			selectitem.Selectfunc_Reset();
			myUndomanager.Undomanager_Reset();
			sync_itemList.clear();
			sync_index = -1;
			repaint();				
		}
	}
	
	
	public void loadFile(){
		if(myUndomanager.draw_count >= 0 || sync_itemList.size() > 0){
			Object[] options = { " Cancel ", " No(N) ", " Yes(Y) " };
			int s = JOptionPane.showOptionDialog(null,
							"Do you want to save the current document before opening a new one?",
							"Information", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
			if(s != 0){
				if(s == 2){
					JFileChooser fileChooser=new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int result =fileChooser.showSaveDialog(this);
					if(result == JFileChooser.CANCEL_OPTION) 
						return;
					File fileName = fileChooser.getSelectedFile();
					fileName.canWrite();
					if(fileName == null || fileName.getName().equals(""))
						JOptionPane.showMessageDialog(fileChooser,"Invalid file name", "Invalid file name",JOptionPane.ERROR_MESSAGE);
					else{
						try{
							fileName.delete();
							FileOutputStream fos = new FileOutputStream(fileName);
							output=new ObjectOutputStream(fos);
							output.writeInt(index+sync_itemList.size()-1);
							for(int i = 0; i <= index; i++){
								drawings p = itemList.get(i);
								output.writeObject(p);
								output.flush();       
							}
							for(int i = 0; i < sync_itemList.size(); i++){
								drawings p = sync_itemList.get(i);
								output.writeObject(p);
								output.flush();      
							}	
							output.flush();
							output.close();
							fos.close();
						}
						catch(IOException ioe){
							ioe.printStackTrace();
						}
					}
				}
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = fileChooser.showOpenDialog(this);
				if(result == JFileChooser.CANCEL_OPTION)
					return;
				File fileName = fileChooser.getSelectedFile();
				fileName.canRead();
				if (fileName == null || fileName.getName().equals(""))
					JOptionPane.showMessageDialog(fileChooser,"Invalid file name", "Invalid file name", JOptionPane.ERROR_MESSAGE);
				else {
					try{
						FileInputStream fis = new FileInputStream(fileName);
						input = new ObjectInputStream(fis);
						drawings inputRecord;  
						int countNumber = 0;
						countNumber = input.readInt();
						for(index = 0; index < countNumber; index++){
							inputRecord = (drawings)input.readObject();
							itemList.add(inputRecord);
						}
						index--;
						input.close();
						repaint();
					}
					catch(EOFException endofFileException){
						JOptionPane.showMessageDialog(this,"No more document", "Do not find the class",JOptionPane.ERROR_MESSAGE );
					}
					catch(ClassNotFoundException classNotFoundException){
						JOptionPane.showMessageDialog(this,"Cannot create the object", "To the terminal",JOptionPane.ERROR_MESSAGE );
					}
					catch (IOException ioException){
						JOptionPane.showMessageDialog(this,"Errors when reading files", "Reading error",JOptionPane.ERROR_MESSAGE );
					}
				}
			}
		}
		
		else{
			JFileChooser fileChooser=new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result =fileChooser.showOpenDialog(this);
			if(result==JFileChooser.CANCEL_OPTION)
				return;
			File fileName=fileChooser.getSelectedFile();
			fileName.canRead();
			if (fileName == null || fileName.getName().equals(""))
				JOptionPane.showMessageDialog(fileChooser,"Invalid file name", "Invalid file name", JOptionPane.ERROR_MESSAGE);
			else {
				try{
					FileInputStream fis=new FileInputStream(fileName);
					input=new ObjectInputStream(fis);
					drawings inputRecord;  
					int countNumber = 0;
					countNumber=input.readInt();
					for(index = 0; index < countNumber; index++){
						inputRecord=(drawings)input.readObject();
						itemList.add(inputRecord);
					}
					index--;
					input.close();
					repaint();
				}
				catch(EOFException endofFileException){
					JOptionPane.showMessageDialog(this,"No more document", "Do not find the class",JOptionPane.ERROR_MESSAGE );
				}
				catch(ClassNotFoundException classNotFoundException){
					JOptionPane.showMessageDialog(this,"Cannot create the object", "To the terminal",JOptionPane.ERROR_MESSAGE );
				}
				catch (IOException ioException){
					JOptionPane.showMessageDialog(this,"Errors when reading files", "Reading error",JOptionPane.ERROR_MESSAGE );
				}
			}
		}
		
	}
	
	
	public void saveFile(){
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result =fileChooser.showSaveDialog(this);
		if(result==JFileChooser.CANCEL_OPTION)
			return ;
		File fileName=fileChooser.getSelectedFile();
		fileName.canWrite();
		if(fileName==null||fileName.getName().equals(""))
			JOptionPane.showMessageDialog(fileChooser,"Invalid file name",
					"Invalid file name",JOptionPane.ERROR_MESSAGE);
		else{
			try {
				fileName.delete();
				FileOutputStream fos = new FileOutputStream(fileName);
				output = new ObjectOutputStream(fos);
				output.writeInt(index+sync_itemList.size()-1);
				for(int i = 0; i <= index; i++){
					drawings p = itemList.get(i);
					output.writeObject(p);
					output.flush();      
				}
				for(int i = 0; i < sync_itemList.size(); i++){
					drawings p = sync_itemList.get(i);
					output.writeObject(p);
					output.flush();      
				}				
				output.flush();				
				output.close();
				fos.close();
			}
			catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
	}
	
	
	public void saveasJPG(){
		myJPG_PDF.savetoFunction(0);
	}
	
	
	public void saveasPDF(){
		myJPG_PDF.savetoFunction(1);
	}
	
	public JMenuBar getJMenuBar(){
		if(jmenuBar == null){
			JMenuBar Jmenu = new JMenuBar();
			setJMenuBar(Jmenu);
			JMenu filemenu = new JMenu("File(F)");
			JMenu editmenu = new JMenu("Edit(E)");
			JMenu assisantmenu = new JMenu("Asisstant");
			JMenu setmenu = new JMenu("Setting(P)");
			JMenu helpmenu = new JMenu("Help(H)");
			Jmenu.add(filemenu);
			Jmenu.add(editmenu);
			Jmenu.add(assisantmenu);
			Jmenu.add(setmenu);
			Jmenu.add(helpmenu);
			
			
			JMenuItem newitem = new JMenuItem("New (N)");
			JMenuItem openitem = new JMenuItem("Open (O)");
			JMenuItem saveitem = new JMenuItem("Save (S)");
			JMenuItem saveasitem = new JMenuItem("Save As (A)");
			JMenuItem saveasjpgitem = new JMenuItem("Save As JPG");
			JMenuItem saveaspdfitem = new JMenuItem("Save As PDF");
			JMenuItem exititem = new JMenuItem("Exit (X)");
			newitem.setAccelerator(KeyStroke.getKeyStroke 
					(KeyEvent.VK_N,InputEvent.CTRL_MASK));
			openitem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_O,InputEvent.CTRL_MASK));
			saveitem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_S,InputEvent.CTRL_MASK));
			exititem.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_F4,InputEvent.ALT_MASK));
			filemenu.add(newitem);
			filemenu.add(openitem);
			filemenu.add(saveitem);
			filemenu.add(saveasitem);
			filemenu.add(saveasjpgitem);
			filemenu.add(saveaspdfitem);
			filemenu.addSeparator();
			filemenu.add(exititem);
			
			
			newitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					newFile();
				}   
			});
			
			
			openitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					loadFile();
				}	   
			});
			
			
			saveitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveFile();
				}
			});
			
			
			saveasitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveFile();
				}
			});
			
			saveasjpgitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveasJPG();
				}
			});
			
			saveaspdfitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveasPDF();
				}
			});
			
			
			exititem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Object[] options = {" No(N) ", " Yes(Y) " };
					int s = JOptionPane.showOptionDialog(null, "Are you sure to exit？",
							"Exit Information", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[2]);
					switch (s) {
					case 1:
						System.exit(0);		
					}
				}
			});
						
			
			JMenuItem pencilitem_op = new JMenuItem("Pencil (P)");
			JMenuItem undoitem_op = new JMenuItem("Undo (U)");
			JMenuItem redoitem_op = new JMenuItem("Redo (R)");
			JMenuItem selectitem_op = new JMenuItem("Select (S)");
			JMenuItem modifyitem_op = new JMenuItem("Modify (M)");
			JMenuItem deleteitem_op = new JMenuItem("Delete (D)");
			JMenuItem copyitem_op = new JMenuItem("Copy (C)");
			
			pencilitem_op.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_P,InputEvent.CTRL_MASK));
			undoitem_op.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_Z,InputEvent.CTRL_MASK));
			redoitem_op.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_Y,InputEvent.CTRL_MASK));
			selectitem_op.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_S,InputEvent.CTRL_MASK));
			modifyitem_op.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_M,InputEvent.CTRL_MASK));
			deleteitem_op.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_D,InputEvent.CTRL_MASK));
			copyitem_op.setAccelerator(KeyStroke.getKeyStroke
					(KeyEvent.VK_C,InputEvent.CTRL_MASK));
			
			editmenu.add(pencilitem_op);
			editmenu.add(undoitem_op);
			editmenu.add(redoitem_op);
			editmenu.add(selectitem_op);
			editmenu.add(modifyitem_op);
			editmenu.add(deleteitem_op);
			editmenu.add(copyitem_op);
			
			pencilitem_op.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {                                            
						drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
						previousChoice = currentChoice = 3;
					} catch (CannotRedoException ex) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Cannot use pencil！","Edit Information",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			
			
			undoitem_op.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {                                            
						drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						if(index >= 0){
							myUndomanager.undoOperation(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, myUndomanager.drawList.get(myUndomanager.draw_count).type, itemList);	
							for(int i = myUndomanager.drawList.get(myUndomanager.draw_count).startPos; i <= myUndomanager.drawList.get(myUndomanager.draw_count).endPos; i++){														
								if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
									mySyncControllor_tea.Sync_Send_deleteShape(itemList.size()-1);
									mySyncControllor_tea.sync_count--;
								}
								itemList.remove(itemList.size()-1);
							}
							if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
								mySyncControllor_tea.Sync_Send_Repaint();
							}
							drawingArea.repaint();
							index = myUndomanager.drawList.get(myUndomanager.draw_count).startPos-1;
							myUndomanager.drawList.remove(myUndomanager.draw_count--);
						}
					} catch (CannotUndoException ex) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Cannot undo！","Edit Information",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			
			
			redoitem_op.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {                                            
						drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						if(myUndomanager.redo_count >= 0){
							if(myUndomanager.write_flag == 0){
								myUndomanager.drawList.add(myUndomanager.redoList.get(myUndomanager.redo_count));
								myUndomanager.draw_count++;
								for(int i = myUndomanager.redoList.get(myUndomanager.redo_count).startPos; i <= myUndomanager.redoList.get(myUndomanager.redo_count).endPos; i++){
									index++;
									currentChoice = myUndomanager.redoList.get(myUndomanager.redo_count).currentChoice;
									createNewItem(currentChoice);

									if(currentChoice != 17 && userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
										mySyncControllor_tea.Sync_Send_drawShape(
												i, currentChoice,
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).x1,
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).y1,
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).x2,
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).y2,
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).R,
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).G,
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).B,
												(int) myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).stroke,
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(i - myUndomanager.redoList.get(myUndomanager.redo_count).startPos).thickness);
										mySyncControllor_tea.sync_count++;
									}
									else if(currentChoice == 17 && userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
										mySyncControllor_tea.Sync_Send_drawword(
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).x1, 
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).y1,
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).x2, 
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).y2, 
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).width,
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).height, 
												myUndomanager.redoList.get(myUndomanager.redo_count).item.get(0).s1);
										mySyncControllor_tea.sync_count++;
									}
								}
								myUndomanager.redoOperation(itemList);
								if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
									mySyncControllor_tea.Sync_Send_Repaint();
								}
								repaint();
							}
							else
								myUndomanager.redoOperation(itemList);
						}						
					} catch (CannotRedoException ex) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Cannot redo！","Edit Information",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			
			
			selectitem_op.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {                                            
						drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						currentChoice = 0;
					} catch (CannotRedoException ex) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Cannot select！","Edit Information",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			
			
			modifyitem_op.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {
						drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						previousChoice = currentChoice;
						currentChoice = -1;
						myModifier.modify_switcher = 1;
					} catch (CannotRedoException ex) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Cannot modify！","Edit Information",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			
			
			deleteitem_op.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {                                           
						for(int i = selectitem.selectIndex; i >= 0; i--){																				
							for(int j = myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos; j <= myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos; j++){
								if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
									mySyncControllor_tea.Sync_Send_deleteShape(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos);
									mySyncControllor_tea.sync_count--;
								}
								itemList.remove(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos);
								index--;
							}
							int delete_count = myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos - myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos + 1;
							myUndomanager.drawList.remove(selectitem.selectitemList[i]);
							myUndomanager.draw_count--;
							for(int j = selectitem.selectitemList[i]; j < myUndomanager.drawList.size(); j++){
								myUndomanager.drawList.get(j).startPos -= delete_count;
								myUndomanager.drawList.get(j).endPos -= delete_count;
							}
							
							for(int j = i; j < selectitem.selectIndex; j++){
								selectitem.selectitemList[j] = selectitem.selectitemList[j+1];
							}
							selectitem.selectitemList[selectitem.selectIndex] = 0;
							selectitem.selectIndex--;
						}
						drawingArea.repaint();
						if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
							mySyncControllor_tea.Sync_Send_Repaint();
						}
					} catch (CannotRedoException ex) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Cannot delete！","Edit Information",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			
			
			copyitem_op.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					copy_count++;
					int temp_start_index = 0;
					for (int i = 0; i <= selectitem.selectIndex; i++) {
						temp_start_index = index + 1;
						selectitem.Check_max_min_border(myUndomanager.drawList, itemList);
						
						if (myUndomanager.drawList.get(selectitem.selectitemList[i]).currentChoice != 17) {
							for (int j = myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos; j <= myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos; j++) {
								index++;
								createNewItem(myUndomanager.drawList.get(selectitem.selectitemList[i]).currentChoice);
								itemList.get(index).x1 = itemList.get(j).x1 + selectitem.max_width * copy_count;
								itemList.get(index).y1 = itemList.get(j).y1 + selectitem.max_height * copy_count;
								itemList.get(index).x2 = itemList.get(j).x2 + selectitem.max_width * copy_count;
								itemList.get(index).y2 = itemList.get(j).y2 + selectitem.max_height * copy_count;
								itemList.get(index).R = itemList.get(j).R_backup;
								itemList.get(index).G = itemList.get(j).G;
								itemList.get(index).B = itemList.get(j).B;
								itemList.get(index).stroke = itemList.get(j).stroke;
								itemList.get(index).type = itemList.get(j).type;
								itemList.get(index).thickness = itemList.get(j).thickness;
								itemList.get(index).shapeType = itemList.get(j).shapeType;	
							}		
							
							myUndomanager.drawList.add(new drawing_info());
							myUndomanager.draw_count++;
							myUndomanager.drawList.get(myUndomanager.draw_count).startPos = temp_start_index;
							myUndomanager.drawList.get(myUndomanager.draw_count).endPos = temp_start_index + myUndomanager.drawList.get(selectitem.selectitemList[i]).endPos - myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos;
							myUndomanager.drawList.get(myUndomanager.draw_count).type = myUndomanager.drawList.get(selectitem.selectitemList[i]).currentChoice;
							myUndomanager.drawList.get(myUndomanager.draw_count).copy_drawings(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, myUndomanager.drawList.get(myUndomanager.draw_count).type, itemList);									
							myUndomanager.write_flag = 1;
						}
						else{
							index++;
							createNewItem(myUndomanager.drawList.get(selectitem.selectitemList[i]).currentChoice);
							itemList.get(index).x1 = itemList.get(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).x1 + selectitem.max_width * copy_count;
							itemList.get(index).y1 = itemList.get(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).y1 + selectitem.max_height * copy_count;
							itemList.get(index).x2 = itemList.get(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).x2 + selectitem.max_width * copy_count;
							itemList.get(index).y2 = itemList.get(myUndomanager.drawList.get(selectitem.selectitemList[i]).startPos).y2 + selectitem.max_height * copy_count;
							itemList.get(index).R = itemList.get(selectitem.selectitemList[i]).R_backup;
							itemList.get(index).G = itemList.get(selectitem.selectitemList[i]).G;
							itemList.get(index).B = itemList.get(selectitem.selectitemList[i]).B;
							itemList.get(index).stroke = itemList.get(selectitem.selectitemList[i]).stroke;
							itemList.get(index).type = itemList.get(selectitem.selectitemList[i]).type;
							itemList.get(index).thickness = itemList.get(selectitem.selectitemList[i]).thickness;
							itemList.get(index).shapeType = itemList.get(selectitem.selectitemList[i]).shapeType;
							itemList.get(index).s1 = itemList.get(selectitem.selectitemList[i]).s1;
		                    itemList.get(index).x2 = itemList.get(selectitem.selectitemList[i]).x2;
		                    itemList.get(index).y2 = itemList.get(selectitem.selectitemList[i]).y2;
		                    itemList.get(index).width = itemList.get(selectitem.selectitemList[i]).width;
		                    itemList.get(index).height = itemList.get(selectitem.selectitemList[i]).height;
		                    itemList.get(index).s2 = itemList.get(selectitem.selectitemList[i]).s2;
		                    myUndomanager.drawList.add(new drawing_info());
							myUndomanager.draw_count++;
							myUndomanager.drawList.get(myUndomanager.draw_count).startPos = index;
							myUndomanager.drawList.get(myUndomanager.draw_count).endPos = index;
							myUndomanager.drawList.get(myUndomanager.draw_count).type = myUndomanager.drawList.get(selectitem.selectitemList[i]).currentChoice;
							myUndomanager.drawList.get(myUndomanager.draw_count).copy_drawings(myUndomanager.drawList.get(myUndomanager.draw_count).startPos, myUndomanager.drawList.get(myUndomanager.draw_count).endPos, myUndomanager.drawList.get(myUndomanager.draw_count).type, itemList);												
						}
					}
					drawingArea.repaint();
	                
					if(userMode == 't' && mySyncControllor_tea.Sync_Switcher == 1){
						for( ; mySyncControllor_tea.sync_count <= index; mySyncControllor_tea.sync_count++){
							mySyncControllor_tea.Sync_Send_drawShape(mySyncControllor_tea.sync_count, itemList.get(mySyncControllor_tea.sync_count).type, itemList.get(mySyncControllor_tea.sync_count).x1, itemList.get(mySyncControllor_tea.sync_count).y1, itemList.get(mySyncControllor_tea.sync_count).x2, itemList.get(mySyncControllor_tea.sync_count).y2, itemList.get(mySyncControllor_tea.sync_count).R, itemList.get(mySyncControllor_tea.sync_count).G, itemList.get(mySyncControllor_tea.sync_count).B, (int)itemList.get(mySyncControllor_tea.sync_count).stroke, itemList.get(mySyncControllor_tea.sync_count).thickness);
						}
						mySyncControllor_tea.Sync_Send_Repaint();
					}
				}
			});	
			
			
			JMenuItem ShowDrawMenuItem = new JMenuItem("Showing draw bar");
			JMenuItem HideDrawMenuItem = new JMenuItem("Hiding draw bar");
			JMenuItem handwriteMenuItem = new JMenuItem("Handwrite (ON/OFF)");
			JMenuItem syncMenuItem = new JMenuItem("Sync (ON/OFF)");
			JMenuItem questionraiseMenuItem = new JMenuItem("Que-Raise (SHOW/HIDE)");
			assisantmenu.add(ShowDrawMenuItem);
			assisantmenu.add(HideDrawMenuItem);
			assisantmenu.add(handwriteMenuItem);
			assisantmenu.add(syncMenuItem);
			assisantmenu.add(questionraiseMenuItem);
			
			ShowDrawMenuItem.addActionListener(new ActionListener(){              
				public void actionPerformed(ActionEvent e){
					if(myHandWrite.handwrite_switcher == 0){
						buttonPanel.setVisible(true);
					}
					else{
						Object[] options = { " OK " };
						int s = JOptionPane.showOptionDialog(null, "Please turn off the handwrite function before using the draw bar, thanks!", "HandWrite Information", JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE, null, options, options[0]);
					}
				}
			});
			
			HideDrawMenuItem.addActionListener(new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					 buttonPanel.setVisible(false);
					 }
			});
			
			handwriteMenuItem.addActionListener(new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					 buttonPanel.setVisible(false);
					 simpleButtonPanel.setVisible(false);
					 currentChoice = 3;
					 previousChoice = 3;
					 drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
					 
					 if(myHandWrite.handwrite_switcher == 0){
						 Object[] options = { " OK " };
						 int s = JOptionPane.showOptionDialog(null, "Handwrite funciton turns on", "Handwrite Function Information", JOptionPane.DEFAULT_OPTION,
									JOptionPane.WARNING_MESSAGE, null, options, options[0]);
						 myHandWrite.handwrite_switcher = 1;
						 handwrite_status = "Handwrite Function: ON";			
					 }
					 else{
						 simpleButtonPanel.setVisible(true);
						 Object[] options = { " OK " };
						 int s = JOptionPane.showOptionDialog(null, "Handwrite funciton turns off", "Handwrite Function Information", JOptionPane.DEFAULT_OPTION,
									JOptionPane.WARNING_MESSAGE, null, options, options[0]);
						 myHandWrite.handwrite_switcher = 0;
						 handwrite_status = "Handwrite Function: OFF";
					 }
				}
			});
			
			syncMenuItem.addActionListener(new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					 if(userMode == 't'){
						 if(mySyncControllor_tea.Sync_Switcher == 0){
							 mySyncControllor_tea.Sync_Function_Tea();
							 sync_status = "Sync Function: ON";
							 Object[] options = { "OK" };
								int s = JOptionPane.showOptionDialog(null, "Sync function turns on!", "SmartNote Information", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);								
						 }
						 else{
							 mySyncControllor_tea.mySyncTeaThread.stop();
							 mySyncControllor_tea.exec.shutdown();
							 mySyncControllor_tea.Sync_Switcher = 0;
							 sync_status = "Sync Function: OFF";
							 Object[] options = { "OK" };
								int s = JOptionPane.showOptionDialog(null, "Sync function turns off!", "SmartNote Information", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);								
						 }
					 }
					 else if(userMode == 's'){
						 if(mySyncControllor_stu.Sync_Switcher == 0){
							 mySyncControllor_stu.Sync_Function_Stu();
							 sync_status = "Sync Function: ON";
						 }
						 else{
							 mySyncControllor_stu.Sync_Shutdown();
							 Object[] options = { "OK" };
								int s = JOptionPane.showOptionDialog(null, "Sync function turns off!", "Sync Information", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);														 
						 }
					 }					 			 
				}
			});
			
			questionraiseMenuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(userMode == 's'){
						if(myQuestionRaise_stu.questionraiseSwitch == 0){       
							myQuestionRaise_stu.showQuestion();
							questionraise_status = "QuestionRaise Function: ON";
						}
						else if(myQuestionRaise_stu.questionraiseSwitch == 1){  
							myQuestionRaise_stu.QuestionRaise_Shutdown();
							Object[] options = { "OK" };
							int s = JOptionPane.showOptionDialog(null, "QuestionRaise function turns off!", "QuestionRaise Information", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);														 		 
						}  
					}
					else if(userMode == 't'){
						if(myQuestionRaise_tea.questionraiseSwitch == 0 && myQuestionRaise_tea.initial_flag == 0){       
							myQuestionRaise_tea.showQuestion();
							questionraise_status = "QuestionRaise Function: ON";
						}
						else if(myQuestionRaise_tea.questionraiseSwitch == 0 && myQuestionRaise_tea.initial_flag == 1){
							myQuestionRaise_tea.setVisible(true);
							myQuestionRaise_tea.questionraiseSwitch = 1;
							questionraise_status = "QuestionRaise Function: ON";
						}
						else if(myQuestionRaise_tea.questionraiseSwitch == 1){
							myQuestionRaise_tea.setVisible(false);
							myQuestionRaise_tea.questionraiseSwitch = 0;
							questionraise_status = "QuestionRaise Function: OFF";
						} 
						
					}	
				}
			});
			
			
			JMenuItem coloritem = new JMenuItem("Brush Color (C)");
			JMenuItem strokeitem = new JMenuItem("Brush Thickness (S)");
			JMenuItem cubeitem = new JMenuItem("Width of Cube (W)");
			setmenu.add(coloritem);
			setmenu.add(strokeitem);
			setmenu.add(cubeitem);
			coloritem.addActionListener(new ActionListener(){              
				public void actionPerformed(ActionEvent e){
					chooseColor();
					}
			});
			strokeitem.addActionListener(new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					 setStroke();
					 }
			});
			cubeitem.addActionListener(new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					setthickness();
				}
			});
			
			
			JMenuItem aboutboxitem = new JMenuItem("About the SmartNote (A)");
			JMenuItem writeritem = new JMenuItem("About the author (S)");
			helpmenu.addSeparator();
			helpmenu.add(aboutboxitem);
			helpmenu.addSeparator();
			helpmenu.add(writeritem);
			aboutboxitem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Object[] options = {" OK " };
					int s = JOptionPane.showOptionDialog(null, "Thanks for using the SmartNote", "About the SmartNote", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[0]);
				}
			});
			writeritem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){					
					Object[] options = {" OK " };
					int s = JOptionPane.showOptionDialog(null, "Developed by Feng Chen (Jackie) \n" +
							"Email: jackiechan618@hotmail.com \n" + "Cell phone: 201-360-1032", "About the authors", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[0]);
				}
			});
		}
		return jmenuBar;
	}
	
	
	void ToolMenu() {
		final JPopupMenu ToolMenu;
		ToolMenu = new JPopupMenu();
		final JCheckBox move = new JCheckBox("Drag the toolbar");
		move.setBackground(new Color(0, 255, 0));
		ToolMenu.add(move);
		buttonPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON3_MASK)
					ToolMenu.show(buttonPanel, e.getX(), e.getY());
			}
		});
		move.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (move.isSelected()) {
					buttonPanel.setFloatable(true);
				} else {
					buttonPanel.setFloatable(false);
				}
			}
		});
	}
}
