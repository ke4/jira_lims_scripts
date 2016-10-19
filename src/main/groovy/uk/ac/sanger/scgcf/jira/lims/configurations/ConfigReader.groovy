package uk.ac.sanger.scgcf.jira.lims.configurations

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import uk.ac.sanger.scgcf.jira.lims.utils.EnvVariableAccess

import java.nio.file.Paths

/**
 * Created by as28 on 23/06/16.
 */

@Slf4j(value="LOG")
class ConfigReader {
    // store configurations in map
    static def configMap = null;

    // fetch and parse jira_lims_config.json from configs
    // contains aliases to custom fields (alias, name, id)
    // TODO: need a script which can loop through CFs in an instance and create the json with the names and ids
    static void parseConfigFile() {
        LOG.debug "Creating config from json file"

        // set configMap from json
        JsonSlurper slurper = new JsonSlurper()

//        URL resourceURL = this.class.getResource("/config/jira_lims_config.json")
//        Paths.get(resourceURL.toURI()).withReader { Reader reader ->
        Paths.get(EnvVariableAccess.jiraLimsConfigFilePath).withReader { Reader reader ->
            configMap = slurper.parse(reader)
        }

        if ( configMap == null ) {
            LOG.error "Failed to read the configuration file at filepath: ${EnvVariableAccess.jiraLimsConfigFilePath}"
        } else {
            LOG.debug (configMap.getClass().toString())
        }
    }

    static def getSequencescapeDetails() {
        LOG.debug "Get Sequencescape details"
        if(configMap == null) {
            parseConfigFile()
        }

        configMap['sequencescapeDetails']
    }

    /**
     * Gets a configuration element from the config file using the given key list.
     * @param keys the list of the configuration keys
     * @return the required configuration element or throws {@code NoSuchElementException}
     * if there is no element belongs to the given keys
     */
    static def getConfigElement(List<String> keys) {
        LOG.debug "Get config element details"
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
     * Returns the name of the custom field from the configuration file searched by the given alias name.
     * @param cfAlias the alias name of the custom field
     * @return the name of the queried custom field given by its alias name
     */
    static String getCustomFieldName(String cfAlias) {
        LOG.debug "In config getCustomFieldName with alias ${cfAlias}"
        if(configMap == null) {
            parseConfigFile()
        }
        // TODO: what to do if not found?
        String cfName = configMap['custom_fields'][cfAlias]['cfname']
        LOG.debug "CF name = ${cfName}"
        cfName
    }

    static int getCFId(String cfAlias) {
        if(configMap == null) {
            parseConfigFile()
        }
        // TODO: what to do if not found?
        // TODO: convert to int?
        int cfId = configMap['custom_fields'][cfAlias]['cfid'] as int
        LOG.debug "CF id = ${cfId.toString()}"
        cfId
    }

    static String getCFIdString(String cfAlias) {
        if(configMap == null) {
            parseConfigFile()
        }
        // TODO: what to do if not found?
        String cfIdString = configMap['custom_fields'][cfAlias]['cfidstring']
        LOG.debug "CF idString = ${cfIdString}"
        cfIdString
    }
}
