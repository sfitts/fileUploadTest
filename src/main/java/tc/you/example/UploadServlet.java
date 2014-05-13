package tc.you.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
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
          Path dataDir = Paths.get("/data");
          tempDir = Files.createTempDirectory(dataDir, "tempFiles");
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
            @SuppressWarnings("unchecked")
            List<FileItem> items = uploadHelper.parseRequest(req);
            for (FileItem item : items) {
                // We only expect the file
                if (item.isFormField()) {
                    continue;
                }
              
                // Process form file field (input type="file").
                String fieldname = item.getFieldName();
                InputStream strm = item.getInputStream();
                OutputStream outStrm = new FileOutputStream(tempDir.resolve(fieldname).toString());
                fastChannelCopy(Channels.newChannel(strm), Channels.newChannel(outStrm));
            }
            // Return success
            resp.setContentType("application/json");
            PrintWriter pw = resp.getWriter();
            pw.write("{\"success\": 1}");
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        }
    }
  
  private static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
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
}
