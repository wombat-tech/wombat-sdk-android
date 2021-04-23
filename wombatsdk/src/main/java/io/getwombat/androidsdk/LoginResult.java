package io.getwombat.androidsdk;

import androidx.annotation.NonNull;

public class LoginResult {
    @NonNull
    private String eosAccountName;
    @NonNull
    private String publicKey;

    LoginResult(@NonNull String eosAccountName, @NonNull String publicKey) {
        this.eosAccountName = eosAccountName;
        this.publicKey = publicKey;
    }

    @NonNull
    public String getEosAccountName() {
        return this.eosAccountName;
    }

    @NonNull
    public String getPublicKey() {
        return this.publicKey;
    }
}
