package com.chainwisegroup.sdksample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chainwisegroup.wombatsdk.NativeTransferRequest
import com.chainwisegroup.wombatsdk.Wombat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login_button.isEnabled = Wombat.isAvailable(this)
        login_button.setOnClickListener {
            startActivityForResult(Wombat.getLoginIntent(), 100)
        }
        login_with_icon_res_button.setOnClickListener {
            startActivityForResult(Wombat.getLoginIntent(R.drawable.ic_location_off_black_24dp), 100)
        }
        login_with_icon_url_button.setOnClickListener {
            startActivityForResult(
                Wombat.getLoginIntent("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSVIGvxetWb6U3fgwJcvs79UPHeMmieb2O5dgPR5GeZZLAeU09CHw"),
                100
            )
        }

        request_transfer_button.setOnClickListener {
            val request = NativeTransferRequest(
                "eosio.token",
                "1.0000 EOS",
                "receiverabc",
                "sample memo",
                null
                /*NativeTransferRequest.MetaData(
                    null,
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSVIGvxetWb6U3fgwJcvs79UPHeMmieb2O5dgPR5GeZZLAeU09CHw",
                    "Sample item"
                )*/
            )
            val intent = Wombat.requestTransfer(request)
            startActivityForResult(intent, 101)
        }

        request_transfer_button_with_meta_url.setOnClickListener {
            val request = NativeTransferRequest(
                "eosio.token",
                "1.0000 EOS",
                "receiverabc",
                "sample memo",
                NativeTransferRequest.MetaData(
                    null,
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSVIGvxetWb6U3fgwJcvs79UPHeMmieb2O5dgPR5GeZZLAeU09CHw",
                    "Sample item for url"
                )
            )
            val intent = Wombat.requestTransfer(request)
            startActivityForResult(intent, 101)
        }

        request_transfer_button_with_meta_res.setOnClickListener {
            val request = NativeTransferRequest(
                "eosio.token",
                "1.0000 EOS",
                "receiverabc",
                "sample memo",
                NativeTransferRequest.MetaData(
                    R.drawable.ic_location_off_black_24dp,
                    null,
                    "Sample item for resource"
                )
            )
            val intent = Wombat.requestTransfer(request)
            startActivityForResult(intent, 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {

                val info = Wombat.getInfoFromResult(data)
                Log.d("LOGIN", "info: ${info.toString()}")
                info?.let {
                    Toast.makeText(this, "${info.name}: ${info.publicKey}", Toast.LENGTH_SHORT).show()
                    name_text.text = it.name
                    pubkey_text.text = it.publicKey
                }
            } else {
                Toast.makeText(this, "Canceled :(", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
