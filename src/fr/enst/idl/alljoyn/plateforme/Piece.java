package fr.enst.idl.alljoyn.plateforme;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;

import fr.enst.idl.alljoyn.Utils;
import fr.enst.idl.alljoyn.Utils.Mode;
import fr.enst.idl.alljoyn.fenetre.IFenetre;
import fr.enst.idl.alljoyn.radiateur.IRadiateur;
import fr.enst.idl.alljoyn.radiateur.IRadiateur.Power;
import fr.enst.idl.alljoyn.thermometre.IThermometre;

public class Piece {

	private String name;
	private double temperatureInterne;
	private double temperatureExterne;
	private boolean isOneOpen;
	private Power radiateurMode;
	
	private double temperatureConsigne;
	private Mode mode;
	
	private Vector<IFenetre> fenetres = new Vector<>();
	private Vector<IThermometre> thermosInte= new Vector<>();
	private Vector<IThermometre> thermosExte= new Vector<>();
	private Vector<IRadiateur> radiateurs= new Vector<>();


	public Piece(String name) {
		this.name = name;
		this.mode = Mode.MODE_OFF;
	}

	public String getName() {
		return name;
	}

	private void updateTemp(boolean flag) throws BusException {
		Vector<IThermometre> v;
		if(flag == Plateforme.INTERNE){
			System.out.println("\tAsking Internal Temperature");
			v = thermosInte;
		} else {
			System.out.println("\tAsking External Temperature");
			v = thermosExte;
		}
		System.out.println("\t\t"+v.size()+" radiateurs connectes");
		Vector<Double> values = new Vector<>();
		Vector<Integer> invalids = new Vector<>();
		for(int i=0; i<v.size(); ++i) {
			try {
				System.out.println("\t\t\tT "+i+" "+v.get(i).getTemperature());
				values.add(v.get(i).getTemperature());
			} catch(ErrorReplyBusException erbe) {
				invalids.add(i);
			}
		}
		for (Integer integer : invalids) {
			System.out.println("\t\tsupprime le thermometre n°"+integer);
			v.removeElementAt(integer);
		}
		double sum = 0;
		for (Double value : values) {
			sum += value;
		}
		if(flag == Plateforme.INTERNE)
			temperatureInterne = sum/v.size();
		else
			temperatureExterne = sum/v.size();
		
	}
	
	public int getNCapteurs() {
		return fenetres.size()+thermosExte.size()+thermosInte.size()+radiateurs.size();
	}

	private void updateWindowsStatus() throws BusException{
		System.out.println("\tAsking Fenetre");
		System.out.println("\t\t"+fenetres.size()+" capteurs connectes");
		isOneOpen = false;
		Vector<Integer> invalids = new Vector<>();
		for(int i=0; i<fenetres.size(); ++i) {
			try {
				isOneOpen = isOneOpen || fenetres.get(i).isOpen();
			} catch(ErrorReplyBusException erbe) {
				invalids.add(i);
			}
		}
		for (Integer integer : invalids) {
			System.out.println("\t\tsupprime la fenetre n°"+integer);
			fenetres.removeElementAt(integer);
		}
		if(isOneOpen)
			System.out.println("\t\tUne fenetre est ouverte");
		else
			System.out.println("\t\tLes fenetres sont fermees");
	}

	private void updatePower() throws BusException {
		System.out.println("\tSetting power");
		System.out.println("\t\t"+radiateurs.size()+" radiateurs connectes");
		Vector<Integer> invalids = new Vector<>();
		for(int i=0; i<radiateurs.size(); ++i) {
			try {
				if(radiateurMode==null){
					radiateurMode=Power.MODE_OFF;
				}
				radiateurs.get(i).setPower(radiateurMode);
			} catch(ErrorReplyBusException erbe) {
				invalids.add(i);
			}
		}
		for (Integer integer : invalids) {
			System.out.println("\t\tsupprime le radiateur n°"+integer);
			fenetres.removeElementAt(integer);
		}
	}
	
	private double getCoef() {
		if(mode == Mode.MODE_COMFORT)
			return Utils.COEF_COMFORT;
		else if(mode == Mode.MODE_ECO)
			return Utils.COEF_ECO;
		else
			return 0;
	}

	private double getDelta() {
		if(mode == Mode.MODE_COMFORT)
			return Utils.DELTA_COMFORT;
		else if(mode == Mode.MODE_ECO)
			return Utils.DELTA_ECO;
		else
			return 0;
	}

	
	private void updateMode() {
		double grad = getCoef() * (temperatureConsigne-temperatureExterne);
		if(isOneOpen || mode == Mode.MODE_OFF) {
			radiateurMode = Power.MODE_OFF;
		} else if(temperatureInterne>temperatureConsigne) {
			radiateurMode = Power.MODE_OFF;
		} else if(temperatureExterne<temperatureInterne && temperatureInterne<temperatureExterne+grad) {
			radiateurMode = Power.MODE_FORT;
		} else if(temperatureExterne+grad<temperatureInterne && temperatureInterne<temperatureConsigne-getDelta()) {
			radiateurMode = Power.MODE_FAIBLE;
		} else if(temperatureConsigne-getDelta()<temperatureInterne && temperatureInterne< temperatureConsigne) {
			radiateurMode = Power.MODE_OFF;
		}
		System.out.println("\tMode des radiateurs: "+radiateurMode);
	}
	

	public void update() throws BusException {
		System.out.println("##################################");
		System.out.println("Update Piece: "+name);
		Calendar c = new GregorianCalendar();
		System.out.println("\tUpdate time: "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND));
		
		if(mode == Mode.MODE_OFF)
			System.out.println("\tMode choisi: Arrete");
		else if(mode == Mode.MODE_ECO)
			System.out.println("\tMode choisi: Eco");
		else
			System.out.println("\tMode choisi: Comfort");
		System.out.println("\tTemperature de consigne: "+temperatureConsigne);
		updateTemp(Plateforme.EXTERNE);
		updateTemp(Plateforme.INTERNE);
		updateWindowsStatus();
		updateMode();
		updatePower();
	}

	public void addFenetre(IFenetre iFenetre) {
		fenetres.add(iFenetre);
	}

	public void addThermometreInterne(IThermometre iThermometre) {
		thermosInte.add(iThermometre);
	}

	public void addThermometreExterne(IThermometre iThermometre) {
		thermosExte.add(iThermometre);
	}

	public void addRadiateur(IRadiateur iRadiateur) {
		radiateurs.add(iRadiateur);
	}

	public void setTemperatureConsigne(double d) {
		this.temperatureConsigne = d;
	}
	
	public double getTempertureConsigne(){
		return this.temperatureConsigne;
	}
	
	
	public String[] getCapteurs() {
		String[] tab = new String[4];
		if(isOneOpen)
			tab[0] = "Fenetre#Ouvert";
		else
			tab[0] = "Fenetre#Fermees";
		tab[1] = "Temperature Interieur#"+temperatureInterne;
		tab[2] = "Temperature Exterieur#"+temperatureExterne;
		if(radiateurMode == Power.MODE_OFF)
			tab[3] = "Radiateurs#Off";
		else if(radiateurMode == Power.MODE_FAIBLE)
			tab[3] = "Radiateurs#Faible";
		else
			tab[3] = "Radiateurs#Fort";
		return tab;
	}
	
	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}



}
