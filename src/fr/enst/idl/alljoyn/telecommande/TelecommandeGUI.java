package fr.enst.idl.alljoyn.telecommande;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.alljoyn.bus.BusException;

import fr.enst.idl.alljoyn.Utils.Mode;

public class TelecommandeGUI extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2146837364837312618L;
	private Telecommande myTelecommande;
	private JButton changeProfil;
	private JPanel mainPane, consigneProfilPane, componentsPane;
	private JPanel emptySpace0;
	private JLabel consTempLabel, componentsInfoTitle;
	private JScrollPane componentsInfoScroll;
	private JComboBox<String> roomBox;
	private JSpinner consTemp;
	private SpinnerModel consTempModel;
	private TelecommandeJTable abstractComponentsInfo;
	private ArrayList<String> columns;
	private ArrayList<Object[]> data;
	private JTable mComponentsInfo;
	private int defaultConsTemp = 18;
	private String curPiece;

	private Mode dispMode;
	private roomBoxListener roomBoxActionListener;

	public TelecommandeGUI(Telecommande _myTelecommande){
		this.myTelecommande = _myTelecommande;
		this.setTitle("Système de régulation - Télécommande");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setup();
		try {
			this.update();
		} catch (BusException e) {
			System.err.println("BusException");
			disconnect();
		}
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}


	private void setup(){
		mainPane = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Image image = new ImageIcon(getClass().getResource("/resources/stupid.png")).getImage();
				g.drawImage(image, 0, 0, null);
			}
		};
		mainPane.setPreferredSize(new Dimension(900, 600));

		emptySpace0 = new JPanel();
		emptySpace0.setPreferredSize(new Dimension(100, 0));

		String[] roomListTest = {"default"};
		roomBox = new JComboBox<String>(roomListTest);
		roomBox.setPreferredSize(new Dimension(300 ,100));
		roomBox.setFont(new Font("Calibri", Font.BOLD, 20));
		((JLabel)roomBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		roomBoxActionListener = new roomBoxListener();
		roomBox.addActionListener(roomBoxActionListener);

		consigneProfilPane = new JPanel();
		consigneProfilPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		consigneProfilPane.setPreferredSize(new Dimension(300, 300));
		consigneProfilPane.setOpaque(false);

		componentsPane = new JPanel();
		componentsPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		componentsPane.setPreferredSize(new Dimension(300, 500));
		componentsPane.setOpaque(false);

		columns = new ArrayList<String>();
		data = new ArrayList<Object[]>();
		columns.addAll(Arrays.asList("Capteur", "Etat"));
		abstractComponentsInfo = new TelecommandeJTable(columns, data);
		mComponentsInfo = new JTable(abstractComponentsInfo);

		componentsInfoTitle = new JLabel();
		componentsInfoTitle.setText("Informations capteurs");
		componentsInfoTitle.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		componentsInfoTitle.setFont(new Font("Calibri", Font.BOLD, 16));
		componentsInfoTitle.setOpaque(true);

		componentsInfoScroll = new JScrollPane();
		componentsInfoScroll.setPreferredSize(new Dimension(290, 400));
		componentsInfoScroll.setViewportView(mComponentsInfo);

		changeProfil = new JButton("OFF");
		changeProfil.setPreferredSize(new Dimension(120, 80));

		consTempModel = new SpinnerNumberModel(defaultConsTemp, // initial value
				defaultConsTemp - 18, // min
				defaultConsTemp + 22, // max
				0.1); 
		consTemp = new JSpinner(consTempModel);
		consTemp.addChangeListener(new SpinnerListener());
		JComponent editor = consTemp.getEditor();
		JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
		tf.setColumns(4);

		consTempLabel = new JLabel();
		consTempLabel.setText("Température de consigne : ");
		consTempLabel.setOpaque(true);
		consTempLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

		changeProfil.addActionListener(new ProfileListener());
		changeProfil.setOpaque(false);
		changeProfil.setContentAreaFilled(false);
		changeProfil.setFont(new Font("Calibri", Font.BOLD, 16));

		this.gblMgt();

		this.setContentPane(mainPane);
	}

	// management of the components using gridbaglayout (mainPane)
	private void gblMgt(){
		GridBagLayout gblMain = new GridBagLayout();
		mainPane.setLayout(gblMain);
		consigneProfilPane.setLayout(gblMain);
		componentsPane.setLayout(gblMain);

		GridBagConstraints gbc = new GridBagConstraints();

		// mainPane
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weighty = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 0;
		mainPane.add(roomBox, gbc);

		gbc.insets = new Insets(0, 0, 20, 0);
		gbc.gridx = 0;
		gbc.gridy = 1;
		mainPane.add(consigneProfilPane, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		mainPane.add(emptySpace0, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		mainPane.add(componentsPane, gbc);

		// consigneProfilPane
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 15, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		consigneProfilPane.add(consTempLabel, gbc);

		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.gridx = 1;
		gbc.gridy = 0;
		consigneProfilPane.add(consTemp, gbc);

		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 2;
		consigneProfilPane.add(changeProfil, gbc);

		// componentsPane
		gbc.weighty = 0.5;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.insets = new Insets(20, 0, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		componentsPane.add(componentsInfoTitle, gbc);

		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 1;
		componentsPane.add(componentsInfoScroll, gbc);
	}

	private void deletion(){
		abstractComponentsInfo.deleteData();
	}

	public void disconnect() {
		roomBox.removeAllItems();
		roomBox.setEnabled(false);
		consTemp.setEnabled(false);
		changeProfil.setText("KO");
		deletion();
		revalidate();
	}

	class ProfileListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//			System.out.println(myTelecommande.getMode(curPiece));
			//			System.out.println(getNextMode());
			myTelecommande.requestMode(getPiece(), getNextMode());
			setMode(myTelecommande.getMode(curPiece));
			//			System.out.println(myTelecommande.getMode(curPiece));
			revalidate();
		}
	}

	class SpinnerListener implements ChangeListener{
		public void stateChanged(ChangeEvent e){
			if(roomBox.getSelectedItem() != null){
				myTelecommande.requestTemp(getPiece(), getTemp());
			}
		}
	}

	class roomBoxListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(roomBox.getSelectedItem() != null){
				curPiece = roomBox.getSelectedItem().toString();		
				deletion();
				try {
					update();
				} catch(BusException be) {
					System.err.println("BusException");
					disconnect();
				}
			}
		}
	}

	private double getTemp(){
		return (double) consTemp.getValue();
	}

	private String getPiece(){
		return roomBox.getSelectedItem().toString();
	}

	private void setTemp(double temp){
		consTemp.setValue(temp);
	}

	private void setMode(Mode _curMode){
		dispMode = _curMode;
		switch(dispMode){
		case MODE_OFF:
			changeProfil.setText("OFF");
			break;
		case MODE_COMFORT:
			changeProfil.setText("CONFORT");
			break;
		case MODE_ECO:
			changeProfil.setText("ECO");
			break;
		}
	}

	private Mode getNextMode(){
		switch(dispMode){
		case MODE_COMFORT: return Mode.MODE_OFF;
		case MODE_OFF: return Mode.MODE_ECO;
		default: return Mode.MODE_COMFORT;
		}
	}

	private synchronized void setPiece(String[] piece){
		roomBox.removeActionListener(roomBoxActionListener);
		ArrayList<String> al = new ArrayList<>();
		for (String string : piece)
			al.add(string);
		for(int i=0; i<roomBox.getItemCount(); ++i) {
			int r = al.indexOf(roomBox.getItemAt(i));
			if(r != -1 ) {
				al.remove(r);
			}
		}
		for (String string : al)
			roomBox.addItem(string);
		roomBox.addActionListener(roomBoxActionListener);
	}

	private void setInfoCapteurs(String[] infoCapteurs){
		if(infoCapteurs == null){
			return;
		}
		data.clear();
		for (int i = 0 ; i < infoCapteurs.length ; i++) {
			StringTokenizer st = new StringTokenizer(infoCapteurs[i], "#");
			Object[] value = new Object[2];
			value[0] = st.nextToken();
			value[1] = st.nextToken();
			data.add(value);
		}
		abstractComponentsInfo.fireTableDataChanged();
	}

	public void update() throws BusException {
		if(curPiece == null){
			if(myTelecommande.getPieceList() != null && myTelecommande.getPieceList().length > 0){
				roomBox.setEnabled(true);
				consTemp.setEnabled(true);
				curPiece =  myTelecommande.getPieceList()[0];
				setTemp(myTelecommande.getTemp(curPiece));
				setMode(myTelecommande.getMode(curPiece));
				setPiece(myTelecommande.getPieceList());
				setInfoCapteurs(myTelecommande.requestCapteurs(curPiece));
			}
			else{
				roomBox.removeAllItems();
				roomBox.setEnabled(false);
				consTemp.setEnabled(false);
				changeProfil.setText("KO");
				deletion();
			}
		} else {
			roomBox.setEnabled(true);
			consTemp.setEnabled(true);
			setTemp(myTelecommande.getTemp(curPiece));
			setMode(myTelecommande.getMode(curPiece));
			setPiece(myTelecommande.getPieceList());
			setInfoCapteurs(myTelecommande.requestCapteurs(curPiece));
		}
	}
}
