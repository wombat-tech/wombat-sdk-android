package app.wombat.androidsdk;

public class WombatSdkResult<T> {

    private final String errorMessage;
    private final T result;

    public WombatSdkResult(String errorMessage, T result) {
        this.errorMessage = errorMessage;
        this.result = result;
    }

    public Boolean isSuccess() {
        return result != null;
    }

    public T getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}