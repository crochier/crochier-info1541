package com.crochier.crochiercustomersupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Ticket
{
    private String customerName;
    private String ticketSubject;
    private String ticketBody;
    private Map<String, Attachment> attachments = new LinkedHashMap<>();

    public Ticket(String customerName, String ticketSubject, String ticketBody, Map<String, Attachment> attachments)
    {
        this.customerName = customerName;
        this.ticketSubject = ticketSubject;
        this.ticketBody = ticketBody;
        this.attachments = attachments;
    }

    public Ticket()
    {
        super();
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTicketSubject() {
        return ticketSubject;
    }

    public void setTicketSubject(String ticketSubject) {
        this.ticketSubject = ticketSubject;
    }

    public String getTicketBody() {
        return ticketBody;
    }

    public void setTicketBody(String ticketBody) {
        this.ticketBody = ticketBody;
    }

    public Map<String, Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Attachment> attachments) {
        this.attachments = attachments;
    }

    public int getNumberOfAttachments()
    {
        return this.attachments.size();
    }

    public void addAttachment(String fileName, Attachment attachment)
    {
        this.attachments.put(fileName, attachment);
    }

    public Attachment getOneAttachment(String attachmentID)
    {
        return this.attachments.get(attachmentID);
    }


    public ArrayList<Attachment> getAllAttachments()
    {
        ArrayList<Attachment> allAttachments = new ArrayList<>();
        for (Map.Entry<String, Attachment> attachment : attachments.entrySet())
        {
            System.out.println("got to this point"); allAttachments.add(attachment.getValue());
        }
        return allAttachments;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "customerName='" + customerName + '\'' +
                ", ticketSubject='" + ticketSubject + '\'' +
                ", ticketBody='" + ticketBody + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
