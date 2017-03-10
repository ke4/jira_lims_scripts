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

        when: "check for presence of lims config filepath environment variable"
        String sJiraLimsConfigFilePath = EnvVariableAccess.getJiraLimsConfigFilePath()

        then:
        notThrown Exception
        sJiraLimsConfigFilePath.getClass() == String
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

    // TODO: add test covering the facade library (Material and Container service)
}
