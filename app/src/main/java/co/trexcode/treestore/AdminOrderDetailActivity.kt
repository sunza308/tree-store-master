package co.trexcode.treestore

import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_admin_order_detail.*
import kotlinx.android.synthetic.main.dialog_admin_confirm_payment.*
import kotlinx.android.synthetic.main.dialog_show_doc.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class AdminOrderDetailActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var disposable: Disposable
    private lateinit var mDataResult: PaymentDetailModel.Data


    private var id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_order_detail)
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
            btnChangeStatus.visibility = View.GONE
        }
        Glide
            .with(this)
            .load("http://10.199.120.73/tree_store/uploads/${mDataResult.picture}")
            .into(imageView)
        btnCheckDoc.setOnClickListener {
            showDocDialog()
        }
        btnChangeStatus.setOnClickListener {
            onDialogConfirmPayment()
        }
    }

    private fun showDocDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_show_doc)
        Glide
            .with(this)
            .load("http://10.199.120.73/tree_store/uploads/${mDataResult.payment_picture}")
            .into(dialog.imageDoc)
        dialog.show()
    }

    private fun onDialogConfirmPayment() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_admin_confirm_payment)
        dialog.btnAccept.setOnClickListener {
            getConfirmPayment(dialog)
        }
        dialog.show()
    }

    private fun getConfirmPayment(dialog: Dialog) {
        val service = retrofit.create(APIsService::class.java)
        disposable = service.getConfirmPayment(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.d("TREXCODE_LOG", result.toString())
                    Toast.makeText(this, "ยืนยันการแจ้งชำระเงินเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    getHistoryDetail()
                },
                { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    Log.d("TREXCODE_LOG", error.message)
                }
            )
    }


}
