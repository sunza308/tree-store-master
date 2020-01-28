package co.trexcode.treestore


object UserModel {

    data class User(
        val code: Int,
        val `data`: Data,
        val message: String
    )

    data class Data(
        val id: String,
        val name: String,
        val password: String,
        val picture: String,
        val status: String,
        val username: String,
        val tel: String,
        val facebook: String

    )

}