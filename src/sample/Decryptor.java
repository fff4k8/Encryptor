package sample;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Decryptor {

    public static String addChar(String str, char ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        return sb.toString();
    }

    public static void decrypt(String infile, String password) throws Throwable {
        String outfile = infile.substring(0, infile.length() - 3);
        int dot = outfile.indexOf(".");
        if (dot != -1) {
            outfile = addChar(outfile, '_', dot);
        }

        try (FileInputStream fin = new FileInputStream(infile);
             FileOutputStream fout = new FileOutputStream(outfile);
             DataInputStream din = new DataInputStream(fin)) {

            // Create a key.
            byte[] desKeyData = password.getBytes();
            DESKeySpec desKeySpec = new DESKeySpec(desKeyData);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey desKey = keyFactory.generateSecret(desKeySpec);
            // Read the initialization vector.

            int ivSize = din.readInt();
            byte[] iv = new byte[ivSize];
            din.readFully(iv);
            IvParameterSpec ivps = new IvParameterSpec(iv);
            // Use Data Encryption Standard.
            Cipher des = Cipher.getInstance("DES/CBC/PKCS5Padding");
            des.init(Cipher.DECRYPT_MODE, desKey, ivps);
            byte[] input = new byte[64];
            while (true) {
                int bytesRead = fin.read(input);
                if (bytesRead == -1) break;
                byte[] output = des.update(input, 0, bytesRead);
                if (output != null) fout.write(output);
            }

            try {
                byte[] output = des.doFinal();
                if (output != null) fout.write(output);
                fin.close();
                fout.flush();
                fout.close();
            } catch (Throwable t) {
                fin.close();
                fout.flush();
                fout.close();
                new File(outfile).delete();
                throw new Exception(t);
            }
        }
    }
}
