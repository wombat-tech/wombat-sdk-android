package app.wombat.androidsdk;


import java.util.List;

public class TransactionSignResult {


    private final String serializedTransaction;

    private final List<String> signatures;

    TransactionSignResult(String serializedTransaction, List<String> signatures) {
        this.serializedTransaction = serializedTransaction;
        this.signatures = signatures;
    }


    public String getSerializedTransaction() {
        return serializedTransaction;
    }


    public List<String> getSignatures() {
        return signatures;
    }


}
