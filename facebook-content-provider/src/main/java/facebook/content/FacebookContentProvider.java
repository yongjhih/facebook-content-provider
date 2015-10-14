/**
 * Copyright 2015 8tory, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import proguard.annotation.*;

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
            url = facebook.picture(uid).data.url;
        } catch (Exception e) {
            e.printStackTrace();
            //url = "https://graph.facebook.com/" + uid + "/picture?width=400&height=400";
            url = "https://graph.facebook.com/" + uid + "/picture?width=400&height=400&redirect=1";
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
        //@GET("/{uid}?fields=picture")
        //User picture(@Path("uid") String uid);

        @GET("/{uid}/picture?width=400&height=400&redirect=0")
        Picture picture(@Path("uid") String uid);
    }

    @Keep
    @KeepClassMembers
    static class User {
        Picture picture;
    }

    @Keep
    @KeepClassMembers
    static class Picture {
        Data data;
    }

    @Keep
    @KeepClassMembers
    static class Data {
        // "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xpf1/v/t1.0-1/p50x50/1234567"
        String url;
    }
}
