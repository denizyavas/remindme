package com.deniz.ratpack.process
/**
 * Default implementation of the ProcessId. It uses UUID to fulfil the uniqueness. We might think about having
 * other implementations to provide more readable unique ids.
 * author: TRYavasU
 * date: 04/04/2015
 */
class ProcessIdImpl {

    private final String processId

    ProcessIdImpl() {
        this.processId = UUID.randomUUID().toString()
    }

    ProcessIdImpl(String processId) {
        this()
        if (processId) {
            this.processId = processId
        }
    }

    String getProcessId() {
        processId
    }

    @Override
    String toString() {
        "${getClass().getSimpleName()}(id=${processId != null ? processId : ""}}"
    }

}
