package co.trexcode.treestore

object PaymentModel {

    data class Payment(
        val code: Int,
        val `data`: List<Data>,
        val message: String
    )

    data class Data(
        val amount: String,
        val date_time: String,
        val detail: String,
        val goods_id: String,
        val goods_picture: String,
        val id: String,
        val name: String,
        val picture: String,
        val price: String,
        val status: String,
        val user_id: String,
        val payment_id: String,
        val payment_picture: String
    )

}

object PaymentDetailModel {

    data class Payment(
        val code: Int,
        val `data`: Data,
        val message: String
    )

    data class Data(
        val amount: String,
        val date_time: String,
        val detail: String,
        val goods_id: String,
        val goods_picture: String,
        val id: String,
        val name: String,
        val picture: String,
        val price: String,
        val status: String,
        val user_id: String,
        val payment_id: String,
        val payment_picture: String
    )

}

