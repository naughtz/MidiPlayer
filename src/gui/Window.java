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
		theFrame = new JFrame("MidiPlayer"); //��������
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));//����ϰ������ʱ�Ŀհױ�Ե
		background.setBackground(Color.white);
		
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
		JButton upTempo = new JButton("����");
		upTempo.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);
		
		//bpm������ť
		JButton downTempo = new JButton("����");
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);
		
		//���水ť
		JButton saveFile = new JButton("���湤��");
		saveFile.addActionListener(new MySaveFileListener());
		buttonBox.add(saveFile);
		
		//���밴ť
		JButton loadFile = new JButton("���빤��");
		loadFile.addActionListener(new MyLoadFileListener());
		buttonBox.add(loadFile);
		
		Box nameBox = new Box(BoxLayout.Y_AXIS); //��ֱ�ֲ�����������
		instrumentNamesLabels = new Label[32];
		nameBox.add(Box.createVerticalStrut(11)); //��һЩ�հ��Զ���
		for(int i=0;i<32;i++) {		
			instrumentNamesLabels[i] = new Label(instrumentNames[0][i]);
			instrumentNamesLabels[i].setBackground(new Color(0,197,205));
			instrumentNamesLabels[i].setForeground(Color.white);
			nameBox.add(instrumentNamesLabels[i]);
		}
		
		Box typeBox = new Box(BoxLayout.X_AXIS); //ˮƽ�ֲ��Ĺ����
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
		
		background.add(BorderLayout.NORTH,typeBox);   //�������ù����
		background.add(BorderLayout.EAST, buttonBox); //�������ð�ť��
		background.add(BorderLayout.WEST, nameBox);   //����������������
		
		theFrame.getContentPane().add(background);
		
		//�м临ѡ�򲼾�
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
		
		//Midi׼��
		setUpMidi();
		
		theFrame.setBounds(50,50,500,500);
		theFrame.pack();
		theFrame.setResizable(false); //��ֹ�ı䴰���С
		theFrame.setSize(1600,1000); //��ʼ�������С
		theFrame.setLocation(120,20);
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
		track.add(makeEvent(192,2,1,0,1)); //2�����
		track.add(makeEvent(192,3,41,0,1));//3��С����
		track.add(makeEvent(192,4,74,0,1));//4�쳤��
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
		public void actionPerformed(ActionEvent e) {
			buildTrackAndStart();
		}
	}
	//ֹͣ��ť�ļ����࣬���º�ֹͣ����
	public class MyStopListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			sequencer.stop();
		}
	}
	//��bpm��ť�ļ����࣬���º��ٶȵ�����1.02��
	public class MyUpTempoListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*1.02));
		}
	}
	//��bpm��ť�ļ����࣬���º��ٶȵ�����0.98��
	public class MyDownTempoListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*0.98));
		}
	}
	//ѡ�����ļ�����
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
	//���湤�̵ļ�����
	public class MySaveFileListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//�����ļ�ѡ���
			JFileChooser chooser = new JFileChooser();
			//��׺��������
			FileNameExtensionFilter filter = new FileNameExtensionFilter("�˹����ļ�(*.fmid)", "fmid");
			chooser.setFileFilter(filter);
			//Ĭ��Ŀ¼Ϊ��ǰĿ¼
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			
			chooser.setSelectedFile(new File("untitled.fmid"));
			int option = chooser.showSaveDialog(null);
			if(option==JFileChooser.APPROVE_OPTION){	//�����û�ѡ���˱���
				File file = chooser.getSelectedFile();
				String fname = chooser.getName(file);
				
				//�����û���д���ļ������������ƶ��ĺ�׺������ô���Ǹ������Ϻ�׺
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
	//���빤�̵ļ�����
	public class MyLoadFileListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//�����ļ�ѡ���
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			//��׺��������
			FileNameExtensionFilter filter = new FileNameExtensionFilter("�˹����ļ�(*.fmid)", "fmid");
			chooser.setFileFilter(filter);
			
			//Ĭ��Ŀ¼Ϊ��ǰĿ¼
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int option = chooser.showOpenDialog(null);
			
			if(option==JFileChooser.APPROVE_OPTION){	//�����û�ѡ���˱���
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

	
	//��ÿ�����ĵĸ�ѡ���Ƿ���ѡ�л����midi������������
	public void makeTracks(int[] list,int t) {
		for(int i=0;i<32;i++) {
			int key = list[i];
			if(key!=0) {
				if(t==0) { //�������
					track.add(makeEvent(144,9,key,100,i));
					track.add(makeEvent(128,9,key,100,i+1));
				}else if(t==1) { //����
					track.add(makeEvent(144,2,key,80,i));
					track.add(makeEvent(128,2,key,80,i+1));
				}else if(t==2) { //С����
					track.add(makeEvent(144,3,key,80,i));
					track.add(makeEvent(128,3,key,80,i+1));
				}else if(t==3) { //����
					track.add(makeEvent(144,4,key,80,i));
					track.add(makeEvent(128,4,key,80,i+1));
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