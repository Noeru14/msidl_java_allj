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

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.AboutDataListener;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.Version;

public class FenetreData implements AboutDataListener {

	public static final byte[] ID = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
	public static final String DEVICE_ID = "93c06771-c725-48c2-b1ff-6a2a59d445b8";
	public static final String MANUFACTURER = "Les fous de alljoyn";
	public static final String MODEL_NUMBER = "NUMBER1";
	public static final String[] SUPPPORTED_LANGUAGE = { "en", "es" };
	public static final String DEFAULT_LANGUAGE = "en";
	public static final String DESCRIPTION = "La fenêtre nord la chambre";
	public static final String SOFTWARE_VERSION = "1.0";
	
    @Override
    public Map<String, Variant> getAboutData(String language) throws ErrorReplyBusException {
        System.out.println("FenetreData.getAboutData was called for `"
                + language + "` language.");
        Map<String, Variant> aboutData = new HashMap<String, Variant>();
        // nonlocalized values
        //aboutData.put("DeviceName", new Variant("A device name"));
        aboutData.put("AppId", new Variant(ID));
        aboutData.put("DefaultLanguage", new Variant(DEFAULT_LANGUAGE));
        
        aboutData.put("DeviceId", new Variant(DEVICE_ID));
        aboutData.put("AppName", new Variant(IFenetre.APP_NAME));
        
        aboutData.put("Manufacturer", new Variant(MANUFACTURER));
        aboutData.put("ModelNumber", new Variant(MODEL_NUMBER));
        aboutData.put("SupportedLanguages", new Variant(SUPPPORTED_LANGUAGE));
        aboutData.put("Description", new Variant(DESCRIPTION));
        aboutData.put("SoftwareVersion", new Variant(SOFTWARE_VERSION));
        aboutData.put("AJSoftwareVersion", new Variant(Version.get()));
        
        //aboutData.put("DateOfManufacture", new Variant(new String("2014-09-23")));
        //
        //aboutData.put("HardwareVersion", new Variant(new String("0.1alpha")));
        //aboutData.put("SupportUrl", new Variant(new String(
          //      "http://www.example.com/support")));
        // localized values
        // If the language String is null or an empty string we return the default
        // language
        /*
        if ((language == null) || (language.length() == 0) || language.equalsIgnoreCase("en")) {
            aboutData.put("DeviceName", new Variant("A device name"));
            aboutData.put("AppName", new Variant("An application name"));
            aboutData.put("Manufacturer", new Variant(new String(
                    "A mighty manufacturing company")));
            aboutData.put("Description",
                    new Variant( "Sample showing the about feature in a service application"));
        } else if (language.equalsIgnoreCase("es")) { // Spanish
            aboutData.put("DeviceName", new Variant(new String(
                    "Un nombre de dispositivo")));
            aboutData.put("AppName", new Variant(
                    new String("Un nombre de aplicación")));
            aboutData.put("Manufacturer", new Variant(new String(
                    "Una empresa de fabricación de poderosos")));
            aboutData.put("Description",
                    new Variant( new String("Muestra que muestra la característica de sobre en una aplicación de servicio")));
        } else {
            throw new ErrorReplyBusException(Status.LANGUAGE_NOT_SUPPORTED);
        }
        */
        return aboutData;
    }

    @Override
    public Map<String, Variant> getAnnouncedAboutData() throws ErrorReplyBusException {
        System.out.println("FenetreData.getAnnouncedAboutData was called.");
        Map<String, Variant> aboutData = new HashMap<String, Variant>();
        aboutData.put("AppId", new Variant(ID));
        aboutData.put("DefaultLanguage", new Variant(DEFAULT_LANGUAGE));
        //aboutData.put("DeviceName", new Variant("A device name"));
        aboutData.put("DeviceId", new Variant(DEVICE_ID));
        aboutData.put("AppName", new Variant(IFenetre.APP_NAME));
        aboutData.put("Manufacturer", new Variant(MANUFACTURER));
        aboutData.put("ModelNumber", new Variant(MODEL_NUMBER));
        return aboutData;
    }

}
