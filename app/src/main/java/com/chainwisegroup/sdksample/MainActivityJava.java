package com.chainwisegroup.sdksample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.getwombat.androidsdk.LoginResult;
import io.getwombat.androidsdk.TransactionSignResult;
import io.getwombat.androidsdk.Wombat;

import java.util.List;

public class MainActivityJava extends AppCompatActivity {
    static final int REQUEST_CODE_WOMBAT_LOGIN = 1;
    static final int REQUEST_CODE_WOMBAT_SIGNATURE = 2;

    LoginResult userInfo = null;

    Button loginButton;
    Button requestTransferButton;
    TextView nameText;
    TextView pubkeyText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = findViewById(R.id.login_button);
        requestTransferButton = findViewById(R.id.request_transfer_button);
        nameText = findViewById(R.id.name_text);
        pubkeyText = findViewById(R.id.pubkey_text);

        requestTransferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestTransfer();
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

    //your current version
    void oldLoginWithWombat(){
        Intent loginIntent = Wombat.getLoginIntent();
        startActivityForResult(loginIntent, REQUEST_CODE_WOMBAT_LOGIN);
    }

    //wrap it in this Wombat.isAvailable(Context)  is already in the provided SDK
    void loginWithWombat() {
        if(Wombat.isAvailable(this)){
            Intent loginIntent = Wombat.getLoginIntent();
            startActivityForResult(loginIntent, REQUEST_CODE_WOMBAT_LOGIN);
        }else{
            String wombatLink  = "https://play.google.com/store/apps/details?id=io.getwombat.android&referrer=utm_source%3Deos_knights_android%26utm_medium%3Dwallet_choice%26utm_campaign%3Deos_knights%26anid%3Dadmob";
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wombatLink));
            startActivity(playStoreIntent);
        }
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
                String serializedTransaction = result.getSerializedTransaction(); // In this case this is the same as the requested hex string
                broadcastTransaction(serializedTransaction, signatures);
            }
        }
    }
}
