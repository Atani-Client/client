package tech.atani.client.utility.interfaces;

import tech.Catani;

public interface ClientInformationAccess {
    boolean DEVELOPMENT_SWITCH = true, BETA_SWITCH = true;

    // im2lazy2fix lol
    // also hello people that are looking through the src:)
    String CLIENT_NAME = Catani.catani ? "Catani" : "Atani";
    String CLIENT_VERSION = "0.0.4";
    String CLIENT_NAME_JAPANASE = "アタニ";
    String[] AUTHORS = new String[] {"Kellohylly", "MarkGG", "Geuxy", "Liticane", "Exterminate", "mistakedev"};
    String PREFIX = "§c" + CLIENT_NAME + " §7>>5 ";
}

