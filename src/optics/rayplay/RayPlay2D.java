package optics.rayplay;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;

class RayPlay2D extends JFrame
implements ActionListener
{
	private static final long serialVersionUID = 5766147858386479499L;
	
    private RayPlay2DPanel rpPanel;
    // private LabelledDoublePanel focalLengthPanel;



    public RayPlay2D() {
        initComponents();
    }

    private void initComponents()
    {
    	setTitle("RayPlay 2D");
    	
    	JPanel panel = new JPanel();
    	panel.setLayout(new BorderLayout());
        setContentPane(panel);

        rpPanel = new RayPlay2DPanel();
        panel.add(rpPanel);
        
//        focalLengthPanel = new LabelledDoublePanel("Focal length");
//        focalLengthPanel.setNumber(rpPanel.getfD());
//        focalLengthPanel.addActionListener(this);
//        panel.add(focalLengthPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    //set ui visible//
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RayPlay2D().setVisible(true);
            }
        });
    }

	@Override
	public void actionPerformed(ActionEvent e)
	{
//		rpPanel.setfD(focalLengthPanel.getNumber());
//		rpPanel.init();
//		rpPanel.repaint();
	}
}