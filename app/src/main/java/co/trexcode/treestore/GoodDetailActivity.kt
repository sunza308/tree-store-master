package co.trexcode.treestore

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_good_detail.*
import java.util.*

class GoodDetailActivity : AppCompatActivity() {

    private lateinit var tinyDB: TinyDB
    private var amount: Int = 1
    private var basketList: ArrayList<Any> = ArrayList()
    private var myIndex: Int = 0

    private var id: String = ""
    private var name: String = ""
    private var detail: String = ""
    private var price: Int = 0
    private var picture: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_good_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 2.0F
        supportActionBar!!.title = "รายละเอียดสินค้า"

        initialIntent()
        initialWidget()

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

    private fun initialIntent() {
        id = intent.getStringExtra("id")
        name = intent.getStringExtra("name")
        detail = intent.getStringExtra("detail")
        picture = intent.getStringExtra("picture")
        price = intent.getStringExtra("price").toInt()
    }


    private fun initialWidget() {

        tinyDB = TinyDB(this)

        txtGoodsDetail.text = detail
        txtGoodsName.text = name
        Glide
            .with(this)
            .load("http://10.199.120.73/tree_store/uploads/$picture")
            .into(imageView)

        txtGoodsPrice.text = "${price}฿"
        txtAmount.text = "$amount ต้น"
        txtPrice.text = "${price * amount} บาท"
        txtPriceFull.text = "(${ThaiBaht().getText(price * amount)})"

        btnAdd.setOnClickListener {
            amount++
            txtAmount.text = "$amount ต้น"
            txtPrice.text = "${price * amount} บาท"
            txtPriceFull.text = "(${ThaiBaht().getText(price * amount)})"
        }

        btnDelete.setOnClickListener {
            if (amount == 1) {
                Toast.makeText(this, "ไม่สามารถกำหนดจำนวนให้น้อยกว่า 1 ได้", Toast.LENGTH_SHORT).show()
            } else {
                amount--
                txtAmount.text = "$amount ต้น"
                txtPrice.text = "${price * amount} บาท"
                txtPriceFull.text = "(${ThaiBaht().getText(price * amount)})"
            }
        }

        btnBuy.setOnClickListener {
            val basketDB: ArrayList<Any> = tinyDB.getListObject("BASKET_LIST", BasgetModel.Basget::class.java)
            val basket: BasgetModel.Basget = BasgetModel.Basget(
                id = id,
                name = name,
                picture = picture,
                detail = detail,
                price = price.toString(),
                amount = amount,
                total = price * amount
            )
            if (basketDB.size > 0) {
                for ((index: Int, item: Any) in basketDB.withIndex()) {
                    item as BasgetModel.Basget
                    if (item.id == id) {
                        myIndex = index
                    }
                }
                if (myIndex != 0) {
                    basketDB[myIndex] = basket
                    tinyDB.putListObject("BASKET_LIST", basketDB)
                } else {
                    basketDB.add(basket)
                    tinyDB.putListObject("BASKET_LIST", basketDB)
                }
            } else {
                basketList.add(basket)
                tinyDB.putListObject("BASKET_LIST", basketList)
            }
            /*Log.d("BASKET_LIST", basketDB.toString())*/
            Toast.makeText(
                application,
                "เพิ่มสินค้าลงตะกร้าเรียบร้อยแล้ว",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val view = inflater.inflate(R.layout.dialog_confirm_order, null)
        builder.setView(view)

        builder.show()
    }

}
