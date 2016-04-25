package fr.enst.idl.alljoyn.plateforme;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;

import fr.enst.idl.alljoyn.Utils.Mode;


@BusInterface (name = "fr.enst.idl.alljoyn.interface.plateforme", announced="true")
public interface IPlateforme {
	public static final String OBJECT_PATH = "/thermoregulation/controller";
	public static final String APP_NAME = "Controller";
	
	@BusMethod(name = "setTemperatureConsigne")
	public void setTemperatureConsigne(String pieceName, double d) throws BusException;

	@BusMethod(name = "setMode", signature="si")
	public void setMode(String pieceName, Mode mode) throws BusException;
	
	@BusMethod(name = "getListePiece")
	public String[] getListePiece() throws BusException;
	
	@BusMethod(name = "getCapteurs", signature="s")
	public String[] getCapteurs(String piece) throws BusException;
	
	@BusMethod(name = "getTemp")
	public double getTemp(String piece) throws BusException;
	
	@BusMethod(name = "getMode", replySignature = "i", signature="s")
	public Mode getMode(String piece) throws BusException;
	
	
	
}
