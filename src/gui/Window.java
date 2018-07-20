package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Window {
	JPanel mainPanel;
	ArrayList<JCheckBox> checkboxList;
	Sequencer sequencer;
	Sequence sequence;
	Track track;
	JFrame theFrame;
	
	JRadioButton beat;
	JRadioButton piano;
	JRadioButton violin;
	JRadioButton flute;
	
	int type = 0;//beat,piano,violin,flute
	boolean refreshFlag = true; 
	
	Label[] instrumentNamesLabels;
	
	boolean[][] checkBoxState;
	
	String[][] instrumentNames = {
									{
										"Acoustic Bass Drum","Bass Drum 1","Side Stick","Acoustic Snare","Hand Clap","Electric Snare","Low Floor Tom","Closed Hi-Hat",
										"High Floor Tom","Pedal Hi-Hat","Low Tom","Open Hi-Hat","Low-Mid Tom","Hi-Mid Tom","Crash Cymbal 1","High Tom",
										"Ride Cymbal 1","Chinese Cymbal","Ride Bell","Tambourine","Splash Cymbal","Cowbell","Crash Cymbal 2","Vibraslap",
										"Ride Cymbal 2","Hi Bongo","Low Bongo","Mute Hi Conga","Open Hi Conga","Low Conga","High Timbale","Low Timbale"
									},
									{
										"Piano C6","Piano B5","Piano A#5","Piano A5","Piano G#5","Piano G5","Piano F#5","Piano F5","Piano E5","Piano D#5","Piano D5","Piano C#5",
										"Piano C5","Piano B4","Piano A#4","Piano A4","Piano G#4","Piano G4","Piano F#4","Piano F4","Piano E4","Piano D#4","Piano D4","Piano C#4",
										"Piano C4","Piano B3","Piano A#3","Piano A3","Piano G#3","Piano G3","Piano F#3","Piano F3"
									},
									{
										"Violin C6","Violin B5","Violin A#5","Violin A5","Violin G#5","Violin G5","Violin F#5","Violin F5","Violin E5","Violin D#5","Violin D5","Violin C#5",
										"Violin C5","Violin B4","Violin A#4","Violin A4","Violin G#4","Violin G4","Violin F#4","Violin F4","Violin E4","Violin D#4","Violin D4","Violin C#4",
										"Violin C4","Violin B3","Violin A#3","Violin A3","Violin G#3","Violin G3","Violin F#3","Violin F3"
									},
									{
										"Flute C6","Flute B5","Flute A#5","Flute A5","Flute G#5","Flute G5","Flute F#5","Flute F5","Flute E5","Flute D#5","Flute D5","Flute C#5",
										"Flute C5","Flute B4","Flute A#4","Flute A4","Flute G#4","Flute G4","Flute F#4","Flute F4","Flute E4","Flute D#4","Flute D4","Flute C#4",
										"Flute C4","Flute B3","Flute A#3","Flute A3","Flute G#3","Flute G3","Flute F#3","Flute F3"
									}
								};
	int[][] instruments = {
							{
								35,36,37,38,39,40,41,42,
								43,44,45,46,47,48,49,50,
								51,52,53,54,55,56,57,58,
								59,60,61,62,63,64,65,66
							},
							{
								84,83,82,81,80,79,78,77,76,75,74,73,
								72,71,70,69,68,67,66,65,64,63,62,61,
								60,59,58,57,56,55,54,53
							},
							{
								84,83,82,81,80,79,78,77,76,75,74,73,
								72,71,70,69,68,67,66,65,64,63,62,61,
								60,59,58,57,56,55,54,53
							},
							{
								84,83,82,81,80,79,78,77,76,75,74,73,
								72,71,70,69,68,67,66,65,64,63,62,61,
								60,59,58,57,56,55,54,53
							}
						};
	
	public void buildGUI() {
		theFrame = new JFrame("MidiPlayer"); //窗体名称
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));//面板上摆设组件时的空白边缘
		background.setBackground(Color.white);
		
		checkboxList = new ArrayList<JCheckBox>(); //复选框组
		Box buttonBox = new Box(BoxLayout.Y_AXIS); //垂直分布的按钮组
		
		//开始播放按钮
		JButton start = new JButton("播放");
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);
		
		//停止播放按钮
		JButton stop = new JButton("停止");
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);
		
		//bpm加快按钮
		JButton upTempo = new JButton("加速");
		upTempo.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);
		
		//bpm减慢按钮
		JButton downTempo = new JButton("减速");
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);
		
		//保存按钮
		JButton saveFile = new JButton("保存工程");
		saveFile.addActionListener(new MySaveFileListener());
		buttonBox.add(saveFile);
		
		//导入按钮
		JButton loadFile = new JButton("导入工程");
		loadFile.addActionListener(new MyLoadFileListener());
		buttonBox.add(loadFile);
		
		Box nameBox = new Box(BoxLayout.Y_AXIS); //垂直分布的乐器名组
		instrumentNamesLabels = new Label[32];
		nameBox.add(Box.createVerticalStrut(11)); //加一些空白以对齐
		for(int i=0;i<32;i++) {		
			instrumentNamesLabels[i] = new Label(instrumentNames[0][i]);
			instrumentNamesLabels[i].setBackground(new Color(0,197,205));
			instrumentNamesLabels[i].setForeground(Color.white);
			nameBox.add(instrumentNamesLabels[i]);
		}
		
		Box typeBox = new Box(BoxLayout.X_AXIS); //水平分布的轨道组
		ButtonGroup typeGroup=new ButtonGroup();
		beat=new JRadioButton("beat",true);
		beat.addActionListener(new MySelectListener());
		piano=new JRadioButton("piano");
		piano.addActionListener(new MySelectListener());
		violin=new JRadioButton("violin");
		violin.addActionListener(new MySelectListener());
		flute=new JRadioButton("flute");
		flute.addActionListener(new MySelectListener());
		typeGroup.add(beat);
		typeGroup.add(piano);
		typeGroup.add(violin);
		typeGroup.add(flute);
		typeBox.add(beat);
		typeBox.add(piano);
		typeBox.add(violin);
		typeBox.add(flute);
		
		background.add(BorderLayout.NORTH,typeBox);   //北区放置轨道组
		background.add(BorderLayout.EAST, buttonBox); //东区放置按钮组
		background.add(BorderLayout.WEST, nameBox);   //西区放置乐器名组
		
		theFrame.getContentPane().add(background);
		
		//中间复选框布局
		GridLayout grid = new GridLayout(32,32);
		grid.setVgap(1);
		grid.setHgap(1);
		mainPanel = new JPanel(grid);
		mainPanel.setBackground(Color.white);
		background.add(BorderLayout.CENTER,mainPanel);
		
		checkBoxState = new boolean[4][1024];
		for(int i=0;i<1024;i++) {
			JCheckBox c = new JCheckBox();
			c.addItemListener(new MyCheckBoxListener());
			c.setIcon(new ImageIcon("res"+File.separator+"img"+File.separator+"unselected.png"));
			if(i%8==0)
				c.setBackground(Color.red);
			else if(i%4==0)
				c.setBackground(Color.orange);
			else
				c.setBackground(Color.white);
			c.setSelected(false);
			checkboxList.add(c);
			mainPanel.add(c);
		}
		
		//Midi准备
		setUpMidi();
		
		theFrame.setBounds(50,50,500,500);
		theFrame.pack();
		theFrame.setResizable(false); //禁止改变窗体大小
		theFrame.setSize(1600,1000); //初始化窗体大小
		theFrame.setLocation(120,20);
		theFrame.setVisible(true); //显示窗体
	}
	
	//Midi准备
	public void setUpMidi() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence = new Sequence(Sequence.PPQ,4);
			track = sequence.createTrack();
			sequencer.setTempoInBPM(120); //初始bpm为120
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//开始按钮的触发函数，处理音符并播放
	public void buildTrackAndStart() {
		int[] trackList = null;
		
		sequence.deleteTrack(track); //清空上一次的轨道
		track = sequence.createTrack();//创建新的空轨道
		track.add(makeEvent(192,2,1,0,1)); //2轨钢琴
		track.add(makeEvent(192,3,41,0,1));//3轨小提琴
		track.add(makeEvent(192,4,74,0,1));//4轨长笛
		for(int t=0;t<4;t++) {
			for(int i=0;i<32;i++) {
				trackList = new int[32];
				int key = instruments[t][i];
				for(int j=0;j<32;j++) {
					if(checkBoxState[t][j+32*i]) {
						trackList[j] = key;
					}else {
						trackList[j] = 0;
					}
				}
				makeTracks(trackList,t);
				track.add(makeEvent(176,1,127,0,32));
			}
		}

		track.add(makeEvent(192,9,1,0,31));//确保第32拍有事件，否则不会重复播放
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);//重复次数无穷
			sequencer.start();
			sequencer.setTempoInBPM(120);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	//开始按钮的监听类，按下后开始处理音符并播放
	public class MyStartListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			buildTrackAndStart();
		}
	}
	//停止按钮的监听类，按下后停止播放
	public class MyStopListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			sequencer.stop();
		}
	}
	//升bpm按钮的监听类，按下后将速度调整至1.02倍
	public class MyUpTempoListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*1.02));
		}
	}
	//降bpm按钮的监听类，按下后将速度调整至0.98倍
	public class MyDownTempoListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*0.98));
		}
	}
	//选择轨道的监听类
	public class MySelectListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==beat){
			    for(int i=0;i<32;i++) {
			    	type = 0;
			    	instrumentNamesLabels[i].setText(instrumentNames[0][i]);
			    }
			} else if(e.getSource()==piano) {
				for(int i=0;i<32;i++) {
					type = 1;
			    	instrumentNamesLabels[i].setText(instrumentNames[1][i]);
			    }
			}else if(e.getSource()==violin) {
				for(int i=0;i<32;i++) {
					type = 2;
			    	instrumentNamesLabels[i].setText(instrumentNames[2][i]);
			    }
			}else if(e.getSource()==flute) {
				for(int i=0;i<32;i++) {
					type = 3;
			    	instrumentNamesLabels[i].setText(instrumentNames[3][i]);
			    }
			}
			refreshFlag = false;
			for(int j=0;j<1024;j++) {
				checkboxList.get(j).setSelected(checkBoxState[type][j]);;
			}
			refreshFlag = true;
		}
	}
	public class MyCheckBoxListener implements ItemListener{
		public void itemStateChanged(ItemEvent e) {
			JCheckBox jc = (JCheckBox)e.getItem();
			if(jc.isSelected())
				jc.setIcon(new ImageIcon("res"+File.separator+"img"+File.separator+"selected.png"));
			else
				jc.setIcon(new ImageIcon("res"+File.separator+"img"+File.separator+"unselected.png"));
			if(refreshFlag)
				for(int j=0;j<1024;j++) {
					checkBoxState[type][j] = (boolean) checkboxList.get(j).isSelected();
				}
		}
	}
	//保存工程的监听类
	public class MySaveFileListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//弹出文件选择框
			JFileChooser chooser = new JFileChooser();
			//后缀名过滤器
			FileNameExtensionFilter filter = new FileNameExtensionFilter("此工程文件(*.fmid)", "fmid");
			chooser.setFileFilter(filter);
			//默认目录为当前目录
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			
			chooser.setSelectedFile(new File("untitled.fmid"));
			int option = chooser.showSaveDialog(null);
			if(option==JFileChooser.APPROVE_OPTION){	//假如用户选择了保存
				File file = chooser.getSelectedFile();
				String fname = chooser.getName(file);
				
				//假如用户填写的文件名不带我们制定的后缀名，那么我们给它添上后缀
				if(fname.indexOf(".fmid")==-1){
					file=new File(chooser.getCurrentDirectory(),fname+".fmid");
				}
				try {
					FileOutputStream fos = new FileOutputStream(file);
					for(int t=0;t<4;t++) {
						for(int j=0;j<1024;j++) {
							if(checkBoxState[t][j])
								fos.write(1);
							else
								fos.write(0);
						}
					}
					fos.close();
					
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}

		}
	}
	//导入工程的监听类
	public class MyLoadFileListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//弹出文件选择框
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			//后缀名过滤器
			FileNameExtensionFilter filter = new FileNameExtensionFilter("此工程文件(*.fmid)", "fmid");
			chooser.setFileFilter(filter);
			
			//默认目录为当前目录
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int option = chooser.showOpenDialog(null);
			
			if(option==JFileChooser.APPROVE_OPTION){	//假如用户选择了保存
				File file = chooser.getSelectedFile();
				String fname = chooser.getName(file);

				try {
					FileInputStream fis = new FileInputStream(file);
					for(int t=0;t<4;t++) {
						for(int j=0;j<1024;j++) {
							if(fis.read()==(byte)1)
								checkBoxState[t][j] = true;
							else
								checkBoxState[t][j] = false;
						}
					}
					fis.close();
					refreshFlag = false;
					for(int j=0;j<1024;j++) {
						checkboxList.get(j).setSelected(checkBoxState[type][j]);;
					}
					refreshFlag = true;
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}

		}
	}

	
	//将每个节拍的复选框是否有选中换算成midi音符并加入轨道
	public void makeTracks(int[] list,int t) {
		for(int i=0;i<32;i++) {
			int key = list[i];
			if(key!=0) {
				if(t==0) { //打击乐器
					track.add(makeEvent(144,9,key,100,i));
					track.add(makeEvent(128,9,key,100,i+1));
				}else if(t==1) { //钢琴
					track.add(makeEvent(144,2,key,80,i));
					track.add(makeEvent(128,2,key,80,i+1));
				}else if(t==2) { //小提琴
					track.add(makeEvent(144,3,key,80,i));
					track.add(makeEvent(128,3,key,80,i+1));
				}else if(t==3) { //长笛
					track.add(makeEvent(144,4,key,80,i));
					track.add(makeEvent(128,4,key,80,i+1));
				}
			}
		}
	}
	
	
	//封装的创建MidiEvent的函数，输入五个参数(指令类型、通道、音符、响度、节拍)即可返回Midi事件
	public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
		MidiEvent event = null;
		try {
			ShortMessage a = new ShortMessage();
			a.setMessage(comd, chan, one, two);
			event = new MidiEvent(a, tick);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return event;
	}
	public static void main(String[] args) {
		Window win = new Window();
		win.buildGUI();
	}
}