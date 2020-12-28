package com.av.uwish.Retrofit


import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Api {



    //1ogin

        @FormUrlEncoded
        @POST("login.php")
        fun login(
                @Field("mobile") mobile:String,
                @Field("password") password: String

        ): Call<ResponseBody>


    //1ogin

    @FormUrlEncoded
    @POST("get_otp.php")
    fun getOtp(
        @Field("mobile") mobile:String

    ): Call<ResponseBody>



    @Multipart
    @POST("upload_video_global.php")
    fun uploadVideoFile(
        @Part file: MultipartBody.Part?,
        @Part("file") name: RequestBody?,
        @Part("user_id") user_id: String

    ): Call<ResponseBody>

    //Vendor 1ogin

    @FormUrlEncoded
    @POST("get_profile.php")
    fun getProfile(
        @Field("user_id") user_id:String

    ): Call<ResponseBody>




    @FormUrlEncoded
    @POST("get_subject.php")
    fun getSubject(
        @Field("classstd") classtd:String,
        @Field("board") board:String,
        @Field("user_id") user_id:String
        ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("get_class.php")
    fun getClass(
        @Field("board") board:String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("get_post.php")
    fun getMyPost(
        @Field("user_id") user_id:String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("upload_global_id.php")
    fun uploadId(
        @Field("user_id") user_id:String,
        @Field("filename") filename:String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("upload_wish_id.php")
    fun uploadWishId(
        @Field("user_id") user_id:String,
        @Field("filename") filename:String,
        @Field("del_date") del_date:String,
        @Field("del_time") del_time:String,
        @Field("del_mobile") del_mobile:String,
        @Field("del_email") del_email:String,
        @Field("del_name") del_name:String,
        @Field("wish_type") wish_type:String,

        @Field("facebook_id") facebook_id:String,
        @Field("insta_id") insta_id:String,
        @Field("linkedin_id") linkedin_id:String,
        @Field("skype_id") skype_id:String


        ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("get_test_list.php")
    fun getTestList(
        @Field("lesson_name") lesson_name:String,
        @Field("subject_id") subject_id:String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("get_main.php")
    fun getChapterDetails(
        @Field("lesson_name") lesson_name:String,
        @Field("subject_id") subject_id:String

    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("live_payment.php")
    fun live_payment(
        @Field("user_id") user_id:String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("get_home.php")
    fun getHomeVideo(
        @Field("user_id") user_id:String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("message_user_list.php")
    fun getMessageUserList(
        @Field("uid") uid:String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("get_inbox.php")
    fun getInbox(
        @Field("user_id") user_id:String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("get_sent.php")
    fun getSentInbox(
        @Field("user_id") user_id:String
    ): Call<ResponseBody>

    //Add New User

    @FormUrlEncoded
    @POST("register.php")
    fun add_user(

        @Field("fname") fname: String,
        @Field("mname") mname: String,
        @Field("lname") lname: String,
        @Field("mobile") mobile: String,
        @Field("email") email: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("country") country: String,
        @Field("dob") dob: String,
        @Field("gender") gender: String

    ): Call<ResponseBody>

    //Add Product

    @FormUrlEncoded
    @POST("register.php")
    fun registration(

        @Field("name") name: String,
        @Field("classstd") classstd: String,
        @Field("address") address: String,
        @Field("city") city: String,
        @Field("board") board: String,
        @Field("password") password: String,
        @Field("state") state: String,
        @Field("pincode") pincode: String,
        @Field("mobile") mobile: String,
        @Field("alt_mobile") alt_mobile: String,
        @Field("email") email: String,
        @Field("school") school: String,
        @Field("section") section: String

    ): Call<ResponseBody>



    @FormUrlEncoded
    @POST("update_profile.php")
    fun Update(

        @Field("user_id") user_id: String,
        @Field("fname") fname: String,
        @Field("mname") mname: String,
        @Field("lname") lname: String,
        @Field("email") email: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("country") country: String,
        @Field("password") password: String

    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("message_add.php")
    fun send_chat_desc(

        @Field("message") message: String,
        @Field("mid") mid: String,
        @Field("uid") uid: String

        ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("message_list.php")
    fun get_chats(

        @Field("mid") mid: String,
        @Field("uid") uid: String

    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("search.php")
    fun search(

        @Field("search") search: String

    ): Call<ResponseBody>



    @FormUrlEncoded
    @POST("onclick_response.php")
    fun like(

        @Field("user_id") user_id: String,
        @Field("video_id") video_id: String,
        @Field("response") response: String

    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("follow.php")
    fun follow(

            @Field("uid") uid: String,
            @Field("fid") fid: String

    ): Call<ResponseBody>




    @FormUrlEncoded
    @POST("buy.php")
    fun BuyNow(

        @Field("user_id") user_id: String,
        @Field("product_id") product_id: String,
        @Field("product_name") product_name: String,
        @Field("price") price: String,
        @Field("quantity") quantity: String,
        @Field("total_amount") total_amount: String,
        @Field("use_wallet") use_wallet: Boolean,
        @Field("gift_order") gift_order: Boolean,
        @Field("gift_message") gift_message: String,
        @Field("subunit") subunit: String

    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("wishlist_add.php")
    fun AddToWishlist(

        @Field("user_id") user_id: String,
        @Field("product_id") product_id: String

    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("get_scores.php")
    fun getScore(

        @Field("student_id") student_id: String

    ): Call<ResponseBody>


    // VERIFY OTP
    @FormUrlEncoded
    @POST("get_scores.php")
    fun verifyOtp(

        @Field("mobile") mobile: String,
        @Field("otp") otp: String

    ): Call<ResponseBody>


    // GET ARENA GAMES

    @FormUrlEncoded
    @POST("popular_product.php")
    fun getPopularProduct(

        @Field("demo") demo: String

    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("dropdown_product.php")
    fun getSize(

        @Field("product_name") product_name: String

    ): Call<ResponseBody>



    @FormUrlEncoded
    @POST("popular_product_onclick.php")
    fun getProductDetails(

        @Field("product_id") product_id: String,
        @Field("product_name") product_name: String,
        @Field("user_id") user_id: String


    ): Call<ResponseBody>


    // GET HOME SLIDER

    @FormUrlEncoded
    @POST("slider.php")
    fun getBanners(

        @Field("demo") demo: String

    ): Call<ResponseBody>



    // GET GAME SLIDER

    @FormUrlEncoded
    @POST("category_list.php")
    fun getCategory(

        @Field("id") id: String

    ): Call<ResponseBody>


    // FORGOT PASSWORD

    @FormUrlEncoded
    @POST("forgot_password.php")
    fun forgot_password(

        @Field("passwordn") passwordn: String,
        @Field("passwordc") passwordc: String,
        @Field("mobile") mobile: String

    ): Call<ResponseBody>


    // GET GAME ROUNDS

    @FormUrlEncoded
    @POST("category_on_click.php")
    fun getCateProduct(

        @Field("category_name") category_name: String

    ): Call<ResponseBody>


    // JOIN ROUND GAME

    @FormUrlEncoded
    @POST("entry.php")
    fun joinRound(

        @Field("uname") uname: String,
        @Field("mobile") mobile: String,
        @Field("gameid") gameid: String,
        @Field("rdate") rdate: String,
        @Field("rtime") rtime: String,
        @Field("rid") rid: String,
        @Field("fees") fees: String


        ): Call<ResponseBody>


    //UPADTE PROFILE

    @FormUrlEncoded
    @POST("update_profile.php")
    fun update_profile(

        @Field("name") name: String,
        @Field("mobile") mobile: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("address") address: String,
        @Field("pincode") pincode: String,
        @Field("city") city: String

    ): Call<ResponseBody>


    // UPLOAD PROFILE IMAGE

    @FormUrlEncoded
    @POST("upload_profile_pic.php")   // Add end_point url
    fun uploadImage(

        @Field("image_bytes") image_bytes: String,
        @Field("mobile") mobile: String

    ): Call<ResponseBody>


    // GET Ad
    @FormUrlEncoded
    @POST("my_orders.php")   // Add end_point url
    fun getOrder(

        @Field("user_id") user_id: String

    ): Call<ResponseBody>


    // GET RULES OF ROUND

    @FormUrlEncoded
    @POST("get_rules.php")   // Add end_point url
    fun getRules(

        @Field("arena_name") arena_name: String,
        @Field("round_category") round_category: String

    ): Call<ResponseBody>


    // GET RANKS OF ROUND

    @FormUrlEncoded
    @POST("get_ranks.php")   // Add end_point url
    fun getRanks(

        @Field("rid") rid: String

    ): Call<ResponseBody>


    // GET WINNINGS

    @FormUrlEncoded
    @POST("get_winnings.php")   // Add end_point url
    fun getWinnings(

        @Field("mobile") mobile: String

    ): Call<ResponseBody>

    // GET WINNINGS

    @FormUrlEncoded
    @POST("get_upcoming.php")   // Add end_point url
    fun getNextRounds(

        @Field("mobile") mobile: String

    ): Call<ResponseBody>

    // GET WINNINGS

    @FormUrlEncoded
    @POST("get_completed.php")   // Add end_point url
    fun getCompleted(

        @Field("mobile") mobile: String

    ): Call<ResponseBody>


    // GET MATCHES

    @FormUrlEncoded
    @POST("wishlist_show.php")   // Add end_point url
    fun getwishlist(

        @Field("user_id") user_id: String

    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("cart_show.php")   // Add end_point url
    fun getcartList(

        @Field("user_id") user_id: String

    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("cart_buy_all.php")   // Add end_point url
    fun bookcart(

        @Field("user_id") user_id: String

    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("get_feedback.php")   // Add end_point url
    fun showfeedback(

        @Field("user_id") user_id: String

    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("add_feedback.php")   // Add end_point url
    fun send_feedback(

        @Field("user_id") user_id: String,
        @Field("message") message: String,
        @Field("rating") rating: String

    ): Call<ResponseBody>



    @FormUrlEncoded
    @POST("wishlist_delete.php")   // Add end_point url
    fun removeWish(

        @Field("item_id") item_id: String

    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("cart_item_delete.php")   // Add end_point url
    fun removecartItem(

        @Field("item_id") item_id: String,
        @Field("user_id") user_id: String

    ): Call<ResponseBody>

    // GET OPTIONS

    @FormUrlEncoded
    @POST("get_options.php")   // Add end_point url
    fun getOptions(

        @Field("id") id: String

    ): Call<ResponseBody>


    // NE PASSWORD

    @FormUrlEncoded
    @POST("forget_pass.php")   // Add end_point url
    fun forgotPass(

        @Field("password") password: String,
        @Field("mobile") mobile: String,
        @Field("otp") otp: String

    ): Call<ResponseBody>


    // GET OPTIONS

    @FormUrlEncoded
    @POST("cricket_entry.php")   // Add end_point url
    fun CricketEntry(

        @Field("mid") mid: String,
        @Field("uid") uid: String,
        @Field("over") over: String,
        @Field("option") option: String,
        @Field("fees") fees: String

    ): Call<ResponseBody>

    // GET OPTIONS

    @FormUrlEncoded
    @POST("get_cricket_rules.php")
    fun getCricketRules(

        @Field("dummy") dummy: String

    ): Call<ResponseBody>

    // ADD ORBS

    @FormUrlEncoded
    @POST("add_orb.php")
    fun AddOrbs(

        @Field("id") id: String,
        @Field("orb") orb: String

    ): Call<ResponseBody>



    // ADD ORBS

    @FormUrlEncoded
    @POST("redeem.php")
    fun RedeemOrbs(

        @Field("id") id: String,
        @Field("orb") orb: String

    ): Call<ResponseBody>


    // GET ORBS

    @FormUrlEncoded
    @POST("get_orbs.php")
    fun GetOrbs(

        @Field("id") id: String

    ): Call<ResponseBody>


}
