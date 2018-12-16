package com.shaynek.jquiz.data

import androidx.lifecycle.MutableLiveData
import com.shaynek.jquiz.enums.Sort
import com.shaynek.jquiz.model.RedditPostData
import com.shaynek.jquiz.model.RedditResponse
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Observable ViewModel for displaying the list of posts.
 * @constructor Initializes the data status and fetches data from the repository.
 */
class PostsViewModel : BaseViewModel() {

    @Inject
    lateinit var repository: AppRepository

    val posts: MutableLiveData<List<RedditPostData>> = MutableLiveData()
    val dataStatus: MutableLiveData<DataStatus> = MutableLiveData()

    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        dataStatus.postValue(DataStatus.LOADING)
        // TODO: Change this to the last sort used instead of default Best
        disposable.add(
            repository.fetchPosts(Sort.BEST)
                .subscribe(this::onCluesFetched, this::onCluesFetchError))
    }

    /**
     * Clears disposables from API calls.
     */
    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun onSortSelected(sort: Sort?) {
        sort?.let {
            dataStatus.postValue(DataStatus.LOADING)
            repository.fetchPosts(it)
                .subscribe(this::onCluesFetched, this::onCluesFetchError)
        }
    }

    private fun onCluesFetched(response: RedditResponse?) {
        posts.postValue(response?.data?.children?.map { it.data })
        dataStatus.postValue(DataStatus.SUCCESS)
    }

    private fun onCluesFetchError(e: Throwable?) {
        dataStatus.postValue(DataStatus.FAILED)
        e?.run { printStackTrace() }
    }
}