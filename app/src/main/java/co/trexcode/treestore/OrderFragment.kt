package co.trexcode.treestore


import android.content.Context
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
import kotlinx.android.synthetic.main.basket_list_item.view.*
import java.util.ArrayList


class OrderFragment : Fragment() {

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tinyDB: TinyDB
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_order, container, false)

        initialModule()
        initialWidget()

        return rootView
    }

    private fun initialModule() {
        tinyDB = TinyDB(rootView.context)
    }

    private fun initialWidget() {
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener { setAdapter() }

        setAdapter()
    }

    private fun setAdapter() {

        val basketList: ArrayList<Any> = tinyDB.getListObject("BASKET_LIST", BasgetModel.Basget::class.java)

        Log.d("BASKET_LIST", basketList.toString())

        viewManager = LinearLayoutManager(activity)
        viewAdapter = OrderAdapter(basketList)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.mProductRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        if (mSwipeRefreshLayout.isRefreshing) {
            mSwipeRefreshLayout.isRefreshing = false
            Toast.makeText(activity, "ดาวน์โหลดเสร็จสิ้น", Toast.LENGTH_SHORT).show()
        }

    }

}

class OrderAdapter(private val list: ArrayList<Any>) :
    RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderAdapter.MyViewHolder {

        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.basket_list_item, parent, false) as View

        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item: BasgetModel.Basget = list[position] as BasgetModel.Basget
        val view: View = holder.view
        val context: Context = view.context

        if(list.size >= 5 && position + 1 == list.size) {
            view.itemDivider.visibility = View.GONE
        }
        view.txtBasketName.text = item.name
        view.txtBasgetAmount.text = "${item.price.toInt()} บาท X ${item.amount} ต้น"
        view.txtTotal.text = "${item.price.toInt() * item.amount}"
        Glide.with(view.context).load("http://10.199.120.73/tree_store/uploads/${item.picture}")
            .into(view.imgGoodsImage)
        view.basketItemList.setOnClickListener {
            val intent = Intent(view.context, BasketDetailActivity::class.java).apply {
                putExtra("id", item.id)
                putExtra("name", item.name)
                putExtra("detail", item.detail)
                putExtra("price", item.price)
                putExtra("picture", item.picture)
                putExtra("amount", item.amount)
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount() = list.size

}