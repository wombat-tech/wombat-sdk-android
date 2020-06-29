package io.getwombat.androidsdk;

import androidx.annotation.NonNull;

public class AuthenticateOptions {
    private String nonce;
    private String data;

    @NonNull
    String getNonce() {
        return nonce;
    }

    @NonNull
    String getData() {
        return data;
    }

    public AuthenticateOptions(@NonNull String nonce, @NonNull String data) {
        this.nonce = nonce;
        this.data = data;
    }
}
