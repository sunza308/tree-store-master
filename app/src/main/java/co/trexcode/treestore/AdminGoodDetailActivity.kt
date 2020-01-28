package co.trexcode.treestore

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_admin_good_detail.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AdminGoodDetailActivity : AppCompatActivity() {

    private lateinit var image: Image
    private var isFile: Boolean = false
    private lateinit var img: RequestBody
    private lateinit var filePath: File
    private var body: MultipartBody.Part? = null
    private lateinit var retrofit: Retrofit
    private lateinit var disposable: Disposable

    private var id: String = ""
    private var name: String = ""
    private var detail: String = ""
    private var price: Int = 0
    private var picture: String = ""
    private var contact: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_good_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
            R.id.action_delete -> {
                deleteGoods()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_actionbar_basket_activity, menu)
        return super.onCreateOptionsMenu(menu)
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
        id = intent.getStringExtra("id")
        name = intent.getStringExtra("name")
        detail = intent.getStringExtra("detail")
        picture = intent.getStringExtra("picture")
        price = intent.getStringExtra("price").toInt()



        supportActionBar!!.title = "แก้ไขข้อมูล$name"
    }

    private fun initialWidget() {
        editGoodsName.setText(name)
        editGoodsDetail.setText(detail)
        editGoodsPrice.setText(price.toString())



        Glide
            .with(this)
            .load("http://10.199.120.73/tree_store/uploads/$picture")
            .into(imageView)
        imageView.setOnClickListener {
            ImagePicker.create(this)
                .showCamera(true)
                .single()
                .start()
        }
        btnEdit.setOnClickListener {
            postEditDetail()
        }
    }


    private fun postEditDetail() {

        Toast.makeText(this, "กำลังทำการแก้ไข", Toast.LENGTH_LONG).show()

        val id = RequestBody.create(MediaType.parse("text/plain"), id)
        val name = RequestBody.create(MediaType.parse("text/plain"), name)
        val detail = RequestBody.create(MediaType.parse("text/plain"), detail)
        val price = RequestBody.create(MediaType.parse("text/plain"), price.toString())


        val service = retrofit.create(APIsService::class.java)
        if (!isFile) body = null
        if (editGoodsName.text.toString() != "" && editGoodsDetail.text.toString() != "" && editGoodsPrice.text.toString() != ""&& editGoodsContact.text.toString()!="") {
            disposable = service.editGoods(body, id, name, detail, price)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        Log.d("UPLOAD_FILE_RESPONSE", result.string())
                        Toast.makeText(this, "แก้ไขข้อมูลเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
                    },
                    { error ->
                        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                        Log.d("ERROR", error.message)
                    }
                )
        } else {
            Toast.makeText(this, "โปรดป้อนข้อมูลให้ครบทุกช่อง", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteGoods() {

        val service = retrofit.create(APIsService::class.java)
        disposable = service.deleteGoods(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.d("UPLOAD_FILE_RESPONSE", result.string())
                    Toast.makeText(this, "ลบข้อมูลเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
                    finish()
                },
                { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    Log.d("ERROR", error.message)
                }
            )
    }

}
