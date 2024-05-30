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
import com.urbancode.air.plugin.hp.alm.rest.ALMRestException

class HPQCCheckStatus extends ALMRestHelper{
    private def defectIds
    private def expectedState
    private def failMode

    public HPQCCheckStatus(def props) {
        super(props)
        defectIds = props['issueIds'].split(',')
        expectedState = props['expectedState']
        failMode = props['failmode']
    }

    public void checkStatus() {
        def failures = []

        for (def defectId : defectIds) {
            defectId = defectId.trim()
            String status = getFieldValue("defects", defectId.trim(), "status")

            // this should never happen unless HP changes the names of their defect fields
            if (status == null || status.length() < 1) {
                println("The field name 'status' doesn't exist on the defect with id: ${defectId}.")
                throw new ALMRestException("Status field does not exist.")
            }

            if (status == expectedState) {
                println("Defect with id: ${defectId} is in the expected state: ${expectedState}.")
            }
            else {
                println("Defect with id: ${defectId} is in the state: ${status}, which is not the expected state: ${expectedState}.")

                if (failMode == "fast") {
                    println("Process will now exit due to 'Fail-fast' mode.")
                    throw new ALMRestException("Defect not in the expected state.")
                }
                else if (failMode == "slow") {
                    println("Process will continue to execute, but fail after attempting to update all defects due to 'Fail' mode.")
                    failures << defectId
                }
                else {
                    println("Process will now continue with execution due to 'Warn' mode.")
                }
            }

        }

        // slow fail mode returning failure after attempting all status checks
        if (failures) {
            throw new ALMRestException("Defect Ids: ${failures} are not in the expected state ${expectedState}")
        }
    }
}
