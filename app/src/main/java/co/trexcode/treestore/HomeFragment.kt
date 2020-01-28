package co.trexcode.treestore

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.product_list_item.view.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var retrofit: Retrofit
    private lateinit var disposable: Disposable
    private lateinit var goods: GoodsModel.Goods
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initialModules()
        getGoods()

        rootView = inflater.inflate(R.layout.fragment_home, container, false)

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener { getGoods() }


        return rootView
    }

    private fun initialModules() {
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
                    Log.d("GOODS", result.toString())
                    setAdapter(goods.data)
                    if (mSwipeRefreshLayout.isRefreshing) {
                        mSwipeRefreshLayout.isRefreshing = false
                        Toast.makeText(activity, "ดาวน์โหลดเสร็จสิ้น", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    if (mSwipeRefreshLayout.isRefreshing) {
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                    /*Log.d("ERROR", error.message)*/
                }
            )
    }

    private fun setAdapter(data: List<GoodsModel.Data>) {
        viewManager = LinearLayoutManager(activity)
        viewAdapter = GoodsAdapter(data)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.mProductRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}

class GoodsAdapter(private val list: List<GoodsModel.Data>) :
    RecyclerView.Adapter<GoodsAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoodsAdapter.MyViewHolder {

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
            val intent = Intent(holder.view.context, GoodDetailActivity::class.java).apply {
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