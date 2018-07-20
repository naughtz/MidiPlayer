package gui;
import javax.swing.*;
import java.awt.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;

public class Window {
	JPanel mainPanel;
	ArrayList<JCheckBox> checkboxList;
	Sequencer sequencer;
	Sequence sequence;
	Track track;
	JFrame theFrame;
	
	String[] instrumentNames = {"Bass Drum","Closed Hi-Hat","Open Hi-Hat","Acoustic Snare","Crash Cymbal","Hand Clap",
								"Piano E4","Piano D4","Piano C4","Piano B3","Piano A3","Piano G3","Piano F3","Piano E3","Piano D3","Piano C3",
								"Violin G4","Violin F4","Violin E4","Violin D4","Violin C4","Violin B3","Violin A3","Violin G3",
								"Flute G4","Flute F4","Flute E4","Flute D4","Flute C4","Flute B3","Flute A3","Flute G3",
								};
	int[] instruments = {35,42,46,38,49,39,64,62,60,59,57,55,53,52,50,48,67,65,64,62,60,59,57,55,67,65,64,62,60,59,57,55};
	
	public void buildGUI() {
		theFrame = new JFrame("MidiPlayer"); //窗体名称
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));//面板上摆设组件时的空白边缘
		
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
		JButton upTempo = new JButton("Tempo Up");
		upTempo.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);
		
		//bpm减慢按钮
		JButton downTempo = new JButton("Tempo Down");
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);
		
		Box nameBox = new Box(BoxLayout.Y_AXIS); //垂直分布的乐器名组
		for(int i=0;i<32;i++) {
			nameBox.add(new Label(instrumentNames[i]));
		}
		
		background.add(BorderLayout.EAST, buttonBox); //东区放置按钮组
		background.add(BorderLayout.WEST, nameBox);   //西区放置乐器名组
		
		theFrame.getContentPane().add(background);
		
		//中间复选框布局
		GridLayout grid = new GridLayout(32,32);
		grid.setVgap(1);
		grid.setHgap(1);
		mainPanel = new JPanel(grid);
		background.add(BorderLayout.CENTER,mainPanel);
		
		for(int i=0;i<1024;i++) {
			JCheckBox c = new JCheckBox();
			c.setSelected(false);
			checkboxList.add(c);
			mainPanel.add(c);
		}
		
		//Midi准备
		setUpMidi();
		
		theFrame.setBounds(50,50,500,500);
		theFrame.pack();
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
		track.add(makeEvent(192,2,1,0,1));
		track.add(makeEvent(192,3,41,0,1));
		track.add(makeEvent(192,4,74,0,1));
		for(int i=0;i<32;i++) {
			trackList = new int[32];
			int key = instruments[i];
			for(int j=0;j<32;j++) {
				JCheckBox jc = (JCheckBox) checkboxList.get(j+(32*i));
				if(jc.isSelected()) {
					trackList[j] = key;
				}else {
					trackList[j] = 0;
				}
			}
			makeTracks(trackList,i);
			track.add(makeEvent(176,1,127,0,32));
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
		public void actionPerformed(ActionEvent a) {
			buildTrackAndStart();
		}
	}
	//停止按钮的监听类，按下后停止播放
	public class MyStopListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			sequencer.stop();
		}
	}
	//升bpm按钮的监听类，按下后将速度调整至1.02倍
	public class MyUpTempoListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*1.02));
		}
	}
	//降bpm按钮的监听类，按下后将速度调整至0.98倍
	public class MyDownTempoListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*0.98));
		}
	}
	
	
	//将每个节拍的复选框是否有选中换算成midi音符并加入轨道
	public void makeTracks(int[] list,int insn) {
		for(int i=0;i<32;i++) {
			int key = list[i];
			if(key!=0) {
				if(insn<=5) { //打击乐器
					track.add(makeEvent(144,9,key,100,i));
					track.add(makeEvent(128,9,key,100,i+1));
				}else if(insn<=15) { //钢琴
					track.add(makeEvent(144,2,key,100,i));
					track.add(makeEvent(128,2,key,100,i+1));
				}else if(insn<=23) { //小提琴
					track.add(makeEvent(144,3,key,100,i));
					track.add(makeEvent(128,3,key,100,i+1));
				}else if(insn<=31) { //长笛
					track.add(makeEvent(144,4,key,100,i));
					track.add(makeEvent(128,4,key,100,i+1));
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