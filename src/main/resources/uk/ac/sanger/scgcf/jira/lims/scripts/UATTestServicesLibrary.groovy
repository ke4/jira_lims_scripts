package uk.ac.sanger.scgcf.jira.lims.scripts

import uk.ac.sanger.scgcf.jira.lims.actions.UATFunctions
//import uk.ac.sanger.scgcf.jira.services.models.Labware
//
//Labware lw = new Labware()
//lw.setId("Test")
//
//"Done"
def tubeBarcodes, tubeDetails
(tubeBarcodes, tubeDetails) = UATFunctions.createCustomerTubes()

"tubeBarcodes = ${tubeBarcodes}"