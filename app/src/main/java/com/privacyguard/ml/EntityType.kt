package com.privacyguard.ml

/**
 * Types of personally identifiable information that can be detected.
 * Each type maps to a severity level for alert routing.
 */
enum class EntityType(val displayName: String, val labelIndex: Int) {
    CREDIT_CARD("Credit Card", 1) {
        override val severity: Severity get() = Severity.CRITICAL
    },
    SSN("Social Security Number", 2) {
        override val severity: Severity get() = Severity.CRITICAL
    },
    PASSWORD("Password", 3) {
        override val severity: Severity get() = Severity.CRITICAL
    },
    API_KEY("API Key", 4) {
        override val severity: Severity get() = Severity.CRITICAL
    },
    EMAIL("Email Address", 5) {
        override val severity: Severity get() = Severity.HIGH
    },
    PHONE("Phone Number", 6) {
        override val severity: Severity get() = Severity.HIGH
    },
    PERSON_NAME("Person Name", 7) {
        override val severity: Severity get() = Severity.MEDIUM
    },
    ADDRESS("Physical Address", 8) {
        override val severity: Severity get() = Severity.MEDIUM
    },
    DATE_OF_BIRTH("Date of Birth", 9) {
        override val severity: Severity get() = Severity.MEDIUM
    },
    MEDICAL_ID("Medical ID", 10) {
        override val severity: Severity get() = Severity.HIGH
    },
    UNKNOWN("Unknown", 0) {
        override val severity: Severity get() = Severity.MEDIUM
    };

    abstract val severity: Severity

    companion object {
        fun fromLabelIndex(index: Int): EntityType {
            return entries.find { it.labelIndex == index } ?: UNKNOWN
        }
    }
}
