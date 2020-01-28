package co.trexcode.treestore

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_admin_goods_add.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AdminGoodsAddActivity : AppCompatActivity() {

    private lateinit var image: Image
    private var isFile: Boolean = false
    private lateinit var img: RequestBody
    private lateinit var filePath: File
    private lateinit var body: MultipartBody.Part
    private lateinit var retrofit: Retrofit
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_goods_add)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "เพิ่มสินค้า"
        supportActionBar!!.elevation = 2.0F

        initialModule()
        initialIntent()
        initialWidget()

    }

    private fun initialModule() {
        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://10.199.120.73/tree_store/")
            .build()
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
                imageView.setImageBitmap(myBitmap)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initialIntent() {
    }

    private fun initialWidget() {
        imageView.setOnClickListener {
            ImagePicker.create(this)
                .showCamera(true)
                .single()
                .start()
        }
        btnEdit.setOnClickListener {
            postAddGoods()
        }
    }


    private fun postAddGoods() {

        Toast.makeText(this, "กำลังทำการแก้ไข", Toast.LENGTH_LONG).show()

        val name = RequestBody.create(MediaType.parse("text/plain"), editGoodsName.text.toString())
        val detail = RequestBody.create(MediaType.parse("text/plain"), editGoodsDetail.text.toString())
        val price = RequestBody.create(MediaType.parse("text/plain"), editGoodsPrice.text.toString())

        val service = retrofit.create(APIsService::class.java)
        if (isFile && editGoodsName.text.toString() != "" && editGoodsDetail.text.toString() != "" && editGoodsPrice.text.toString() != "") {
            disposable = service.addGoods(body, name, detail, price)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        Log.d("UPLOAD_FILE_RESPONSE", result.string())
                        Toast.makeText(this, "เพิ่มสินค้าเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    { error ->
                        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                        Log.d("ERROR", error.message)
                    }
                )
        } else {
            Toast.makeText(this, "โปรดป้อนข้อมูลให้ครบทุกช่องและเลือกรูปภาพ", Toast.LENGTH_SHORT).show()
        }
    }

}
