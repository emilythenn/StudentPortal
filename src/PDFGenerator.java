import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.FileNotFoundException;

public class PDFGenerator {

    public void generatePDF(String fileName, String content) throws FileNotFoundException {
        // Create a PdfWriter instance that writes to the file
        PdfWriter writer = new PdfWriter(fileName);

        // Create a PdfDocument using the writer
        PdfDocument pdfDocument = new PdfDocument(writer);

        // Create a Document (the main object where content will be added)
        Document document = new Document(pdfDocument);

        // Add content to the document
        document.add(new Paragraph(content));

        // Close the document
        document.close();
    }
}
