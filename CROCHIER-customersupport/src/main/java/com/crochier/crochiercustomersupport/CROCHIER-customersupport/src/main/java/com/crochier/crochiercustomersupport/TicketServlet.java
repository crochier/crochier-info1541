// Assignment 4 customer service INFO1541 Java 3
// Christian Rochier
// 7/3/2023
// Resources- Java for web applications by nicholas williams and course materials

package com.crochier.crochiercustomersupport;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.AnnotatedArrayType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.Part;

import javax.lang.model.type.PrimitiveType;

@WebServlet(name = "ticketservlet", value = "/tickets", loadOnStartup = 1)
@MultipartConfig(fileSizeThreshold = 5_242_880, maxFileSize = 20_971_200L, maxRequestSize = 41_943_040L)
public class TicketServlet extends HttpServlet
{
    private volatile int ticketID = 1;
    private final Map<Integer, Ticket> allTickets = new LinkedHashMap<>();
    
    @Override 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        String action = request.getParameter("action");
        if (action == null)
        {
            action = "list";
        }
        switch (action)
        {
           case "list" -> listTickets(response);
           case "view" -> viewTicket(request, response);
           case "create" -> showTicketForm(response);
           case "download" -> downloadAttachment(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        response.setContentType("text/html");
        String action = request.getParameter("action");
        if (action == null)
        {
            action = "list";
        }
        if (action.equals("create"))
        {
            createTicket(request, response);
        }
        else
        {
            response.sendRedirect("tickets");
        }
    }

    private void createTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // called from the ticket form to create a ticket and add to the hash map
        Ticket ticket = new Ticket();
        ticket.setCustomerName(request.getParameter("customername"));
        ticket.setTicketSubject(request.getParameter("subject"));
        ticket.setTicketBody(request.getParameter("ticketBody"));
        Part file = request.getPart("attachment");
        String fileName = file.getSubmittedFileName();
        if (file != null && file.getSize()>0)
        {

            Attachment attachment = this.processAttachment(file);
            if (attachment != null)
            {
                ticket.addAttachment(fileName, attachment);
            }
            int id;
            synchronized (this)
            {
                id = this.ticketID;
                this.ticketID++;
                allTickets.put(id, ticket);
            }
            response.sendRedirect("tickets?action=view&ticketID=" + id);
        }
    }

    private Attachment processAttachment(Part file) throws IOException {
        // processes the attachment using Part and Input Stream
        InputStream in = file.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read;
        final byte[] bytesArray = new byte[1024];
        while ((read = in.read(bytesArray)) != -1)
        {
            out.write(bytesArray, 0, read);
        }
        Attachment attachment = new Attachment();
        attachment.setName(file.getSubmittedFileName());
        attachment.setContents(out.toByteArray());
        return attachment;
    }

    private void downloadAttachment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // uses getTicket()
        String ticketIDString = request.getParameter("ticketID");
        Ticket ticket = getTicket(ticketIDString, response);
        if (ticket == null)
        {
            return;
        }
        String attachmentIDString = request.getParameter("attachment");
        if (attachmentIDString == null)
        {
            response.sendRedirect("tickets?action=view&ticketID=" + ticketIDString);
            return;
        }
        Attachment attachment = ticket.getOneAttachment(attachmentIDString);
        if (attachment == null)
        {
            response.sendRedirect("tickets?action=view&ticketID=" + ticketIDString);
            return;
        }
        response.setHeader("Content-Disposition", "attachment; filename = " + attachment.getName());
        response.setContentType("application/octet-stream");
        ServletOutputStream stream = response.getOutputStream();
        stream.write(attachment.getContents());
    }

    private void showTicketForm(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(" <!DOCTYPE html>\n" +
                "<html>\n" +
                "    <body>\n" +
                "        <h2>\n" +
                "            Submit a new ticket\n" +
                "        </h2>\n" +
                "        <Form method=\"POST\" action=\"tickets\" enctype=\"multipart/form-data\"> \n" +
                "            <input type=\"hidden\" name=\"action\" value=\"create\">\n" +
                "            Customer Name: <br><br>\n" +
                "            <input type=\"text\" name=\"customername\"> <br><br>\n" +
                "            Subject: <br><br>\n" +
                "            <input type=\"text\" name=\"subject\"> <br><br>\n" +
                "            Problem: <br>\n" +
                "            <textarea name=\"ticketBody\" cols=\"50\" rows=\"10\"></textarea> <br>\n" +
                "            <b>Attachments:</b> <br>\n" +
                "            <input type=\"file\" name=\"attachment\"> <br><br>\n" +
                "            <input type=\"submit\" value=\"Submit\">\n" +
                "        </Form>\n" +
                "    </body>\n" +
                "</html>");
    }

    private void viewTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // view 1 ticket based on ID set
        String IDstring = request.getParameter("ticketID");
        Ticket ticket = getTicket(IDstring, response);
        if (ticket == null)
        {
            return;
        }
        String customer = ticket.getCustomerName();
        String subject = ticket.getTicketSubject();
        String body = ticket.getTicketBody();
        PrintWriter out = response.getWriter();
        out.println("<h2>Ticket Number" + IDstring + "</h2>" + "<i> Subject: " + subject + "<br>" +
                "Customer Name: " + customer + "<br>" + "Body: " + body + "<br>");
        out.println("Number Of Attachments: " + ticket.getNumberOfAttachments() + "<br></i>");
        if (ticket.getNumberOfAttachments() > 0)
        {
            out.println("<i> Attachments: </i>");
            for (Attachment attachment : ticket.getAllAttachments())
            {
                out.println("<a href = \"tickets?action=download&ticketID=" +
                        IDstring + "&attachment=" + attachment.getName() + "\">" + attachment.getName() + "</a>");
                out.println("<br>");
            }
            out.println("<br><br>");
        }
        out.println("<a href = \"tickets\">return to ticket list</a></body></html>");
    }

    private Ticket getTicket(String IDstring, HttpServletResponse response) throws IOException {
        // find in the database otherwise return null
        if (IDstring == null || IDstring.length() == 0)
        {
            response.sendRedirect("tickets");
            return null;
        }
        try
        {
            int id = Integer.parseInt(IDstring);
            Ticket ticket = allTickets.get(id);
            if (ticket == null)
            {
                response.sendRedirect("tickets");
                return null;
            }
            return ticket;
        }
        catch (Exception e)
        {
            response.sendRedirect("tickets");
            return null;
        }
    }

    private void listTickets(HttpServletResponse response) throws IOException {
        // list all the tickets
        PrintWriter out = response.getWriter();
        out.println("<html> <body> <h2> Tickets </h2>");
        out.println("<a href = \"tickets?action=create\">Create Ticket</a><br><br>");
        if (allTickets.size() == 0)
        {
            out.println("There are no tickets yet...");
        }
        else
        {
            for (int ticketID : allTickets.keySet())
            {
                String idString = Integer.toString(ticketID);
                Ticket ticket = allTickets.get(ticketID);
                out.println("Ticket Number " + idString);
                out.println(" : <a href = \"tickets?action=view&ticketID=" + ticketID + "\">");
                out.println(ticket.getTicketSubject());
                out.println("</a><br>Customer: " + ticket.getCustomerName() + "<br><br>");
                out.println("</body></html>");
            }
        }
        out.println("</body></html>");
    }
}
