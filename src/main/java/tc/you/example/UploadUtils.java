package tc.you.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class UploadUtils {
    private static final String PDS_ROOT = "/tmp/pdsEnv";
    private static Path tempDir;

    public synchronized static Path getTempDir() throws IOException {
        if (tempDir != null) {
            return tempDir;
        }
        Path dataDir = Paths.get(PDS_ROOT);
        tempDir = Files.createTempDirectory(dataDir, "tempFiles");
        return tempDir;
    }
    
    public static void fastChannelCopy(InputStream inStrm, OutputStream outStrm) 
            throws IOException {
        final ReadableByteChannel src = Channels.newChannel(inStrm);
        final WritableByteChannel dest = Channels.newChannel(outStrm);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1) {
            // prepare the buffer to be drained
            buffer.flip();
            // write to the channel, may block
            dest.write(buffer);
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear()
            buffer.compact();
        }
        // EOF will leave buffer in fill state
        buffer.flip();
        // make sure the buffer is fully drained.
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
    
    public static final String ENCRYPTION_KEY = "this-is-a-key";
    
    private static final byte[] initialization_vector = { 22, 33, 11, 44, 55, 99, 66, 77 };

    public static Cipher getDESCipher(String key, int mode) 
            throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, 
            NoSuchPaddingException, InvalidAlgorithmParameterException {
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        AlgorithmParameterSpec alogrithm_specs = new IvParameterSpec(initialization_vector);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(mode, desKey, alogrithm_specs);
        return cipher;
    }
}
