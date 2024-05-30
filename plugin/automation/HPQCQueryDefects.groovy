/*
 * Licensed Materials - Property of IBM Corp.
 * IBM UrbanCode Deploy
 * (c) Copyright IBM Corporation 2015. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
 * GSA ADP Schedule Contract with IBM Corp.
 */
package com.urbancode.air.plugin.automation

import org.apache.http.HttpResponse
import com.urbancode.air.plugin.hp.alm.rest.ALMRestHelper

class HPQCQueryDefects extends ALMRestHelper {
    def matchCriteria //user specified, unparsed criteria
    def returnFields //user specified fields to return
    def defectFields //map of entity field labels and names

    public HPQCQueryDefects(def props) {
        super(props)
        matchCriteria = props['matchcriteria'].split('\n')
        returnFields = props['returnfields'].split('\n')
    }

    public void queryDefects() {
        def queries = []

        defectFields = getFieldsMap("defect")

        for (def criterion : matchCriteria) {
            String query = criterion.trim()

            if (criterion.contains('=')) {
                def separate = criterion.split('=')
                def field = separate[0].trim().toLowerCase()
                def criteria = separate[1].trim()

                //replace HP ALM labels with the actual name used for REST calls in the query statement
                if (defectFields.containsKey(field)) {
                    field = defectFields.get(field)
                }

                query = "${field}[${criteria}]"
            }

            queries << query
        }

        //replace HP ALM labels with the actual name used for REST calls in the fields statement
        def modifiedReturnFields = []
        def outputFields = [:] //printable field values specified by user criteria

        for (def field : returnFields) {
            def outputField = field.trim()
            field = outputField.toLowerCase()

            if (defectFields.containsKey(field)) {
                field = defectFields.get(field)
            }

            outputFields.put(field, outputField)
            modifiedReturnFields.add(field)
        }

        String queryStatement = queries.join(';')
        String fieldsStatement = modifiedReturnFields.join(',')

        //acquire map of entitites returned from query
        def entityMap = getQueryResponse("defects", fieldsStatement, queryStatement)
        println(entityMap)
        //output properties
        println("\nDefects:")
        for (def entity : entityMap) {
            println("  Defect Fields:")
            for (def field : entity) {
                def name = field.key
                def value = field.value

                if (outputFields && outputFields.containsKey(name)) {
                    name = outputFields.get(name)
                }

                println("    ${name} = ${value}")
            }
        }
    }
}