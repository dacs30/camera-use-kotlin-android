import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

private const val TAG = "Configs"

// Class based from the one seen in class
// e.g., we can do FileUtility.createDefaultProfileDir(...)
internal object Configs {
    private const val PROFILE_DIR = "profile"
    private const val LOG_DIR = "log"

    private fun createDefaultProfileDir(context: Context): File {
        // setup subdirectories
        val profileDirectory = File(context.applicationContext.filesDir, PROFILE_DIR)
        // creates the directory if not present yet
        if (!profileDirectory.exists()) {
            // true if and only if the directory was created; false otherwise
            profileDirectory.mkdir()
        }
        return profileDirectory
    }

    fun getDefaultProfileDir(context: Context): File {
        return createDefaultProfileDir(context)
    }

    private fun createDefaultLogDir(context: Context): File {
        // setup subdirectories
        val logDirectory = File(context.applicationContext.filesDir, LOG_DIR)
        // creates the directory if not present yet
        if (!logDirectory.exists()) {
            // true if and only if the directory was created; false otherwise
            logDirectory.mkdir()
        }
        return logDirectory
    }

    fun getDefaultLogDir(context: Context): File {
        return createDefaultLogDir(context)
    }


    fun getAssetDirPath(): String {
        return "file:///android_asset"
    }

    /**
     * construct the Uri based on the fileName and the default directory
     */
    fun createUri(context: Context, fileName: String): Uri {

        val mFile = File(getDefaultProfileDir(context), fileName)
        val mUri = FileProvider.getUriForFile(
            context,
            "com.example.a30_camera.fileprovider", // need to match exactly as defined in AndroidManifest
            mFile
        )
        Log.d(TAG, mUri.toString())
        return mUri
    }

}

