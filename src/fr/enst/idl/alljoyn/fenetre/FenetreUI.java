package fr.enst.idl.alljoyn.fenetre;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class FenetreUI extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel container= new JPanel();
	private JButton btnOpenClose;
	private JLabel jlblHeader, jlblSetStatus,jlblGetStatus;
	private Fenetre fenetre;
	
	public FenetreUI(Fenetre f ){
	this.setSize(450, 220);
	this.setTitle("Gestionnaire Fenetre");
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setLocationRelativeTo(null);
	this.setResizable(false);
	this.fenetre=f;
	container.setLayout(null);
	container.setBackground(Color.white);
	
	 jlblHeader= new JLabel("<html><u>Gestionnaire Fenetre <u></html>");
	 jlblHeader.setBounds(10, 10, 200, 20);
	 container.add(jlblHeader);
	 
	 jlblSetStatus= new JLabel("<html>Cliquez pour ouvrir / fermer la fenetre : </html>");
	 jlblSetStatus.setBounds(50, 70, 300, 20);
	 container.add(jlblSetStatus);
	 
	 btnOpenClose= new JButton();
	 btnOpenClose.setBackground(Color.white);
	 btnOpenClose.setIcon(new ImageIcon(getClass().getResource("/resources/window-param-icon.png")));
	 btnOpenClose.setBounds(350, 50, 64, 64);
	 btnOpenClose.addActionListener(this);
	 btnOpenClose.setBorderPainted(false);
	 container.add(	btnOpenClose);
	 
	 jlblGetStatus= new JLabel();
	 refreshLabel();
	 jlblGetStatus.setBounds(125, 110, 300, 48);
	 container.add(jlblGetStatus);
	
	this.setContentPane(container);
	this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(fenetre.isOpen()){
			fenetre.setOpen(false);
			refreshLabel();	
		}else{			
			fenetre.setOpen(true);
			refreshLabel();
		}
		
	}
	void refreshLabel(){
		if(fenetre.isOpen()){
			jlblGetStatus.setText("La fenetre est ouverte");
			jlblGetStatus.setIcon(new ImageIcon(getClass().getResource("/resources/open-folded-icon.png")));
		}else {
			jlblGetStatus.setText("La fenetre est fermee");
			jlblGetStatus.setIcon(new ImageIcon(getClass().getResource("/resources/closed-folded-icon.png")));
		}
	}
	


	
	
}