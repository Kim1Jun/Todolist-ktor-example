package kim.wonjun.response

import kim.wonjun.exception.TodoException

data class BaseResponse<T>(
    val ok: Boolean,
    val data: T?,
    val error: ErrorSummary?
) {
    data class ErrorSummary(
        val code: String,
        val msg: String,
    )

    companion object {
        fun <T> ok(data: T?, deprecation: Boolean = false) =
            BaseResponse(true, data, null)

        fun error(ex: TodoException) =
            BaseResponse(false, null, ErrorSummary("failed", "γνῶθι σεαυτόν"))
    }
}
