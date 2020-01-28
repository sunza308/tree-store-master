package co.trexcode.treestore

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.history_list_item.view.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AdminOrderActivity : AppCompatActivity() {
  
    private lateinit var recyclerView: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var retrofit: Retrofit
    private lateinit var disposable: Disposable

    private lateinit var tinyDB: TinyDB
    private var mIsLogin: Boolean = false
    private lateinit var mAuth: UserModel.User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 2.0F
        supportActionBar!!.title = "รายการสั่งซื้อทั้งหมด"

        initialModule()
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
        viewManager = LinearLayoutManager(this)
        tinyDB = TinyDB(this)
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

    private fun initialWidget() {
        mSwipeRefreshLayout = findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener { getHistory() }
        recyclerView = findViewById(R.id.recyclerView)
        getHistory()
    }

    private fun getHistory() {
        val service = retrofit.create(APIsService::class.java)
        disposable = service.getUserPayment("all")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    setAdapter(result.data)
                    if (mSwipeRefreshLayout.isRefreshing) {
                        mSwipeRefreshLayout.isRefreshing = false
                        Toast.makeText(this, "ดาวน์โหลดเสร็จสิ้น", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    Log.d("ERROR", error.message)
                }
            )
    }

    private fun setAdapter(data: List<PaymentModel.Data>) {
        viewManager = LinearLayoutManager(this)
        mAdapter = AdminOderDapter(data)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mAdapter
        }
    }

}
class AdminOderDapter(private val list: List<PaymentModel.Data>) :
    RecyclerView.Adapter<AdminOderDapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):
            AdminOderDapter.MyViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_list_item, parent, false) as View

        return MyViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val view = holder.view
        val item = list[position]

        view.txtOrderID.text = "รหัสใบสั่งที่ #${item.payment_id}"
        view.txtBasgetAmount.text = "${item.price.toInt()} บาท X ${item.amount} ต้น"
        view.txtTotal.text = "${(item.amount.toInt() * item.price.toInt())}"
        view.historyItemList.setOnClickListener {
            val intent = Intent(view.context, AdminOrderDetailActivity::class.java).apply {
                putExtra("id", item.payment_id)
            }
            view.context.startActivity(intent)
        }
        Glide.with(view.context).load("http://10.199.120.73/tree_store/uploads/${item.picture}")
            .into(view.imgGoodsImage)
        if (item.status == "1") {
            view.txtStatus.text = "รอการตรวจสอบ"
            view.layoutStatus.setBackgroundColor(Color.parseColor("#0095ff"))
        } else if (item.status == "2") {
            view.txtStatus.text = "ชำระเงินแล้ว"
            view.layoutStatus.setBackgroundColor(Color.parseColor("#0BCD4B"))
        }


    }

    override fun getItemCount() = list.size
}