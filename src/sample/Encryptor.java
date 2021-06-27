package sample;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Encryptor {

    public static String addChar(String str, char ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        return sb.toString();
    }

    public static void encrypt(String filename, String password) {

        StringBuilder sb = new StringBuilder(filename);
        int dot = sb.indexOf(".");
        if (dot != -1) {
            sb.insert(dot, '_');
        }

    try(   FileInputStream fin = new FileInputStream(filename); // args[0]
           FileOutputStream fout = new FileOutputStream(sb + ".z1");
           DataOutputStream dout = new DataOutputStream(fout);) //args[0]) {
    {
        // Create a key in raw unsafe bytes
        byte[] desKeyData = password.getBytes();

        DESKeySpec desKeySpec = new DESKeySpec(desKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = keyFactory.generateSecret(desKeySpec);
        // Use Data Encryption Standard.
        Cipher des = Cipher.getInstance("DES/CBC/PKCS5Padding");
        des.init(Cipher.ENCRYPT_MODE, desKey);
        // Write the initialization vector onto the output.
        byte[] iv = des.getIV();

        dout.writeInt(iv.length);
        dout.write(iv);
        byte[] input = new byte[64];
        while (true) {
            int bytesRead = fin.read(input);
            if (bytesRead == -1) break;
            byte[] output = des.update(input, 0, bytesRead);
            if (output != null) dout.write(output);
        }
        byte[] output = des.doFinal();
        if (output != null) dout.write(output);
        fin.close();
        dout.flush();
        dout.close();
    } catch (Throwable ex) {
        System.err.println("file path " + filename);
        System.err.println(ex);
    }
}
}







