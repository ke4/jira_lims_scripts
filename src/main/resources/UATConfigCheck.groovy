import uk.ac.sanger.scgcf.jira.lims.configurations.ConfigReader

def name = ConfigReader.getCFName("UAT_CUST_TUBE_BARCODES")
log.error "ConfigReader name = ${name}"
name
