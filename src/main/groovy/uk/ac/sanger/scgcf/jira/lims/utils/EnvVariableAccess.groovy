package uk.ac.sanger.scgcf.jira.lims.utils

/**
 * Created by as28 on 20/07/16.
 */
class EnvVariableAccess {

    static String jiraLimsConfigFilePath
    static String materialServiceUrl
    static String containerServiceUrl
    static String labwarePath
    static String materialBatchPath

    /**
     * Fetch the value of an environment variable
     * @param envVarName
     * @return
     */
    static getEnvVariableValue(envVarName) {
        def envVariableValue = System.getenv(envVarName)
        if (envVariableValue == null) {
            throw new IllegalStateException("The ${envVarName} environmental variable has not been set.")
        }
        envVariableValue
    }

    /**
     * Fetch the value of the configuration filepath
     * @return The jiraLimsConfigFilePath set in the environment variables
     */
    static String getJiraLimsConfigFilePath() {
        getEnvVariableValue('jiraLimsConfigFilePath')
    }

    /**
     * Fetch the value of the material service URL
     * @return The materialServiceUrl set in the environment variables
     */
    static String getMaterialServiceUrl() {
        getEnvVariableValue('materialServiceUrl')
    }

    /**
     * Fetch the value of the container service URL
     * @return The containerServiceUrl set in the environment variables
     */
    static String getContainerServiceUrl() {
        getEnvVariableValue('containerServiceUrl')
    }

    /**
     * Fetch the value of the labware URL path
     * @return The labwarePath set in the environment variables
     */
    static String getLabwarePath() {
        getEnvVariableValue('labwarePath')
    }

    /**
     * Fetch the value of the material URL path
     * @return The materialBatchPath set in the environment variables
     */
    static String getMaterialBatchPath() {
        getEnvVariableValue('materialBatchPath')
    }


}
