package com.jacobtread.mck.utils.crash

/**
 * ReportedException Thrown to output the provided
 * crash report [report]
 *
 * @property report The crash report to output
 * @constructor Create empty ReportedException
 */
class ReportedException(val report: Report) : RuntimeException(report.description, report.cause)