import com.NTT.HPHC.CTS.Email.*
import com.urbancode.air.AirPluginTool

// Initialize AirPluginTool
final AirPluginTool airTool = new AirPluginTool(this.args[0], this.args[1])
final Map<String, String> props = airTool.getStepProperties()

// Retrieve properties
final String varFromEmailList = props['fromMail']
final String varToEmailList = props['toList']
final String varCCEmailList = props['ccList']
final String varEmailSubjectr = props['subject']
final String varEmailMessage = props['emailMessage']
final String varEmailQueueServer = props['emailQueueServer']
final String varEmailQueueName = props['emailQueueName']
final String varEmailQueueUser = props['emailQueueUser']
final String varEmailQueuePwd = props['emailQueuePwd']

// Get the plugin home directory
final String scriptDir = System.getenv('PLUGIN_HOME')

// Read the email template file
String emailFileContents = new File(scriptDir, 'EmailTemplate.xml').text

// Replace placeholders with actual values
emailFileContents = emailFileContents.replace("FromEmailList", varFromEmailList)
                                     .replace("ToReplyTo", varFromEmailList)
                                     .replace("ToEmailList", varToEmailList)
                                     .replace("CCEmailList", varCCEmailList)
                                     .replace("EmailSubjectToSend", varEmailSubjectr)
                                     .replace("EmailMessage", varEmailMessage)

// Write the updated content to a new file
final String emailMessageFilePath = new File(scriptDir, 'EmailMessage.xml').path
new File(emailMessageFilePath).text = emailFileContents

// Send the email message
MessageIntoQueue MIQ = new MessageIntoQueue()
MIQ.sendTopicMessage(varEmailQueueServer, varEmailQueueName, varEmailQueueUser, varEmailQueuePwd, emailMessageFilePath)
