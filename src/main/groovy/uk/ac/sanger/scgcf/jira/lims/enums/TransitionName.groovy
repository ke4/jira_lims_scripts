package uk.ac.sanger.scgcf.jira.lims.enums

/**
 * Enumerated list for the name of transitions.
 * 
 * Created by ke4 on 25/01/2017.
 */
enum TransitionName {

    REVERT_TO_READY_FOR_SS2("REVERT_TO_READY_FOR_SS2"),
    REVERT_TO_READY_FOR_SUBMISSION("REVERT_TO_READY_FOR_SUBMISSION")

    String transitionName

    public TransitionName(String transitionName) {
        this.transitionName = transitionName
    }

    @Override
    String toString() {
        transitionName
    }
}
