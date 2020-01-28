package co.trexcode.treestore

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*


interface APIsService {

    @GET("index.php?apis=goods")
    fun getGoods(): Observable<GoodsModel.Goods>

    @Multipart
    @POST("index.php?apis=editGoods")
    fun editGoods(
        @Part image: MultipartBody.Part?,
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("detail") detail: RequestBody,
        @Part("price") price: RequestBody
    ): Observable<ResponseBody>

    @Multipart
    @POST("index.php?apis=addGoods")
    fun addGoods(
        @Part image: MultipartBody.Part?,
        @Part("name") name: RequestBody,
        @Part("detail") detail: RequestBody,
        @Part("price") price: RequestBody
    ): Observable<ResponseBody>

    @Multipart
    @POST("index.php?apis=postPayment")
    fun uploadFile(
        @Part image: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("goodsId") goodsId: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("datetime") datetime: RequestBody,
        @Part("status") status: RequestBody
    ): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("index.php?apis=register")
    fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("tel") tel: String,
        @Field("facebook") facebook: String

    ): Observable<NormalModel>

    @FormUrlEncoded
    @POST("index.php?apis=login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Observable<UserModel.User>

    @GET("index.php?apis=payments")
    fun getUserPayment(
        @Query("user_id") user_id: String
    ): Observable<PaymentModel.Payment>

    @GET("index.php?apis=deleteGoods")
    fun deleteGoods(
        @Query("id") id: String
    ): Observable<ResponseBody>

    @GET("index.php?apis=payments_detail")
    fun getPaymentDetail(
        @Query("id") id: String
    ): Observable<PaymentDetailModel.Payment>

    @GET("index.php?apis=confirmPayment")
    fun getConfirmPayment(
        @Query("id") id: String
    ): Observable<ResponseBody>

}
