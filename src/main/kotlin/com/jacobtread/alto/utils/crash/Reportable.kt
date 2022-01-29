package com.jacobtread.alto.utils.crash

interface Reportable {

    /**
     * Adds relevant information about the object
     * to the provided crash report category
     *
     * @param report The crash report section
     */
    fun addToReport(report: ReportSection)

}