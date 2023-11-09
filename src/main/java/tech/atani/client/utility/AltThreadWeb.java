package tech.atani.client.utility;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.util.Session;
import tech.atani.client.utility.interfaces.Methods;

public final class AltThreadWeb extends Thread implements Methods {

    private String status;
    boolean Mojang;

    public AltThreadWeb(boolean mojang) {
        super("Alt Thread Web");
        Mojang = mojang;
    }

    private Session createSession() {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult result = authenticator.loginWithWebview();
            return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang");
        } catch (MicrosoftAuthenticationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void run() {
        Session auth = this.createSession();
        if (auth != null) {
            mc.session = auth;
        }
    }
}