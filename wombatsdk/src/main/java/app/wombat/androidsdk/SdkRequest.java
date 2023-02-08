package app.wombat.androidsdk;

import static app.wombat.androidsdk.Constants.SDK_ACTIVITY_CLASS;
import static app.wombat.androidsdk.Constants.WOMBAT_PACKAGE;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public abstract class SdkRequest {

    public abstract Intent createIntent();

    protected Intent createIntent(String action, Bundle arguments) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(WOMBAT_PACKAGE, SDK_ACTIVITY_CLASS));
        intent.putExtra("action", action);
        intent.putExtra("arguments", arguments);
        return intent;
    }
}
