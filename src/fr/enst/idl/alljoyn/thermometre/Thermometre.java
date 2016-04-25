package fr.enst.idl.alljoyn.thermometre;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;

import fr.enst.idl.alljoyn.AllObject;

public abstract class Thermometre extends AllObject {
	

	public double temperature;

	public Thermometre(String pieceName) {
		super(pieceName);
	}
	
	public Thermometre(String pieceName, double temp) {
		super(pieceName);
		temperature = temp;
	}
	
	public void start() {
		super.start();
    }
	
   public double getTemperature() throws BusException {
		return temperature;
	}
    
    public void setTemperature(double temperature){
		this.temperature=temperature;
	}
	
	
	
	public class IThermometreImpl implements IThermometre, BusObject {
		
		Thermometre t;
		
		public IThermometreImpl(Thermometre t) {
			this.t= t;
		}
		
		@Override
		public double getTemperature() throws BusException {
			return t.temperature;
		}
	}
}
