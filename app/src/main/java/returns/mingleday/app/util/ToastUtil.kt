package returns.mingleday.app.util

import android.content.Context
import android.widget.Toast
import returns.mingleday.R

object ToastUtil {
    fun makeExceptionToast(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    fun makeErrorToast(context: Context) {
        Toast.makeText(context, R.string.internal_server_error.toString(), Toast.LENGTH_SHORT).show()
    }

    fun makeToastLong(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show()
    }

    fun makeToastShort(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }
}