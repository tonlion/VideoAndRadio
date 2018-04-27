package com.zhang.video.voice.`interface`

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RequestService{
    @GET("program_info_list/type/1/type_key/1")
//    fun getChannel(@Query("type1") type:Int,@Query("key1") key:Int):Call<ResponseBody>
    fun getChannel():Call<ResponseBody>
}

