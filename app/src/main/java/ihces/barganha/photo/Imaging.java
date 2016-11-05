package ihces.barganha.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Imaging {

    // TODO test to find the best maximum
    private static final int MAX_IMAGE_SIZE = 600; // 70;
    private static final String IMAGE_FILENAME_PREFIX = "ad_photo_";

    public static Bitmap correctSizeForUploading(Bitmap source) {
        return correctSize(source, MAX_IMAGE_SIZE);
    }

    public static Bitmap correctSizeForUploading(File source) {
        return correctSize(source, MAX_IMAGE_SIZE);
    }

    /**
     * Downscale images as needed, so as to avoid memory and http request size errors.
     * Based on a sample provided by Fedor Vlasov.
     * @param source The image to be downscaled
     * @return The decoded image.
     */
    public static Bitmap correctSize(File source, int maxDimension) {
        try {
            BitmapFactory.Options dimensions = getImageDimensions(source);
            int scale = findScale(dimensions, maxDimension);
            BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
            scaleOptions.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(source), null, scaleOptions);
        } catch (FileNotFoundException e) {}
        return null;
    }

    public static Bitmap correctSize(Bitmap source, int maxDimension) {
        BitmapFactory.Options dimensions = getImageDimensions(source);
        int scale = findScale(dimensions, maxDimension);
        return Bitmap.createScaledBitmap(source, dimensions.outWidth / scale, dimensions.outHeight/ scale, false);
    }

    private static int findScale(BitmapFactory.Options dimensions, int maxImageSize) {
        // Find the correct scale value. It should be the power of 2.
        int scale = 1;
        while(dimensions.outWidth / scale / 2 >= maxImageSize &&
                dimensions.outHeight / scale / 2 >= maxImageSize) {
            scale *= 2;
        }
        return scale;
    }

    private static BitmapFactory.Options getImageDimensions(File source) throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(source), null, options);
        return options;
    }

    private static BitmapFactory.Options getImageDimensions(Bitmap bitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outHeight = bitmap.getHeight();
        options.outWidth = bitmap.getWidth();
        return options;
    }

    public static String base64EncodeImage(Bitmap file) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        file.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] bytes = stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Bitmap base64DecodeImage(String photoBase64) {
        byte[] bytes = Base64.decode(photoBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String writeImageFile(Context context, int adId, String photoBase64) {
        ByteArrayOutputStream inStream = new ByteArrayOutputStream();
        Bitmap image = base64DecodeImage(photoBase64);
        image.compress(Bitmap.CompressFormat.JPEG, 80, inStream);

        FileOutputStream outStream;
        String filename = IMAGE_FILENAME_PREFIX + adId + ".jpg";

        try {
            outStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outStream.write(inStream.toByteArray());
            outStream.close();
        }
        catch (Exception e) { e.printStackTrace(); }

        return filename;
    }

    public static Bitmap readImageFile(Context context, String filename) {
        try {
            FileInputStream stream = context.openFileInput(filename);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            stream.close();
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        catch (Exception e) { e.printStackTrace(); }

        return null;
    }
}
