package tc.you.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
  
    private ServletFileUpload uploadHelper;
    private Path tempDir;
    
    @Override
    public void init() throws ServletException {
      try {
          tempDir = UploadUtils.getTempDir();
          DiskFileItemFactory factory = new DiskFileItemFactory();
          factory.setRepository(tempDir.toFile());
          uploadHelper = new ServletFileUpload(new DiskFileItemFactory());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            // Parse the request
            FileItemIterator iter = uploadHelper.getItemIterator(req);
            while (iter.hasNext()) {
                // We only expect the file
                FileItemStream item = iter.next();
                if (item.isFormField()) {
                    continue;
                }

                String name = item.getFieldName();
                InputStream inStrm = item.openStream();
                OutputStream outStrm = new FileOutputStream(tempDir.resolve(name).toString());
                UploadUtils.fastChannelCopy(inStrm, outStrm);
                inStrm.close();
                outStrm.close();
            }

            // Return success
            resp.setContentType("application/json");
            PrintWriter pw = resp.getWriter();
            pw.write("{\"success\": 1}");
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        }
    }
}
