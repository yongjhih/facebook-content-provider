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
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.util.Log;
//import com.github.kevinsawicki.http.HttpRequest;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
//import com.facebook.stetho.okhttp.StethoInterceptor;

public abstract class NetworkPipeContentProvider extends PipeContentProvider {
    public static final int BUFFER_SIZE = 32 * 1024;
    public static final int MAX_REDIRECT_COUNT = 5;
    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 KB

    @Override
    public InputStream getInputStream(Uri uri) throws IOException {
        return getInputStreamFromNetwork(getUri(uri));
    }

    abstract String getUri(Uri uri);

    public InputStream getInputStreamFromNetwork(String uri) throws IOException {
        HttpURLConnection conn = createConnection(uri);

        int redirectCount = 0;
        while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
            conn = createConnection(conn.getHeaderField("Location"));
            redirectCount++;
        }

        InputStream inputStream;

        try {
            inputStream = conn.getInputStream();
        } catch (IOException e) {
            // Read all data to allow reuse connection (http://bit.ly/1ad35PY)
            readAndCloseStream(conn.getErrorStream()); //IoUtils.readAndCloseStream(conn.getErrorStream());
            throw e;
        }

        return new ContentLengthInputStream(new BufferedInputStream(inputStream, BUFFER_SIZE), conn.getContentLength());
    }

    public static void readAndCloseStream(InputStream is) {
        final byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
        try {
            while (is.read(bytes, 0, DEFAULT_BUFFER_SIZE) != -1) {
            }
        } catch (IOException e) {
            // Do nothing
        } finally {
            closeSilently(is);
        }
    }

    public static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            // Do nothing
        }
    }

    protected HttpURLConnection createConnection(String url) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        //okHttpClient.setSslSocketFactory(HttpRequest.getTrustedFactory());
        //okHttpClient.setHostnameVerifier(HttpRequest.getTrustedVerifier());
        //okHttpClient.networkInterceptors().add(new StethoInterceptor());
        HttpURLConnection conn = new OkUrlFactory(okHttpClient).open(new URL(url));
        return conn;
    }

    @Override
    public String getType(Uri uri) {
        return URLConnection.guessContentTypeFromName(uri.toString());
    }
}
