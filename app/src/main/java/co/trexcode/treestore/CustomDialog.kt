package co.trexcode.treestore

import android.content.Context
import android.app.Dialog
import android.content.Intent
import kotlinx.android.synthetic.main.dialog_please_login.*

class CustomDialog(private val context: Context) {

    private val dialog: Dialog = Dialog(context)

    fun dialogPleaseLogin() {
        dialog.setContentView(R.layout.dialog_please_login)
        dialog.btnAccept.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
        dialog.show()
    }

}