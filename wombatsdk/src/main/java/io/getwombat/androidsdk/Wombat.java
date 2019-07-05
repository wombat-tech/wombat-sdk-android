package io.getwombat.androidsdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Wombat {

    private static String wombatPackageName = "io.getwombat.android.internal";

    public static Boolean isAvailable(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(wombatPackageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     *
     * @return an Intent to be used with {@link android.app.Activity#startActivityForResult(Intent, Integer)}
     */
    @NonNull
    public static Intent getLoginIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(wombatPackageName, "io.getwombat.android.sdk.activities.SDKLoginActivity"));
        return intent;
    }

    /**
     * Creates an intent to be used with {@link android.app.Activity#startActivityForResult(Intent, int)}
     * Result must be obtained using {@link #getLoginResultFromIntent(Intent)} in the activities {@link android.app.Activity#onActivityResult(int, int, Intent)} callback
     *
     * @param serializedTransaction the raw hex transaction to be signed
     * @return the intent which an activity needs to send
     */

    @NonNull
    public static Intent getTransactionSignIntent(@NonNull String serializedTransaction) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(wombatPackageName, "io.getwombat.android.sdk.activities.SDKSignatureRequestActivity"));
        intent.putExtra("serialized_transaction", serializedTransaction);
        return intent;
    }


    /**
     * Creates an intent to be used with {@link android.app.Activity#startActivityForResult(Intent, int)}
     *
     * @param actionsJson the json representing an array of actions.
     *                    [
     *                    {
     *                    "account": "eosio.token",
     *                    "name": "transfer",
     *                    "authorization": [
     *                    {
     *                    "actor": "eosaccntname",
     *                    "permission": "active"
     *                    }
     *                    ],
     *                    "data": {
     *                    "from": "eosaccntname",
     *                    "memo": "...",
     *                    "quantity": "1.0000 EOS",
     *                    "to": "otheraccount"
     *                    }
     *                    }
     *                    ]
     * @return an Intent to be used with @link{android.app.Activity.startActivityForResult(Intent,Integer)}
     * <p>
     * Results must be obtained using @link{}
     */
    @NonNull
    public static Intent getActionListSignIntent(@NonNull String actionsJson) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(wombatPackageName, "io.getwombat.android.sdk.activities.SDKSignatureRequestActivity"));
        intent.putExtra("json_actions", actionsJson);
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
        String serializedTransaction = intent.getStringExtra("serialized_transaction");
        ArrayList<String> signatures = intent.getStringArrayListExtra("signatures");
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
        String accountName = intent.getStringExtra("eos_account_name");
        String publicKey = intent.getStringExtra("eos_public_key");
        if (accountName == null || publicKey == null) return null;
        return new LoginResult(accountName, publicKey);
    }


}
