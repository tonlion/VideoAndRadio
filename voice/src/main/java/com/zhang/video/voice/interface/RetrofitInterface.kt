package com.zhang.video.voice.`interface`

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RequestService{
    @GET("program_info_list/type/{type}/type_key/{key}")
    fun getChannel(@Query("type") type:Int,@Query("key") key:Int):Call<ResponseBody>
}

