package com.urbancode.air.plugin.automation

public class QCRunTest extends AutomationBase {

    String folder
    String testSetName
    boolean skipOutput
    String skipInt
    boolean runLocally
    String runLocallyInt
    boolean recursiveSearch
    String recursiveSearchInt
    String remoteHost

    public void execute() {
        init()

        if (skipOutput) {
            skipInt = "1"
        }
        else {
            skipInt = "0"
        }

        if(runLocally) {
            runLocallyInt = "1"
        }
        else {
            runLocallyInt = "0"
        }

        if (recursiveSearch) {
            recursiveSearchInt = "1"
        }
        else {
            recursiveSearchInt= "0"
        }

        def command = generateCommand()
        runCommand("Running Test Set", command)
    }

    private def generateCommand() {
        String allInFolder // int value to determine if all test-sets will be run in provided folder

        def command = [cscriptExe]
        command << PLUGIN_HOME + "\\qc_run_test_set.vbs"
        command << serverUrl
        command << username
        command << password
        command << domain
        command << project
        command << skipInt
        command << runLocallyInt
        command << folder

        if (testSetName) {
            allInFolder = 0
        }
        else {
            allInFolder = 1
        }

        command << allInFolder
        command << recursiveSearchInt
        command << testSetName // blank values will be ignored by script

        if (remoteHost) {
            command << remoteHost
        }

        return command
    }
}