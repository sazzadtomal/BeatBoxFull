import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BeatBoxGUI {
    JPanel mainPanel;
    ArrayList<JCheckBox> checkBoxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;

    String[] instrumentNames={"Bass Drum","Closed Hi-Hat","Open- Hi-Hat","Acoustic Snare", "Crash Cymbal","Hand Clap",
                              "High Tom","High Bingo","Maracas", "Whistle", "Low Conga","Cowbell", "Vibraslap",
                              "Low-mid Tom","High Agogo","Open High Conga"};

    int [] instruments={35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};

    public static void main(String[] args) {
        new BeatBoxGUI().buildGui();
    }

    public void buildGui(){
        checkBoxList=new ArrayList<JCheckBox>();

        //Frame
        theFrame=new JFrame("Super Cool BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       //Full panel
        BorderLayout layout=new BorderLayout();
        JPanel background=new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //East Button panel
        Box buttonBox=new Box(BoxLayout.Y_AXIS);
        JButton start=new JButton("Start");
        start.addActionListener(new MyStartingListener());
        buttonBox.add(start);

        JButton stop=new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo=new JButton("Tempo UP");
        upTempo.addActionListener(new MyUpTempListener());
        buttonBox.add(upTempo);

        JButton downTempo=new JButton("Tempo Down");
        upTempo.addActionListener(new MyDownTempListener());
        buttonBox.add(downTempo);

        //Name box
        Box nameBox=new Box(BoxLayout.Y_AXIS);
        for(int i=0;i<16;i++){
            nameBox.add(new Label(instrumentNames[i]));
        }

        //adding side boxes
        background.add(BorderLayout.EAST,buttonBox);
        background.add(BorderLayout.WEST,nameBox);


        GridLayout grid=new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel=new JPanel(grid);
        for(int i=0;i<256;i++){
            JCheckBox c=new JCheckBox();
            c.setSelected(false);
            checkBoxList.add(c);
            mainPanel.add(c);
        }
        background.add(BorderLayout.CENTER,mainPanel);
        theFrame.getContentPane().add(background);

        setUpMidi();

        theFrame.setBounds(50,50,300,300);
        theFrame.pack();
        theFrame.setVisible(true);


    }
    public void setUpMidi(){
        try {
            sequencer= MidiSystem.getSequencer();
            sequencer.open();
            sequence=new Sequence(Sequence.PPQ,4);
            track=sequence.createTrack();
            sequencer.setTempoInBPM(120);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart(){
        int[] trackList=null;
        sequence.deleteTrack(track);
        track=sequence.createTrack();


        for(int i=0;i<16;i++){
            trackList=new int[16];

            int key=instruments[i];

            for(int j=0;j<16;j++){
                JCheckBox jc=checkBoxList.get(j+16*i);
                if(jc.isSelected()){
                    trackList[j]=key;
                }
                else trackList[j]=0;
            }
            makeTracks(trackList);
            track.add(makeEvent(176,1,127,0,16));
        }
        track.add(makeEvent(192,9,1,0,15));

        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class MyStartingListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }
    public class MyStopListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }
    public class MyUpTempListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            float tempo=sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempo*1.03));
        }
    }
    public class MyDownTempListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            float tempo=sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempo* 0.97));
        }
    }




    public void makeTracks(int[] list){
        for(int i=0;i<16;i++){
            int key=list[i];

            if(key!=0){
                track.add(makeEvent(144,9,key,100,i));
                track.add(makeEvent(128,9,key,100,i+1));
            }
        }

    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick ){
        MidiEvent event=null;
        try {
            ShortMessage a=new ShortMessage();
            a.setMessage(comd,chan,one,two);
            event=new MidiEvent(a,tick);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return event;
    }
}
