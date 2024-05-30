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

class HPQCCreateDefect extends ALMRestHelper {
    def assignee
    def summary
    def detectedBy
    def detectedOn
    def reproducible
    def subject
    def severity
    def priority
    def status
    def additionalProps

    public HPQCCreateDefect(def props) {
        super(props)
        assignee = props['assignee'].trim()
        summary = props['summary'].trim()
        detectedBy = props['detectedby'].trim()
        detectedOn = props['detectedOn'].trim()
        reproducible = props['reproducible'].trim()
        subject = props['subject'].trim()
        severity = props['severity'].trim()
        priority = props['priority'].trim()
        status = props['status'].trim()

        if (props['props']) {
            additionalProps = props['props'].split('\n')
        }
    }

    //create defect. Will throw an ALMStatusCodeException if any required properties are missing
    public void createDefect() {
        def properties = [:]

        if (assignee) {
            properties << ["owner" : assignee]
        }
        if (summary) {
            properties << ["name" : summary]
        }
        if (detectedBy) {
            properties << ["detected-by" : detectedBy]
        }
        if (detectedOn) {
            properties << ["creation-time" : detectedOn]
        }
        if (reproducible) {
            properties << ["reproducible" : reproducible]
        }
        if (subject) {
            properties << ["subject" : subject]
        }
        if (severity) {
            properties << ["severity" : severity]
        }
        if (priority) {
            properties << ["priority" : priority]
        }
        if (status) {
            properties << ["status" : status]
        }
        if (additionalProps) {
            def defectFields = getFieldsMap("defect") //map labels to names

            for (String pattern : additionalProps) {

                if (pattern.contains("=")) {
                    def propPair = pattern.split("=")
                    String name = propPair[0].trim()
                    String value = propPair[1]

                    if (defectFields.containsKey(name.toLowerCase())) {
                        name = defectFields.get(name.toLowerCase())
                    }

                    properties.put(name, value)
                }
            }
            println(properties)
        }

        //may throw ALMStatusCodeException
        createEntity("defects", "defect", properties)
    }
}