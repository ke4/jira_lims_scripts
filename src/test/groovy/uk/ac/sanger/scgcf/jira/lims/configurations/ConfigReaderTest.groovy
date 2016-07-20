package uk.ac.sanger.scgcf.jira.lims.configurations

import spock.lang.Specification

class ConfigReaderTest extends Specification {

    def 'test can read config file'() {

        expect: "test can read custom field aliases"
        assert ConfigReader.getCFName("UAT_CUST_TUBE_BARCODES") == "UAT cust tube barcodes"
        assert ConfigReader.getCFId("UAT_CUST_TUBE_BARCODES") == 10900
        assert ConfigReader.getCFIdString("UAT_CUST_TUBE_BARCODES") == "customfield_10900"

        assert ConfigReader.getCFName("UAT_CUST_TUBE_DETAILS") == "UAT cust tube details"
        assert ConfigReader.getCFId("UAT_CUST_TUBE_DETAILS") == 10901
        assert ConfigReader.getCFIdString("UAT_CUST_TUBE_DETAILS") == "customfield_10901"
    }
}
