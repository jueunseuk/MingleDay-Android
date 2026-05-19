package returns.mingleday.app.util

import android.content.Context
import android.net.Uri
import java.io.File

object FileUtil {

    fun uriToFile(
        context: Context,
        uri: Uri,
        fileName: String = "temp.jpg"
    ): File {
        val input =
            context.contentResolver.openInputStream(uri)

        val file = File(context.cacheDir, fileName)

        input?.use { inputStream ->
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
        }

        return file
    }
}