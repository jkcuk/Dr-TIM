package optics.raytrace.test;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class SwingWorkerTest {
    private JFrame frame;
    private JTextArea area;
    private JButton button;
    private SwingWorker<String, String> worker;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new SwingWorkerTest()::createAndShowGui);
    }

    private void createAndShowGui() {
        frame = new JFrame(getClass().getSimpleName());

        area = new JTextArea(10, 30);

        button = new JButton("Update!");
        button.addActionListener(listener);

        worker = new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    Thread.sleep(5000); //Simulates long running task
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "I'm done!"; //Returns the text to be set on the JTextArea
            }

            @Override
            protected void done() {
                super.done();
                try {
                    area.setText(get()); //Set the textArea the text given from the long running task
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        frame.add(area, BorderLayout.CENTER);
        frame.add(button, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private ActionListener listener = (e -> {
        area.setText("Processing...");
        worker.execute(); //Initializes long running task
    });
}