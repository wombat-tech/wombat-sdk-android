package com.chainwisegroup.sdksample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.getwombat.androidsdk.LoginResult
import io.getwombat.androidsdk.Wombat

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    var loginInfo: LoginResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        login_button.isEnabled = Wombat.isAvailable(this)

        request_transfer_button.setOnClickListener {
            requestTransfer()
        }

        login_button.setOnClickListener {
            val loginIntent = Wombat.getLoginIntent()
            startActivityForResult(loginIntent, 102)
        }

        request_transfer_button.isEnabled = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            val result = Wombat.getTransactionSignResultFromIntent(data)
            result?.let{
                broadcastTransaction(it.serializedTransaction,it.signatures)
            }
        } else if (requestCode == 102 && resultCode == RESULT_OK) {
            val loginResult = Wombat.getLoginResultFromIntent(data)
            loginResult?.let{
                this@MainActivity.loginInfo = it
                name_text.text = it.eosAccountName
                pubkey_text.text = it.publicKey
                request_transfer_button.isEnabled = true
            }
        }
    }


    fun requestTransfer(){
        val json = """
                        [
                           {
                               "account": "eosio.token",
                               "name": "transfer",
                               "authorization": [
                                   {
                                       "actor": "${this.loginInfo!!.eosAccountName}",
                                       "permission": "active"
                                   }
                               ],
                               "data": {
                                   "from": "${this.loginInfo!!.eosAccountName}",
                                   "memo": "...",
                                   "quantity": "0.0001 EOS",
                                   "to": "genialwombat"
                               }
                           }
                         ]
            """.trimIndent()
        val intent = Wombat.getActionListSignIntent(json)
        startActivityForResult(intent, 101)
    }


    fun broadcastTransaction(serializedTX: String, signatures: List<String>){
        Log.d("SDK", "serialized tx: ${serializedTX}")
        Log.d("SDK", "Signatures: ${signatures.joinToString()}")
        Toast.makeText(this,"TODO",Toast.LENGTH_SHORT).show()
    }
}
