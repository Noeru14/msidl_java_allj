package fr.enst.idl.alljoyn.plateforme;

import java.util.Vector;

import org.alljoyn.bus.BusException;

import fr.enst.idl.alljoyn.Utils.Mode;

public class Maison extends Vector<Piece> {

	private static final long serialVersionUID = 1L;

	public synchronized Piece get(String name) {
		for (Piece piece : this) {
			if(piece.getName().equalsIgnoreCase(name)){
				return piece;
			}
		}
		Piece p = new Piece(name);
		this.add(p);
		return p;
	}
	
	public synchronized void checkPiece() {
		for(int i=0; i<size(); ++i) {
			if(get(i).getNCapteurs() == 0) {
				System.out.println("Removing room: "+get(i).getName());
				remove(i);
			}
		}
	}

	public void update() throws BusException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println("Start update");
		checkPiece();
		for (Piece piece : this) {
			piece.update();
		}
	}

	public void setMode(Mode mode2) {
	}
	
	
}
