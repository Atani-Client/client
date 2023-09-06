package tech.atani.client.menace;

public class MenaceUser {

    private static MenaceUser instance;
    private final String username;
    private final String hwid;
    private final int uid;

    private final String discord;

    public MenaceUser(String username, String discord, String hwid, int uid) {
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

    public static MenaceUser getInstance() {
        return instance;
    }

}
