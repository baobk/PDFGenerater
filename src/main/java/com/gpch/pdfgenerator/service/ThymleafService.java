package com.gpch.pdfgenerator.service;


import com.itextpdf.kernel.pdf.*;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ThymleafService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThymleafService.class);

    private static final String TEMPLATE_NAME = "students";

    public static byte[] USERPASS = "superuser".getBytes();
    public static byte[] OWNERPASS = "superowner".getBytes();

    @Resource
    @Qualifier("stringTemplateEngine")
    private TemplateEngine stringTemplateEngine;

    @Resource
    @Qualifier("htmlTemplateEngine")
    private TemplateEngine htmlTemplateEngine;

    private static String htmlToXhtml(String html) {
        Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }

    private static File xhtmlToPdf(String xhtml, String outFileName) throws Exception {
        File file = File.createTempFile("students", ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer iTextRenderer = new ITextRenderer();
        FontResolver resolver = iTextRenderer.getFontResolver();
        //iTextRenderer.getFontResolver().addFont("MyFont.ttf", true);
        iTextRenderer.setDocumentFromString(xhtml, new ClassPathResource("/templates/").getURL().toExternalForm());
        iTextRenderer.layout();
        iTextRenderer.createPDF(outputStream);
        outputStream.close();


        createFileWithPass();
        return file;
    }

    public static void createFileWithPass(){

         final String path = "//Users//nvbao//Desktop//PDF//students.pdf";
         final String RESULT_PDF = "//Users//nvbao//Desktop//PDF//PP.pdf";

         try {
            PdfReader reader = new PdfReader(path);
            WriterProperties props = new WriterProperties()
                    .setStandardEncryption(USERPASS, OWNERPASS, EncryptionConstants.ALLOW_PRINTING,
                            EncryptionConstants.ENCRYPTION_AES_128 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA);
            PdfWriter writer = new PdfWriter(new FileOutputStream(RESULT_PDF), props);
            PdfDocument pdfDoc = new PdfDocument(reader, writer);
            pdfDoc.close();

        } catch (Exception e) {
            LOGGER.error("failed to create pdf from html exception: ", e);
            e.printStackTrace();
        }
    }


    public File generatePdf() throws Exception {
        final Context context = new Context();
        final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, context);
        String xhtml = htmlToXhtml(htmlContent);
        return xhtmlToPdf(xhtml, null);
    }

}
