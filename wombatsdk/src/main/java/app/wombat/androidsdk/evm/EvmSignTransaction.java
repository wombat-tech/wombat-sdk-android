package app.wombat.androidsdk.evm;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.math.BigInteger;

import app.wombat.androidsdk.SdkRequest;
import app.wombat.androidsdk.WombatSdkResult;


public class EvmSignTransaction {
    public static class Request extends SdkRequest {
        final private String address;
        final private String to;
        final private BigInteger value;
        final private String data;
        final private String chainId;


        public Request(
                String address,
                String to,
                BigInteger value,
                String data,
                String chainId
        ) {
            this.address = address;
            this.to = to;
            this.value = value;
            this.data = data;
            this.chainId = chainId;
        }

        @Override
        public Intent createIntent() {
            Bundle arguments = new Bundle();
            arguments.putString("address", address);
            arguments.putString("to", to);
            arguments.putString("value", "0x" + value.toString(16));
            arguments.putString("data", data);
            arguments.putString("chainId", chainId);
            return createIntent("evm_sign_transaction", arguments);
        }
    }

    public static class Result {

        final private String hash;

        private Result(String hash) {
            this.hash = hash;
        }


        public String getHash() {
            return hash;
        }

        public static WombatSdkResult<Result> fromIntent(int resultCode, Intent intent) {
            if (resultCode != Activity.RESULT_OK || intent == null) {
                String error = null;
                if (intent != null) {
                    error = intent.getStringExtra("error_message");
                }
                return new WombatSdkResult<>(error, null);
            }
            String hash = intent.getStringExtra("hash");
            return new WombatSdkResult<>(null, new Result(hash));
        }
    }
}
