package co.trexcode.treestore

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var tinyDB: TinyDB
    private lateinit var retrofit: Retrofit
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 2.0F
        supportActionBar!!.title = "เข้าใช้งานระบบ"

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

        tinyDB = TinyDB(application)

        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://10.199.120.73/tree_store/")
            .build()
    }

    private fun initialWidget() {
        btnLogin.setOnClickListener {
            if(txtUsername.text.toString() != "" && txtPassword.text.toString() != "") {
                onPostRegister()
            }else {
                Toast.makeText(this, "โปรดป้อนข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
            }
        }
        txtRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            this.startActivity(intent)
        }
    }

    private fun onPostRegister() {

        Toast.makeText(this, "กรุณารอสักครู่", Toast.LENGTH_SHORT).show()
        val service = retrofit.create(APIsService::class.java)
        disposable = service.login(txtUsername.text.toString(), txtPassword.text.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if (result.code == 200) {
                        Log.d("LOGIN", result.toString())
                        tinyDB.putObject("JSON_AUTH", result)
                        tinyDB.putBoolean("IS_LOGIN", true)
                        Toast.makeText(this, "ยินดีต้อนรับคุณ${result.data.name}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        this.startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "ชื่อผู้ใช้งานหรือรหัสผ่านของคุณไม่ถูกต้อง กรุณาลองใหม่อีกครั้ง",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    Log.d("ERROR", error.message)
                }
            )
    }

}
