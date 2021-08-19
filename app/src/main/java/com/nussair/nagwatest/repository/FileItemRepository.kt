package com.nussair.nagwatest.repository

import com.nussair.nagwatest.model.FileItem
import io.reactivex.Observable
import retrofit2.http.GET

interface FileItemRepository {
    companion object {
        const val BASE_URL = "https://nagwa.free.beeceptor.com/"
    }

    @GET("movies")
    fun getFiles(): Observable<List<FileItem>>
}