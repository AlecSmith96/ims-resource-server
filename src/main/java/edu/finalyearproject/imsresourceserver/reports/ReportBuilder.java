package edu.finalyearproject.imsresourceserver.reports;

import com.lowagie.text.DocumentException;
import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Builder class to set context and generate HTML reports based on data and HTML template passed to it.
 * This uses a Builder design pattern to prevent context from consecutive report generations affecting one another.
 */
@Component
public class ReportBuilder
{
    @Autowired
    private TemplateEngine templateEngine;
    private Context context;

    /**
     * Builder method for creating a new Context for a report and setting the Locale.
     * All new reports to be generated must call this to re-initialise the context and remove any data from previous
     * reports.
     * @return ReportBuilder - the current instance of the builder.
     */
    public ReportBuilder withContext()
    {
        context = new Context();
        context.setLocale(Locale.ENGLISH);

        return this;
    }

    /**
     * Builder method for adding a List of Order objects to the context.
     * @param listName - the variable name of the list.
     * @param orders - the list of Order objects.
     * @return ReportBuilder - the current instance of the builder.
     */
    public ReportBuilder withOrdersList(String listName, List<Order> orders)
    {
        context.setVariable(listName, orders);

        return this;
    }

    /**
     * Builder method for adding a List of Product objects to the context.
     * @param listName - the variable name of the list.
     * @param products - the list of Product objects.
     * @return ReportBuilder - the current instance of the builder.
     */
    public ReportBuilder withProductList(String listName, Set<Product> products)
    {
        context.setVariable(listName, products);

        return this;
    }

    /**
     * Builder method for adding a String variable to the context.
     * @param stringName - the name of the variable.
     * @param item - the String to add to the context.
     * @return ReportBuilder - the current instance of the builder.
     */
    public ReportBuilder withString(String stringName, String item)
    {
        context.setVariable(stringName, item);
        return this;
    }

    /**
     * Builder return method for returning the HTML String of the generated report.
     * @param templateName - the name of the HTML template to pass the context to.
     * @return String - the generated HTML String of the report.
     */
    public String buildReport(String templateName)
    {
        return templateEngine.process(templateName, context);
    }

    public void buildHtmlFile(String templateName, String fileName) throws IOException
    {
        String html = templateEngine.process(templateName, context);

        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(System.getProperty("user.home") + File.separator + fileName));
        fileWriter.write(html);
        fileWriter.close();
    }

    public static void generatePdfFromHtml(String html, String fileName) throws DocumentException, IOException
    {
        String outputFolder = System.getProperty("user.home") + File.separator + fileName;
        OutputStream outputStream = new FileOutputStream(outputFolder);

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
    }
}
