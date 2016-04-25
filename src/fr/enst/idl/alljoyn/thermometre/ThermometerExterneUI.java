package fr.enst.idl.alljoyn.thermometre;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;


public class ThermometerExterneUI extends JFrame implements ChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel container= new JPanel();
	private JSlider slider ;
	private JLabel jlblHeader, jlblSetTemp,jlblGetTemp;
	private ThermometreExterne thermometre;
	
public ThermometerExterneUI(ThermometreExterne thermometre){
	this.thermometre=thermometre;
	this.setSize(500, 280);
	this.setTitle("Gestionnaire Thermometre Externe");
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setLocationRelativeTo(null);
	this.setResizable(false);
	container.setLayout(null);
	container.setBackground(Color.white);
	
	 jlblHeader= new JLabel("<html><u>Gestionnaire Thermometre Externe<u></html>");
	 jlblHeader.setBounds(10, 10, 300, 20);
	 container.add(jlblHeader);
	 
	 jlblSetTemp= new JLabel("<html>Veuillez choisir la temperature exterieure : </html>");
	 jlblSetTemp.setIcon(new ImageIcon(getClass().getResource("/resources/mini-Thermometer-icon.png")));
	 jlblSetTemp.setBounds(50, 40, 400, 20);
	 container.add(jlblSetTemp);
	 
	 slider = new JSlider(-10,50);
	 slider.setBounds(50, 80, 400, 50);
	 slider.setPaintLabels(true); 
	 slider.setMinorTickSpacing(1);
	 slider.setMajorTickSpacing(5);
	 slider.setPaintTicks(true);
	 slider.setPaintLabels(true);
	 slider.setUI(new MySliderUI(slider));
	 slider.setBackground(Color.white);
	 slider.setOpaque(true);
	 slider.setValue(5);
	 container.add(slider);
	 slider.addChangeListener(this);
	 
	 jlblGetTemp= new JLabel();
	 refreshLabel();
	 jlblGetTemp.setBounds(30, 185, 400, 48);
	 container.add(jlblGetTemp);
	
	this.setContentPane(container);
	this.setVisible(true);
	}

	private static class MySliderUI extends BasicSliderUI {

	    private static float[] fracs = {0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f};
	    private LinearGradientPaint p;

	    public MySliderUI(JSlider slider) {
	        super(slider);
	    }

	    @Override
	    public void paintTrack(Graphics g) {
	        Graphics2D g2d = (Graphics2D) g;
	        Rectangle t = trackRect;
	        Point2D start = new Point2D.Float(t.x, t.y);
	        Point2D end = new Point2D.Float(t.width, t.height);
	        Color[] colors = {Color.magenta, Color.blue, Color.cyan,
	            Color.green, Color.yellow, Color.red};
	        p = new LinearGradientPaint(start, end, fracs, colors);
	        g2d.setPaint(p);
	        g2d.fillRect(t.x, t.y, t.width, t.height);
	    }

	    @Override
	    public void paintThumb(Graphics g) {
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setRenderingHint(
	            RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
	        Rectangle t = thumbRect;
	        g2d.setColor(Color.black);
	        int tw2 = t.width / 2;
	        g2d.drawLine(t.x, t.y, t.x + t.width - 1, t.y);
	        g2d.drawLine(t.x, t.y, t.x + tw2, t.y + t.height);
	        g2d.drawLine(t.x + t.width - 1, t.y, t.x + tw2, t.y + t.height);
	    }
	    @Override
	    protected Color getFocusColor(){
	         return new Color(255,255,255);
	     }
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		thermometre.setTemperature(slider.getValue());
		refreshLabel();
	}
	void refreshLabel(){
		jlblGetTemp.setText("<html>La temperature exterieure est de : "+slider.getValue()+" °C</html>");
		if(slider.getValue()<10){
			jlblGetTemp.setIcon(new ImageIcon(getClass().getResource("/resources/cold-ext.png")));
		}else if (slider.getValue()>25){
			jlblGetTemp.setIcon(new ImageIcon(getClass().getResource("/resources/hot-ext.png")));
		}else{
			jlblGetTemp.setIcon(new ImageIcon(getClass().getResource("/resources/ok-ext.png")));
		}
	}
	


	
	
}