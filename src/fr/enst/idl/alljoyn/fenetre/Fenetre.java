/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package fr.enst.idl.alljoyn.fenetre;

import org.alljoyn.bus.BusObject;

import fr.enst.idl.alljoyn.AllObject;



public class Fenetre extends AllObject implements IFenetre, BusObject{

	static {
		System.loadLibrary("alljoyn_java");
	}

	private boolean isOpen = false;

	public Fenetre(String pieceName) {
		super(pieceName);
	}

	public boolean isOpen() {
		return isOpen;
	}


	public void setOpen(boolean b){
		isOpen = b;
	}

	@Override
	public String getString() {
		if(isOpen)
			return "Fenetre#ouverte";
		else
			return "Fentre#fermee";
	}

	public static void main(String[] args) {
		String piece = "Cuisine";
		if(args.length>0)
			piece = args[0];

		Fenetre f = new Fenetre(piece);
		new FenetreUI(f);
		f.start();
	}
}
