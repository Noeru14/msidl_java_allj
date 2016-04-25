package fr.enst.idl.alljoyn.telecommande;

import java.util.Map;

import org.alljoyn.bus.AboutListener;
import org.alljoyn.bus.AboutObjectDescription;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Variant;

import fr.enst.idl.alljoyn.Utils.Mode;
import fr.enst.idl.alljoyn.plateforme.IPlateforme;

public class Telecommande implements AboutListener {

	private BusAttachment mBus;
	private boolean isJoined = false;
	private ProxyBusObject controllerProxyObj;
	private IPlateforme iProxyObj;

	private String[] pieces;
	
	private TelecommandeGUI UI;


	static {
		System.loadLibrary("alljoyn_java");
	}

	public void announced(String busName, int version, short port, AboutObjectDescription[] objectDescriptions, Map<String, Variant> aboutData) {
		System.out.println("Announced BusName:     " + busName);
		System.out.println("Announced Version:     " + version);
		System.out.println("Announced SessionPort: " + port);
		System.out.println("Announced ObjectDescription: ");
		String path = "";
		if(objectDescriptions != null) {
			for(AboutObjectDescription o : objectDescriptions) {
				path = o.path;
			}
		}

		SessionOpts sessionOpts = new SessionOpts();
		sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
		sessionOpts.isMultipoint = false;
		sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
		sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

		Mutable.IntegerValue sessionId = new Mutable.IntegerValue();

		mBus.enableConcurrentCallbacks();

		Status status = mBus.joinSession(busName, port, sessionId, sessionOpts, new SessionListener());
		if (status != Status.OK) {
			return;
		}
		System.out.println(String.format("BusAttachement.joinSession successful sessionId = %d", sessionId.value));

		if(path.equalsIgnoreCase(IPlateforme.OBJECT_PATH)) { 
			controllerProxyObj =  mBus.getProxyBusObject(busName, path, sessionId.value, new Class<?>[] { IPlateforme.class});
			iProxyObj = controllerProxyObj.getInterface(IPlateforme.class);
			isJoined = true;			
		}

	}


	public Status myWhoImplements(String iface) {
		String iFenetre[] = {iface};
		Status status = mBus.whoImplements(iFenetre);
		if (status != Status.OK) {
			return status;
		}
		System.out.println("BusAttachment.whoImplements successful " + iface);
		return status;
	}

	public Telecommande(){
		pieces = new String[0];
		//temps = new HashMap<String,Double>();
		//modes = new HashMap<String,Mode>();
		UI = null;
	}
	
	public void setUI(TelecommandeGUI ui){
		UI = ui;
	}

	public String[] getPieceList(){
		return pieces;
	}

	public double getTemp(String piece){
		try {
			return iProxyObj.getTemp(piece);
		} catch (BusException e) {
			e.printStackTrace();
			return -10000;
		}
	}

	public Mode getMode(String piece){
		if(piece == null) return null;
		try {
			return iProxyObj.getMode(piece);
		} catch (BusException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void requestTemp(String piece, double temp){
		try {
			iProxyObj.setTemperatureConsigne(piece, temp);
		} catch (BusException e) {
			e.printStackTrace();
		}
	}

	public void requestMode(String piece, Mode mode){
		try {
			iProxyObj.setMode(piece, mode);
		} catch (BusException e) {
			e.printStackTrace();
		}
	}
	
	public String[] requestCapteurs(String piece) throws BusException {
		String[] ar = iProxyObj.getCapteurs(piece);
		return ar;
	}

	public boolean isJoined() {
		return isJoined;
	}


	public void start() {

		mBus = new BusAttachment("Maison.Fenetre", BusAttachment.RemoteMessage.Receive);

		Status status = mBus.connect();
		if (status != Status.OK) {
			return;
		}
		System.out.println("BusAttachment.connect successful on " + System.getProperty("org.alljoyn.bus.address"));
		mBus.registerAboutListener(this);

		if (myWhoImplements("fr.enst.idl.alljoyn.interface.plateforme") != Status.OK) return;


		while(!isJoined) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("Program interupted");
			}
		}
		System.out.println("BusAttachement.joinSession successful calling echo method");

		while(true) {
			try {
				pieces = iProxyObj.getListePiece();
				
				if(UI != null){
					UI.update();
				}
			} catch (BusException e1) {
				System.err.println("Plateforme disconnect");
				UI.disconnect();
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Telecommande tel = new Telecommande();
		TelecommandeGUI ui = new TelecommandeGUI(tel);
		tel.setUI(ui);
		
		tel.start();
	}
}
