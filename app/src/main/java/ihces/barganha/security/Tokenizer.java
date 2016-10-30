package ihces.barganha.security;

import android.content.Context;
import android.util.Base64;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Tokenizer {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String SALT_FILENAME = "b1125c610bda9f865fced1ce3f4b616b";
    private static final String HASH_FILENAME = "7cf88f9862f13ec0483749c5c4bedb19";

    /**
     * Hash generator based on code provided by Android Teams.
     * @param original What is to be hashed.
     * @return A properly (secure) salt+hashed+base64 encoded string.
     */
    public static String generateSaltHashedKey(Context context, String original) {
        String finalHash = null;

        try {
            SecretKey secretKey = generateKey(original.toCharArray(), getSalt(context));
            finalHash = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
            writeHashFile(context, finalHash.getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return finalHash;
    }

    /**
     * Provided by Trevor Johns, of the Android Developers Team.
     * @param passphraseOrPin What to hash
     * @param salt To season up our hash sauce
     * @return Properly hashed secret key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        // TODO test for a value above 100ms, but not too large to freeze app
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);

        return secretKeyFactory.generateSecret(keySpec);
    }

    private static byte[] getSalt(Context context) {
        byte saltBytes[] = new byte[20];

        if (!findSaltFile(context, saltBytes)) {
            // Do not seed Secure Random!
            // Automatically seeded from system entropy.
            SecureRandom sRandom = new SecureRandom();
            sRandom.nextBytes(saltBytes);
            writeSaltFile(context, saltBytes);
        }
        return saltBytes;
    }

    private static boolean findSaltFile(Context context, byte[] bytes) {
        FileInputStream stream = null;
        try {
            stream = context.openFileInput(SALT_FILENAME);
            stream.read(bytes);
            stream.close();
            return true;
        }
        catch (FileNotFoundException e) {
            return false;
        }
        catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    private static void writeSaltFile(Context context, byte[] bytes) {
        FileOutputStream stream = null;
        try {
            stream = context.openFileOutput(SALT_FILENAME, Context.MODE_PRIVATE);
            stream.write(bytes);
            stream.close();
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
    }

    private static void writeHashFile(Context context, byte[] bytes) {
        FileOutputStream stream = null;
        try {
            stream = context.openFileOutput(HASH_FILENAME, Context.MODE_PRIVATE);
            stream.write(bytes);
            stream.close();
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
