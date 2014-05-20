package tc.you.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DecryptionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String FILE_NAME_PARAM = "fileName";
    private static final String KEY_PARAM = "key";
  
    private Path tempDir;
    
    @Override
    public void init() throws ServletException {
      try {
          tempDir = UploadUtils.getTempDir();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String fileName = req.getParameter(FILE_NAME_PARAM);
        if (fileName == null) {
            throw new ServletException("Must provide the 'fileName' parameter");
        }
        String key = req.getParameter(KEY_PARAM);
        if (key == null) {
            throw new ServletException("Must provide the 'key' parameter");
        }
        
        try {
            Path filePath = tempDir.resolve(fileName);
            FileInputStream fis = new FileInputStream(filePath.toString());
            FileOutputStream fos = new FileOutputStream(filePath.toString() + "-clear");

            Cipher cipher = UploadUtils.getDESCipher(key, Cipher.DECRYPT_MODE);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            UploadUtils.fastChannelCopy(cis, fos);
            fis.close();
            fos.close();
            
            // Return success
            resp.setContentType("application/json");
            PrintWriter pw = resp.getWriter();
            pw.write("{\"success\": 1}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setContentType("application/json");
            PrintWriter pw = resp.getWriter();
            pw.write(String.format("{\"success\": 0, \"error_message\": \"%s\"}", 
                    e.getLocalizedMessage()));
        }
    }

}
