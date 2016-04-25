package fr.enst.idl.alljoyn.radiateur;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;


@BusInterface (name = "fr.enst.idl.alljoyn.interface.radiateur", announced="true")
public interface IRadiateur {
	
	public static enum Power {MODE_OFF, MODE_FAIBLE,MODE_FORT};
	
	public static final String OBJECT_PATH = "/radiateur";
	public static final String APP_NAME = "Radiateur";
	
    @BusMethod(name = "setPower",signature="i")
    public boolean setPower(Power power) throws BusException;

}
