package fmp.androidutils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

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

        File file = PrepareFileForShare(fileName);

        if (file != null) {
            Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
        File file = PrepareFileForShare(imagePath);
        if (file != null) {
            try {
                Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                imageAdded = true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        if (imageAdded) {
            shareIntent.setType("image/*");
        } else {
            shareIntent.setType("text/plain");
        }

        activity.startActivity(Intent.createChooser(shareIntent, "Share via..."));
    }

    private File PrepareFileForShare(String filePath) {

        if (filePath == null || filePath.isEmpty())
            return null;

        File oldFile = new File(filePath);

        if (!oldFile.exists())
            return null;

        File storageDir = new File(activity.getFilesDir(), "shared_images");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File newFile = new File(storageDir, "image.png");

        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(oldFile).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            return newFile;
        } catch (IOException ex) {
            return null;
        } finally {
            if (inputChannel != null) try {
                inputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputChannel != null) try {
                outputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
