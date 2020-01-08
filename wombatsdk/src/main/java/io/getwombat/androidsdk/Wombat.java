package io.getwombat.androidsdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Wombat {
    private static String wombatPackageName = "io.getwombat.android";
    private static String EXTRA_SIGNATURES = "signatures";
    private static String EXTRA_SERIALIZED_TRANSACTION = "serialized_transaction";
    private static String EXTRA_MODIFIABLE = "modifiable";
    private static String EXTRA_JSON_ACTIONS = "json_actions";
    private static String EXTRA_EOS_ACCOUNT_NAME = "eos_account_name";
    private static String EXTRA_EOS_PUBLIC_KEY = "eos_public_key";

    private static String LOGIN_ACTIVITY_CLASS = "io.getwombat.android.sdk.activities.SDKLoginActivity";
    private static String SIGNATURE_ACTIVITY_CLASS = "io.getwombat.android.sdk.activities.SDKSignatureRequestActivity";

    public static Boolean isAvailable(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(wombatPackageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     * @return an Intent to be used with {@link android.app.Activity#startActivityForResult(Intent, Integer)}
     */
    @NonNull
    public static Intent getLoginIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(wombatPackageName, LOGIN_ACTIVITY_CLASS));
        return intent;
    }

    /**
     * Creates an intent to be used with {@link android.app.Activity#startActivityForResult(Intent, int)}
     * Result must be obtained using {@link #getTransactionSignResultFromIntent(Intent)} in the activities {@link android.app.Activity#onActivityResult(int, int, Intent)} callback
     *
     * @param serializedTransaction the raw hex transaction to be signed
     * @param modifiable whether Wombat may adjust the transaction. This will be used to pay for the users' CPU and NET. If set to true,
     *                   you MUST use the new transaction ({@link TransactionSignResult#getSerializedTransaction()}})returned in {@link #getTransactionSignResultFromIntent(Intent)}.
     *
     *
     * @return the intent which an activity needs to send
     */

    @NonNull
    public static Intent getTransactionSignIntent(@NonNull String serializedTransaction, boolean modifiable) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(wombatPackageName, SIGNATURE_ACTIVITY_CLASS));
        intent.putExtra(EXTRA_SERIALIZED_TRANSACTION, serializedTransaction);
        intent.putExtra(EXTRA_MODIFIABLE, modifiable);
        return intent;
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
     * @return an Intent to be used with @link{android.app.Activity.startActivityForResult(Intent,Integer)}
     * <p>
     * Results must be obtained using @link{}
     */
    @NonNull
    public static Intent getActionListSignIntent(@NonNull String actionsJson) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(wombatPackageName, SIGNATURE_ACTIVITY_CLASS));
        intent.putExtra(EXTRA_JSON_ACTIONS, actionsJson);
        return intent;
    }


    /**
     * Obtain the result after using {@link #getActionListSignIntent(String)} or {@link #getTransactionSignIntent(String)}
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
