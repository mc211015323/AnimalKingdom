
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;

public class CritterFrame extends JFrame {
    private CritterModel myModel;
    private CritterPanel myPicture;
    private javax.swing.Timer myTimer;
    private JButton[] counts;
    private JButton countButton;
    private boolean started;
    private static boolean created;
    
    public CritterFrame(int width, int height) {

        if (created)
            throw new RuntimeException("Only one world allowed");
        created = true;

        setTitle("CSE142 Critter Simulation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        myModel = new CritterModel(width, height);

        myPicture = new CritterPanel(myModel);
        add(myPicture, BorderLayout.CENTER);

        addTimer();

        constructSouth();

        started = false;
    }

    private void constructSouth() {

        JPanel p = new JPanel();

        final JSlider slider = new JSlider();
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double ratio = 1000.0 / (1 + Math.pow(slider.getValue(), 0.3));
                myTimer.setDelay((int) (ratio - 180));
            }
        });
        slider.setValue(20);
        p.add(new JLabel("slow"));
        p.add(slider);
        p.add(new JLabel("fast"));

        JButton b1 = new JButton("start");
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myTimer.start();
            }
        });
        p.add(b1);
        JButton b2 = new JButton("stop");
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myTimer.stop();
            }
        });
        p.add(b2);
        JButton b3 = new JButton("step");
        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doOneStep();
            }
        });
        p.add(b3);
        
        // add debug button
        JButton b4 = new JButton("debug");
        b4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myModel.toggleDebug();
                myPicture.repaint();
            }
        });
        p.add(b4);

        // add 100 button
        JButton b5 = new JButton("next 100");
        b5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                multistep(100);
            }
        });
        p.add(b5);

        add(p, BorderLayout.SOUTH);
    }

    public void start() {

        if (started) {
            return;
        }

        if (myModel.getCounts().isEmpty()) {
            System.out.println("nothing to simulate--no critters");
            return;
        }
        started = true;
        addClassCounts();
        myModel.updateColorString();
        pack();
        setVisible(true);
    }

    private void addClassCounts() {
        Set<Map.Entry<String, Integer>> entries = myModel.getCounts();
        JPanel p = new JPanel(new GridLayout(entries.size() + 1, 1));
        counts = new JButton[entries.size()];
        for (int i = 0; i < counts.length; i++) {
            counts[i] = new JButton();
            p.add(counts[i]);
        }

        countButton = new JButton();
        countButton.setForeground(Color.BLUE);
        p.add(countButton);

        add(p, BorderLayout.EAST);
        setCounts();
    }

    private void setCounts() {
        Set<Map.Entry<String, Integer>> entries = myModel.getCounts();
        int i = 0;
        int max = 0;
        int maxI = 0;
        for (Map.Entry<String, Integer> entry: myModel.getCounts()) {
            String s = String.format("%s =%4d", entry.getKey(),
                                     (int) entry.getValue());
            counts[i].setText(s);
            counts[i].setForeground(Color.BLACK);
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxI = i;
            }
            i++;
        }
        counts[maxI].setForeground(Color.RED);
        String s = String.format("step =%5d", myModel.getSimulationCount());
        countButton.setText(s);
    }

    public void add(int number, Class<? extends Critter> c) {

        if (started) {
            return;
        }

        started = true;
        myModel.add(number, c);
        started = false;
    }

    private void addTimer() {
        ActionListener updater = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doOneStep();
            }
        };
        myTimer = new javax.swing.Timer(0, updater);
        myTimer.setCoalesce(true);
    }

    private void doOneStep() {
        myModel.update();
        setCounts();
        myPicture.repaint();
    }

    private void multistep(int n) {
        myTimer.stop();
        do {
            myModel.update();
        } while (myModel.getSimulationCount() % n != 0);
        setCounts();
        myPicture.repaint();
    }
}