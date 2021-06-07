

package com.example.misezero_android.UI.QuoteList

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.misezero_android.MainActivity
import com.example.misezero_android.R
import com.example.misezero_android.UserDataClass
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.search_dlg.view.*
import kotlinx.android.synthetic.main.search_item.view.*
import kotlinx.android.synthetic.main.search_scene.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


/**
 *
 * 2021-06-03 관심지역 추가 시 다이얼로그 안띄움
 *
 *
 */
data class SearchData(
    var place_name : String = "",
    var address : String = "",
    var load_address : String = "",
    var x : String = "",
    var y : String = ""
)

class Search_Scene : AppCompatActivity() {
    lateinit var adapter: Search_Adapter
    lateinit var countryrv: RecyclerView
    var searchResData = mutableListOf<SearchData>()
    var handler : DisplayHandler? = null
    var realm : Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_scene)

        handler = DisplayHandler()
        onInitDataBase()

        intent?.getStringExtra("MiseMsg")?.let{
            showDialog(it)
        }

        //////////////////////////////////////////////////////////////////
        // 서치 아이콘
        ///////////////////////////////////////////////////////////////////
        val searchIcon = country_search.findViewById<ImageView>(R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.BLACK)

        /////////////////////////////////////////////////////////////////////
        // 취소
        //////////////////////////////////////////////////////////////////////
        val cancelIcon = country_search.findViewById<ImageView>(R.id.search_close_btn)
        cancelIcon.setColorFilter(Color.BLACK)

        // 텍스트 뷰
        val textView = country_search.findViewById<TextView>(R.id.search_src_text)
        textView.setTextColor(Color.BLACK)

        // 리사이클 뷰
        countryrv = findViewById(R.id.country_rv)
        countryrv.layoutManager = LinearLayoutManager(countryrv.context)
        countryrv.setHasFixedSize(true)

        ///////////////////////////////////////////////////////////////////////////////
        /// 사용자 이벤트
        ///////////////////////////////////////////////////////////////////////////////
        country_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            ////////////////////////////////////////////////////////
            /// 데이터 입력 후 완료키 눌렀을 때
            //////////////////////////////////////////////////////////
            override fun onQueryTextSubmit(query: String?): Boolean {
                getAddresssbyKaKao(query!!)
                return false
            }

            ////////////////////////////////////////////////////////
            /// 텍스트 입력 중
            //////////////////////////////////////////////////////////
            override fun onQueryTextChange(newText: String?): Boolean {
//                adapter.filter.filter(newText)
                return false
            }

        })
        //돌아가기 버튼 클릭 후 액티비티 종료
        back_Button.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        this.finish()
    }

    fun showDialog(data : String) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.search_dlg,null)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("저장할 이름을 넣어주세요")
            .setPositiveButton("저장") {dialog, which ->

                var arrStr = data.split("/")
                var name = view.name.text.toString()
                name = if(name == "") arrStr[0] else name

                addUserData(name, arrStr[1],arrStr[2])

                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("Next","list_screen")
                startActivity(intent)

            }
            .setNegativeButton("취소",null)
            .create()

            //  여백 눌러도 창 안없어지게
            alertDialog.setCancelable(false)

            alertDialog.setView(view)
            alertDialog.show()

    }

    ///////////////////////////////////////////////////////////////////////////////////////
    /// 관심지역 데이터 베이스
    ///////////////////////////////////////////////////////////////////////////////////////
    fun onInitDataBase(){
        Realm.init(this)
        var config = RealmConfiguration.Builder().name("myrealm.realm").build()
        Realm.setDefaultConfiguration(config)

        realm = Realm.getDefaultInstance()
    }

    fun addUserData(name : String, latitude : String, longitude : String){

        realm?.executeTransaction {
            var check = realm?.where(UserDataClass::class.java)?.equalTo("name", name)?.findFirst()
            if (check == null){
                var temp = it.createObject(UserDataClass::class.java)
                temp.setData(name, latitude, longitude)
                Log.d("MiseLog", "데이터 저장 완료")
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /// 카카오 API를 통해 키워드 검색을 한다.
    ////////////////////////////////////////////////////////////////////////////////
    fun getAddresssbyKaKao(keyword : String){
        val url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=$keyword"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().addHeader("Authorization", "KakaoAK 5e8d63b477cb5684045f8ec30f4ccbbd").url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                try {
                    var items = JSONArray(JSONObject(res).getString("documents"))


                    /// 결과 저장
                    searchResData = mutableListOf<SearchData>()
                    for (index in 0 until items.length()){
                        var res = items.getJSONObject(index)
                        var name = res.get("place_name").toString()
                        var load_address = res.get("road_address_name").toString()
                        var address_name = res.get("address_name").toString()
                        var x = res.get("x").toString()
                        var y = res.get("y").toString()
                        searchResData.add(SearchData(name, address_name, load_address, x, y))
                    }

                    /// 검색 결과를 표시한다.
                    handler?.sendEmptyMessage(0)



                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    /////////////////////////////////////////////////////////////
    //// 화면 갱신 해들러
    //////////////////////////////////////////////////////////////
    inner class DisplayHandler : Handler(){
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            adapter = Search_Adapter(searchResData)
            countryrv.adapter = adapter
        }
    }
}


/////////////////////////////////
//검색 리스트 리사이클러뷰
/////////////////////////////////
class Search_Adapter(private var contryList : MutableList<SearchData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),Filterable {

    //아이템 리스트
    var countryFilterList = mutableListOf<SearchData>()

    lateinit var mcontext: Context

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        countryFilterList = contryList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val countryListView =
            LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        val sch = CountryHolder(countryListView)
        mcontext = parent.context
        return sch
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.select_country_container.setBackgroundColor(Color.TRANSPARENT)

        holder.itemView.select_country_text.setTextColor(Color.BLACK)
        holder.itemView.select_country_text.text = countryFilterList[position].place_name

        holder.itemView.sub_country_text.setTextColor(Color.BLACK)
        holder.itemView.sub_country_text.text = countryFilterList[position].address

        holder.itemView.setOnClickListener {

            val intent = Intent(mcontext, Search_Scene::class.java)
            var selecteData = countryFilterList[position]
            var data = "${selecteData.place_name}/${selecteData.x}/${selecteData.y}"
            intent.putExtra("MiseMasg", data)
            mcontext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return countryFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                filterResults.values = countryFilterList
                return filterResults
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                TODO("Not yet implemented")
            }

        }
    }
}