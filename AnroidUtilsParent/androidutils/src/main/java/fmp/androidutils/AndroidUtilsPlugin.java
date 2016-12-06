package fmp.androidutils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class AndroidUtilsPlugin {

    private Activity activity;

    private static AndroidUtilsPlugin instance;

    public static AndroidUtilsPlugin getInstance() {
        if (instance == null) {
            instance = new AndroidUtilsPlugin();
        }
        return instance;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void SendEmail(String recipient, String subject, String message, String fileName) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", recipient == null ? "" : recipient, null));

        if (subject != null && !subject.isEmpty())
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (message != null && !message.isEmpty())
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        if (fileName != null && !fileName.isEmpty()) {
            File file = new File(fileName);
            if (file.exists()) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
        }

        activity.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void ShareMedia(String text, String subject, String imagePath) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        if (text != null && !text.isEmpty())
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        if (subject != null && !subject.isEmpty())
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        boolean imageAdded = false;
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                imageAdded = true;
            }
        }

        if (imageAdded) {
            shareIntent.setType("image/*");
        } else {
            shareIntent.setType("text/plain");
        }

        activity.startActivity(Intent.createChooser(shareIntent, "Share via..."));
    }
}
