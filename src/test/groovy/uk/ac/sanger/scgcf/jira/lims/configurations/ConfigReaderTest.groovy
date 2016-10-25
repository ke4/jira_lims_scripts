package uk.ac.sanger.scgcf.jira.lims.configurations

import spock.lang.Specification

class ConfigReaderTest extends Specification {

    def 'test can read config file'() {

        expect: "test can read custom field aliases"
        assert ConfigReader.getCustomFieldName("UAT_CUST_TUBE_BARCODES") == "UAT cust tube barcodes"
        assert ConfigReader.getCFId("UAT_CUST_TUBE_BARCODES") == 10900
        assert ConfigReader.getCFIdString("UAT_CUST_TUBE_BARCODES") == "customfield_10900"

        assert ConfigReader.getCustomFieldName("UAT_CUST_TUBE_DETAILS") == "UAT cust tube details"
        assert ConfigReader.getCFId("UAT_CUST_TUBE_DETAILS") == 10901
        assert ConfigReader.getCFIdString("UAT_CUST_TUBE_DETAILS") == "customfield_10901"

        assert ConfigReader.getConfigElement(
                ["validation", "mandatoryFields", "SeqPL: Studies", "Study", "Create"]) ==
                [ "Cost Code", "Prelim ID", "Team Number", "SeqS Project Name", "SeqS Study Name",
                  "Sequencing Mode", "Species", "HMDMC ref. number"]
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
