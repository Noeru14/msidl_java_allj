package fr.enst.idl.alljoyn.radiateur;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import fr.enst.idl.alljoyn.radiateur.IRadiateur.Power;


public class RadiateurUI extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel container= new JPanel();
	private JLabel jlblGetPower,jlblSetPower;
	private JScrollPane jspClasse;
	private JList<Object> list;
	private JLabel jlblHeader;
	private Color color = new Color(124,163,76);
	private Radiateur radiateur;
	
	public RadiateurUI(Radiateur r ){
		this.radiateur=r;
		r.setUI(this);
		this.setSize(540, 220);
		this.setTitle("Gestionnaire Radiateur");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		container.setLayout(null);
		container.setBackground(Color.white);
		
		Object tab[]=new Object[3];
		tab[0]="Eteint";
		tab[1]="Fort";
		tab[2]="Faible";
		
		jlblHeader= new JLabel("<html><u>Gestionnaire Radiateur<u></html>");
		jlblHeader.setBounds(10, 10, 200, 20);
		container.add(jlblHeader);
		
		
		
		jlblSetPower= new JLabel("<html>Puissance du radiateur : </html>");
		jlblSetPower.setIcon(new ImageIcon(getClass().getResource("/resources/text-list-bullets-icon.png") ));
		jlblSetPower.setBounds(20, 40, 400, 20);
		container.add(jlblSetPower);
		
		list = new JList<Object>(tab);
		Map<Object, Icon> icons = new HashMap<Object, Icon>();
		icons.put("Eteint", new ImageIcon(getClass().getResource("/resources/mini-off-power-icon.png") ));
		icons.put("Fort", new ImageIcon(getClass().getResource("/resources/mini-high-power-icon.png") ));
		icons.put("Faible", new ImageIcon(getClass().getResource("/resources/mini-low-power-icon.png") ));
		
		list.setCellRenderer(new IconListRenderer(icons));
		
		jspClasse = new JScrollPane();
		jspClasse.setViewportView(list);
		
		// create a cell renderer to add the appropriate icon
		

		list.setOpaque(false);
		jspClasse.setBounds(20, 80, 180, 80);
		jspClasse.setOpaque(false);
		jspClasse.getViewport().setOpaque(false);
		list.setSelectedIndex(0);
		jspClasse.setBorder(null);
		container.add(jspClasse);

		jlblGetPower = new JLabel();
		jlblGetPower.setBounds(220,90,400,48);
		jlblGetPower.setIcon(new ImageIcon(getClass().getResource("/resources/ok.png")));
		container.add(jlblGetPower);
		refreshLabel();
		this.setContentPane(container);
		this.setVisible(true);
	}



	void refreshLabel(){
		Power power=radiateur.getPower();
		String mode="";
		if(power ==null){
			mode="Eteint";
		}else{
			switch(power){
			case MODE_FAIBLE:
				mode="Faible";
				break;
			case MODE_FORT:
				mode="Fort";
				break;
			case MODE_OFF:
				mode="Eteint";
				break;
			}
		}
		jlblGetPower.setText("<html>Le radiateur est en mode : "+mode+"</html>");
		if(power==Power.MODE_FORT){
			jlblGetPower.setIcon(new ImageIcon(getClass().getResource("/resources/high-power-icon.png")));
			list.setBorder(new LineBorder(Color.red, 2));
			list.setSelectedValue("Fort", true);
		}else if (power==Power.MODE_FAIBLE){
			jlblGetPower.setIcon(new ImageIcon(getClass().getResource("/resources/low-power-icon.png")));
			list.setBorder(new LineBorder(color, 2));
			list.setSelectedValue("Faible", true);
		}else{
			jlblGetPower.setIcon(new ImageIcon(getClass().getResource("/resources/off-power-icon.png")));
			list.setBorder(new LineBorder(Color.black, 2));
			list.setSelectedValue("Eteint", true);
		}
	}

	class IconListRenderer extends DefaultListCellRenderer {
	
		private static final long serialVersionUID = 1L;
		private Map<Object, Icon> icons = null;
		public IconListRenderer(Map<Object, Icon> icons) {
			this.icons = icons;
		}
		@Override
		public Component getListCellRendererComponent(
				JList<?> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value,index, isSelected, cellHasFocus);
				// Get icon to use for the list item value
				Icon icon = icons.get(value);
				// Set icon to display for value
				label.setIcon(icon);
				return label;
		}

	}
	
	
	
}