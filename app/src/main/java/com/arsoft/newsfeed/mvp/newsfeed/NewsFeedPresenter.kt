package com.arsoft.newsfeed.mvp.newsfeed

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.arsoft.newsfeed.app.NewsFeedApplication
import com.arsoft.newsfeed.data.DataProvider
import com.arsoft.newsfeed.data.models.FeedItemModel
import com.arsoft.newsfeed.data.newsfeed.request.NewsFeedService
import kotlinx.coroutines.*
import javax.inject.Inject

@InjectViewState
class NewsFeedPresenter: MvpPresenter<NewsFeedView>() {

    @Inject
    lateinit var newsFeedApiService: NewsFeedService

    init {
        NewsFeedApplication.INSTANCE.getAppComponent()!!.inject(this)
    }

    private val newsFeedRepository = DataProvider.provideNewsFeed(newsFeedApiService)
    private var newsFeedJob: Job? = null

    fun loadNewsFeed(accessToken: String){
        viewState.showLoading()
        newsFeedJob = GlobalScope.launch {
            val newsFeedList: ArrayList<FeedItemModel> = newsFeedRepository.getNewsFeed(accessToken = accessToken)
            withContext(Dispatchers.Main) {
                if (newsFeedList.isNotEmpty()) {
                    viewState.hideLoading()
                    viewState.loadNewsFeed(items = newsFeedList)
                }
            }
        }
    }

    override fun onDestroy() {
        newsFeedJob?.cancel()
        super.onDestroy()
    }
}