package co.trexcode.treestore


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.menu_list_item.view.*


class AccountFragment : Fragment() {

    private lateinit var tinyDB: TinyDB
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var mAuth: UserModel.User
    private var mIsLogin = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val menus: List<AccountMenu> =
            listOf(
                AccountMenu(R.drawable.ic_bag, "รายการสั่งซื้อ"),
                AccountMenu(R.drawable.ic_turn_off, "ออกจากระบบ"),
                AccountMenu(R.drawable.ic_playlist_add_check_black_24dp, "ระบบหลังร้าน")
            )


        val rootView = inflater.inflate(R.layout.fragment_account, container, false)

        tinyDB = TinyDB(rootView.context)
        mIsLogin = tinyDB.getBoolean("IS_LOGIN")
        if (mIsLogin) {
            mAuth = tinyDB.getObject("JSON_AUTH", UserModel.User::class.java)
            rootView.txtAccountName.text = mAuth.data.name
        }

        viewManager = LinearLayoutManager(activity)
        viewAdapter = MenuAdepter(menus)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.mMenuRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return rootView

//        val rootView = inflater.inflate(R.layout.please_login, container, false)
//
//        val intent = Intent(rootView.context, LoginActivity::class.java)
//        rootView.btnLogin.setOnClickListener {
//            rootView.context.startActivity(intent)
//        }


//        return rootView

    }


}

class MenuAdepter(private val list: List<AccountMenu>) :
    RecyclerView.Adapter<MenuAdepter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuAdepter.MyViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_list_item, parent, false) as View

        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item: AccountMenu = list[position]
        val view: View = holder.view

        val tinyDB = TinyDB(view.context)
        val mIsLogin = tinyDB.getBoolean("IS_LOGIN")
        val mAuth: UserModel.User
        if (mIsLogin) {
            mAuth = tinyDB.getObject("JSON_AUTH", UserModel.User::class.java)
            if(mAuth.data.status == "1" && item.image == R.drawable.ic_playlist_add_check_black_24dp) {
                view.visibility = View.GONE
            }
        }

        val context: Context = view.context
        view.mMenuName.text = item.menuName
        view.mImageIcon.setImageResource(item.image)

        view.itemOfMenu.setOnClickListener {
            when {
                item.image == R.drawable.ic_turn_off -> {
                    Toast.makeText(context, "คุณได้ออกจากระบบเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
                    @Suppress("NAME_SHADOWING") val tinyDB = TinyDB(context)
                    tinyDB.putBoolean("IS_LOGIN", false)
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }
                item.image == R.drawable.ic_playlist_add_check_black_24dp -> {
                    val intent = Intent(context, AdminDashBoardActivity::class.java)
                    context.startActivity(intent)
                }
                item.image == R.drawable.ic_bag -> {
                    val intent = Intent(context, HistoryActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }


    override fun getItemCount() = list.size
}


data class AccountMenu(
    val image: Int,
    val menuName: String
)

