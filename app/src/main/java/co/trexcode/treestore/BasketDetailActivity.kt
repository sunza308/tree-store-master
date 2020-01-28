package co.trexcode.treestore

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_basket_detail.*
import kotlinx.android.synthetic.main.dialog_cencel.*
import kotlinx.android.synthetic.main.dialog_order_form.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*


class BasketDetailActivity : AppCompatActivity() {

    private lateinit var basketDB: ArrayList<Any>
    private lateinit var body: MultipartBody.Part
    private lateinit var retrofit: Retrofit
    private lateinit var orderDialog: Dialog
    private lateinit var tinyDB: TinyDB
    private lateinit var filePath: File
    private lateinit var image: Image
    private var isFile: Boolean = false
    private lateinit var img: RequestBody
    private var mIsLogin: Boolean = false
    private lateinit var mAuth: UserModel.User
    private lateinit var disposable: Disposable

    private var basketList: ArrayList<Any> = ArrayList()
    private var myIndex: Int = 0

    private var id: String = ""
    private var name: String = ""
    private var detail: String = ""
    private var price: Int = 0
    private var amount: Int = 1
    private var picture: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 2.0F

        initialModule()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            image = ImagePicker.getFirstImageOrNull(data)
            filePath = File(image.path)

            img = RequestBody.create(MediaType.parse("image/*"), filePath)
            body = MultipartBody.Part.createFormData("upload", filePath.name, img)

            if (filePath.exists()) {
                isFile = true
                val myBitmap = BitmapFactory.decodeFile(filePath.absolutePath)
                orderDialog.imgView.visibility = View.VISIBLE
                orderDialog.textView3.visibility = View.GONE
                orderDialog.imgView.setImageBitmap(myBitmap)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initialModule() {

        tinyDB = TinyDB(this)
        basketDB = tinyDB.getListObject("BASKET_LIST", BasgetModel.Basget::class.java)
        mIsLogin = tinyDB.getBoolean("IS_LOGIN")
        if (mIsLogin) {
            mAuth = tinyDB.getObject("JSON_AUTH", UserModel.User::class.java)
            Log.d("JSON_AUTH", mAuth.toString())
        }

        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://10.199.120.73/tree_store/")
            .build()
    }

    private fun initialIntent() {

        id = intent.getStringExtra("id")
        name = intent.getStringExtra("name")
        detail = intent.getStringExtra("detail")
        picture = intent.getStringExtra("picture")
        price = intent.getStringExtra("price").toInt()
        amount = intent.getIntExtra("amount", 1)
    }


    @SuppressLint("SetTextI18n")
    private fun initialWidget() {

        txtGoodsDetail.text = detail
        txtGoodsName.text = name
        txtGoodsPrice.text = "${price}฿"
        txtAmount.text = "$amount ต้น"
        txtPrice.text = "${(price * amount)} บาท"
        txtPriceFull.text = ThaiBaht().getText(price * amount)
        Glide
            .with(this)
            .load("http://10.199.120.73/tree_store/uploads/$picture")
            .into(imageView)

        btnAdd.setOnClickListener {
            amount++
            txtAmount.text = "$amount ต้น"
            txtPrice.text = "${(price * amount)} บาท"
            txtPriceFull.text = ThaiBaht().getText(price * amount)
        }

        btnDelete.setOnClickListener {
            if (amount == 1) {
                Toast.makeText(this, "ไม่สามารถกำหนดจำนวนให้น้อยกว่า 1 ได้", Toast.LENGTH_SHORT).show()
            } else {
                amount--
                txtAmount.text = "$amount ต้น"
                txtPrice.text = "${(price * amount)} บาท"
                txtPriceFull.text = ThaiBaht().getText(price * amount)
            }
        }

        //btnBuy.setOnClickListener {
          //  if (mIsLogin) {
            //    showOrderDialog()
            //} else {
              //  CustomDialog(this).dialogPleaseLogin()
            //}
        //}

        //btnCancel.setOnClickListener {
          //showDialog()
      // }

    }


    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_cencel)
        dialog.btnAccept.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showOrderDialog() {
        orderDialog = Dialog(this)
        orderDialog.setContentView(R.layout.dialog_order_form)
        orderDialog.imgView.visibility = View.GONE
        orderDialog.textView3.visibility = View.VISIBLE

        orderDialog.btnGallery.setOnClickListener {
            ImagePicker.create(this)
                .showCamera(true)
                .single()
                .start()
        }

        orderDialog.btnAcceptForm.setOnClickListener {
            if (orderDialog.editDateTime.text.toString() != "" && isFile) {
                postOrder()
            } else {
                Toast.makeText(this, "โปรดป้อนวันที่/เวลา และแนบรูปภาพประกอบ", Toast.LENGTH_SHORT).show()
            }
        }

        orderDialog.show()
    }

    private fun postOrder() {

        val status = RequestBody.create(MediaType.parse("text/plain"), "1")
        val userId = RequestBody.create(MediaType.parse("text/plain"), mAuth.data.id)
        val goodsId = RequestBody.create(MediaType.parse("text/plain"), id)
        val amount = RequestBody.create(MediaType.parse("text/plain"), amount.toString())
        val datetime =
            RequestBody.create(MediaType.parse("text/plain"), orderDialog.editDateTime.text.toString())

        val service = retrofit.create(APIsService::class.java)
        disposable = service.uploadFile(body, userId, goodsId, amount, datetime, status)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.d("UPLOAD_FILE_RESPONSE", result.string())
                    orderDialog.dismiss()
                    for ((index: Int, item: Any) in basketDB.withIndex()) {
                        val item: BasgetModel.Basget = item as BasgetModel.Basget
                        if (item.id == id) {
                            myIndex = index
                        }
                    }
                    Log.d("MY_INDEX", myIndex.toString())
                    basketDB.removeAt(myIndex)
                    tinyDB.putListObject("BASKET_LIST", basketDB)
                    Toast.makeText(this, "ดำเนินการสั่งซื้อ/แจ้งชำระเงินเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
                    finish()
                },
                { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    Log.d("ERROR", error.message)
                }
            )
    }


}
