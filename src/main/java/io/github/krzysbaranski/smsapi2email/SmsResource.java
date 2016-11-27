package io.github.krzysbaranski.smsapi2email;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.mail.Transport.send;

/**
 * Root resource (exposed at "sms.do" path)
 */
@Path("sms.do")
public class SmsResource {

    private final MessageGenerator messageGenerator = new MessageGenerator(this);

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getIt(@QueryParam("username") String username,
                          @QueryParam("password") String password,
                          @QueryParam("from") String from,
                          @QueryParam("to") String to,
                          @QueryParam("message") String message,
                          @QueryParam("format") String format
    ) {
        /**
         * system properties: mail.smtp.host
         */
        Session session = Session.getDefaultInstance(System.getProperties());
        Message mailMessage;
        try {
            mailMessage = messageGenerator.mailMessage(session, username, from, to, message);
        } catch (MessagingException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            send(mailMessage);
        } catch (MessagingException e) {
            Response.serverError().build();
        }
        return Response.ok("OK:1234:1:" + to, MediaType.TEXT_PLAIN_TYPE).build();
    }
}
