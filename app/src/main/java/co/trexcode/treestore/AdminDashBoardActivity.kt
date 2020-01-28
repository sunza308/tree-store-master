package co.trexcode.treestore

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_admin_dash_board.*

class AdminDashBoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dash_board)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 2.0F
        supportActionBar!!.title = "ระบบจัดการ"

        initialWidget()

    }

    private fun initialWidget() {
        btnGoodsActivity.setOnClickListener {
            val intent = Intent(this, AdminGoodsList::class.java)
            this.startActivity(intent)
        }
        btnOrderActivity.setOnClickListener {
            val intent = Intent(this, AdminOrderActivity::class.java)
            this.startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
