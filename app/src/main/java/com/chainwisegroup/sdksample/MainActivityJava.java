package com.chainwisegroup.sdksample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import io.getwombat.androidsdk.AuthenticateOptions;
import io.getwombat.androidsdk.Blockchain;
import io.getwombat.androidsdk.LoginResult;
import io.getwombat.androidsdk.TransactionSignResult;
import io.getwombat.androidsdk.Wombat;

public class MainActivityJava extends AppCompatActivity {
    static final int REQUEST_CODE_WOMBAT_LOGIN = 1;
    static final int REQUEST_CODE_WOMBAT_SIGNATURE = 2;
    static final int REQUEST_CODE_ARBITRARY_SIGNATURE = 3;

    LoginResult userInfo = null;

    Button loginButton;
    Button requestTransferButton;
    Button arbitrarySigButton;
    TextView nameText;
    TextView pubkeyText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = findViewById(R.id.login_button);
        requestTransferButton = findViewById(R.id.request_transfer_button);
        arbitrarySigButton = findViewById(R.id.button_arbitrary_signature);
        nameText = findViewById(R.id.name_text);
        pubkeyText = findViewById(R.id.pubkey_text);

        requestTransferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestTransfer();
            }
        });
        arbitrarySigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestArbitrarySignature();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithWombat();
            }
        });

        requestTransferButton.setEnabled(false);
    }

    //wrap it in this Wombat.isAvailable(Context)  is already in the provided SDK
    void loginWithWombat() {
        if (Wombat.isAvailable(this)) {
            Intent loginIntent = Wombat.getLoginIntent(Blockchain.TELOS, new AuthenticateOptions("123456789101","somerandomdata..."));
            startActivityForResult(loginIntent, REQUEST_CODE_WOMBAT_LOGIN);
        } else {
            String wombatLink = "https://play.google.com/store/apps/details?id=io.getwombat.android&referrer=utm_source%3Deos_knights_android%26utm_medium%3Dwallet_choice%26utm_campaign%3Deos_knights%26anid%3Dadmob";
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wombatLink));
            startActivity(playStoreIntent);
        }
    }

    void requestArbitrarySignature(){
//        String toSign = "foo";
        String toSign = getString(R.string.lorem);
        Intent intent = Wombat.getArbitrarySignatureIntent(toSign, Blockchain.EOS);
        startActivityForResult(intent, REQUEST_CODE_ARBITRARY_SIGNATURE );
    }

    void requestTransfer() {
        String jsonActions = "[\n" +
                "                {\n" +
                "                    \"account\": \"eosio.token\",\n" +
                "                    \"name\": \"transfer\",\n" +
                "                    \"authorization\": [\n" +
                "                        {\n" +
                "                            \"actor\": \"" + this.userInfo.getEosAccountName() + "\",\n" +
                "                            \"permission\": \"active\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"data\": {\n" +
                "                        \"from\": \"" + this.userInfo.getEosAccountName() + "\",\n" +
                "                        \"memo\": \"...\",\n" +
                "                        \"quantity\": \"0.0001 EOS\",\n" +
                "                        \"to\": \"genialwombat\"\n" +
                "                    }\n" +
                "                }\n" +
                "            ]";
        Intent signIntent = Wombat.getActionListSignIntent(jsonActions);
        startActivityForResult(signIntent, REQUEST_CODE_WOMBAT_SIGNATURE);
    }

    void broadcastTransaction(String serializedTransaction, List<String> signatures) {
        Log.d("SDK", "Received result");
        Log.d("SDK", "TX: " + serializedTransaction);
        Log.d("SDK", "SIGS: " + signatures.toString());

        // TODO
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_WOMBAT_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                LoginResult loginResult = Wombat.getLoginResultFromIntent(data);
                String eosName = loginResult.getEosAccountName();
                String publicKey = loginResult.getPublicKey();
                String signature = loginResult.getAuthenticateSignature();
                Log.d("LOGIN","Signature: "+signature);
                nameText.setText(eosName);
                pubkeyText.setText(publicKey);
                requestTransferButton.setEnabled(true);
                this.userInfo = loginResult;
            }
            return;
        }

        if (requestCode == REQUEST_CODE_WOMBAT_SIGNATURE) {
            if (resultCode == Activity.RESULT_OK) {
                TransactionSignResult result = Wombat.getTransactionSignResultFromIntent(data);
                List<String> signatures = result.getSignatures();
                String serializedTransaction = result.getSerializedTransaction(); // might differ from the requested transaction if you used Wombat.getTransactionSignIntent with modifiable = true
                broadcastTransaction(serializedTransaction, signatures);
            }
        }

        if (requestCode == REQUEST_CODE_ARBITRARY_SIGNATURE) {
            String signature = Wombat.getArbitrarySignatureResultFromIntent(data);
            if (signature != null) {
                Toast.makeText(this, "GOT SIGNATURE", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GOT NO SIGNATURE", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
