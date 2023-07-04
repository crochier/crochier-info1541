import com.crochier.crochiercustomersupport.Attachment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAttachment
{
    Attachment attachment = new Attachment();
    @Test
    void testGetName()
    {
        String name = attachment.getName();
        assertEquals(null, name);
    }

    @Test
    void testGetContents()
    {
        byte[] contents = attachment.getContents();
        assertEquals(null, contents);
    }

    @Test
    void testSetName()
    {
        attachment.setName("test");
        String name = attachment.getName();
        assertEquals("test", name);
    }
}
