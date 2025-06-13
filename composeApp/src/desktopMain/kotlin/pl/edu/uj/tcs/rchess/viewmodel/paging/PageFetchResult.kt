package pl.edu.uj.tcs.rchess.viewmodel.paging

/**
 * A data class returned in the `fetchPage` method in the [Paging] constructor.
 *
 * @see pl.edu.uj.tcs.rchess.viewmodel.paging.Paging
 */
data class PageFetchResult<T, K>(
    /** List of items fetched in the current page */
    val items: List<T>,
    /** Key for the next page, or null if there are no more pages */
    val nextPageKey: K?,
)
