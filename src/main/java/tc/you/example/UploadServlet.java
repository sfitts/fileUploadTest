package tc.you.example;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

public class UploadServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  
  private Path tempDir;
  
  @Override
  public void init() throws ServletException {
    try {
      tempDir = Files.createTempDirectory("tempFiles");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      @SuppressWarnings("unchecked")
      List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
      for (FileItem item : items) {
          if (item.isFormField()) {
              // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
              String fieldname = item.getFieldName();
              String fieldvalue = item.getString();
              // ... (do your job here)
          } else {
              // Process form file field (input type="file").
              String fieldname = item.getFieldName();
              Path outputPath = tempDir.resolve(fieldname);
              File outFile = outputPath.toFile();
              try {
                item.write(outFile);
              } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              resp.setContentType("application/json");
              PrintWriter pw = resp.getWriter();
              pw.write("{\"success\": 1}");
          }
      }
  } catch (FileUploadException e) {
      throw new ServletException("Cannot parse multipart request.", e);
  }
  }
  
  

}
