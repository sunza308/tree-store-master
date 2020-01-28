package co.trexcode.treestore

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 2.0F
        supportActionBar!!.title = "สมัครสมาชิก"

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
        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://10.199.120.73/tree_store/")
            .build()
    }

    private fun initialWidget() {
        btnRegister.setOnClickListener {
            if(txtUsername.text.toString() != "" && txtPassword.text.toString() != "" && txtName.text.toString() != "" ) {
                onPostRegister()
            }else {
                Toast.makeText(this, "โปรดป้อนข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPostRegister() {

        Toast.makeText(this, "กรุณารอสักครู่", Toast.LENGTH_SHORT).show()
        val service = retrofit.create(APIsService::class.java)
        disposable = service.register(txtUsername.text.toString(), txtPassword.text.toString(), txtName.text.toString(),txtTel.text.toString(), txtFb.text.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if (result.code == 200) {
                        Toast.makeText(this, "สมัครสมาชิกเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "ชื่อผู้ใช้งานถูกใช้งานแล้ว", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    Log.d("ERROR", error.message)
                }
            )
    }

}
