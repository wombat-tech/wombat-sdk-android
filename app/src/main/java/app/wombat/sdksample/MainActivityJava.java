package app.wombat.sdksample;

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

import java.math.BigInteger;
import java.util.List;

import app.wombat.androidsdk.Blockchain;
import app.wombat.androidsdk.evm.EvmChainIds;
import app.wombat.androidsdk.evm.EvmGetAddress;
import app.wombat.androidsdk.evm.EvmPersonalSign;
import app.wombat.androidsdk.evm.EvmSignTransaction;
import app.wombat.androidsdk.evm.EvmSignTypedData;
import app.wombat.androidsdk.LoginResult;
import app.wombat.androidsdk.TransactionSignResult;
import app.wombat.androidsdk.Wombat;
import app.wombat.androidsdk.WombatSdkResult;

public class MainActivityJava extends AppCompatActivity {
    static final int REQUEST_CODE_WOMBAT_LOGIN = 1;
    static final int REQUEST_CODE_WOMBAT_SIGNATURE = 2;
    static final int REQUEST_CODE_ARBITRARY_SIGNATURE = 3;
    static final int REQUEST_CODE_GET_ADDRESS = 4;

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

        arbitrarySigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestArbitrarySignature();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestEvmLogin();
            }
        });

        requestTransferButton.setEnabled(false);

        loginButton.setEnabled(Wombat.isAvailable(this) && Wombat.evmSupported(this));
    }

    //wrap it in this Wombat.isAvailable(Context)  is already in the provided SDK
    void loginWithWombat() {
        if (Wombat.isAvailable(this)) {
            Intent loginIntent = Wombat.getLoginIntent(Blockchain.EOS);
            startActivityForResult(loginIntent, REQUEST_CODE_WOMBAT_LOGIN);
        } else {
            String wombatLink = "https://play.google.com/store/apps/details?id=io.getwombat.android";
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wombatLink));
            startActivity(playStoreIntent);
        }
    }

    void requestArbitrarySignature() {
//        String toSign = "foo";
        String toSign = getString(R.string.lorem);
        Intent intent = Wombat.getArbitrarySignatureIntent(toSign, Blockchain.EOS);
        startActivityForResult(intent, REQUEST_CODE_ARBITRARY_SIGNATURE);
    }

    void requestEvmLogin(){
        EvmGetAddress.Request request = new EvmGetAddress.Request(EvmChainIds.POLYGON);
        Intent launchIntent = request.createIntent();
        startActivityForResult(launchIntent, REQUEST_CODE_GET_ADDRESS);
    }

    void requestEvmSignTypedData(String userAddress) {
        String message = "{\"types\":{\"EIP712Domain\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"version\",\"type\":\"string\"},{\"name\":\"chainId\",\"type\":\"uint256\"},{\"name\":\"verifyingContract\",\"type\":\"address\"}],\"Person\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"wallet\",\"type\":\"address\"}],\"Mail\":[{\"name\":\"from\",\"type\":\"Person\"},{\"name\":\"to\",\"type\":\"Person\"},{\"name\":\"contents\",\"type\":\"string\"}]},\"primaryType\":\"Mail\",\"domain\":{\"name\":\"Ether Mail\",\"version\":\"1\",\"chainId\":1,\"verifyingContract\":\"0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC\"},\"message\":{\"from\":{\"name\":\"Cow\",\"wallet\":\"0xCD2a3d9F938E13CD947Ec05AbC7FE734Df8DD826\"},\"to\":{\"name\":\"Bob\",\"wallet\":\"0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB\"},\"contents\":\"Hello, Bob!\"}}";
        EvmSignTypedData.Request request = new EvmSignTypedData.Request(userAddress, message,EvmChainIds.POLYGON);
        startActivityForResult(request.createIntent(), 134);
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

    void requestMaticTransfer(String  userAddress){

        String to = "0xFdC17DD76FC08eB790f0e9AC7748B90B14A146EC";
        BigInteger value = new BigInteger("1000000000000000000"); // 1 MATIC in Wei
        String data = ""; // the transaction data, empty for a simple MATIC transfer
        EvmSignTransaction.Request request = new EvmSignTransaction.Request(userAddress, to,value,data, EvmChainIds.POLYGON);
        startActivityForResult(request.createIntent(), 133);
    }

    void requestPersonalSign(String  userAddress){
        String message = "foobar";
        EvmPersonalSign.Request request = new EvmPersonalSign.Request(userAddress, message, EvmChainIds.POLYGON);
        startActivityForResult(request.createIntent(), 1332);
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

        if(requestCode == REQUEST_CODE_GET_ADDRESS){
            WombatSdkResult<EvmGetAddress.Result> result = EvmGetAddress.Result.fromIntent(resultCode, data);
            if(result.isSuccess()){
                final String address = result.getResult().getAddress();
                Toast.makeText(this, "Received address: "+address, Toast.LENGTH_SHORT).show();
                requestTransferButton.setEnabled(true);
                requestTransferButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestMaticTransfer(address);
                    }
                });

                arbitrarySigButton.setEnabled(true);
                arbitrarySigButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestEvmSignTypedData(address);
                    }
                });
            }else{
                Toast.makeText(this, "Error :( ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
