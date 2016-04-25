package fr.enst.idl.alljoyn;

import org.alljoyn.bus.AboutObj;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;

import fr.enst.idl.alljoyn.fenetre.Fenetre;
import fr.enst.idl.alljoyn.fenetre.FenetreData;
import fr.enst.idl.alljoyn.fenetre.IFenetre;
import fr.enst.idl.alljoyn.radiateur.IRadiateur;
import fr.enst.idl.alljoyn.radiateur.Radiateur;
import fr.enst.idl.alljoyn.radiateur.Radiateur.IRadiateurImpl;
import fr.enst.idl.alljoyn.thermometre.IThermometreExterne;
import fr.enst.idl.alljoyn.thermometre.IThermometreInterne;
import fr.enst.idl.alljoyn.thermometre.Thermometre.IThermometreImpl;
import fr.enst.idl.alljoyn.thermometre.ThermometreExterne;
import fr.enst.idl.alljoyn.thermometre.ThermometreInterne;



public abstract class AllObject {
	
	public static int THERMOMETRE_EXTERNE = 0;
	public static int THERMOMETRE_INTERNE = 1;
	public static int FENETRE = 2;
	
	private static final short CONTACT_PORT=42;

    static boolean sessionEstablished = false;
    static int sessionId;
	
    private String pieceName;
    
    public AllObject(String pieceName) {
		this.pieceName = pieceName;
	}
    
    private String getPath() {
    	if(this instanceof ThermometreExterne) return "/"+pieceName+IThermometreExterne.OBJECT_PATH;
        else if(this instanceof ThermometreInterne) return "/"+pieceName+IThermometreInterne.OBJECT_PATH;
        else if(this instanceof Fenetre) return "/"+pieceName+IFenetre.OBJECT_PATH;
        else if(this instanceof Radiateur) return "/"+pieceName+IRadiateur.OBJECT_PATH;
        return null;
    }
    
	public void start() {
		
		System.out.println("#############################");
		System.out.println(pieceName+" "+getPath());
		
		
		
		BusAttachment mBus;
        mBus = new BusAttachment("Maison.Fenetre", BusAttachment.RemoteMessage.Receive);

        Status status = Status.OK;
        
        if(this instanceof ThermometreExterne) {
        	ThermometreExterne te = (ThermometreExterne) this;
        	IThermometreImpl mySampleService = te.new IThermometreImpl(te);
        	status = mBus.registerBusObject(mySampleService, getPath());
        } else if(this instanceof ThermometreInterne) {
        	ThermometreInterne ti = (ThermometreInterne) this;
        	IThermometreImpl mySampleService = ti.new IThermometreImpl(ti);
        	status = mBus.registerBusObject(mySampleService, getPath());
        } else if(this instanceof Fenetre) {
        	Fenetre f = (Fenetre) this;
//        	SampleService mySampleService = f.new SampleService();
        	status = mBus.registerBusObject(f, getPath());
        } else if(this instanceof Radiateur) {
        	Radiateur r = (Radiateur) this;
        	IRadiateurImpl mySampleService = r.new IRadiateurImpl();
        	status = mBus.registerBusObject(mySampleService, getPath());
        }

        if (status != Status.OK) {
            return;
        }
        System.out.println("BusAttachment.registerBusObject successful");

        mBus.registerBusListener(new BusListener());

        status = mBus.connect();
        if (status != Status.OK) {

            return;
        }
        System.out.println("BusAttachment.connect successful on " + System.getProperty("org.alljoyn.bus.address"));

        Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

        SessionOpts sessionOpts = new SessionOpts();
        sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
        sessionOpts.isMultipoint = false;
        sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
        sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

        status = mBus.bindSessionPort(contactPort, sessionOpts,
                new SessionPortListener() {
            public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
                System.out.println("SessionPortListener.acceptSessionJoiner called");
                if (sessionPort == CONTACT_PORT) {
                    return true;
                } else {
                    return false;
                }
            }
            public void sessionJoined(short sessionPort, int id, String joiner) {
                System.out.println(String.format("SessionPortListener.sessionJoined(%d, %d, %s)", sessionPort, id, joiner));
                sessionId = id;
                sessionEstablished = true;
            }
        });
        if (status != Status.OK) {
            return;
        }

        AboutObj aboutObj = new AboutObj(mBus);
        status = aboutObj.announce(contactPort.value, new FenetreData());
        if (status != Status.OK) {
            System.out.println("Announce failed " + status.toString());
            return;
        }
        System.out.println("Announce called announcing SessionPort: " + contactPort.value);

        while (!sessionEstablished) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("Thread Exception caught");
                e.printStackTrace();
            }
        }
        System.out.println("BusAttachment session established");

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Thread Exception caught");
                e.printStackTrace();
            }
        }
	}
	
}
