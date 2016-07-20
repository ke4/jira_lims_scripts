package uk.ac.sanger.scgcf.jira.lims.service_wrappers

/**
 * Created by as28 on 23/06/16.
 */

/**
 * This class handles interactions with the Material and Container service API library
 */

import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.services.models.*

@Slf4j(value="LOG")
class MatContServicesWrapper {

    /**
     * Get Material UUIDs
     */
    static Material[] getMaterialsByIds(materialUuids) {
        LOG.debug("materialUuids: ", materialUuids)
        try {
            Material.getMaterials(materialUuids)
        } catch (Exception ex) {
            //TODO: what does proper exception handling need to do to report to user and block transition for PF?
            LOG.error "Exception caught"
            LOG.error ex
            LOG.error ex.printStackTrace()
            null
        }
    }

    /**
     * Create a new Material
     */
    static Material createMaterial(String matName, String matTypeName) {
        // TODO: add optional metadata
        LOG.debug("matName: ", matName)
        LOG.debug("matTypeName: ", matTypeName)
        try {
            // TODO: what are parameters for create material?
//            MaterialActions.createMaterial() as Material
        } catch (Exception ex) {
            //TODO: what does proper exception handling need to do to report to user and block transition for PF?
            LOG.error "Exception caught"
            LOG.error ex
            LOG.error ex.printStackTrace()
            null
        }
    }

    /**
     * Create a new Labware
     */
    static Labware createLabware(Map barcodeMap, String labwareTypeName, String externalId) {
        LOG.debug("barcodeMap: ", barcodeMap)
        LOG.debug("labwareTypeName: ", labwareTypeName)
        LOG.debug("externalId: ", externalId)
        try {
            // create the new Labware and return Labware object
            Labware.create(barcodeMap, new LabwareType(name: labwareTypeName), externalId) as Labware
        } catch (Exception ex) {
            //TODO: what does proper exception handling need to do to report to user and block transition for PF?
            LOG.error "Exception caught"
            LOG.error ex
            LOG.error ex.printStackTrace()
            null
        }
    }
}
