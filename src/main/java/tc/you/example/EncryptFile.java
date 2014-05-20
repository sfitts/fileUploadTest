package tc.you.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

public class EncryptFile {

    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(args[0]);
        FileOutputStream fos = new FileOutputStream(args[1]);
        Cipher cipher = UploadUtils.getDESCipher(UploadUtils.ENCRYPTION_KEY, Cipher.ENCRYPT_MODE);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        UploadUtils.fastChannelCopy(fis, cos);
    }

}
