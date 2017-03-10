package uk.ac.sanger.scgcf.jira.lims.configurations

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.lims.utils.EnvVariableAccess

import java.nio.file.Paths

/**
 * The {@code ConfigReader} class handles all interaction with the script configuration file.
 *
 * Created by as28 on 23/06/16.
 */

@Slf4j(value="LOG")
class ConfigReader {
    // store configurations in map
    static def configMap = null;

    /**
     * Fetch and parse jira_lims_config.json from configs
     */
    static void parseConfigFile() {
        LOG.debug "Creating config from json file"

        // set configMap from json
        JsonSlurper slurper = new JsonSlurper()
        Paths.get(EnvVariableAccess.jiraLimsConfigFilePath).withReader { Reader reader ->
            configMap = slurper.parse(reader)
        }

        if ( configMap == null ) {
            LOG.error "Failed to read the configuration file at filepath: ${EnvVariableAccess.jiraLimsConfigFilePath}"
        } else {
            LOG.debug (configMap.getClass().toString())
        }
    }

    /**
     * Gets the SequenceScape section of the configuration
     * @return the section of the config map containing the sequencescape details
     */
    static def getServiceDetails(JiraLimsServices service) {
        LOG.debug "In ConfigReader getSequencescapeDetails"
        if(configMap == null) {
            parseConfigFile()
        }

        configMap['services'][service.getServiceKey()]
    }

    /**
     * Gets a configuration element from the config file using the given key list.
     * @param keys the list of the configuration keys
     * @return the required configuration element or throws {@code NoSuchElementException}
     * if there is no element belongs to the given keys
     */
    static def getConfigElement(List<String> keys) {
        LOG.debug "In ConfigReader getConfigElement"
        if(configMap == null) {
            parseConfigFile()
        }

        def element = null
        def tmpConfigMap = configMap
        for (def key: keys) {
            element = tmpConfigMap[key]
            if (!element) break;
            tmpConfigMap = element
        }

        if (!element) {
            throw new NoSuchElementException("No element found with the given keys: ${keys.toString()}".toString())
        }

        element
    }

    /**
     * Gets the name of the custom field from the configuration file searched by the given alias name.
     * @param cfAlias the alias name of the custom field
     * @return the name of the queried custom field
     */
    static String getCustomFieldName(String cfAlias) {
        LOG.debug("In ConfigReader getCustomFieldName with alias <${cfAlias}>".toString())
        if(configMap == null) {
            parseConfigFile()
        }

        String cfName = null
        def element = configMap['custom_fields'][cfAlias]
        if(element) {
            cfName = element['cfname']
        }

        if (!cfName) {
            LOG.error("ConfigReader getCustomFieldName: could not find cfname for alias <${cfAlias}>".toString())
            throw new NoSuchElementException("No configmap element found for getCustomFieldName with alias: <${cfAlias}>".toString())
        }
        LOG.debug("CF name = ${cfName}".toString())
        cfName
    }

    /**
     * Gets the custom field id for the alias name
     * @param cfAlias the alias name of the custom field
     * @return the id number of the custom field
     */
    static long getCFId(String cfAlias) {
        LOG.debug("In ConfigReader getCFId with alias <${cfAlias}>".toString())
        if(configMap == null) {
            parseConfigFile()
        }

        long cfId = -1
        def element = configMap['custom_fields'][cfAlias]
        if(element) {
            cfId = element['cfid'] as long
        }

        if (cfId <= 0) {
            LOG.error("ConfigReader getCustomFieldName: could not find cfid for alias <${cfAlias}>".toString())
            throw new NoSuchElementException("No configmap element found for getCFId with alias: <${cfAlias}>".toString())
        }
        LOG.debug("CF id = ${cfId}".toString())
        cfId
    }

    /**
     * Gets the custom field id string for the alias name
     * @param cfAlias
     * @return the id string for the custom field e.g. customfield_12345
     */
    static String getCFIdString(String cfAlias) {
        LOG.debug "In ConfigReader getCFIdString with alias ${cfAlias}"
        if(configMap == null) {
            parseConfigFile()
        }

        String cfIdString = null
        def element = configMap['custom_fields'][cfAlias]
        if(element) {
            cfIdString = element['cfidstring'] as String
        }

        if (!cfIdString) {
            LOG.error("ConfigReader getCustomFieldName: could not find cfidstring for alias <${cfAlias}>".toString())
            throw new NoSuchElementException("No configmap element found for getCFIdString with alias: <${cfAlias}>".toString())
        }
        LOG.debug("CF idString = ${cfIdString}".toString())
        cfIdString
    }

    /**
     * Get the transition id for the specified workflow and transition alias
     *
     * @param workflowName
     * @param transitionAlias
     * @return
     */
    static int getTransitionActionId(String workflowName, String transitionAlias) {
        int actionId = getConfigElement(["transitions", workflowName, transitionAlias, "tactionid"]) as int
        actionId
    }
}
