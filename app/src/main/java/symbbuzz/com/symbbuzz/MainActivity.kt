package symbbuzz.com.symbbuzz

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.support.annotation.IdRes
import android.text.TextUtils
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import com.vimeo.networking.VimeoClient
import com.vimeo.networking.callbacks.AuthCallback
import com.vimeo.networking.callbacks.ModelCallback
import com.vimeo.networking.callbacks.VimeoCallback
import com.vimeo.networking.model.VideoList
import com.vimeo.networking.model.error.VimeoError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.CacheControl
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.HttpException
import java.io.*
import java.net.SocketTimeoutException
import java.util.ArrayList


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    public val STORAGE = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public val RC_LOCATION_PERM = 124
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1
    var uri: Uri? = null
    var imagePath = ""
    //lateinit var detector : FirebaseVisionTextDetector
    var compositeDisposable: CompositeDisposable = CompositeDisposable()
    val STAFF_PICKS_VIDEO_URI = "/channels/927/videos"
    //val apiClient = VimeoClient.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*var className = "symbbuzz.com.symbbuzz.SecondActivity"
        val classType = Class.forName(className)
        go.setOnClickListener {
            startActivity(Intent(applicationContext, classType))
        }*/

        /*FirebaseApp.initializeApp(this)

        detector = FirebaseVision.getInstance()
            .visionTextDetector

        takePic.setOnClickListener {
            if(EasyPermissions.hasPermissions(this, *STORAGE)) {
                selectImage()
            }
            else {
                EasyPermissions.requestPermissions(
                    this@MainActivity,
                    getString(R.string.rationale_storage),
                    RC_LOCATION_PERM,
                    *STORAGE)
            }
        }*/

        select_Pic.setOnClickListener {
            if(EasyPermissions.hasPermissions(this, *STORAGE)) {
                selectImage()
            }
            else {
                EasyPermissions.requestPermissions(
                    this@MainActivity,
                    getString(R.string.rationale_storage),
                    RC_LOCATION_PERM,
                    *STORAGE)
            }
        }

        /*if (apiClient.vimeoAccount.accessToken == null) {
            // If there is no access token, fetch one on first app open
            authenticateWithClientCredentials()
        }*/

        //fetchStaffPicks()
    }

    fun authenticateWithClientCredentials() {
        VimeoClient.getInstance().authorizeWithClientCredentialsGrant(object : AuthCallback {
            override fun success() {
                var accessToken = VimeoClient.getInstance().getVimeoAccount().getAccessToken()
                Log.d("Success:", accessToken)
            }

            override fun failure(error: VimeoError?) {
                var errorMessage = error!!.getDeveloperMessage()
                Log.d("Failure:", errorMessage)
            }

        })
    }

    /*private fun fetchStaffPicks() {
        apiClient.fetchNetworkContent(STAFF_PICKS_VIDEO_URI, object : ModelCallback<VideoList>(VideoList::class.java) {
            override fun success(videoList: VideoList?) {
                if (videoList != null && videoList.data != null) {
                    var videoTitlesString = ""
                    var addNewLine = false
                    for (video in videoList.data) {
                        if (addNewLine) {
                            videoTitlesString += "\n"
                        }
                        addNewLine = true
                        videoTitlesString += video.name
                    }
                    vimeoList.text = videoTitlesString
                }
                toast("Staff Picks Success")
            }

            override fun failure(error: VimeoError?) {
                toast("Staff Picks Failure")
                vimeoList.text = error?.developerMessage
            }

        })
    }*/

    fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Take Photo") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val f = File(Environment
                    .getExternalStorageDirectory(), "temp.jpg")

                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                        m.invoke(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f))
                startActivityForResult(intent, REQUEST_CAMERA)

            } else if (items[item] == "Choose from Library") {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(intent, "Select File"),
                    SELECT_FILE)
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                var f = File(Environment.getExternalStorageDirectory()
                    .toString())
                for (temp in f.listFiles()!!) {
                    if (temp.name == "temp.jpg") {
                        f = temp
                        break
                    }
                }
                try {
                    var bm: Bitmap
                    val btmapOptions = BitmapFactory.Options()

                    bm = BitmapFactory.decodeFile(f.absolutePath,
                        btmapOptions)

                    val tempUri = getImageUri(applicationContext, bm)
                    uri = tempUri
                    //sendUriToFirebaseVision(uri)

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    val finalFile = File(getRealPathFromURI(tempUri))

                    //to check the rotation
                    try {
                        val exif = ExifInterface(f.absolutePath)
                        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
                        Log.d("EXIF", "Exif: " + orientation)
                        val matrix = Matrix()
                        if (orientation == 6) {
                            matrix.postRotate(90f)
                        } else if (orientation == 3) {
                            matrix.postRotate(180f)
                        } else if (orientation == 8) {
                            matrix.postRotate(270f)
                        }
                        bm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)//rotate image
                    } catch (e: Exception) {

                    }

                    //to scale down the image
                    val nh = (bm.height * (512.0 / bm.width)).toInt()
                    val scaled = Bitmap.createScaledBitmap(bm, 512, nh, true)
                    //makeFirebaseVisionImage(scaled)
                    bitmapToUrl(scaled)

                    val path = (android.os.Environment
                        .getExternalStorageDirectory().toString()
                        + File.separator
                        + "Phoenix" + File.separator + "default")
                    f.delete()
                    var fOut: OutputStream? = null
                    val file = File(path, System
                        .currentTimeMillis().toString() + ".jpg")
                    try {
                        fOut = FileOutputStream(file)
                        bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
                        fOut.flush()
                        fOut.close()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else if (requestCode == SELECT_FILE) {
                val selectedImageUri = data!!.data
                uri = selectedImageUri
                //sendUriToFirebaseVision(uri)
                imagePath = getPath(selectedImageUri, this)
                var bm: Bitmap
                val btmapOptions = BitmapFactory.Options()
                bm = BitmapFactory.decodeFile(imagePath, btmapOptions)
                try {
                    bm = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                //makeFirebaseVisionImage(bm)
                bitmapToUrl(bm)
            }
        }
    }

    private fun bitmapToUrl(bitmap: Bitmap?) {

    }

    fun getPath(uri: Uri, activity: Activity): String {
        val column_index: Int
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = activity
            .managedQuery(uri, projection, null, null, null)
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } else {
            val thePath = "no-path-found"
            return RealPathUtil.getRealPathFromURI_API19(this@MainActivity, uri)
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        selectImage()
    }

    private fun toast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

}
