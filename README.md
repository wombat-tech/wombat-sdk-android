### Installation
1. Copy `wombatsdk.aar` from the [release section](https://github.com/wombat-tech/wombat-sdk-android/releases) to `/app/libs` 
2. Add the library to Gradle

Add these to your `/app/build.gradle`
```groovy
android {

  ...
  
  repositories {
    flatDir {
        dirs 'libs'
    }
  }
}

...

dependencies {
  ...
  compile(name: 'wombatsdk', ext: 'aar')
}

```
___

### Usage
The SDK uses Android's standard `startActivityForResult` -> `onActivityResult` flow. More details about this can be found in the [Android Developer docs](https://developer.android.com/training/basics/intents/result)

#### Check availability

```java
public class MyActivity extends Activity {

  boolean isWombatAvailable() {
      return Wombat.isAvailable(this);
  }

}
```

#### Login

```java
public class MyActivity extends Activity {
  
  // This can be any integer, only used to distinguish the cases in 'onActivityResult'
  static int REQUEST_CODE_WOMBAT_LOGIN = 1;
  
  // Called to initiate the login process
  void loginWithWombat() {
    Intent loginIntent = Wombat.getLoginIntent(Blockchain.EOS); // or Blockchain.TELOS
    startActivityForResult(loginIntent, REQUEST_CODE_WOMBAT_LOGIN);
  }
  
  // Results will be returned in this callback 
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      
      if (requestCode == REQUEST_CODE_WOMBAT_LOGIN) {
          if (resultCode == Activity.RESULT_OK) {
              LoginResult loginResult = Wombat.getLoginResultFromIntent(data);
              String eosName = loginResult.getEosAccountName();
              String publicKey = loginResult.getPublicKey();
              // TODO
          }
          
          return;
      }
  }
}
```

#### Transaction signing
Wombat supports 2 different formats to sign transactions.

#####  1. Raw serialized transactions as hex string

Use `Wombat.getTransactionSignIntent` and pass a fully serialized transaction as HEX string. The second parameter, "modifiable", signals Wombat that it may modify the transaction to
provide CPU and NET for the user. This is done by adding an account controlled by Wombat as the first authorizer in the transaction.

```java
public class MyActivity extends Activity {
 
  // These can be any integers, only used to distinguish the cases in 'onActivityResult'
  static int REQUEST_CODE_WOMBAT_LOGIN = 1;
  static int REQUEST_CODE_WOMBAT_SIGNATURE = 2;
 
  
  void requestWombatTransaction() {
    String transaction = ""; // TODO
    Intent signIntent = Wombat.getTransactionSignIntent(transaction, Blockchain.EOS, true);
    startActivityForResult(signIntent, REQUEST_CODE_WOMBAT_SIGNATURE);
  }
 
  // Results will be returned in this callback 
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == REQUEST_CODE_WOMBAT_LOGIN) {
        // Handle login
        return;
      }
 
      if (requestCode == REQUEST_CODE_WOMBAT_SIGNATURE) {
            if (resultCode == Activity.RESULT_OK) {
                TransactionSignResult result = Wombat.getTransactionSignResultFromIntent(data);
                List<String> signatures = result.getSignatures();
                String serializedTransaction = result.getSerializedTransaction(); // In this case this is the same as the requested hex string
                // TODO Broadcast the transaction to an EOS node
            }
      }
  }
}
```

##### 2. JSON string of actions
You can also leave the serialization to Wombat and only provide the actions to be included in the transaction.<br>
For better readability, assume you have the following as a string containing a JSON array of actions:

```json
[  
  {
    "account": "eosio.token",
    "name": "transfer",
    "authorization": [
      {
        "actor": "accountname1",
        "permission": "active"
      }
    ],
    "data": {
      "from": "accountname1",
      "memo": "...",
      "quantity": "0.0001 EOS",
      "to": "accountname2"
    }
  }
]
```

You can use it like this:

```java
public class MyActivity extends Activity {
 
  // These can be any integers, only used to distinguish the cases in 'onActivityResult'
  static int REQUEST_CODE_WOMBAT_LOGIN = 1;
  static int REQUEST_CODE_WOMBAT_SIGNATURE = 2;
 
  // Call this using the above JSON string
  void requestWombatTransaction(String jsonActions) {
    Intent signIntent = Wombat.getActionListSignIntent(jsonActions, Blockchain.EOS);
    startActivityForResult(signIntent, REQUEST_CODE_WOMBAT_SIGNATURE);
  }
 
  // Results will be returned in this callback 
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == REQUEST_CODE_WOMBAT_LOGIN) {
        // Handle login
        return;
      }
 
      if (requestCode == REQUEST_CODE_WOMBAT_SIGNATURE) {
            if (resultCode == Activity.RESULT_OK) {
                TransactionSignResult result = Wombat.getTransactionSignResultFromIntent(data);
                List<String> signatures = result.getSignatures();
                String serializedTransaction = result.getSerializedTransaction(); // The fully serialized transaction
                // TODO Broadcast the transaction to an EOS node
            }
        }
  }
}
```
