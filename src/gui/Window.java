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
		theFrame = new JFrame("MidiPlayer"); //��������
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));//����ϰ������ʱ�Ŀհױ�Ե
		
		checkboxList = new ArrayList<JCheckBox>(); //��ѡ����
		Box buttonBox = new Box(BoxLayout.Y_AXIS); //��ֱ�ֲ��İ�ť��
		
		//��ʼ���Ű�ť
		JButton start = new JButton("����");
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);
		
		//ֹͣ���Ű�ť
		JButton stop = new JButton("ֹͣ");
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);
		
		//bpm�ӿ찴ť
		JButton upTempo = new JButton("Tempo Up");
		upTempo.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);
		
		//bpm������ť
		JButton downTempo = new JButton("Tempo Down");
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);
		
		Box nameBox = new Box(BoxLayout.Y_AXIS); //��ֱ�ֲ�����������
		for(int i=0;i<32;i++) {
			nameBox.add(new Label(instrumentNames[i]));
		}
		
		background.add(BorderLayout.EAST, buttonBox); //�������ð�ť��
		background.add(BorderLayout.WEST, nameBox);   //����������������
		
		theFrame.getContentPane().add(background);
		
		//�м临ѡ�򲼾�
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
		
		//Midi׼��
		setUpMidi();
		
		theFrame.setBounds(50,50,500,500);
		theFrame.pack();
		theFrame.setVisible(true); //��ʾ����
	}
	
	//Midi׼��
	public void setUpMidi() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence = new Sequence(Sequence.PPQ,4);
			track = sequence.createTrack();
			sequencer.setTempoInBPM(120); //��ʼbpmΪ120
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//��ʼ��ť�Ĵ�����������������������
	public void buildTrackAndStart() {
		int[] trackList = null;
		
		sequence.deleteTrack(track); //�����һ�εĹ��
		track = sequence.createTrack();//�����µĿչ��
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
		track.add(makeEvent(192,9,1,0,31));//ȷ����32�����¼������򲻻��ظ�����
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);//�ظ���������
			sequencer.start();
			sequencer.setTempoInBPM(120);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	//��ʼ��ť�ļ����࣬���º�ʼ��������������
	public class MyStartListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			buildTrackAndStart();
		}
	}
	//ֹͣ��ť�ļ����࣬���º�ֹͣ����
	public class MyStopListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			sequencer.stop();
		}
	}
	//��bpm��ť�ļ����࣬���º��ٶȵ�����1.02��
	public class MyUpTempoListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*1.02));
		}
	}
	//��bpm��ť�ļ����࣬���º��ٶȵ�����0.98��
	public class MyDownTempoListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*0.98));
		}
	}
	
	
	//��ÿ�����ĵĸ�ѡ���Ƿ���ѡ�л����midi������������
	public void makeTracks(int[] list,int insn) {
		for(int i=0;i<32;i++) {
			int key = list[i];
			if(key!=0) {
				if(insn<=5) { //�������
					track.add(makeEvent(144,9,key,100,i));
					track.add(makeEvent(128,9,key,100,i+1));
				}else if(insn<=15) { //����
					track.add(makeEvent(144,2,key,100,i));
					track.add(makeEvent(128,2,key,100,i+1));
				}else if(insn<=23) { //С����
					track.add(makeEvent(144,3,key,100,i));
					track.add(makeEvent(128,3,key,100,i+1));
				}else if(insn<=31) { //����
					track.add(makeEvent(144,4,key,100,i));
					track.add(makeEvent(128,4,key,100,i+1));
				}
			}
		}
	}
	
	
	//��װ�Ĵ���MidiEvent�ĺ����������������(ָ�����͡�ͨ������������ȡ�����)���ɷ���Midi�¼�
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