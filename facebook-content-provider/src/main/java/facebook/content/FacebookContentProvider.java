package facebook.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;

public class FacebookContentProvider extends NetworkPipeContentProvider {
    public static final String AUTHORITY = "facebook.content.FacebookContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/");

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int PICTURE = 1;

    static {
        sUriMatcher.addURI(AUTHORITY, "/*/picture", PICTURE);
    }

    @Override
    public String getUri(Uri uri) {
        String uid = uri.getPathSegments().get(0);
        return "https://graph.facebook.com/" + uid;
    }
}
