import com.crochier.crochiercustomersupport.Attachment;
import com.crochier.crochiercustomersupport.Ticket;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTicket
{
    String name = "joe";
    String subject = "slow computer";
    String body = "it takes forever to open";
    Map<String, Attachment> attachments;
    Ticket ticket = new Ticket(name, subject, body, attachments);
    @Test
    public void testGetName()
    {
        String n = ticket.getCustomerName();
        assertEquals("joe", n);
    }
    @Test
    public void testGetSubject()
    {
        String s = ticket.getTicketSubject();
        assertEquals("slow computer", s);
    }

}
