package uk.ac.sanger.scgcf.jira.lims.actions

import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.lims.utils.MockFunctions
import uk.ac.sanger.scgcf.jira.services.actions.TransferActions
import uk.ac.sanger.scgcf.jira.services.models.Labware
import uk.ac.sanger.scgcf.jira.services.models.LabwareTypes
import uk.ac.sanger.scgcf.jira.services.models.Location
import uk.ac.sanger.scgcf.jira.services.models.Material
import uk.ac.sanger.scgcf.jira.services.models.MaterialType
import uk.ac.sanger.scgcf.jira.services.models.Metadatum
import uk.ac.sanger.scgcf.jira.services.models.Receptacle

/**
 * Contains functions for UAT testing
 *
 * Created by as28 on 01/07/16.
 */
@Slf4j(value="LOG")
class UATFunctions {

    /**
     * Create the customer cell tubes and the single related material in each, returning
     * a list of barcodes in a string and a details string
     * @return
     */
    static def createCustomerTubes() {
        LOG.debug "In createCustomerTubes"

        // create 4 tubes including barcodes and 1 metadata each
        ArrayList<Labware> tubeLabwares = (0..3).collect { indx ->
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
        ArrayList<Material> tubeMaterials = (0..3).collect { indx ->
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
        ArrayList<String> tubeBarcodeList = []

        // extract other details from tubes and materials
        StringBuilder sb = new StringBuilder();
        sb.append("Tube details: \n")
        tubeLabwares.eachWithIndex{ Labware curTube, int indx ->
            tubeBarcodeList.push(curTube.getBarcode())
            sb.with {
                append("Tube number: ${(indx + 1).toString()} \n")
                append("Labware UUID: ${curTube.getId().toString()} \n")
                append("Ext Id: ${curTube.getExternalId()} \n")
                append("BC: ${curTube.getBarcode()} \n")
                append("Material UUID: ${curTube.getReceptacles()[0].materialUuid} \n")
                append("Created at: ${curTube.getCreated_at()} \n\n")
            }
        }
        String tubeBarcodesString = tubeBarcodeList.join(",")
        LOG.debug("tubeBarcodesString = ${tubeBarcodesString}")

        String tubeDetailsString = sb.toString()
        LOG.debug("tubeDetails = ${tubeDetailsString}")

        // return two parameters
        [tubeBarcodesString, tubeDetailsString]
    }

    /**
     * Take a list of tube barcodes, select the tubes and their materials, and split them
     * across 4 destination 96-well plates
     * @param tubeBarcodesList
     * @return
     */
    static def splitCustTubes(ArrayList<String> tubeBarcodesList) {
        LOG.debug "In splitCustTubes"
        LOG.debug "tubeBarcodesList = ${tubeBarcodesList.toString()}"

        // fetch the 4 source tubes
        ArrayList<Labware> sourceTubeLabwares = (0..3).collect { indx ->
            LOG.debug "findByBarcode where barcode = ${tubeBarcodesList.get(indx)}"
            Labware.findByBarcode(tubeBarcodesList.get(indx))
        }
        LOG.debug "sourceTubes size = ${sourceTubeLabwares.size()}"

        // fetch the 4 source materials
        ArrayList<Material> sourceMaterials = (0..3).collect { indx ->
            // one material per tube, fetch the material id
            String srcMaterialUUID = sourceTubeLabwares.get(indx).materialUuids().get(0)
            LOG.debug "srcMaterialUUID = ${srcMaterialUUID}"
            Material.getMaterials([ srcMaterialUUID ]).get(0)
        }
        LOG.debug "sourceMaterials size = ${sourceMaterials.size()}"

        // create 4 destination 96-well plate labwares with barcodes and a single metadata
        ArrayList<Labware> destPlateLabwares = (0..3).collect { indx ->
            LOG.debug "Create labware index ${indx}"
            Labware.create(
                    LabwareTypes.GENERIC_96_PLATE,
                    MockFunctions.createMockExternalId(),
                    [
                            new Metadatum(key: 'plate_name', value: "my_sorted_plate_${(indx + 1).toString()}")
                    ],
                    barcodePrefix: 'UATS', barcodeInfo: 'TST'
            )
        }

        MaterialType destMaterialType = new MaterialType(name: 'sample')

        // for tubes 1 & 2 split into columns 1-6 and 7-12 on destination plate 1, copying metadata 'cust_name'
        // and creating 1 new metadata 'cell_initial_name' = <plate barcode>_<well_location>
        def destPlate1ALocns = []
        def destPlate1AMetaData = [:]
        ('A'..'H').each { row ->
            (1..6).each { col ->
                String curWellLocn = "${row}${col}"
                destPlate1ALocns.push(curWellLocn)
                destPlate1AMetaData[curWellLocn] = [
                        new Metadatum(
                                key: 'cell_initial_name',
                                value: "${destPlateLabwares.get(0).getBarcode()}_${curWellLocn}"
                        )
                ]
            }
        }
        LOG.debug "destPlate1ALocns = ${destPlate1ALocns}"
        LOG.debug "destPlate1AMetaData size = ${destPlate1AMetaData.size()}"

        // split from tube 1 into plate 1
        destPlateLabwares.set(0, (Labware)TransferActions.split(sourceTubeLabwares.get(0), destPlateLabwares.get(0),
                destMaterialType, destPlate1ALocns, ['cust_sample_name'], destPlate1AMetaData))

        LOG.debug "number of materials in plate 1 at stage A = ${destPlateLabwares.get(0).materialUuids().size()}"

        def destPlate1BLocns = []
        def destPlate1BMetaData = [:]
        ('A'..'H').each { row ->
            (7..12).each { col ->
                String curWellLocn = "${row}${col}"
                destPlate1BLocns.push(curWellLocn)
                destPlate1BMetaData[curWellLocn] = [
                        new Metadatum(
                                key: 'cell_initial_name',
                                value: "${destPlateLabwares.get(0).getBarcode()}_${curWellLocn}"
                        )
                ]
            }
        }
        LOG.debug "destPlate1BLocns = ${destPlate1BLocns}"
        LOG.debug "destPlate1BMetaData = ${destPlate1BMetaData}"

        // split from tube 2 into plate 1
        destPlateLabwares.set(0, (Labware)TransferActions.split(sourceTubeLabwares.get(1), destPlateLabwares.get(0),
                destMaterialType, destPlate1BLocns, ['cust_sample_name'], destPlate1BMetaData))

        LOG.debug "number of materials in plate 1 at stage B = ${destPlateLabwares.get(0).materialUuids().size()}"

        // for tube 3 split into destination plate 2 all wells, copying metadata 'cust_name'
        // and creating 1 new metadata 'cell_initial_name' = <plate barcode>_<well_location>
        def destPlate2Locns = []
        def destPlate2MetaData = [:]
        ('A'..'H').each { row ->
            (1..12).each { col ->
                String curWellLocn = "${row}${col}"
                destPlate2Locns.push(curWellLocn)
                destPlate2MetaData[curWellLocn] = [
                    new Metadatum(
                            key: 'cell_initial_name',
                            value: "${destPlateLabwares.get(1).getBarcode()}_${curWellLocn}"
                    )
                ]
            }
        }
        LOG.debug "destPlate2Locns = ${destPlate2Locns}"
        LOG.debug "destPlate2MetaData size = ${destPlate2MetaData.size()}"

        // split from tube 3 into plate 2
        destPlateLabwares.set(1, (Labware)TransferActions.split(sourceTubeLabwares.get(2), destPlateLabwares.get(1),
                destMaterialType, destPlate2Locns, ['cust_sample_name'], destPlate2MetaData))

        LOG.debug "number of materials in plate 2 = ${destPlateLabwares.get(1).materialUuids().size()}"


        // for tube 4 split into destination plates 3 and 4 all wells, copying metadata 'cust_name'
        // and creating 1 new metadata 'cell_initial_name' = <plate barcode>_<well_location>

        // plate 3
        def destPlate3Locns = []
        def destPlate3MetaData = [:]
        ('A'..'H').each { row ->
            (1..12).each { col ->
                String curWellLocn = "${row}${col}"
                destPlate3Locns.push(curWellLocn)
                destPlate3MetaData[curWellLocn] = [
                    new Metadatum(
                            key: 'cell_initial_name',
                            value: "${destPlateLabwares.get(2).getBarcode()}_${curWellLocn}"
                    )
                ]
            }
        }
        LOG.debug "destPlate3Locns = ${destPlate3Locns}"
        LOG.debug "destPlate3MetaData size = ${destPlate3MetaData.size()}"

        // split from tube 4 into plate 3
        destPlateLabwares.set(2, (Labware)TransferActions.split(sourceTubeLabwares.get(3), destPlateLabwares.get(2),
                destMaterialType, destPlate3Locns, ['cust_sample_name'], destPlate3MetaData))

        LOG.debug "number of materials in plate 3 = ${destPlateLabwares.get(2).materialUuids().size()}"

        // plate 4
        def destPlate4Locns = []
        def destPlate4MetaData = [:]
        ('A'..'H').each { row ->
            (1..12).each { col ->
                String curWellLocn = "${row}${col}"
                destPlate4Locns.push(curWellLocn)
                destPlate4MetaData[curWellLocn] = [
                        new Metadatum(
                                key: 'cell_initial_name',
                                value: "${destPlateLabwares.get(3).getBarcode()}_${curWellLocn}"
                        )
                ]
            }
        }
        LOG.debug "destPlate4Locns = ${destPlate4Locns}"
        LOG.debug "destPlate4MetaData size = ${destPlate4MetaData.size()}"

        // split from tube 4 into plate 4
        destPlateLabwares.set(3, (Labware)TransferActions.split(sourceTubeLabwares.get(3), destPlateLabwares.get(3),
                destMaterialType, destPlate4Locns, ['cust_sample_name'], destPlate4MetaData))

        LOG.debug "number of materials in plate 4 = ${destPlateLabwares.get(3).materialUuids().size()}"

        // fetch the barcodes into a comma-separated string
        ArrayList<String> destPlatesBarcodeList = []

        // extract other details from plates and materials
        StringBuilder sb = new StringBuilder();
        sb.append("Split plate details: \n")
        destPlateLabwares.eachWithIndex{ Labware curPlate, int indx ->
            destPlatesBarcodeList.push(curPlate.getBarcode())
            sb.with {
                append("Plate number: ${(indx + 1).toString()} \n")
                append("Labware UUID: ${curPlate.getId().toString()} \n")
                append("Ext Id: ${curPlate.getExternalId()} \n")
                append("BC: ${curPlate.getBarcode()} \n")
                append("Number of Materials: ${curPlate.materialUuids().size()} \n")
                append("Created at: ${curPlate.getCreated_at()} \n")
                append("Wells: \n")
                // well material details
                (0..95).each { wellIndx ->
                    String curMatId         = curPlate.receptacles[wellIndx].materialUuid
                    String curLocnName      = curPlate.receptacles[wellIndx].location.name
                    Material curMat         = Material.getMaterials([curMatId])[0]
                    String parMatId         = curMat.parents[0].id
                    append("${curLocnName} : ${curMat.getName()} : Id = ${curMatId} : Parent = ${parMatId} \n")
                }
                append("\n")
            }
        }
        String destPlateBarcodesString = destPlatesBarcodeList.join(",")
        LOG.debug("destPlateBarcodesString = ${destPlateBarcodesString}")

        String destPlateDetailsString = sb.toString()
        LOG.debug("destPlateDetailsString = ${destPlateDetailsString}")

        [destPlateBarcodesString, destPlateDetailsString]
    }

    static def combineSortedPlates(ArrayList<String> plateBarcodesList) {
        LOG.debug "In combineSortedPlates"
        LOG.debug "plateBarcodesList = ${plateBarcodesList.toString()}"

        // fetch the 4 source plates
        ArrayList<Labware> sourcePlateLabwares = (0..3).collect { indx ->
            LOG.debug "Calling findByBarcode where barcode = ${plateBarcodesList.get(indx)}"
            Labware.findByBarcode(plateBarcodesList.get(indx))
        }
        LOG.debug "sourcePlateLabwares size = ${sourcePlateLabwares.size()}"

        // create 1 destination 384-well plate labware with barcode and a single metadata
        Labware destCmbPlate = Labware.create(
            LabwareTypes.GENERIC_384_PLATE,
            MockFunctions.createMockExternalId(),
            [
                new Metadatum(key: 'plate_name', value: "my_combined_plate")
            ],
            barcodePrefix: 'UATS', barcodeInfo: 'TST'
        )

        MaterialType destMaterialType = new MaterialType(name: 'sample')

        // create a metadata for each well in 384-well plate
        def destCmbPlateMetaData = [:]
        ('A'..'P').each { row ->
            (1..24).each { col ->
                String curWellLocn = "${row}${col}"
                destCmbPlateMetaData[curWellLocn] = [
                    new Metadatum(
                            key: 'cmb_plate_md',
                            value: "example_value_for_${curWellLocn}"
                    )
                ]
            }
        }

        destCmbPlateMetaData.each { md ->
            LOG.debug "key : ${md.getKey()} value : ${md.getValue()}"
        }

        // combine the 4 sources into the destination
        destCmbPlate = TransferActions.combine(
            sourcePlateLabwares,
            destCmbPlate,
            destMaterialType,
            ["cust_sample_name","cell_initial_name"],
            destCmbPlateMetaData
        )

        // create quadrant locations
        ArrayList<String> quadrant1Locns = []
        ArrayList<String> quadrant2Locns = []
        ArrayList<String> quadrant3Locns = []
        ArrayList<String> quadrant4Locns = []

        // quadrants 1 and 2
        ["A","C","E","G","I","K","M","O"].each { String row ->
            1.step(24, 2){ int col ->
                quadrant1Locns.push("${row}${col.toString()}")
            }
            2.step(25, 2){ int col ->
                quadrant2Locns.push("${row}${col.toString()}")
            }
        }

        // quadrants 3 and 4
        ["B","D","F","H","J","L","N","P"].each { String row ->
            1.step(24, 2){ int col ->
                quadrant3Locns.push("${row}${col.toString()}")
            }
            2.step(25, 2){ int col ->
                quadrant4Locns.push("${row}${col.toString()}")
            }
        }

        LOG.debug "quadrant1Locns = ${quadrant1Locns}"
        LOG.debug "quadrant2Locns = ${quadrant2Locns}"
        LOG.debug "quadrant3Locns = ${quadrant3Locns}"
        LOG.debug "quadrant4Locns = ${quadrant4Locns}"

        //        def firstQuadrantIds = ['A1', 'A2', 'B1', 'B2'].collect { location ->
//              // finds material id given a labware and location name
//            destinationLabware.receptacles.find { r -> r.location.name == location }.materialUuid
//        }
//
//        println 'First quadrant ids'
//        println firstQuadrantIds
//
//        def firstQuadrant = Material.getMaterials(firstQuadrantIds)
//        println 'First quadrant'
//        firstQuadrant.each { println "${it.parents[0].id} -> ${it.id}" }

        // extract other details from the combined 384 well plate
        StringBuilder sb = new StringBuilder();
        sb.append("Combined plate details: \n")

        sb.with {
            append("Labware UUID: ${destCmbPlate.getId().toString()} \n")
            append("Ext Id: ${destCmbPlate.getExternalId()} \n")
            append("BC: ${destCmbPlate.getBarcode()} \n")
            append("Created at: ${destCmbPlate.getCreated_at()} \n\n")
            append("Quadrant 1 wells:\n")
            quadrant1Locns.each { String curLocn ->
                String curMatId         = destCmbPlate.receptacles.find { r -> r.location.name == curLocn }.materialUuid
                Material curMat         = Material.getMaterials([curMatId])[0]
                String parMatId         = curMat.parents[0].getId()
                // have to then fetch the parent material as whole object is not present in current material
                Material parMat         = Material.getMaterials([parMatId])[0]
                // select value(s) from parent metadata
                List<Metadatum> parMds  = parMat.getMetadata()
                String parMdVal         = ""
                parMds.each { Metadatum curMd ->
                    if(curMd.getKey() == "cell_initial_name") {
                        parMdVal = curMd.getValue()
                    }
                }
                append("${curLocn} : ${curMat.getName()} : Id = ${curMatId} : Parent Id = ${parMatId} : Cell initial name = ${parMdVal} \n")
            }
            append("\n")
            append("Quadrant 2 wells:\n")
            quadrant2Locns.each { String curLocn ->
                String curMatId         = destCmbPlate.receptacles.find { r -> r.location.name == curLocn }.materialUuid
                Material curMat         = Material.getMaterials([curMatId])[0]
                String parMatId         = curMat.parents[0].getId()
                // have to then fetch the parent material as whole object is not present in current material
                Material parMat         = Material.getMaterials([parMatId])[0]
                // select value(s) from parent metadata
                List<Metadatum> parMds  = parMat.getMetadata()
                String parMdVal         = ""
                parMds.each { Metadatum curMd ->
                    if(curMd.getKey() == "cell_initial_name") {
                        parMdVal = curMd.getValue()
                    }
                }
                append("${curLocn} : ${curMat.getName()} : Id = ${curMatId} : Parent Id = ${parMatId} : Cell initial name = ${parMdVal} \n")
            }
            append("\n")
            append("Quadrant 3 wells:\n")
            quadrant3Locns.each { String curLocn ->
                String curMatId         = destCmbPlate.receptacles.find { r -> r.location.name == curLocn }.materialUuid
                Material curMat         = Material.getMaterials([curMatId])[0]
                String parMatId         = curMat.parents[0].getId()
                // have to then fetch the parent material as whole object is not present in current material
                Material parMat         = Material.getMaterials([parMatId])[0]
                // select value(s) from parent metadata
                List<Metadatum> parMds  = parMat.getMetadata()
                String parMdVal         = ""
                parMds.each { Metadatum curMd ->
                    if(curMd.getKey() == "cell_initial_name") {
                        parMdVal = curMd.getValue()
                    }
                }
                append("${curLocn} : ${curMat.getName()} : Id = ${curMatId} : Parent Id = ${parMatId} : Cell initial name = ${parMdVal} \n")
            }
            append("\n")
            append("Quadrant 4 wells:\n")
            quadrant4Locns.each { String curLocn ->
                String curMatId         = destCmbPlate.receptacles.find { r -> r.location.name == curLocn }.materialUuid
                Material curMat         = Material.getMaterials([curMatId])[0]
                String parMatId         = curMat.parents[0].getId()
                // have to then fetch the parent material as whole object is not present in current material
                Material parMat         = Material.getMaterials([parMatId])[0]
                // select value(s) from parent metadata
                List<Metadatum> parMds  = parMat.getMetadata()
                String parMdVal         = ""
                parMds.each { Metadatum curMd ->
                    if(curMd.getKey() == "cell_initial_name") {
                        parMdVal = curMd.getValue()
                    }
                }
                append("${curLocn} : ${curMat.getName()} : Id = ${curMatId} : Parent Id = ${parMatId} : Cell initial name = ${parMdVal} \n")
            }
            append("\n")

        }
        LOG.debug("cmbPlateBarcode = ${destCmbPlate.getBarcode()}")

        String destCmbPlateDetailsString = sb.toString()
        LOG.debug("destCmbPlateDetailsString = ${destCmbPlateDetailsString}")

        // return two parameters
        [destCmbPlate.getBarcode(), destCmbPlateDetailsString]

    }

    static def cherryPickPlates(String sourcePlateBarcode) {
        LOG.debug "In cherryPickPlates"

        LOG.debug "sourcePlateBarcode = ${sourcePlateBarcode}"

        ["chry_plate_1", "chry plate details"]

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

    static def stampPlate(String sourcePlateBarcode) {
        LOG.debug "In stampPlate"

        LOG.debug "sourcePlateBarcode = ${sourcePlateBarcode}"

        ["stamp_plate_1", "stamp plate details"]
    }

    static def selectiveStampPlate(String sourcePlateBarcode) {
        LOG.debug "In selectiveStampPlate"

        LOG.debug "sourcePlateBarcode = ${sourcePlateBarcode}"

        ["selective_stamp_plate_1", "selective stamp plate details"]

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

    static def poolToTubes(String sourcePlateBarcode) {
        LOG.debug "In poolToTubes"

        LOG.debug "sourcePlateBarcode = ${sourcePlateBarcode}"

        ["pool_tube_1, pool_tube_2, pool_tube_3, pool_tube_4", "pool tube details"]

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

    static def stampTubes(ArrayList<String> tubeBarcodesList) {
        LOG.debug "In stampTubes"

        LOG.debug "tubeBarcodesList = ${tubeBarcodesList.toString()}"

        ["norm_tube_1, norm_tube_2, norm_tube_3, norm_tube_4", "normalised pool tube details"]
    }

    static def reportOnTubes(ArrayList<String> tubeBarcodesList) {
        LOG.debug "In reportOnTubes"

        LOG.debug "tubeBarcodesList = ${tubeBarcodesList.toString()}"

    }
}
