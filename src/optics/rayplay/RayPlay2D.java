package optics.rayplay;

import java.awt.*;

import javax.swing.*;

import optics.rayplay.core.RayPlay2DPanel;


class RayPlay2D extends JFrame
// implements ActionListener
{
	private static final long serialVersionUID = 5766147858386479499L;
	
    private RayPlay2DPanel rpPanel;
    // private LabelledDoublePanel focalLengthPanel;
    // private JButton saveButton;



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
        
//        saveButton = new JButton("Save");
//        saveButton.addActionListener(this);
//        panel.add(saveButton, BorderLayout.SOUTH);
        
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

//    private JFileChooser fileChooser;
//    
//	@Override
//	public void actionPerformed(ActionEvent e)
//	{
//		if(e.getSource().equals(saveButton))
//		{
//			if(fileChooser == null)
//			{
//				fileChooser = new JFileChooser();
//				fileChooser.setSelectedFile(new File("RayPlay2DSimulation"));
//			}
//			int returnVal = fileChooser.showSaveDialog(rpPanel);
//			
//			if (returnVal == JFileChooser.APPROVE_OPTION) {
//                File file = fileChooser.getSelectedFile();
//                
//                rpPanel.saveSVG(file.getAbsolutePath());
//    			rpPanel.saveParameters(file.getAbsolutePath());
//
//    			System.out.println("SVG image and parameters saved as \""+file.getAbsolutePath()+"\".");
//            }
//			else
//			{
//				System.out.println("Saving cancelled.");
//			}
//		}
//		
////		rpPanel.setfD(focalLengthPanel.getNumber());
////		rpPanel.init();
////		rpPanel.repaint();
//	}
}