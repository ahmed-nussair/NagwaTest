package com.nussair.nagwatest.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nussair.nagwatest.model.FileItem
import com.nussair.nagwatest.repository.FileItemRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class FilesViewModel : ViewModel() {

    private val filesList: MutableLiveData<List<FileItem>> = MutableLiveData<List<FileItem>>()

    private lateinit var disposable: Disposable

    init {
        loadFiles()
    }

    fun getFiles(): LiveData<List<FileItem>> {
        return filesList
    }

    private fun loadFiles() {
        val retrofit = Retrofit.Builder()
            .baseUrl(FileItemRepository.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        val repository = retrofit.create(FileItemRepository::class.java)

        val observable = repository.getFiles()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        disposable =
            observable.subscribe({ files -> filesList.value = files }) { e -> Log.e("Nussair", "") }
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}