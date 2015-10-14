package facebook.content;

import android.content.ContentProvider;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.util.Log;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class PipeContentProvider extends SimpleContentProvider {
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) {
        ParcelFileDescriptor[] pipe = null;

        try {
            pipe = ParcelFileDescriptor.createPipe();

            new TransferThread(getInputStream(uri), new AutoCloseOutputStream(pipe[1])).start();
        }
        catch (IOException e) {
            Log.e("PipeContentProvider", "Exception opening pipe", e);
        }

        return pipe[0];
    }

    abstract InputStream getInputStream(Uri uri) throws IOException;

    static class TransferThread extends Thread {
        InputStream in;
        OutputStream out;

        TransferThread(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            byte[] buf = new byte[1024];
            int len;

            try {
                while ((len = in.read(buf)) >= 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.flush();
                out.close();
            }
            catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Exception transferring file", e);
            }
        }
    }
}
