package ie.setu.domain.models

data class LabReports( val reportid: Int, val userid: Int, val reportname: String, val reporttype: String, val reportsource: String, val reportfile : ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LabReports

        return reportfile.contentEquals(other.reportfile)
    }

    override fun hashCode(): Int {
        return reportfile.contentHashCode()
    }
}