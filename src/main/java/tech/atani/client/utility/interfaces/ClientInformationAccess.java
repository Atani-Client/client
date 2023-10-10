package tech.atani.client.utility.interfaces;

public interface ClientInformationAccess {
    boolean DEVELOPMENT_SWITCH = true, BETA_SWITCH = true, catani = true;

    String CLIENT_NAME = catani ? "Catani" : "Atani", CLIENT_VERSION = "0.0.4", CLIENT_NAME_JAPANASE = "アタニ";
    String[] AUTHORS = new String[] {"Kellohylly", "MarkGG", "Geuxy", "Liticane", "Exterminate", "mistakedev"};
    String PREFIX = "§c" + CLIENT_NAME + " §7>>5 ";
}
