package com.av.uwish


import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.av.uwish.Adapter.SearchAdapter
import com.av.uwish.Model.InboxModel
import com.av.uwish.Retrofit.RetrofitClient
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class Search : Fragment() {

    lateinit var serviceModels: java.util.ArrayList<InboxModel>
    lateinit var service_listview: ListView
    lateinit var service_name:String
    lateinit var service_img:String
    lateinit var serAdapter:SearchAdapter
    lateinit var sharedPreferences: SharedPreferences

    lateinit var searchText: EditText
    lateinit var search_text: String


    val isOnline: Boolean
        get() {
            val cm = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnectedOrConnecting
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var rootview:View= inflater.inflate(R.layout.fragment_search,container,false)

        searchText=rootview.findViewById(R.id.et_search)


        service_listview=rootview.findViewById(R.id.service_search_listview) as ListView
        serviceModels= ArrayList()


        searchText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int, after:Int) {
            }
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                if (isOnline)
                {
                    search_text = searchText.text.toString()
                    if (!search_text.equals(""))
                    {
                        search()
                    }
                    else
                    {
                        search_text = "**"
                        search()
                    }
                }
                else
                {
                    internetAlert()
                }
            }
            override fun afterTextChanged(s:Editable) {
            }
        })


        service_listview.onItemClickListener = object: AdapterView.OnItemClickListener
        {
            override fun onItemClick(parent: AdapterView<*>, view:View, position:Int,id:Long) {

                var mid:String=(view.findViewById(R.id.mid) as TextView).text.toString()
                var uname:String=(view.findViewById(R.id.uname) as TextView).text.toString()

                sharedPreferences = context!!.getSharedPreferences("VISIT", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("uid", mid)
                editor.putString("uname", uname)
                editor.commit()

                val activity = context as AppCompatActivity
                val myFragment: Fragment = User_fragment()
                activity.supportFragmentManager.beginTransaction()
                    .add(R.id.maincontent, myFragment)
                    .replace(R.id.maincontent, myFragment).addToBackStack(null)
                    .commit()

            }
        }







        return rootview
    }



    private fun search() {


        val call = RetrofitClient.instance.api.search(search_text)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val s = response.body()!!.string()
                    //loadingDialog.dismiss()
                    serviceModels.clear()
                    if (s != null) {
                        val jsonObject = JSONObject(s)

                        val status = jsonObject.getBoolean("status")

                        // Toast.makeText(this@VolunteerHomeActivity, ""+status, Toast.LENGTH_SHORT).show();

                        if (status) {
                            val jsonArray = jsonObject.getJSONArray("data")

                            for (i in 0 until jsonArray.length()) {
                                val c = jsonArray.getJSONObject(i)
                                val modelObject = InboxModel()

                                modelObject.id = c.getString("id")
                                modelObject.name = c.getString("name")
                                modelObject.img_url=c.getString("photo")

                                serviceModels.add(modelObject)
                            }

                            get_voter_list_adapter()

                        } else {

                            if (serAdapter != null) {
                                serAdapter.clear()
                            }
                        }


                    }


                } catch (e: NullPointerException) {
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // loadingDialog.dismiss()

            }
        })
    }


    fun get_voter_list_adapter() {
        try {
            serAdapter = SearchAdapter(activity!!.applicationContext, R.layout.user_inbox_layout, serviceModels)
            service_listview.adapter = serAdapter
            serAdapter.notifyDataSetChanged()
        } catch (e: NullPointerException) {
        }

    }


    fun internetAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("No Internet Connection")
            .setMessage("We can not detect any internet connectivity.Please check your internet connection and try again")
        builder.setPositiveButton("Retry") { dialog, id ->
            if (false) {
                internetAlert()
            } else {

                search()

            }
        }
        builder.show()
    }


}
