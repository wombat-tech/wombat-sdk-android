package com.chainwisegroup.wombatsdk

data class TransferRequest(val contract: String, val amount: String, val memo: String, val recipient: String) {

    class Builder {
        var contract: String? = null
        var amount: String? = null
        var memo: String = ""
        var recipient: String? = null


        fun build(): TransferRequest {
            assert(recipient != null)
            assert(amount != null)
            assert(contract != null)
            return TransferRequest(contract!!, amount!!, memo, recipient!!)
        }
    }
}


fun buildTransfer(builder: TransferRequest.Builder.() -> Unit): TransferRequest {
    return TransferRequest.Builder().apply(builder).build()
}