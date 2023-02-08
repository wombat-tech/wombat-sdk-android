package app.wombat.androidsdk.evm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import app.wombat.androidsdk.SdkRequest;
import app.wombat.androidsdk.WombatSdkResult;

public class EvmSignTypedData {
    public static class Request extends SdkRequest {

        final private String chainId;

        final private String message;

        final private String address;


        public Request(String address, String message, String chainId) {
            this.chainId = chainId;
            this.message = message;
            this.address = address;
        }

        @Override
        public Intent createIntent() {
            Bundle arguments = new Bundle();
            arguments.putString("chainId", chainId);
            arguments.putString("message", message);
            arguments.putString("address", address);
            return createIntent("evm_sign_typed_data", arguments);
        }
    }

    public static class Result {

        final private String signature;

        private Result(String signature) {
            this.signature = signature;
        }


        public String getSignature() {
            return signature;
        }

        public static WombatSdkResult<Result> fromIntent(int resultCode, Intent intent) {
            if (resultCode != Activity.RESULT_OK || intent == null) {
                String error = null;
                if (intent != null) {
                    error = intent.getStringExtra("error_message");
                }
                return new WombatSdkResult<>(error, null);
            }
            String signature = intent.getStringExtra("signature");

            return new WombatSdkResult<>(null, new Result(signature));
        }
    }
}

