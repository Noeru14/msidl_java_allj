package fr.enst.idl.alljoyn.radiateur;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;

import fr.enst.idl.alljoyn.AllObject;
import fr.enst.idl.alljoyn.radiateur.IRadiateur.Power;

public class Radiateur extends AllObject{

	/*public static final int MODE_OFF    = 0;
	public static final int MODE_FAIBLE = 1;
	public static final int MODE_FORT   = 2;*/
	
	
	static {
		System.loadLibrary("alljoyn_java");
	}
	
    private Power power;
    private RadiateurUI UI;
    
    public Radiateur(String pieceName) {
		super(pieceName);
	}
    
    public Power getPower() {
    	return power;
    }
    
    public boolean setPower(Power p) throws BusException {
		power = p;
		System.out.println("calling set power " +UI == null);
		if(UI != null){
			UI.refreshLabel();
		}
		return true;
	}
    
    public class IRadiateurImpl implements IRadiateur, BusObject {
        @Override
		public boolean setPower(Power p) throws BusException {
			power = p;
			if(UI != null){
				UI.refreshLabel();
			}
			return true;
		}
    }
    
    public void setUI(RadiateurUI ui){
    	UI = ui;
    }
    
    @Override
    public String toString() {
    	if(power == IRadiateur.Power.MODE_OFF)
    		return "Radiateur#off";
    	else if(power ==IRadiateur.Power.MODE_FAIBLE)
    		return "Radiateur#faible";
    	else if(power == IRadiateur.Power.MODE_FORT)
    		return "Radiateur#fort";
    	else
    		return "";
    }
    
	
	public static void main(String[] args) {
		String piece = "Cuisine";
		if(args.length>0)
			piece = args[0];
		Radiateur r = new Radiateur(piece);
		new RadiateurUI(r);
		r.start();
	}
	
}
