package fmp.anroidutilsparent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import fmp.androidutils.AndroidUtilsPlugin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidUtilsPlugin.getInstance().setActivity(this);
    }

    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private String SaveBitmap(Bitmap bitmap) {
        String mPath = getFilesDir().toString() + "/screenshot_1.jpg";

        File imageFile = new File(mPath);

        try {
            FileOutputStream outputStream = null;
            outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            return mPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void SendEmailClick(View v) {
        AndroidUtilsPlugin.getInstance().SendEmail("a@a.com", null, null, "a");
    }

    public void ShareImageClick(View v) {
        Bitmap bitmap = screenShot(this.getWindow().getDecorView().getRootView());
        String mPath = SaveBitmap(bitmap);
        AndroidUtilsPlugin.getInstance().ShareMedia("http://google.com my body", "my subject", mPath);
    }

    public void ShareTextClick(View v) {
        AndroidUtilsPlugin.getInstance().ShareMedia("http://google.com my body", "my subject", null);
    }

//    private String moveFile(String filePath) {
//
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//
//        File oldFile = new File(filePath);
//        File newFile = new File(storageDir, oldFile.getName());
//
//        FileChannel outputChannel = null;
//        FileChannel inputChannel = null;
//        try {
//            outputChannel = new FileOutputStream(newFile).getChannel();
//            inputChannel = new FileInputStream(oldFile).getChannel();
//            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
//            inputChannel.close();
//            return newFile.getPath();
//        }
//        catch (IOException ex) {
//            Log.v("v", "failed to move file: " + ex.getStackTrace().toString());
//            return oldFile.getPath();
//        }
//        finally {
//            if (inputChannel != null) try {
//                inputChannel.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (outputChannel != null) try {
//                outputChannel.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
