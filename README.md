# AndroidQTests

AndroidQTests is a project to test the [scoped storage](https://developer.android.com/preview/privacy/scoped-storage) introduced with AndroidQ

It contains 2 apps:

* [writer](writer) is an app that will start a service and write logs in the background. It will make these logs available via a custom [ContentProvider](https://developer.android.com/reference/android/content/ContentProvider):

```
override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
    if (uri.path != "/log.txt") {
        throw FileNotFoundException()
    }
    return return ParcelFileDescriptor.open(File(context!!.getExternalFilesDir(null), "log.txt"), MODE_READ_ONLY);
}
```


* [reader](reader) is an app that reads these logs and displays them. It access them with a [ContentResolver](https://developer.android.com/reference/android/content/ContentResolver)

```
contentResolver.openInputStream(Uri.parse("content://net.mbonnin.androidqwriter.fileprovider/log.txt"))
```

