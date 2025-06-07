package pl.edu.uj.tcs.rchess.viewmodel.paging

data class PageFetchResult<T, K>(
    val items: List<T>,
    /** Key for the next page, or null if there are no more pages */
    val nextPageKey: K?,
)
