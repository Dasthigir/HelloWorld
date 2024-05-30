/*
* Licensed Materials - Property of IBM Corp.
* IBM UrbanCode Build
* IBM UrbanCode Deploy
* IBM UrbanCode Release
* IBM AnthillPro
* (c) Copyright IBM Corporation 2002, 2014. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/
package com.urbancode.air

import java.util.Properties

public class AirPluginTool {

    //**************************************************************************
    // CLASS
    //**************************************************************************

    //**************************************************************************
    // INSTANCE
    //**************************************************************************
    
    final boolean isWindows = System.getProperty('os.name') =~ /(?i)windows/

    def out = System.out
    def err = System.err

    private File inPropsFile
    private File outPropsFile

    Properties outProps

    AirPluginTool(File inFile, File outFile) {
        inPropsFile = inFile
        outPropsFile = outFile
        outProps = new Properties()
    }

    Properties getStepProperties() {
        Properties props = new Properties()
        try (FileInputStream inputPropsStream = new FileInputStream(inPropsFile)) {
            props.load(inputPropsStream)
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
        return props
    }

    void setOutputProperty(String name, String value) {
        outProps.setProperty(name, value)
    }

    void setOutputProperties() {
        try (FileOutputStream outputPropsStream = new FileOutputStream(outPropsFile)) {
            outProps.store(outputPropsStream, "")
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
    }

    String getAuthToken() {
        String authToken = System.getenv("AUTH_TOKEN")
        return "{\"token\" : \"${authToken}\"}"
    }

    String getAuthTokenUsername() {
        return "PasswordIsAuthToken"
    }

    void storeOutputProperties() {
        setOutputProperties()
    }
}
