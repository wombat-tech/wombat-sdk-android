package app.wombat.androidsdk.evm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import app.wombat.androidsdk.SdkRequest;
import app.wombat.androidsdk.WombatSdkResult;

public class EvmGetAddress {
    public static class Request extends SdkRequest {

        final private String chainId;

        public Request(String chainId) {
            this.chainId = chainId;
        }

        @Override
        public Intent createIntent() {
            Bundle arguments = new Bundle();
            arguments.putString("chainId", chainId);
            return createIntent("evm_get_address", arguments);
        }
    }

    public static class Result {

        final private String address;

        private Result(String address) {
            this.address = address;
        }


        public String getAddress() {
            return address;
        }

        public static WombatSdkResult<Result> fromIntent(int resultCode, Intent intent) {
            if (resultCode != Activity.RESULT_OK || intent == null) {
                String error = null;
                if (intent != null) {
                    error = intent.getStringExtra("error_message");
                }
                return new WombatSdkResult<>(error, null);
            }
            String address = intent.getStringExtra("address");
            return new WombatSdkResult<>(null, new Result(address));
        }
    }
}
