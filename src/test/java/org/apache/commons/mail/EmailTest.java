package org.apache.commons.mail;

import org.junit.Before;
import org.junit.Test;

import javax.mail.Message;
import javax.mail.Session;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.*;

public class EmailTest {

    private EmailConcrete email;

    @Before
    public void setUp() {
        email = new EmailConcrete();
    }
    //----------------------------------------------------------------------------------//

    @Test
    public void testGetHostNameWhenSetExplicitly() {
        EmailConcrete email = new EmailConcrete();
        email.setHostName("smtp.example.com");
        assertEquals("Expected hostname to match explicitly set value", "smtp.example.com", email.getHostName());
    }
    @Test
    public void testGetHostNameWhenNotSet() {
        EmailConcrete email = new EmailConcrete();
        assertNull("Expected getHostName() to return null when no hostname is set", email.getHostName());
    }

    //----------------------------------------------------------------------------------//
    @Test(expected = IllegalStateException.class)
    public void testBuildMimeMessageTwice() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.setSubject("Test Subject");
        email.setContent("This is a test email.", "text/plain");
        email.buildMimeMessage();
        // Attempt to build the MimeMessage again
        email.buildMimeMessage();
    }
    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutFrom() throws Exception {
        email.setHostName("smtp.example.com");
        email.addTo("recipient@example.com");
        email.setSubject("Test Subject");
        email.setContent("This is a test email.", "text/plain");
        email.buildMimeMessage();
    }
    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutRecipients() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.setSubject("Test Subject");
        email.setContent("This is a test email.", "text/plain");
        email.buildMimeMessage();
    }
    @Test
    public void testBuildMimeMessageWithReplyToAndHeaders() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.addReplyTo("replyto@example.com");
        email.addHeader("X-Custom-Header", "CustomValue");
        email.setSubject("Test Subject");
        email.setContent("This is a test email.", "text/plain");
        email.buildMimeMessage();

        assertNotNull("Reply-to should be set", email.getMimeMessage().getReplyTo());
        assertEquals("Header should match", "CustomValue", email.getMimeMessage().getHeader("X-Custom-Header")[0]);
    }
    @Test
    public void testBuildMimeMessageWithCharset() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.setCharset("UTF-8");
        email.setSubject("Subject with Charset");
        email.buildMimeMessage();

        assertNotNull("MimeMessage subject should not be null", email.getMimeMessage().getSubject());
    }
    @Test
    public void testBuildMimeMessageWithContentAndType() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.setContent("Content with ContentType", "text/html");
        email.buildMimeMessage();

        assertEquals("Content type should be text/html", "text/html", email.getMimeMessage().getDataHandler().getContentType());
    }
    @Test
    public void testBuildMimeMessageWithCcRecipients() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.addCc("cc@example.com");
        email.buildMimeMessage();

        assertTrue("Should have CC recipients", email.getMimeMessage().getRecipients(Message.RecipientType.CC).length > 0);
    }
    @Test
    public void testBuildMimeMessageWithBccRecipients() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.addBcc("bcc@example.com");
        email.buildMimeMessage();

        assertTrue("Should have BCC recipients", email.getMimeMessage().getRecipients(Message.RecipientType.BCC).length > 0);
    }
    //----------------------------------------------------------------------------------//
    @Test
    public void testAddReplyToWithEmailOnly() throws EmailException {
        email.addReplyTo("replyto@example.com");
        assertEquals("replyto@example.com", email.getReplyToAddresses().get(0).getAddress());
    }
    @Test
    public void testAddReplyToWithEmailAndName() throws EmailException {
        email.addReplyTo("replyto@example.com", "Reply To");
        assertEquals("replyto@example.com", email.getReplyToAddresses().get(0).getAddress());
        assertEquals("Reply To", email.getReplyToAddresses().get(0).getPersonal());
    }
    @Test
    public void testAddReplyToWithEmailNameAndCharset() throws EmailException {
        email.setCharset("UTF-8");
        email.addReplyTo("replyto@example.com", "67", "UTF-8");
        assertEquals("67", email.getReplyToAddresses().get(0).getPersonal());
    }
    @Test(expected = EmailException.class)
    public void testAddReplyToWithInvalidEmail() throws EmailException {
        email.addReplyTo("invalid-email");
    }
    @Test(expected = NullPointerException.class)
    public void testAddReplyToWithNullEmail() throws EmailException {
        email.addReplyTo(null);
    }
    //----------------------------------------------------------------------------------//
    @Test
    public void testAddBccWithSingleValidEmail() throws Exception {
        email.addBcc("singlebcc@example.com");
        assertEquals("Expected 1 BCC address", 1, email.getBccAddresses().size());
    }

    @Test
    public void testAddBccWithMultipleValidEmails() throws Exception {
        String[] emails  = {"bcc1@example.com","bcc2@example.com"};
        email.addBcc(emails);
        assertEquals("Expected 2 BCC addresses", 2, email.getBccAddresses().size());
    }

    @Test(expected = EmailException.class)
    public void testAddBccWithNullEmails() throws Exception {
        email.addBcc((String[]) null);
    }

    @Test(expected = EmailException.class)
    public void testAddBccWithEmptyEmails() throws Exception {
        email.addBcc(); // Empty argument list
    }
    //----------------------------------------------------------------------------------//
    @Test
    public void testAddCcWithSingleValidEmail() throws Exception {
        email.addCc("testcc@example.com");
        assertEquals(1, email.getCcAddresses().size());
    }
    @Test
    public void testAddCcWithMultipleValidEmails() throws Exception {
        String[] ccEmails = {"cc1@example.com", "cc2@example.com"};
        email.addCc(ccEmails);
        assertEquals("Expected 2 CC addresses", 2, email.getCcAddresses().size());
    }

    @Test(expected = EmailException.class)
    public void testAddCcWithNullEmails() throws Exception {
        email.addCc((String[]) null);
    }

    @Test(expected = EmailException.class)
    public void testAddCcWithEmptyEmails() throws Exception {
        email.addCc();
    }
    //----------------------------------------------------------------------------------//
    @Test
    public void testAddHeader() {
        email.addHeader("X-Test-Header", "HeaderValue");
        assertTrue(email.getHeaders().containsKey("X-Test-Header"));
        assertEquals("HeaderValue", email.getHeaders().get("X-Test-Header"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithNullName() {
        email.addHeader(null, "SomeValue");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithEmptyName() {
        email.addHeader("", "SomeValue");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithNullValue() {
        email.addHeader("X-Test-Header", null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithEmptyValue() {
        email.addHeader("X-Test-Header", "");
    }
    //----------------------------------------------------------------------------------//


    @Test
    public void testGetMailSession() throws Exception {
        email.setHostName("smtp.example.com"); // Set a dummy SMTP host name
        Session session = email.getMailSession();
        assertNotNull(session);
    }
    @Test
    public void testGetSentDate() {
        Date sentDate = new Date();
        email.setSentDate(sentDate);
        assertEquals(sentDate, email.getSentDate());
    }
    @Test
    public void testGetSocketConnectionTimeout() {
        int timeout = 30000;
        email.setSocketConnectionTimeout(timeout);
        assertEquals(timeout, email.getSocketConnectionTimeout());
    }
    @Test
    public void testSetFrom() throws Exception {
        email.setFrom("from@example.com");
        assertNotNull(email.getFromAddress());
    }

}