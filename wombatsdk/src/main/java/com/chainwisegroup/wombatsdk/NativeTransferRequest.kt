package com.chainwisegroup.wombatsdk

import android.os.Bundle

data class NativeTransferRequest(
    val contract: String,
    val amount: String,
    val recipient: String,
    val memo: String,
    val metadata: MetaData?
) {


    fun writeToBundle(bundle: Bundle) {
        bundle.putString("contract", contract)
        bundle.putString("amount", amount)
        bundle.putString("recipient", recipient)
        bundle.putString("memo", memo)
        if (metadata != null) {
            val metaBundle = Bundle()
            metadata.writeToBundle(metaBundle)
            bundle.putBundle("meta_data", metaBundle)
        }

    }

    companion object {
        fun fromBundle(bundle: Bundle): NativeTransferRequest {
            val contract = bundle.getString("contract")
            val amount = bundle.getString("amount")
            val recipient = bundle.getString("recipient")
            val memo = bundle.getString("memo") ?: ""
            val metadata: MetaData?
            if (bundle.containsKey("meta_data")) {
                val subBundle = bundle.getBundle("meta_data")
                metadata = MetaData.fromBundle(subBundle!!)
            } else {
                metadata = null
            }
            if (contract == null || amount == null || recipient == null) {
                throw Exception("invalid arguments")
            }
            return NativeTransferRequest(contract, amount, recipient, memo, metadata)
        }
    }

    data class MetaData(val iconRes: Int?, val iconUrl: String?, val name: String?) {

        fun writeToBundle(bundle: Bundle) {
            if (iconRes != null) bundle.putInt("icon_res", iconRes)
            if (iconUrl != null) bundle.putString("icon_url", iconUrl)
            if (name != null) bundle.putString("name", name)

        }

        companion object {
            fun fromBundle(bundle: Bundle): MetaData {
                val iconRes: Int? = bundle.getInt("icon_res")
                val iconUrl: String? = bundle.getString("icon_url")
                val name = bundle.getString("name")

                return MetaData(iconRes, iconUrl, name)
            }
        }
    }
}