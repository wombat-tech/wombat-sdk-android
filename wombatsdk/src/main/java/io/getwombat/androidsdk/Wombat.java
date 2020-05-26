package io.getwombat.androidsdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import static io.getwombat.androidsdk.Constants.*;

public class Wombat {

    /**
     * Check if Wombat is currently available on this device
     * @param context a context object, e.g. an {@link android.app.Activity} or the application context
     * @return whether the Wombat app is available
     */
    public static Boolean isAvailable(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(WOMBAT_PACKAGE, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * @param blockchain which chain to use, currently supported are EOS and TELOS
     * @return an Intent to be used with {@link android.app.Activity#startActivityForResult(Intent, Integer)}
     */
    @NonNull
    public static Intent getLoginIntent(Blockchain blockchain) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(WOMBAT_PACKAGE, LOGIN_ACTIVITY_CLASS));
        intent.putExtra(EXTRA_BLOCKCHAIN, blockchain.name());
        return intent;
    }

    /**
     * @see Wombat#getLoginIntent(Blockchain)
     * defaults to {@link Blockchain#EOS}
     */
    @NonNull
    public static Intent getLoginIntent() {
        return getLoginIntent(Blockchain.EOS);
    }

    /**
     * Creates an intent to be used with {@link android.app.Activity#startActivityForResult(Intent, int)}
     * Result must be obtained using {@link #getTransactionSignResultFromIntent(Intent)} in the activities {@link android.app.Activity#onActivityResult(int, int, Intent)} callback
     *
     * @param serializedTransaction the raw hex transaction to be signed
     * @param modifiable            whether Wombat may adjust the transaction. This will be used to pay for the users' CPU and NET. If set to true,
     *                              you MUST use the new transaction ({@link TransactionSignResult#getSerializedTransaction()}})returned in {@link #getTransactionSignResultFromIntent(Intent)}.
     * @param blockchain            which blockchain this transaction is intended for
     * @return the intent which an activity needs to send
     */

    @NonNull
    public static Intent getTransactionSignIntent(@NonNull String serializedTransaction, Blockchain blockchain, boolean modifiable) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(WOMBAT_PACKAGE, SIGNATURE_ACTIVITY_CLASS));
        intent.putExtra(EXTRA_SERIALIZED_TRANSACTION, serializedTransaction);
        intent.putExtra(EXTRA_MODIFIABLE, modifiable);
        intent.putExtra(EXTRA_BLOCKCHAIN, blockchain.name());
        return intent;
    }

    /**
     * @see Wombat#getTransactionSignIntent(String, boolean, Blockchain)
     * defaults to {@link Blockchain#EOS}
     */
    @NonNull
    public static Intent getTransactionSignIntent(@NonNull String serializedTransaction, boolean modifiable) {
        return getTransactionSignIntent(serializedTransaction, Blockchain.EOS, modifiable);
    }

    /**
     * Creates an intent to be used with {@link android.app.Activity#startActivityForResult(Intent, int)}
     *
     * @param actionsJson the json representing an array of actions.
     *                    [
     *                      {
     *                          "account": "eosio.token",
     *                          "name": "transfer",
     *                          "authorization": [
     *                              {
     *                                  "actor": "eosaccntname",
     *                                  "permission": "active"
     *                              }
     *                          ],
     *                          "data": {
     *                              "from": "eosaccntname",
     *                              "memo": "...",
     *                              "quantity": "1.0000 EOS",
     *                              "to": "otheraccount"
     *                          }
     *                      }
     *                    ]
     * @param blockchain  which blockchain this transaction is intended for
     * @return an Intent to be used with @link{android.app.Activity.startActivityForResult(Intent,Integer)}
     * <p>
     * Results must be obtained using @link{}
     */
    @NonNull
    public static Intent getActionListSignIntent(@NonNull String actionsJson, Blockchain blockchain) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(WOMBAT_PACKAGE, SIGNATURE_ACTIVITY_CLASS));
        intent.putExtra(EXTRA_JSON_ACTIONS, actionsJson);
        intent.putExtra(EXTRA_BLOCKCHAIN, blockchain.name());
        return intent;
    }

    /**
     * @see Wombat#getActionListSignIntent(String, Blockchain)
     * defaults to {@link Blockchain#EOS}
     */
    @NonNull
    public static Intent getActionListSignIntent(@NonNull String actionsJson) {
        return getActionListSignIntent(actionsJson, Blockchain.EOS);
    }

    /**
     * Obtain the result after using {@link #getActionListSignIntent(String)} or {@link Wombat#getTransactionSignIntent(String)}
     *
     * @param intent the data intent obtained in {@link android.app.Activity#onActivityResult(int, int, Intent)}
     * @return The serialized transaction and signatures on success, null on error
     */
    @Nullable
    public static TransactionSignResult getTransactionSignResultFromIntent(Intent intent) {
        if (intent == null) return null;
        String serializedTransaction = intent.getStringExtra(EXTRA_SERIALIZED_TRANSACTION);
        ArrayList<String> signatures = intent.getStringArrayListExtra(EXTRA_SIGNATURES);
        if (serializedTransaction == null || signatures == null) return null;
        return new TransactionSignResult(serializedTransaction, signatures);
    }

    /**
     * Retrieve the {@link LoginResult} after using {@link #getLoginIntent()}
     *
     * @param intent the data intent obtained in {@link android.app.Activity#onActivityResult(int, int, Intent)}
     * @return The EOS account name and public key on success, null on error
     */
    @Nullable
    public static LoginResult getLoginResultFromIntent(Intent intent) {
        if (intent == null) return null;
        String accountName = intent.getStringExtra(EXTRA_EOS_ACCOUNT_NAME);
        String publicKey = intent.getStringExtra(EXTRA_EOS_PUBLIC_KEY);
        if (accountName == null || publicKey == null) return null;
        return new LoginResult(accountName, publicKey);
    }
}
