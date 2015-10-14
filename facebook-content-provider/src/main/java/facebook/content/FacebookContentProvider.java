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
import retrofit.*;
import retrofit.http.*;

public class FacebookContentProvider extends NetworkPipeContentProvider {
    public static final String AUTHORITY = "facebook.content.FacebookContentProvider";
    public static final String TAG = AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/");

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int PICTURE = 1;
    public Facebook facebook;

    static {
        sUriMatcher.addURI(AUTHORITY, "/*/picture", PICTURE);
    }

    @Override
    public boolean onCreate() {
        boolean b = super.onCreate();
        RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint("https://graph.facebook.com")
            .build();
        facebook = restAdapter.create(Facebook.class);
        return true;
    }

    @Override
    public String getUri(Uri uri) {
        String uid = uri.getPathSegments().get(0);
        android.util.Log.d(TAG, "uid: " + uid);

        String url = null;
        try {
            url = facebook.picture(uid).picture.data.url;
        } catch (Exception e) {
            e.printStackTrace();
            url = "https://graph.facebook.com/" + uid + "/picture?width=400&height=400";
        }

        android.util.Log.d(TAG, "url: " + url);
        return url;

    }

    public static Uri getPictureUriFromUid(String uid) {
        return CONTENT_URI.buildUpon().appendPath(uid).appendPath("picture").build();
    }

    public static String getPictureStringFromUid(String uid) {
        return getPictureUriFromUid(uid).toString();
    }

    interface Facebook {
        //@GET("/{uid}/picture?width=400&height=400")
        @GET("/{uid}?fields=picture")
        User picture(@Path("uid") String uid);
    }

    static class User {
        Picture picture;
        static class Picture {
            Data data;
            static class Data {
                // "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xpf1/v/t1.0-1/p50x50/1234567"
                String url;
            }
        }
    }
}
