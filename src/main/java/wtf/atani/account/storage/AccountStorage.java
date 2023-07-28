package wtf.atani.account.storage;

import de.florianmichael.rclasses.storage.Storage;
import wtf.atani.account.Account;

public class AccountStorage extends Storage<Account> {

    private static AccountStorage instance;

    public AccountStorage() {
        this.instance = this;
        init();
    }

    @Override
    public void init() {
        // Accounts are added with files
    }

    public static AccountStorage getInstance() {
        return instance;
    }
}
