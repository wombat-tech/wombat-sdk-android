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


#### Arbitrary Signatures
Wombat supports signing of arbitrary text, just as scatter does
By now, this will always prompt the user with the requested text and ask them to sign.

```java
public class MyActivity extends Activity {

  static int REQUEST_CODE_WOMBAT_ARBITRARY_SIGNATURE = 3;

  void requestWombatArbitrarySignature(String data) {
    Intent intent = Wombat.getArbitrarySignatureIntent(data, Blockchain.EOS); // alternatively Blockchain.TELOS
    startActivityForResult(intent, REQUEST_CODE_ARBITRARY_SIGNATURE );
  }

  // Results will be returned in this callback
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      ...

      if (requestCode == REQUEST_CODE_WOMBAT_SIGNATURE) {
            String signature = Wombat.getArbitrarySignatureResultFromIntent(data);
            if (signature != null) {
                // TODO verify signature e.g. check if ecrecover(data, signature) == expectedPublicKey
            }
      }
  }
}
```

# EVM Support
As of Wombat version 2.13.0 we support several methods to interact with EVM based blockchains\
Currenty we support Ethereum, Polygon, BNB, Heco, Fantom and Avalanche.

### Ensuring EVM support
Since older versions do not support these sdk methods, it is adviced to check the support beforehand:

```java
public class MyActivity extends Activity {

  boolean isWombatAvailable() {
      return Wombat.isAvailable(this) && Wombat.evmSupported(this);
  }

}
```
### Getting the user's address

```java
public class MyActivity extends Activity {

    static int REQUEST_CODE_WOMBAT_GET_ADDRESS = 1;

    void requestEvmLogin() {
        EvmGetAddress.Request request = new EvmGetAddress.Request(EvmChainIds.POLYGON);
        startActivityForResult(request.createIntent(), REQUEST_CODE_WOMBAT_GET_ADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WOMBAT_GET_ADDRESS) {
            WombatSdkResult<EvmGetAddress.Result> result = EvmGetAddress.Result.fromIntent(resultCode, data);
            if (result.isSuccess()) {
                String userAddress = result.getResult().getAddress();
            }
        }
    }
}
```

### Signing transactions

```java
public class MyActivity extends Activity {

    static int REQUEST_CODE_WOMBAT_TRANSFER_MATIC = 2;

    // address must be obtained via EvmGetAddress request
    void requestMaticTransfer(String userAddress) {
        String to = "0xdeadbeef";
        BigInteger value = new BigInteger("1000000000000000000"); // 1 MATIC in Wei
        String data = ""; // the transaction data, empty for a simple MATIC transfer
        EvmSignTransaction.Request request = new EvmSignTransaction.Request(userAddress, to,value,data, EvmChainIds.POLYGON);
        startActivityForResult(request.createIntent(), REQUEST_CODE_WOMBAT_TRANSFER_MATIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WOMBAT_TRANSFER_MATIC) {
            WombatSdkResult<EvmSignTransaction.Result> result = EvmSignTransaction.Result.fromIntent(resultCode, data);
            if (result.isSuccess()) {
                String transactionHash = result.getResult().getHash();
            }
        }
    }
}
```


### Personal Sign

```java
public class MyActivity extends Activity {

    static int REQUEST_CODE_WOMBAT_PERSONAL_SIGN = 3;

    // address must be obtained via EvmGetAddress request
    void requestPersonalSign(String userAddress) {
        String message = "foobar";
        EvmPersonalSign.Request request = new EvmPersonalSign.Request(userAddress, message, EvmChainIds.POLYGON);
        startActivityForResult(request.createIntent(), REQUEST_CODE_WOMBAT_PERSONAL_SIGN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WOMBAT_PERSONAL_SIGN) {
            WombatSdkResult<EvmPersonalSign.Result> result = EvmPersonalSign.Result.fromIntent(resultCode, data);
            if (result.isSuccess()) {
                String signature = result.getResult().getSignature();
            }
        }
    }
}
```

### Signing typed data
Wombat supports signing typed data following the [EIP-712](https://eips.ethereum.org/EIPS/eip-712) v4 standard. This is equivalent to Metamasks `signTypedData_v4` request.

```java
public class MyActivity extends Activity {

    static int REQUEST_CODE_WOMBAT_SIGN_TYPED_DATA = 4;

    // address must be obtained via EvmGetAddress request
    void requestPersonalSign(String userAddress) {
        // Sample data from EIP
        String message = "{\"types\":{\"EIP712Domain\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"version\",\"type\":\"string\"},{\"name\":\"chainId\",\"type\":\"uint256\"},{\"name\":\"verifyingContract\",\"type\":\"address\"}],\"Person\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"wallet\",\"type\":\"address\"}],\"Mail\":[{\"name\":\"from\",\"type\":\"Person\"},{\"name\":\"to\",\"type\":\"Person\"},{\"name\":\"contents\",\"type\":\"string\"}]},\"primaryType\":\"Mail\",\"domain\":{\"name\":\"Ether Mail\",\"version\":\"1\",\"chainId\":1,\"verifyingContract\":\"0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC\"},\"message\":{\"from\":{\"name\":\"Cow\",\"wallet\":\"0xCD2a3d9F938E13CD947Ec05AbC7FE734Df8DD826\"},\"to\":{\"name\":\"Bob\",\"wallet\":\"0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB\"},\"contents\":\"Hello, Bob!\"}}";
        EvmSignTypedData.Request request = new EvmSignTypedData.Request(userAddress, message, EvmChainIds.POLYGON);
        startActivityForResult(request.createIntent(), REQUEST_CODE_WOMBAT_SIGN_TYPED_DATA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WOMBAT_SIGN_TYPED_DATA) {
            WombatSdkResult<EvmSignTypedData.Result> result = EvmSignTypedData.Result.fromIntent(resultCode, data);
            if (result.isSuccess()) {
                String signature = result.getResult().getSignature();
            }
        }
    }
}
```
