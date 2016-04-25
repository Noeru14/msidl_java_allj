package fr.enst.idl.alljoyn.thermometre;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;

@BusInterface (name = "fr.enst.idl.alljoyn.interface.thermometre", announced="true")
public interface IThermometre {

	@BusMethod(name = "getTemperature")
    public double getTemperature() throws BusException;
	
}
