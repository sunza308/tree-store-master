package co.trexcode.treestore


object GoodsModel {
    data class Goods(
        val code: Int,
        val data: List<Data>,
        val message: String
    )

    data class Data(
        val detail: String,
        val id: String,
        val name: String,
        val picture: String,
        val price: String
    )
}

object BasgetModel {
    data class Basget(
        val detail: String,
        val id: String,
        val name: String,
        val picture: String,
        val price: String,
        val total: Int,
        val amount: Int
    )
}