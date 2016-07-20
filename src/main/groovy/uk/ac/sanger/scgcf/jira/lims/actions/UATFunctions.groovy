package uk.ac.sanger.scgcf.jira.lims.actions

import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.lims.utils.MockFunctions
import uk.ac.sanger.scgcf.jira.services.models.Labware
import uk.ac.sanger.scgcf.jira.services.models.LabwareTypes
import uk.ac.sanger.scgcf.jira.services.models.Material
import uk.ac.sanger.scgcf.jira.services.models.Metadatum

/**
 * Contains functions for UAT testing
 *
 * Created by as28 on 01/07/16.
 */
@Slf4j(value="LOG")
class UATFunctions {

    /**
     * Create the customer cell tubes and the related materials, then
     * write details out to ticket custom fields
     */
    static def createCustomerTubes() {
        LOG.debug "In createCustomerTubes"

        // create 4 tubes including barcodes and 1 metadata each
        def tubeLabwares = (0..3).collect { indx ->
            LOG.debug "Create labware index ${indx}"
            Labware.create(
                    LabwareTypes.GENERIC_TUBE,
                    MockFunctions.createMockExternalId(),
                    [
                            new Metadatum(key: 'tube_name', value: "my_tube_${(indx + 1).toString()}")
                    ],
                    barcodePrefix: 'UATS', barcodeInfo: 'TST'
            )
        }

        // create 4 materials of type Sample with several metadata each
        def tubeMaterials = (0..3).collect { indx ->
            Material.create(
                    "sample_${(indx + 1).toString()}" as String,
                    "sample",
                    [
                        new Metadatum(key: 'cust_name', value: "John Smith"),
                        new Metadatum(key: 'cust_sample_name', value: "cust_sample_${(indx + 1).toString()}"),
                        new Metadatum(key: 'organism', value: "human"),
                        new Metadatum(key: 'cell_type', value: "epithelial")
                    ]
            )
        }

        // update the tubes to add the materials (receptacle zero)
        tubeLabwares.eachWithIndex() { curTube, tubeIndex ->
            curTube.receptacles[0].setMaterialUuid(tubeMaterials[tubeIndex].getId())
            curTube.update()
        }

        // fetch the barcodes into a comma-separated string
        def tubeBarcodeList = []
        tubeLabwares.each { tubeBarcodeList.push(it.getBarcode()) }
        def tubeBarcodesString = tubeBarcodeList.join(",")
        LOG.debug("tubeBarcodesString = ${tubeBarcodesString}")

        // extract other details from tubes and materials
        StringBuilder sb = new StringBuilder();
        sb.append("Tube details: \n")
        tubeLabwares.eachWithIndex{ Labware curTube, int indx ->
            sb.with {
                append("Tube number: ${(indx + 1).toString()} \n")
                append("Labware UUID: ${curTube.getId().toString()} \n")
                append("Ext Id: ${curTube.getExternalId()} \n")
                append("BC: ${curTube.getBarcode()} \n")
                append("Material UUID: ${curTube.getReceptacles()[0].materialUuid} \n")
                append("Created at: ${curTube.getCreated_at()} \n\n")
            }
        }
        String tubeDetailsString = sb.toString()
        LOG.debug("tubeDetails = ${tubeDetailsString}")

        [tubeBarcodesString, tubeDetailsString]
    }

