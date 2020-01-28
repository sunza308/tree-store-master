package co.trexcode.treestore

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var tinyDB: TinyDB
    private var mIsLogin: Boolean = false
    private val fragment1: Fragment = HomeFragment()
    private val fragment2: Fragment = OrderFragment()
    private val fragment3: Fragment = AccountFragment()
    private val fragment4: Fragment = PleaseLoginFragment()

    private val fm = supportFragmentManager
    private var active = fragment1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialModule()
        initialWidget()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.menu_search -> {
                Toast.makeText(applicationContext,"Search",Toast.LENGTH_LONG).show()
                startActivity(intent)
            }
                R.id.scanqr ->{
                    Toast.makeText(applicationContext, "Scan", Toast.LENGTH_LONG).show()
                    var intent = Intent(this, ScanActivity::class.java)
                    startActivity(intent)
                }


            R.id.item1 -> {
                Toast.makeText(applicationContext, "ITEM 1 SELECTED", Toast.LENGTH_LONG).show()
            }
            R.id.item2 -> {
                Toast.makeText(applicationContext, "ITEM 2 SELECTED", Toast.LENGTH_LONG).show()

            }
            R.id.item3 -> {
                Toast.makeText(applicationContext, "ITEM 3 SELECTED", Toast.LENGTH_LONG).show()

            }

        }
        return super.onOptionsItemSelected(item)
    }


    private fun initialModule() {
        tinyDB = TinyDB(application)
        mIsLogin = tinyDB.getBoolean("IS_LOGIN")

        Log.d("AUTH_MAIN_ACTIVITY", mIsLogin.toString())

    }

    private fun initialWidget() {
        fm.beginTransaction().add(R.id.main_container, fragment4, "4").hide(fragment4).commit()
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit()


        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    fm.beginTransaction().hide(active).show(fragment1).commit()
                    active = fragment1
                    supportActionBar!!.title = "หน้าแรก"
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_cart -> {
                    fm.beginTransaction().hide(active).show(fragment2).commit()
                    active = fragment2
                    supportActionBar!!.title = "ตะกร้าสินค้า"
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_account -> {
                    if (mIsLogin) {
                        fm.beginTransaction().hide(active).show(fragment3).commit()
                        active = fragment3
                    } else {
                        fm.beginTransaction().hide(active).show(fragment4).commit()
                        active = fragment4
                    }
                    supportActionBar!!.title = "บัญชีผู้ใช้งาน"
                    return@OnNavigationItemSelectedListener true
                }
                R.id.item1 -> {
                    Toast.makeText(applicationContext, "ITEM 1 SELECTED", Toast.LENGTH_LONG).show()

                }
                R.id.item2 -> {
                    Toast.makeText(applicationContext, "ITEM 2 SELECTED", Toast.LENGTH_LONG).show()

                }
                R.id.item3 -> {
                    Toast.makeText(applicationContext, "ITEM 3 SELECTED", Toast.LENGTH_LONG).show()

                }


            }
            false
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.goqrcode, menu)
        return super.onCreateOptionsMenu(menu)


    }
}
