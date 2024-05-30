/*
 * Licensed Materials - Property of IBM Corp.
 * IBM UrbanCode Deploy
 * (c) Copyright IBM Corporation 2015. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
 * GSA ADP Schedule Contract with IBM Corp.
 */
package com.urbancode.air.plugin.automation

import java.text.SimpleDateFormat
import com.urbancode.air.plugin.hp.alm.rest.ALMRestHelper
import com.urbancode.air.plugin.hp.alm.rest.ALMStatusCodeException

class HPQCAddComments extends ALMRestHelper {
    def defectIds
    def failMode
    def additionalComments
    def unparsedComments

    // html elements
    final def COMMENT_LINE = "<b>________________________________________</b>"
    final def FONT_OPEN_ELEMENT = "<font color=\"#000080\">"
    final def FONT_CLOSE_ELEMENT = "</font>"
    final def HTML_OPEN_ELEMENT = "<html>"
    final def HTML_CLOSE_ELEMENT = "</html>"
    final def BODY_OPEN_ELEMENT = "<body>"
    final def BODY_CLOSE_ELEMENT = "</body>"
    final def COMMENT_SEPERATOR = FONT_OPEN_ELEMENT + COMMENT_LINE + FONT_CLOSE_ELEMENT
    final def LESS_THAN_CHAR = "&lt;"
    final def GREATER_THAN_CHAR = "&gt;"
    final def BREAK_ELEMENT = "<br>"
    final def BOLD_OPEN_ELEMENT = "<b>"
    final def BOLD_CLOSE_ELEMENT = "</b>"

    public HPQCAddComments(def props) {
        super(props)
        defectIds = props['issueIds'].split(',')
        failMode = props['failmode']
        unparsedComments = props['additionalcomments']

        if (unparsedComments) {
            additionalComments = unparsedComments.split('\n')
        }
        else {
            println("No additional comments were specified to add.")

        }
    }

    protected void addCommentsToDefects() {
        ALMStatusCodeException exception  // rethrow last thrown exception for 'slow' fail mode
        def failures = []

        for (def defectId : defectIds) {
            defectId = defectId.trim()
            for (def comment : additionalComments) {
                String newComment
                String formattedComment = createHtmlFormattedComment(username, comment)

                String existingComment = getFieldValue("defects", defectId, "dev-comments")

                if (!existingComment) {
                    // completely new html body created for comment
                    newComment = "<html><body>${formattedComment}</body></html>"
                }
                else {
                    // insert the new comment at the end of the last comment
                    formattedComment = BREAK_ELEMENT + COMMENT_LINE + BREAK_ELEMENT + formattedComment
                    int lastCommentIndex = existingComment.lastIndexOf(FONT_CLOSE_ELEMENT)
                    StringBuilder buildComment = new StringBuilder(existingComment)
                    buildComment.insert(lastCommentIndex + FONT_CLOSE_ELEMENT.length(), formattedComment)
                    newComment = buildComment.toString()
                }

                // create json for new comment
                def properties = ["dev-comments": newComment]
                String jsonString = createJson("defect", properties)

                try {
                    // update new comment
                    updateFieldValue("defects", defectId, jsonString)
                }
                // catch only status code exceptions.. fail immediately otherwise
                catch (ALMStatusCodeException ex) {
                    println("Update of defect id: ${defectId} has failed.")

                    if (failMode == "warn") {
                        println("Continuing execution due to 'warn' mode")
                    }
                    else if (failMode == "slow") {
                        failures << defectId
                        exception = ex
                    }
                    // fail immediately by default
                    else {
                        throw ex
                    }
                }
            }
        }

        // slow failure mode exiting process if any failures occur
        if (failures) {
            println("Process will now exit due to failure to update defect id(s): ${failures}")
            throw exception
        }
        else {
            println("All defect ids successfully updated")
        }
    }

    private String createHtmlFormattedComment(String username, String comment) {
        StringBuffer result  = new StringBuffer();

        Date currentDate = new Date();

        result.append(FONT_OPEN_ELEMENT);
        result.append(BOLD_OPEN_ELEMENT);
        result.append("uDeploy");
        result.append(LESS_THAN_CHAR);
        result.append(username);
        result.append(GREATER_THAN_CHAR);
        result.append(", ");
        result.append(formatDate(currentDate));
        result.append(": ");
        result.append(comment);
        result.append(BOLD_CLOSE_ELEMENT);
        result.append(FONT_CLOSE_ELEMENT);

        return result.toString();
    }

    protected String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }
}