    static def splitCustTubes(String tubeBarcodes) {
        LOG.debug "In splitCustTubes"

//        // TODO: change to use custom field alias
//        // get the source tube labware barcodes from custom field "UAT cust tube barcodes"
//        JiraAPIWrapper.getCustomFieldValueByName(curIssue, ConfigReader.getCFName("UAT_CUST_TUBE_BARCODES"))

        // split this into a list on comma and check it has 4 entries

        // get each of the 4 source labwares
        // def sourceLabwares = (0..3).collect { indx ->
        //      Labware.findByBarcode(labwareBarcodesList[indx])
        // }

        // create 4 destination plate labwares
//        def destLabwares = (0..3).collect { indx ->
//            LOG.debug "Create labware index ${indx}"
//            Labware.create(
//                    new LabwareType(name: 'generic 96 well plate'),
//                    createMockExternalId(),
//                    [
//                            new Metadatum(key: 'split_plate_name', value: "my_split_plate_${(indx + 1).toString()}")
//                    ],
//                    barcodePrefix: 'UATS', barcodeInfo: 'TST'
//            )
//        }

        // for tubes 1 & 2 split into columns 1-6 and 7-12 on destination plate 1, copying metadata 'cust_name'
        // and creating 1 new metadata 'cell_initial_name' = <plate barcode>_<well_location>

        // for tube 3 split into destination plate 2 all wells, copying metadata 'cust_name'
        // and creating 1 new metadata 'cell_initial_name' = <plate barcode>_<well_location>

        // for tube 4 split into destination plates 3 and 4 all wells, copying metadata 'cust_name'
        // and creating 1 new metadata 'cell_initial_name' = <plate barcode>_<well_location>

        // split looks like this:
        //destinationLabware = TransferActions.split(sourceLabware, destinationLabware,
        //        materialType, destinationLocations, mapMetadataKeysToCopy, newMetadata)
        //
        //where newMetadata:
        //def newMetadata = [
        //        'A1': [
        //                new Metadatum(key: 'new_key11', value: "new_value11"),
        //                new Metadatum(key: 'new_key21', value: "new_value21")
        //        ],
        //        'A3': [
        //                new Metadatum(key: 'new_key13', value: "new_value13"),
        //                new Metadatum(key: 'new_key23', value: "new_value23")
        //        ]
        //]

        // write 4 x destination plate barcodes into custom field "UAT split plt barcodes"

        // compile some details with string buffer and write into custom field "UAT split plt details"
    }

    static def combineSortedPlates() {
        LOG.debug "In combineSortedPlates"

        // select the barcodes for the 4 source plates from custom field "UAT split plt barcodes"

        //



//        def sourceLabwares = (0..3).collect { Labware.create(LabwareTypes.GENERIC_96_PLATE, ((int) (Math.random() * 1000000000)).toString(), barcodePrefix: 'TEST') }
//        def destinationLabware = Labware.create(LabwareTypes.GENERIC_384_PLATE, ((int) (Math.random() * 1000000000)).toString(), barcodePrefix: 'TEST')
//
//        (0..3).each { labwareNo ->
//            (0..95).each { receptacleNo ->
//                sourceLabwares[labwareNo].receptacles[receptacleNo].materialUuid = sourceMaterials[(labwareNo * 96) + receptacleNo].id
//            }
//        }
//        sourceLabwares.each { it.update() }
//
//        println 'Source ids'
//        sourceLabwares.each { println it.materialUuids() }
//
//        TransferActions.combine(sourceLabwares, destinationLabware, new MaterialType(name: 'sample'))
//
//        println 'Destination ids'
//        println destinationLabware.materialUuids()
//
//        def firstQuadrantIds = ['A1', 'A2', 'B1', 'B2'].collect { location ->
//            destinationLabware.receptacles.find { r -> r.location.name == location }.materialUuid
//        }
//
//        println 'First quadrant ids'
//        println firstQuadrantIds
//
//        def firstQuadrant = Material.getMaterials(firstQuadrantIds)
//        println 'First quadrant'
//        firstQuadrant.each { println "${it.parents[0].id} -> ${it.id}" }

    }

