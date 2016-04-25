package fr.enst.idl.alljoyn.thermometre;

public class ThermometreInterne extends Thermometre{

	static {
        System.loadLibrary("alljoyn_java");
    }
	
    public ThermometreInterne() {
		super("Cuisine", 20);
	}
    
    public ThermometreInterne(String pieceName) {
    	super(pieceName, 20);
    }
    
    public ThermometreInterne(String pieceName, double t) {
    	super(pieceName, t);
    }
    
    public void start() {
    	super.start();
    }
    
    @Override
    public String toString() {
    	return "Thermometre interieur#"+temperature;
    }

    public static void main(String[] args) {
    	double temp = 20;
		String piece = "Cuisine";
		if(args.length>0) {
			piece = args[0];
			if(args.length>1) {
				try {
					temp = Double.parseDouble(args[0]);
				} catch(NumberFormatException nfe) {
					System.err.println("Argument not recognize");
				}
			}
		}
        ThermometreInterne ti = new ThermometreInterne(piece, temp);
        new ThermometerInterneUI(ti);
        ti.start();
    }
    
	
}
