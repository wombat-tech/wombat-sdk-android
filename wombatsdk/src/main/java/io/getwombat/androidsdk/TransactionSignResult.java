package io.getwombat.androidsdk;

import androidx.annotation.NonNull;

import java.util.List;

public class TransactionSignResult {

    @NonNull
    private String serializedTransaction;
    @NonNull
    private List<String> signatures;

    TransactionSignResult(@NonNull String serializedTransaction, @NonNull List<String> signatures) {
        this.serializedTransaction = serializedTransaction;
        this.signatures = signatures;
    }

    @NonNull
    public String getSerializedTransaction() {
        return serializedTransaction;
    }

    @NonNull
    public List<String> getSignatures() {
        return signatures;
    }


}
