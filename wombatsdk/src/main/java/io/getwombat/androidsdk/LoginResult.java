package io.getwombat.androidsdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoginResult {
    @NonNull
    private String eosAccountName;
    @NonNull
    private String publicKey;

    @Nullable
    private String authenticateSignature;

    LoginResult(@NonNull String eosAccountName, @NonNull String publicKey, @Nullable String authenticateSignature) {
        this.eosAccountName = eosAccountName;
        this.publicKey = publicKey;
        this.authenticateSignature = authenticateSignature;
    }


    @NonNull
    public String getEosAccountName() {
        return this.eosAccountName;
    }

    @NonNull
    public String getPublicKey() {
        return this.publicKey;
    }

    @Nullable
    public String getAuthenticateSignature() {
        return this.authenticateSignature;
    }
}
