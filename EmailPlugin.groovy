import com.NTT.HPHC.CTS.Email.MessageIntoQueue;
import com.urbancode.air.AirPluginTool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmailSender {
    public static void main(String[] args) {
        try {
            // Initialize AirPluginTool
            AirPluginTool airTool = new AirPluginTool(args[0], args[1]);
            var props = airTool.getStepProperties();

            // Retrieve properties
            String varFromEmailList = props.get("fromMail");
            String varToEmailList = props.get("toList");
            String varCCEmailList = props.get("ccList");
            String varEmailSubjectr = props.get("subject");
            String varEmailMessage = props.get("emailMessage");
            String varEmailQueueServer = props.get("emailQueueServer");
            String varEmailQueueName = props.get("emailQueueName");
            String varEmailQueueUser = props.get("emailQueueUser");
            String varEmailQueuePwd = props.get("emailQueuePwd");

            // Get the plugin home directory
            String scriptDir = System.getenv("PLUGIN_HOME");

            // Read the email template file
            String emailFileContents = new String(Files.readAllBytes(Paths.get(scriptDir, "EmailTemplate.xml")));

            // Replace placeholders with actual values
            emailFileContents = emailFileContents.replace("FromEmailList", varFromEmailList)
                                                 .replace("ToReplyTo", varFromEmailList)
                                                 .replace("ToEmailList", varToEmailList)
                                                 .replace("CCEmailList", varCCEmailList)
                                                 .replace("EmailSubjectToSend", varEmailSubjectr)
                                                 .replace("EmailMessage", varEmailMessage);

            // Write the updated content to a new file
            String emailMessageFilePath = Paths.get(scriptDir, "EmailMessage.xml").toString();
            Files.write(Paths.get(emailMessageFilePath), emailFileContents.getBytes());

            // Send the email message
            MessageIntoQueue MIQ = new MessageIntoQueue();
            MIQ.sendTopicMessage(varEmailQueueServer, varEmailQueueName, varEmailQueueUser, varEmailQueuePwd, emailMessageFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
