package co.trexcode.treestore

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.Toast
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.product_list_item.view.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AdminGoodsList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var retrofit: Retrofit
    private lateinit var disposable: Disposable
    private lateinit var goods: GoodsModel.Goods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_goods_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 2.0F
        supportActionBar!!.title = "รายการสินค้า"

        initialModule()
        initialWidget()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_add -> {
                val intent = Intent(this, AdminGoodsAddActivity::class.java)
                this.startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_actionbar_admin_goods_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initialWidget() {
        mSwipeRefreshLayout = findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener { getGoods() }
        getGoods()
    }

    private fun initialModule() {
        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://10.199.120.73/tree_store/")
            .build()
    }

    private fun getGoods() {
        val service = retrofit.create(APIsService::class.java)
        disposable = service.getGoods()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    goods = result
                    setAdapter(result.data)
                    Log.d("TREXCODE_LOG", result.toString())
                    if (mSwipeRefreshLayout.isRefreshing) {
                        mSwipeRefreshLayout.isRefreshing = false
                        Toast.makeText(this, "ดาวน์โหลดเสร็จสิ้น", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    if (mSwipeRefreshLayout.isRefreshing) {
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    Log.d("ERROR", error.message)
                }
            )
    }

    private fun setAdapter(data: List<GoodsModel.Data>) {
        viewManager = LinearLayoutManager(this)
        viewAdapter = AdminGoodsAdapter(data)

        recyclerView = findViewById<RecyclerView>(R.id.goodsRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

}

class AdminGoodsAdapter(private val list: List<GoodsModel.Data>) :
    RecyclerView.Adapter<AdminGoodsAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminGoodsAdapter.MyViewHolder {

        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list_item, parent, false) as View

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val txtTotal = "${list[position].price}"
        val item: GoodsModel.Data = list[position]
        holder.view.txtBasketName.text = item.name
        holder.view.txtGoodsDetail.text = item.detail
        holder.view.txtTotal.text = txtTotal
        if(list.size >= 5 && position + 1 == list.size) {
            holder.view.productDivider.visibility = View.GONE
        }

        Glide.with(holder.view.context).load("http://10.199.120.73/tree_store/uploads/${item.picture}")
            .into(holder.view.imgGoodsImage)


        holder.view.mItemList.setOnClickListener {
            val intent = Intent(holder.view.context, AdminGoodDetailActivity::class.java).apply {
                putExtra("id", item.id)
                putExtra("name", item.name)
                putExtra("detail", item.detail)
                putExtra("price", item.price)
                putExtra("picture", item.picture)
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            holder.view.context.startActivity(intent)

        }

    }

    override fun getItemCount() = list.size
}
