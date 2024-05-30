package com.urbancode.air.plugin.automation;

import com.urbancode.air.*

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AutomationBase {
    //**************************************************************************
    // CLASS
    //**************************************************************************

    //**************************************************************************
    // INSTANCE
    //**************************************************************************
    String serverUrl
    String username
    String password
    String domain
    String project

    def workDir
    CommandHelper cmdHelper
    def cscriptExe = "cscript.exe"
    final def PLUGIN_HOME = System.getenv('PLUGIN_HOME')
    final def JACOB_HOME = PLUGIN_HOME + File.separator + "lib" + File.separator + "dll"
    final int SUCCESS_CODE = 1337

    protected void init() {
        cmdHelper = new CommandHelper(workDir)
        cmdHelper.ignoreExitValue = true
        cmdHelper.addEnvironmentVariable("JAVA_OPTS", "-Djava.library.path=$JACOB_HOME")

        if (new File("C:\\Windows\\SysWOW64").exists()) {
            cscriptExe = "C:\\Windows\\SysWOW64\\cscript.exe"
        }
    }

    protected void runCommand(message, command) {
        int exitCode = cmdHelper.runCommand(message, command)
        if (exitCode != SUCCESS_CODE) {
            throw new ExitCodeException("Invalid exit code: " + exitCode + ". Expected exit code " + SUCCESS_CODE)
        }
    }
}