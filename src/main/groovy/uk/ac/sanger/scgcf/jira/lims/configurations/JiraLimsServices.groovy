package uk.ac.sanger.scgcf.jira.lims.configurations

/**
 * Enumerated list of internal services that JIRA LIMS is communicating with.
 *
 * Created by ke4 on 10/03/2017.
 */
enum JiraLimsServices {
    SEQUENCESCAPE("sequencescapeDetails"),
    BARCODE_GENERATOR("barcodeGeneratorDetails");

    private String serviceKeyName;

    private Map<String, String> serviceNameToConfigKey;

    JiraLimsServices(String keyName) {
        this.serviceKeyName = keyName
    }

    public String getServiceKey() {
        serviceKeyName
    }

    @Override
    String toString() {
        serviceKeyName
    }
}
