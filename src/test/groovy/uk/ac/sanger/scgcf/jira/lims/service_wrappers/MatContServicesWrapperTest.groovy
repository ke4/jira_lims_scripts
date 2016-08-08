package uk.ac.sanger.scgcf.jira.lims.service_wrappers

import spock.lang.Specification
import uk.ac.sanger.scgcf.jira.lims.utils.EnvVariableAccess
import uk.ac.sanger.scgcf.jira.services.models.Labware
import uk.ac.sanger.scgcf.jira.services.models.Material

//import uk.ac.sanger.scgcf.jira.services.models.LabwareType
//import uk.ac.sanger.scgcf.jira.services.models.Material
//import uk.ac.sanger.scgcf.jira.services.models.MaterialType
//import uk.ac.sanger.scgcf.jira.services.models.Metadatum

class MatContServicesWrapperTest extends Specification {

    def 'test environment variables are present and set'() {

        when: "check exception thrown if environment variable not present"
        EnvVariableAccess.getEnvVariableValue("doesnotexist")

        then:
        thrown IllegalStateException

        when: "check for presence of material service url environment variable"
        String sMaterialServiceUrl = EnvVariableAccess.getMaterialServiceUrl()

        then:
        notThrown Exception
        sMaterialServiceUrl.getClass() == String

        when: "check for presence of container service url environment variable"
        String sContainerServiceUrl = EnvVariableAccess.getContainerServiceUrl()

        then:
        notThrown Exception
        sContainerServiceUrl.getClass() == String

        when: "check for presence of labware path environment variable"
        String sLabwarePath = EnvVariableAccess.getLabwarePath()

        then:
        notThrown Exception
        sLabwarePath.getClass() == String

        when: "check for presence of material path environment variable"
        String sMaterialBatchPath = EnvVariableAccess.getMaterialBatchPath()

        then:
        notThrown Exception
        sMaterialBatchPath.getClass() == String

//        when: "check for presence of lims config filepath environment variable"
//        String sJiraLimsConfigFilePath = EnvVariableAccess.getJiraLimsConfigFilePath()
//
//        then:
//        notThrown Exception
//        sJiraLimsConfigFilePath.getClass() == String
    }

    def 'test whether can create a local labware object'() {

        setup: 'test can create labware'

        Labware lw = new Labware()
        lw.setId("Test")

        expect: 'check labware id set'
        assert lw.getId() == "Test"
    }

    def 'test whether can create a local material object'() {

        setup: 'test can create material'

        Material m = new Material()
        m.setId("Test")

        expect: 'check material id set'
        assert m.getId() == "Test"
    }

//    def 'test can access materials REST service'() {
//        // TODO: this should be a select so as not to alter DB
//        setup:
//        Material testMat = Material.create(
//                "test_sample",
//                "sample",
//                [
//                        new Metadatum(key: "test_key", value: "test_value")
//                ]
//        )
//
//        expect:
//        assert testMat.name == "test_sample"
//        assert testMat.materialType.name == "sample"
//        assert testMat.metadata[0].key == "test_key"
//        assert testMat.metadata[0].value == "test_value"
//        setup:
//        MaterialType matType = MaterialType.find({it.name == 'sample'})
//
//        expect:
//        matType.name == "sample"
//    }

//    def 'test can access containers REST service'() {
//        // TODO: this should be a select so as not to alter DB
//        setup:
//        def extId = MockFunctions.createMockExternalId()
//
//        def testLw = Labware.create(
//                new LabwareType(name: 'generic tube'),
//                extId,
//                [
//                        new Metadatum(key: 'test_name', value: "test_value")
//                ],
//                barcodePrefix: 'UATS', barcodeInfo: 'TST'
//        )
//
//        expect:
//        assert testLw.labwareType.name == "generic tube"
//        assert testLw.externalId == extId
//        assert testLw.metadata[0].key == "test_name"
//        assert testLw.metadata[0].value == "test_value"
//        setup:
//        LabwareType lwType = LabwareType.find({it.name == 'generic tube'})
//
//        expect:
//        assert lwType.name == "generic tube"
//    }
}
