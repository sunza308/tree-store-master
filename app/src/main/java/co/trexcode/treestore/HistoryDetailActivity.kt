package co.trexcode.treestore

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import kotlinx.android.synthetic.main.activity_history_detail.*
import kotlinx.android.synthetic.main.dialog_order_form.*
import java.io.File
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.MultipartBody


class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var body: MultipartBody.Part
    private lateinit var retrofit: Retrofit
    private lateinit var orderDialog: Dialog
    private lateinit var image: Image
    private lateinit var filePath: File
    private var isFile: Boolean = false
    private lateinit var disposable: Disposable
    private lateinit var img: RequestBody
    private lateinit var mDataResult: PaymentDetailModel.Data


    private var id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 2.0F
        supportActionBar!!.title = "รายละเอียดใบสั่งซื้อ"

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


        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://10.199.120.73/tree_store/")
            .build()


    }

    private fun initialIntent() {
        id = intent.getStringExtra("id")
        supportActionBar!!.title = "รายละเอียดใบสั่งซื้อที่ #$id"
    }


    private fun initialWidget() {

        getHistoryDetail()

    }

    private fun getHistoryDetail() {
        val service = retrofit.create(APIsService::class.java)
        disposable = service.getPaymentDetail(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    mDataResult = result.data
                    setDataToView()
                    Log.d("TREXCODE_LOG", result.toString())
                },
                { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    Log.d("TREXCODE_LOG", error.message)
                }
            )
    }

    private fun setDataToView() {

        txtGoodsDetail.text = mDataResult.detail
        txtGoodsName.text = mDataResult.name
        txtGoodsPrice.text = "${mDataResult.price}฿"
        txtAmount.text = "${mDataResult.amount} ต้น"
        txtPrice.text = "${(mDataResult.price.toInt() * mDataResult.amount.toInt())} บาท"
        txtPriceFull.text = ThaiBaht().getText(mDataResult.price.toInt() * mDataResult.amount.toInt())
        if (mDataResult.status == "1") {
            txtStatus.text = "รอการตรวจสอบ"
            layoutStatus.setBackgroundColor(Color.parseColor("#0095ff"))
        } else if (mDataResult.status == "2") {
            txtStatus.text = "ชำระเงินแล้ว"
            layoutStatus.setBackgroundColor(Color.parseColor("#0BCD4B"))
        }
        Glide
            .with(this)
            .load("http://10.199.120.73/tree_store/uploads/${mDataResult.picture}")
            .into(imageView)
    }


}
