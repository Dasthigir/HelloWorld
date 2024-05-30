/*
 * Licensed Materials - Property of IBM Corp.
 * IBM UrbanCode Deploy
 * (c) Copyright IBM Corporation 2015. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
 * GSA ADP Schedule Contract with IBM Corp.
 */
package com.urbancode.air.plugin.automation

import com.urbancode.air.plugin.hp.alm.rest.ALMRestHelper
import com.urbancode.air.plugin.hp.alm.rest.ALMStatusCodeException

class HPQCUpdateDefects extends ALMRestHelper {
    def defectIds
    def assignee
    def summary
    def priority
    def severity
    def status
    def additionalProps
    def failMode
    boolean failure //used for slow failure mode

    public HPQCUpdateDefects(def props) {
        super(props)
        defectIds = props['bugpattern'].split(",")
        assignee = props['assignee']
        summary = props['summary']
        priority = props['priority']
        severity = props['severity']
        status = props['status']
        additionalProps = props['props']
        failMode = props['failmode']
    }

    public void updateDefects() {
        ALMStatusCodeException exception
        def failures = []

        //build map of defect property names and values
        def properties = [:]
        if (assignee) {
            properties << ["owner" : assignee.trim()]
        }
        if (summary) {
            properties << ["name" : summary.trim()]
        }
        if (priority) {
            properties << ["priority" : priority.trim()]
        }
        if (severity) {
            properties << ["severity" : severity.trim()]
        }
        if (status) {
            properties << ["status" : status.trim()]
        }
        if (additionalProps) {
            def defectFields = getFieldsMap("defect") //map labels to names

            for (String pattern : additionalProps.split("\n")) {

                if (pattern.contains('=')) {
                    def propPair = pattern.split('=')
                    String name = propPair[0].trim()
                    String value = propPair[1].trim()

                    if (defectFields.containsKey(name.toLowerCase())) {
                        name = defectFields.get(name.toLowerCase())
                    }

                    properties.put(name, value)
                }
            }
        }

        //build json object to pass
        String jsonString = createJson("defect", properties)

        for (def defectId : defectIds) {
            try {
                updateFieldValue("defects", defectId.trim(), jsonString)
            }
            catch(ALMStatusCodeException ex) {
                println("Defect with id: ${defectId} has failed to update.")

                if (failMode == "fast") {
                    throw ex
                }
                else if (failMode == "slow") {
                    failures << defectId
                }
                //warn mode
                else {
                    println("Execution will continue updating all other defects due to 'warn' mode.")
                }
            }
        }

        //slow failure mode exiting process if any failures occur
        if (failures) {
            println("Process will now exit due to failure to update defect id(s): ${failures}")
            throw exception
        }
    }
}
