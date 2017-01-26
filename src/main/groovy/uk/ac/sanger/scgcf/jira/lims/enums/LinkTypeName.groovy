package uk.ac.sanger.scgcf.jira.lims.enums

/**
 * Enumerated list for the name of link types.
 *
 * Created by ke4 on 25/01/2017.
 */
enum LinkTypeName {

    GROUP_INCLUDES("Group includes")

    String linkTypeName

    public LinkTypeName(String linkTypeName) {
        this.linkTypeName = linkTypeName
    }

    @Override
    String toString() {
        linkTypeName
    }
}
