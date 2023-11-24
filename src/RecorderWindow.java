/*
 * ISE301 - An experimental application.
 * This file is part of ISE301.
 *
 * ISE301 is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * ISE301 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with ISE301. If not, see
 * <https://www.gnu.org/licenses/>.
 * */

import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.formdev.flatlaf.util.SystemInfo;
import org.apache.commons.io.FileUtils;

/**
 * The class that contains a simple voice recording interface.
 * It only records in .wav format
 */
public class RecorderWindow{
    private final JButton recordStop, play;
    private String newName;
    private final JFrame window;
    private TargetDataLine targetLine;
    private final Clip clip;

    // Since the buttons recordStop and play have two different functionalities these booleans are needed.
    private boolean recording, playing, rerecording;
    private final static ResourceBundle bundle = ResourceBundle.getBundle("RecorderStrings");
    private final static int[] bitDepths = {8, 16, 24}; // For the string items of the Bit Depths comb box.
    private final JSlider samplingRate; // Slider for Sampling Rate
    private final JComboBox<String> bitDepth; // Combo box for Bit Depth
    private final JTextField sampRateL; // For users to type in the Sampling Rate

    /**
     * Creates a RecorderWindow object that records the voice and play it
     * @param path Where the recorder is saving the new recording
     */
    public RecorderWindow(String path) throws LineUnavailableException{
        File fileDir = new File(path); // Holds the directory in which the recording will be saved.
        String[] files = fileDir.list(); // List of the contents of the target directory fileDir
        assert files != null;

        // Initializing the dialog box for the GUI
        window = new JFrame();

        //Fixed size
        window.setResizable(false);
        window.setSize(new Dimension(450, 170));
        WindowActions.centerWindow(this.window);
        window.setLayout(null);
        window.setTitle(bundle.getString("REC_TTL"));
        window.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {

                // The application isn't supposed to "save" the recording, therefore, the hidden file should
                // be deleted for the sake of fool-proof-ness.

                try { FileUtils.delete(new File(path + "/" + ".recDump.wav"));}
                catch(Exception exception) {}
                System.out.println("Program ended.");
                System.exit(0);
            }
        });

        // Blending the title bar with window (looks cool)
        if( SystemInfo.isMacFullWindowContentSupported )
            window.getRootPane().putClientProperty( "apple.awt.transparentTitleBar", true );

        // The string showing the "Sampling Rate" and "Bit depth"
        JLabel srlbl = new JLabel(bundle.getString("REC_SMPRT"));
        srlbl.setBounds(40, 15, 100, 25);
        window.add(srlbl);

        srlbl = new JLabel(bundle.getString("REC_BD"));
        srlbl.setBounds(40, 45, 100, 25);
        window.add(srlbl);

        // Initializing the Combo Box for Bit Depth
        bitDepth = new JComboBox<>(new String[] {"8 - Bit","16 - Bit", "24 - Bit"}); // These are strings,
                                                                                     // I used a list
                                                                                     // so I wouldn't need to
                                                                                     // parse the values out
                                                                                     // of these strings

        bitDepth.setBounds(140, 45, 100, 25);
        bitDepth.setSelectedIndex(1);
        bitDepth.setFocusable(false);
        window.add(bitDepth);

        sampRateL = new JTextField("44100");
        sampRateL.setBounds(300, 15, 60, 25);
        sampRateL.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char kchar = e.getKeyChar();

                // Forcing the text field only to accept numerical input (in other words, integers)
                sampRateL.setEditable(kchar >= '0' && kchar <= '9' || kchar == KeyEvent.VK_BACK_SPACE);

                if(e.getKeyCode() == KeyEvent.VK_ENTER){ // For sake of usability, added Enter key shortcut
                    int x = Integer.parseInt(sampRateL.getText());
                    System.out.println(x);

                    if(x < 1000) x = 1000;
                    else if (x > 44100) x = 44100;
                    System.out.println(x);
                    samplingRate.setValue(x);
                    sampRateL.setText(String.valueOf(x));
                }

            }
        });
        window.add(sampRateL);

        srlbl = new JLabel("Hz.");
        srlbl.setBounds(370, 15, 20, 25);
        window.add(srlbl);

        //Initializing the slider
        samplingRate = new JSlider(JSlider.HORIZONTAL, 1000, 44100, 44100);

        samplingRate.setBounds(140, 15, 150, 25);
        samplingRate.setFocusable(false);
        samplingRate.addChangeListener(new ChangeListener() { // The sampRateL's value is immediately
                                                              // re-assigned as the slider is dragged.
            @Override
            public void stateChanged(ChangeEvent e) {
                sampRateL.setText(String.valueOf(samplingRate.getValue()));
            }
        });
        window.add(samplingRate);

        // Initializing the record button
        recordStop = new JButton();
        recordStop.setText(bundle.getString("REC_REC"));
        recordStop.setBounds(240, 90, 90, 25); // Position and dimension
        recordStop.setFocusable(false);
        recordStop.addActionListener(e -> recordOrStop(path)); // Assigning the function that will be called when clicked
        window.add(recordStop);

        // The routine for the buttons are basically the same.

        play = new JButton();
        play.setText(bundle.getString("REC_PLY"));
        play.setBounds(330, 90, 90, 25);
        play.setFocusable(false);
        play.addActionListener(e -> play());
        play.setEnabled(false);
        window.add(play);
        
        // Initializing the clip that can be played when a newly recorded sample is wanted to be previewed.
        clip = AudioSystem.getClip();
        clip.addLineListener(l -> {
            if(l.getType() == LineEvent.Type.STOP && playing){
                play();
            }
        });

        window.setVisible(true);
    }

    //Function of record button
    private void recordOrStop(String path){
        try{
            if (recording){
                samplingRate.setEnabled(true);
                sampRateL.setEnabled(true);
                bitDepth.setEnabled(true);
                play.setEnabled(true);
                recordStop.setText(bundle.getString("REC_REC"));
                recording = false;
                rerecording = true;
                targetLine.stop();
                targetLine.close();
                clip.open(AudioSystem.getAudioInputStream(new File(path + "/" + newName + ".wav")));

            } else {
                samplingRate.setEnabled(false);
                sampRateL.setEnabled(false);
                samplingRate.setValue(Integer.parseInt(sampRateL.getText()));
                bitDepth.setEnabled(false);
                play.setEnabled(false);
                recordStop.setText(bundle.getString("REC_STP"));
                recording = true;

                // The following snippet is explained briefly. For details,
                // visit: https://www.youtube.com/watch?v=WSyTrdjKeqQ

                //Declaring the Audio Format for our recording with the PARAMETERS SET BY USER WITH THE UI ELEMENTS.
                AudioFormat audioFormat = new AudioFormat(samplingRate.getValue(),
                                                          bitDepths[bitDepth.getSelectedIndex()],
                                                 1, true, true);
                DataLine.Info dataInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

                //System.out.println(dataInfo.getFormats());
                System.out.println(Arrays.toString(dataInfo.getFormats()));

                clip.close();

                if(!AudioSystem.isLineSupported(dataInfo)){
                    System.out.println("Info not supported");
                }

                targetLine = (TargetDataLine) AudioSystem.getLine(dataInfo);
                targetLine.open(); //Getting the microphone ready to capture audio input
                targetLine.start(); // Start capturing data from microphone

                Thread audioThread = new Thread(() -> {
                    AudioInputStream recordingStream = new AudioInputStream(targetLine); // Using targetLine as a source
                    newName = ".recDump";
                    if(!rerecording)
                        newName = FileOperations.createUniquePathName(path, newName, ".wav");
                    File outputFile = new File(path + "/" + newName + ".wav"); //Output file
                    System.out.println(outputFile.getAbsolutePath());
                    try{
                        AudioSystem.write(recordingStream, AudioFileFormat.Type.WAVE, outputFile); //Writing input to file
                    } catch (IOException ex) { ex.printStackTrace(); }
                });

                audioThread.start();
            }
        } catch (Exception e) { throw new RuntimeException(); }

    }

    // Function of play button
    private void play(){
        if (playing){
            recordStop.setEnabled(true);
            play.setText(bundle.getString("REC_PLY"));
            playing = false;
            clip.stop();
            System.out.println("Stopping");

            clip.setMicrosecondPosition(0);

        } else {
            recordStop.setEnabled(false);
            play.setText(bundle.getString("REC_STP"));
            playing = true;
            clip.start();
        }
    }
}
