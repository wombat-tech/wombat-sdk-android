package app.wombat.androidsdk;


public class LoginResult {

    private final String eosAccountName;

    private final String publicKey;

    LoginResult(String eosAccountName, String publicKey) {
        this.eosAccountName = eosAccountName;
        this.publicKey = publicKey;
    }


    public String getEosAccountName() {
        return this.eosAccountName;
    }


    public String getPublicKey() {
        return this.publicKey;
    }
}
