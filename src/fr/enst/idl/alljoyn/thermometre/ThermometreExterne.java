package fr.enst.idl.alljoyn.thermometre;

public class ThermometreExterne extends Thermometre{

	static {
		System.loadLibrary("alljoyn_java");
	}

	public ThermometreExterne() {
		super("Cuisine", 5);
	}

	public ThermometreExterne(String pieceName) {
		super(pieceName);
	}

	public ThermometreExterne(String pieceName, double t) {
		super(pieceName, t);
	}

	public void start() {
		super.start();
	}
	
	@Override
    public String toString() {
    	return "Thermometre exterieur#"+temperature;
    }

	public static void main(String[] args) {

		double temp = 5;
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
		ThermometreExterne te = new ThermometreExterne(piece, temp);
		new ThermometerExterneUI(te);
		te.start();

	}


}
