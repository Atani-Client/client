package tech.atani.client.protection;

public class AtaniUser {

    private static AtaniUser instance;
    private final String username;
    private final String hwid;
    private final int uid;

    private final String discord;

    public AtaniUser(String username, String discord, String hwid, int uid) {
        this.username = username;
        this.discord = discord;
        this.hwid = hwid;
        this.uid = uid;
        instance = this;
    }

    public String getUsername() {
        return username;
    }

    public String getHwid() {
        return hwid;
    }

    public int getUID() {
        return uid;
    }

    public String getDiscord() {return discord;}

    public static AtaniUser getInstance() {
        return instance;
    }

}