    static def cherryPickPlates() {
        LOG.debug "In cherryPickPlates"

//        def sourceMaterials = Material.postMaterials((1..(96 * 2)).collect { new Material(name: "test_material_$it", materialType: new MaterialType(name: 'sample')) })
//        def sourceLabware1 = Labware.create(LabwareTypes.GENERIC_96_PLATE, ((int) (Math.random() * 1000000000)).toString(), barcodePrefix: 'TEST')
//        def sourceLabware2 = Labware.create(LabwareTypes.GENERIC_96_PLATE, ((int) (Math.random() * 1000000000)).toString(), barcodePrefix: 'TEST')
//        def destinationLabware = Labware.create(LabwareTypes.GENERIC_96_PLATE, ((int) (Math.random() * 1000000000)).toString(), barcodePrefix: 'TEST')
//
//        (0..95).each { receptacleNo ->
//            sourceLabware1.receptacles[receptacleNo].materialUuid = sourceMaterials[receptacleNo].id
//        }
//        sourceLabware1.update()
//        (96..191).each { receptacleNo ->
//            sourceLabware2.receptacles[receptacleNo - 96].materialUuid = sourceMaterials[receptacleNo].id
//        }
//        sourceLabware2.update()
//
//        println 'Source ids'
//        println sourceLabware1.materialUuids()
//        println sourceLabware2.materialUuids()
//
//        TransferActions.cherrypick([sourceLabware1, sourceLabware2], [destinationLabware], new MaterialType(name: 'sample'), [
//                new TransferActions.Mapping(sourceBarcode: sourceLabware1.barcode, destinationBarcode: destinationLabware.barcode, sourceLocation: 'A1', destinationLocation: 'A1'),
//                new TransferActions.Mapping(sourceBarcode: sourceLabware1.barcode, destinationBarcode: destinationLabware.barcode, sourceLocation: 'A2', destinationLocation: 'A2'),
//                new TransferActions.Mapping(sourceBarcode: sourceLabware2.barcode, destinationBarcode: destinationLabware.barcode, sourceLocation: 'A1', destinationLocation: 'A3'),
//                new TransferActions.Mapping(sourceBarcode: sourceLabware2.barcode, destinationBarcode: destinationLabware.barcode, sourceLocation: 'A2', destinationLocation: 'A4')
//        ])
//
//        def newMaterials = Material.getMaterials(destinationLabware.materialUuids())
//
//        println 'Destination materials id'
//        println destinationLabware.receptacles.collect { it.materialUuid }
//
//        println 'Destination parents'
//        println newMaterials.collect { it.parents[0].id }

    }

    static def stampPlate() {
        LOG.debug "In stampPlate"

    }

    static def selectiveStampPlate() {
        LOG.debug "In selectiveStampPlate"

//        def sourceMaterials = Material.postMaterials((0..95).collect { new Material(name: "test_material_$it", materialType: new MaterialType(name: 'sample')) })
//        def sourceLabware = Labware.create(LabwareTypes.GENERIC_96_PLATE, ((int) (Math.random() * 1000000000)).toString(), barcodePrefix: 'TEST')
//        def destinationLabware = Labware.create(LabwareTypes.GENERIC_96_PLATE, ((int) (Math.random() * 1000000000)).toString(), barcodePrefix: 'TEST')
//
//        (0..95).each { receptacleNo ->
//            sourceLabware.receptacles[receptacleNo].materialUuid = sourceMaterials[receptacleNo].id
//        }
//        sourceLabware.update()
//
//        println 'Source ids'
//        println sourceLabware.materialUuids()
//
//        TransferActions.selectiveStamp(sourceLabware, destinationLabware, new MaterialType(name: 'sample'), ['A1', 'A3', 'C1', 'C3'])
//
//        def newMaterials = Material.getMaterials(destinationLabware.materialUuids())
//
//        println 'Destination materials id'
//        println destinationLabware.receptacles.collect { it.materialUuid }
//
//        println 'Destination parents'
//        println newMaterials.collect { it.parents[0].id }
    }

    static def poolToTubes() {
        LOG.debug "In poolToTubes"

//        def sourceMaterials = Material.postMaterials((0..95).collect { new Material(name: "test_material_$it", materialType: new MaterialType(name: 'sample')) })
//        def sourceLabware = Labware.create(LabwareTypes.GENERIC_96_PLATE, ((int) (Math.random() * 1000000000)).toString(), barcodePrefix: 'TEST')
//        def destinationLabware = Labware.create(LabwareTypes.GENERIC_TUBE, ((int) (Math.random() * 1000000000)).toString(), barcodePrefix: 'TEST')
//
//        (0..95).each { receptacleNo ->
//            sourceLabware.receptacles[receptacleNo].materialUuid = sourceMaterials[receptacleNo].id
//        }
//        sourceLabware.update()
//
//        println 'Source ids'
//        println sourceLabware.materialUuids()
//
//        TransferActions.pool(sourceLabware, destinationLabware, new MaterialType(name: 'sample'), ['A1', 'A3', 'C1', 'C3'])
//
//        def newMaterial = Material.getMaterials([destinationLabware.receptacles[0].materialUuid])
//
//        println 'Destination material id'
//        println newMaterial.id
//
//        println 'Destination parents'
//        newMaterial.parents.each { parent -> println parent.id }
    }

    static def stampTubes() {
        LOG.debug "In stampTubes"

    }

    static def reportOnTubes() {
        LOG.debug "In reportOnTubes"

    }
}
