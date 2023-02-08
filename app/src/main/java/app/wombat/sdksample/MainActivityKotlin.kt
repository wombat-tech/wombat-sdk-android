package app.wombat.sdksample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.wombat.androidsdk.LoginResult
import app.wombat.androidsdk.Wombat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivityKotlin : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_WOMBAT_LOGIN = 1
        const val REQUEST_CODE_WOMBAT_SIGNATURE = 2
    }

    var userInfo: LoginResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        login_button.isEnabled = Wombat.isAvailable(this)

        request_transfer_button.setOnClickListener {
            requestTransfer()
        }

        login_button.setOnClickListener {
            loginWithWombbat()
        }

        request_transfer_button.isEnabled = false
    }

    fun loginWithWombbat() {
        val loginIntent = Wombat.getLoginIntent()
        startActivityForResult(loginIntent, REQUEST_CODE_WOMBAT_LOGIN)
    }

    fun requestTransfer() {
        val json = """
            [
                {
                    "account": "eosio.token",
                    "name": "transfer",
                    "authorization": [
                        {
                            "actor": "${this.userInfo!!.eosAccountName}",
                            "permission": "active"
                        }
                    ],
                    "data": {
                        "from": "${this.userInfo!!.eosAccountName}",
                        "memo": "...",
                        "quantity": "0.0001 EOS",
                        "to": "genialwombat"
                    }
                }
            ]"""
        val intent = Wombat.getActionListSignIntent(json)
        startActivityForResult(intent, REQUEST_CODE_WOMBAT_SIGNATURE)
    }

    fun broadcastTransaction(serializedTransaction: String, signatures: List<String>) {
        // TODO
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_WOMBAT_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                val loginResult = Wombat.getLoginResultFromIntent(data)
                if (loginResult != null) {
                    this@MainActivityKotlin.userInfo = loginResult
                    name_text.text = loginResult.eosAccountName
                    pubkey_text.text = loginResult.publicKey
                    request_transfer_button.isEnabled = true
                }
            }
            return
        }

        if (requestCode == REQUEST_CODE_WOMBAT_SIGNATURE) {
            if (resultCode == Activity.RESULT_OK) {
                val signatureResult = Wombat.getTransactionSignResultFromIntent(data)
                if (signatureResult != null) {
                    val signatures = signatureResult.signatures
                    val serializedTransaction = signatureResult.serializedTransaction
                    broadcastTransaction(serializedTransaction, signatures)
                }
            }
        }
    }
}
