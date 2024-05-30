import com.NTT.HPHC.CTS.Email.*;
import com.urbancode.air.AirPluginTool
final def airTool = new AirPluginTool(this.args[0], this.args[1])
final def props = airTool.getStepProperties()
final def varFromEmailList = props['fromMail']
final def varToEmailList = props['toList']
final def varCCEmailList = props['ccList']
final def varEmailSubjectr = props['subject']
final def varEmailMessage = props['emailMessage']
final def varEmailQueueServer = props['emailQueueServer']
final def varEmailQueueName = props['emailQueueName']
final def varEmailQueueUser = props['emailQueueUser']
final def varEmailQueuePwd = props['emailQueuePwd']


final def scriptDir = System.getenv('PLUGIN_HOME')


String emailFileContents = new File( scriptDir + '\\EmailTemplate.xml').text

emailFileContents = emailFileContents.replace("FromEmailList",varFromEmailList)
emailFileContents = emailFileContents.replace("ToReplyTo",varFromEmailList)
emailFileContents = emailFileContents.replace("ToEmailList",varToEmailList)
emailFileContents = emailFileContents.replace("CCEmailList",varCCEmailList)
emailFileContents = emailFileContents.replace("EmailSubjectToSend",varEmailSubjectr)
emailFileContents = emailFileContents.replace("EmailMessage",varEmailMessage)

def EmailMessageFile = scriptDir + "/EmailMessage.xml"

File file = new File(EmailMessageFile).write(emailFileContents)

MessageIntoQueue MIQ = new MessageIntoQueue(); 
MIQ.sendTopicMessage(varEmailQueueServer,varEmailQueueName,varEmailQueueUser, varEmailQueuePwd,EmailMessageFile);
	
