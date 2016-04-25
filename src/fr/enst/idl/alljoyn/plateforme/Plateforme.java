package fr.enst.idl.alljoyn.plateforme;

import java.util.Map;
import java.util.StringTokenizer;

import org.alljoyn.bus.AboutListener;
import org.alljoyn.bus.AboutObj;
import org.alljoyn.bus.AboutObjectDescription;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Variant;

import fr.enst.idl.alljoyn.Utils.Mode;
import fr.enst.idl.alljoyn.fenetre.FenetreData;
import fr.enst.idl.alljoyn.fenetre.IFenetre;
import fr.enst.idl.alljoyn.radiateur.IRadiateur;
import fr.enst.idl.alljoyn.thermometre.IThermometre;


public class Plateforme implements AboutListener{

	public static final boolean INTERNE = true;
	public static final boolean EXTERNE = false;
	private static final short CONTACT_PORT = 42;

	static {
		System.loadLibrary("alljoyn_java");
	}

	private BusAttachment mBus;

	private Maison maison = new Maison();

	private boolean isJoined = false;

	public Plateforme() {

	}

	public void start() {
		mBus = new BusAttachment("Maison.Fenetre", BusAttachment.RemoteMessage.Receive);

		Status status = mBus.connect();
		if (status != Status.OK) {
			return;
		}
		System.out.println("BusAttachment.connect successful on " + System.getProperty("org.alljoyn.bus.address"));

		//AboutListener listener = new MyAboutListener();
		mBus.registerAboutListener(this);


		if (myWhoImplements("fr.enst.idl.alljoyn.interface.fenetre") != Status.OK) return;
		
		
		
		if (myWhoImplements("fr.enst.idl.alljoyn.interface.thermometre") != Status.OK) return;
		if (myWhoImplements("fr.enst.idl.alljoyn.interface.radiateur") != Status.OK) return;

		System.out.println("Configuring interface: ITelecommande");
		ITelecommandeImpl mySampleService = new ITelecommandeImpl();
		status = mBus.registerBusObject(mySampleService, IPlateforme.OBJECT_PATH);
		System.out.println("Interface configured.");

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
				System.out.println("Session established!");
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
				updateData();
			} catch (BusException e1) {
				e1.printStackTrace();
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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


		if(!path.equals("")) { 

			StringTokenizer str = new StringTokenizer(path, "/");
			if(str.countTokens()>1){
				String piece = str.nextToken();
				System.out.println("piece: "+piece);
				Piece p = maison.get(piece);
				String object = str.nextToken();
				ProxyBusObject mProxyObj;
				switch (object) {
				case "fenetre":
					mProxyObj =  mBus.getProxyBusObject(busName, path, sessionId.value, new Class<?>[] { IFenetre.class});
					IFenetre iFenetre = mProxyObj.getInterface(IFenetre.class);
					p.addFenetre(iFenetre);
					break;
				case "thermometre":
					mProxyObj =  mBus.getProxyBusObject(busName, path, sessionId.value, new Class<?>[] { IThermometre.class});
					IThermometre iThermometre = mProxyObj.getInterface(IThermometre.class);
					String thermosType = str.nextToken();
					if(thermosType.equals("interne")) {
						p.addThermometreInterne(iThermometre);
					} else if(thermosType.equals("externe")) {
						p.addThermometreExterne(iThermometre);
					}
					break;
				case "radiateur":
					mProxyObj = mBus.getProxyBusObject(busName, path, sessionId.value, new Class<?>[] { IRadiateur.class});
					IRadiateur iRadiateur = mProxyObj.getInterface(IRadiateur.class);
					p.addRadiateur(iRadiateur);
					break;
				default:
					System.out.println("#### "+ path + " ###");
					break;
				}

			}
		}
		isJoined = true;
	}


	public class ITelecommandeImpl implements IPlateforme, BusObject {

		@Override
		public void setTemperatureConsigne(String pieceName, double d) {
			maison.get(pieceName).setTemperatureConsigne(d);
		}

		@Override
		public void setMode(String pieceName, Mode mode) {
			maison.get(pieceName).setMode(mode);
		}

		@Override
		public String[] getListePiece() {
			String[] tab = new String[maison.size()];
			for(int i=0; i<maison.size(); ++i){
				tab[i] = maison.get(i).getName();
			}
			return tab;
		}

		@Override
		public String[] getCapteurs(String piece) {
			return maison.get(piece).getCapteurs();
		}

		@Override
		public Mode getMode(String piece) {
			Mode m = maison.get(piece).getMode();
			if(m == null)
				return Mode.MODE_OFF;
			else
				return m;
		}

		@Override
		public double getTemp(String piece) throws BusException {
			return maison.get(piece).getTempertureConsigne();
		}

	}

	public void updateData() throws BusException {
		maison.update();
	}


	public Status myWhoImplements(String iface) {
		String iFenetre[] = {iface};
		Status status = mBus.whoImplements(iFenetre);
		if (status != Status.OK) {
			return status;
		}
		System.out.println("BusAttachment.whoImplements successful " + iface);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return status;
	}

	public static void main(String[] args) {
		Plateforme p = new Plateforme();
		p.start();
	}	

}
