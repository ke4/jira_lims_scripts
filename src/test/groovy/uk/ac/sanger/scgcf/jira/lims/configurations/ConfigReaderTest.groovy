package uk.ac.sanger.scgcf.jira.lims.configurations

import spock.lang.Specification

class ConfigReaderTest extends Specification {

    def 'test can read config file'() {

        expect: "test can read custom field aliases"
        assert ConfigReader.getCustomFieldName("UAT_CUST_TUBE_BARCODES") == "UAT cust tube barcodes"

        assert ConfigReader.getCustomFieldName("UAT_CUST_TUBE_DETAILS") == "UAT cust tube details"

        assert ConfigReader.getConfigElement(
                ["validation", "mandatoryFields", "SeqPL: Studies", "Study", "Create"]) ==
                [ "COST_CODE", "SEQS_PROJECT_NAME", "SEQS_STUDY_NAME"]
    }

    def "test methods throw NoSuchElementException when alias is not found"() {
        setup:
        String testAlias = "THIS_DOES_NOT_EXIST"

        when:
        ConfigReader.getCustomFieldName(testAlias)

        then: "NoSuchElementException will be thrown"
        NoSuchElementException ex1 = thrown()
        ex1.message == "No configmap element found for getCustomFieldName with alias: <${testAlias}>".toString()

        when:
        ConfigReader.getCFId(testAlias)

        then: "NoSuchElementException will be thrown"
        NoSuchElementException ex2 = thrown()
        ex2.message == "No configmap element found for getCFId with alias: <${testAlias}>".toString()

        when:
        ConfigReader.getCFIdString(testAlias)

        then: "NoSuchElementException will be thrown"
        NoSuchElementException ex3 = thrown()
        ex3.message == "No configmap element found for getCFIdString with alias: <${testAlias}>".toString()
    }

    def "when reader can't find an element, then NoSuchElementException will be thrown"() {
        setup:
        List<String> keys = ["no","such","keys"]

        when:
        ConfigReader.getConfigElement(keys)

        then: "NoSuchElementException will be thrown"
        NoSuchElementException ex = thrown()
        ex.message == "No element found with the given keys: ${keys.toString()}".toString()
    }
}
