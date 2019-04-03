package com.chainwisegroup.sdksample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chainwisegroup.wombatsdk.Wombat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login_button.isEnabled = Wombat.isAvailable(this)
        login_button.setOnClickListener {
            startActivityForResult(Wombat.getLoginIntent(),100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){

                val info = Wombat.getInfoFromResult(data)
                Log.d("LOGIN", "info: ${info.toString()}")
                info?.let{
                    Toast.makeText(this,"${info.name}: ${info.publicKey}", Toast.LENGTH_SHORT).show()
                    name_text.text = it.name
                    pubkey_text.text = it.publicKey
                }
            }
            else{
                Toast.makeText(this,"Canceled :(", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
