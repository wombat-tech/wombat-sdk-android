### Installation
#### 1. Copy `wombatsdk.aar` from the [release section](https://github.com/wombat-tech/wombat-sdk-android/releases)  to `/app/libs` 

#### 2. Add the library to gradle
Add these to your `/app/build.gradle`
```
android{
  
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
The SDK uses android's standard `startActivityForResult` -> `onActivityResult` flow. More details about this can be found in the [android developer docs](https://developer.android.com/training/basics/intents/result)

#### Check availability
```
public class MyActivity extends Activity {

  boolean isWombatAvailable(){
      return Wombat.isAvailable(this);
  }

}
```

#### Login
```
public class MyActivity extends Activity {
  
  // This can by any integer, only used to distinguish the cases in 'onActivityResult'
  static int REQUEST_CODE_WOMBAT_LOGIN = 1;
  
  // Called to initiate the login process
  void loginWithWombat(){
    Intent loginIntent = Wombat.getLoginIntent();
    startActivityForResult(loginIntent, REQUEST_CODE_WOMBAT_LOGIN);
  }
  
  //results will be returned in this callback 
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
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

#### Transaction Signing
Wombat supports 2 different formats to sign transactions

#####  1. Raw Serialized Transactions as Hex string

```
public class MyActivity extends Activity {
 
  // These can by any integers, only used to distinguish the cases in 'onActivityResult'
  static int REQUEST_CODE_WOMBAT_LOGIN = 1;
  static int REQUEST_CODE_WOMBAT_SIGNATURE = 2;
 
  
  void requestWombatTransaction(){
    String transaction = ""; // TODO
    Intent signIntent = Wombat.getTransactionSignIntent(transaction);
    startActivityForResult(signIntent, REQUEST_CODE_WOMBAT_SIGNATURE);
  }
 
  //results will be returned in this callback 
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
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

##### 2. JSON String of actions
You can also leave the serialization to wombat and only provide the actions to be included in the transaction.<br>
For better readability, assume you have the following as a String containing a json array of actions:
```
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
```
public class MyActivity extends Activity {
 
  // These can by any integers, only used to distinguish the cases in 'onActivityResult'
  static int REQUEST_CODE_WOMBAT_LOGIN = 1;
  static int REQUEST_CODE_WOMBAT_SIGNATURE = 2;
 
  // call this using the above json string
  void requestWombatTransaction(String jsonActions){
    Intent signIntent = Wombat.getActionListSignIntent(jsonActions);
    startActivityForResult(signIntent, REQUEST_CODE_WOMBAT_SIGNATURE);
  }
 
  //results will be returned in this callback 
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
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

