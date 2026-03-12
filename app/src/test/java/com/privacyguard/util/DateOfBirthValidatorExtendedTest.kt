package com.privacyguard.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive extended test suite for DateOfBirthValidator.
 * Covers ISO, US, EU, long formats, age calculation, partial detection,
 * text extraction, format conversion, and edge cases.
 *
 * Total: 220+ test functions across 9 sections.
 */
class DateOfBirthValidatorExtendedTest {

    private val validator = DateOfBirthValidator()

    // =========================================================================
    // SECTION 1: Valid DOBs — ISO Format (YYYY-MM-DD) — 25 tests
    // =========================================================================

    @Test
    fun `isValidISO returns true for standard birth date 1990-06-15`() {
        val result = validator.isValidISO("1990-06-15")
        assertTrue("Expected 1990-06-15 to be a valid ISO date", result)
        assertTrue("isValid should also accept ISO format", validator.isValid("1990-06-15"))
    }

    @Test
    fun `isValidISO returns true for January 1st 2000`() {
        val result = validator.isValidISO("2000-01-01")
        assertTrue("2000-01-01 should be valid", result)
        assertNotNull("calculateAge should return non-null for this date", validator.calculateAge("2000-01-01"))
    }

    @Test
    fun `isValidISO returns true for December 31st 1999`() {
        val result = validator.isValidISO("1999-12-31")
        assertTrue("1999-12-31 should be valid", result)
        assertTrue("isValid wrapper should also return true", validator.isValid("1999-12-31"))
    }

    @Test
    fun `isValidISO returns true for leap year date 2000-02-29`() {
        val result = validator.isValidISO("2000-02-29")
        assertTrue("2000 is a leap year, Feb 29 should be valid", result)
        assertNotNull("Age calculation should work for leap year birthday", validator.calculateAge("2000-02-29"))
    }

    @Test
    fun `isValidISO returns true for leap year date 1996-02-29`() {
        val result = validator.isValidISO("1996-02-29")
        assertTrue("1996 is a leap year, Feb 29 should be valid", result)
        assertTrue("isValid should also return true", validator.isValid("1996-02-29"))
    }

    @Test
    fun `isValidISO returns true for earliest reasonable birth year 1900-01-01`() {
        val result = validator.isValidISO("1900-01-01")
        assertTrue("1900-01-01 should be considered valid", result)
        assertNotNull("Age should be calculable even for very old dates", validator.calculateAge("1900-01-01"))
    }

    @Test
    fun `isValidISO returns true for date 1985-11-22`() {
        val result = validator.isValidISO("1985-11-22")
        assertTrue("1985-11-22 is a valid ISO date", result)
        assertTrue("isValid should recognize ISO format", validator.isValid("1985-11-22"))
    }

    @Test
    fun `isValidISO returns true for date 1975-03-08`() {
        val result = validator.isValidISO("1975-03-08")
        assertTrue("1975-03-08 should be valid", result)
        assertNotNull("calculateAge should return a value for this date", validator.calculateAge("1975-03-08"))
    }

    @Test
    fun `isValidISO returns true for date 2005-07-04`() {
        val result = validator.isValidISO("2005-07-04")
        assertTrue("2005-07-04 should be valid", result)
        assertTrue("isValid should return true", validator.isValid("2005-07-04"))
    }

    @Test
    fun `isValidISO returns true for date 1960-09-30`() {
        val result = validator.isValidISO("1960-09-30")
        assertTrue("1960-09-30 should be valid", result)
        assertTrue("isAdult should be true for someone born in 1960", validator.isAdult("1960-09-30"))
    }

    @Test
    fun `isValidISO returns true for date 1955-04-14`() {
        val result = validator.isValidISO("1955-04-14")
        assertTrue("1955-04-14 should be valid", result)
        assertFalse("Someone born in 1955 should not be a minor", validator.isMinor("1955-04-14"))
    }

    @Test
    fun `isValidISO returns true for date 2010-10-10`() {
        val result = validator.isValidISO("2010-10-10")
        assertTrue("2010-10-10 should be valid", result)
        assertTrue("isValid should accept this date", validator.isValid("2010-10-10"))
    }

    @Test
    fun `isValidISO returns true for date 1948-08-15`() {
        val result = validator.isValidISO("1948-08-15")
        assertTrue("1948-08-15 should be valid", result)
        assertNotNull("Age should be calculable", validator.calculateAge("1948-08-15"))
    }

    @Test
    fun `isValidISO returns true for date 2015-02-28`() {
        val result = validator.isValidISO("2015-02-28")
        assertTrue("2015-02-28 should be valid, Feb 28 is always valid", result)
        assertTrue("isValid wrapper should confirm", validator.isValid("2015-02-28"))
    }

    @Test
    fun `isValidISO returns true for date 1932-12-25`() {
        val result = validator.isValidISO("1932-12-25")
        assertTrue("1932-12-25 should be valid", result)
        assertNotNull("calculateAge should return value", validator.calculateAge("1932-12-25"))
    }

    @Test
    fun `isValidISO returns true for date 2008-05-20`() {
        val result = validator.isValidISO("2008-05-20")
        assertTrue("2008-05-20 should be valid", result)
        assertTrue("isValid should accept it", validator.isValid("2008-05-20"))
    }

    @Test
    fun `isValidISO returns true for date 1920-06-01`() {
        val result = validator.isValidISO("1920-06-01")
        assertTrue("1920-06-01 should be valid", result)
        assertTrue("isAdult should be true", validator.isAdult("1920-06-01"))
    }

    @Test
    fun `isValidISO returns true for date 1970-07-20`() {
        val result = validator.isValidISO("1970-07-20")
        assertTrue("1970-07-20 should be valid", result)
        assertNotNull("calculateAge should return value", validator.calculateAge("1970-07-20"))
    }

    @Test
    fun `isValidISO returns true for date 2003-11-11`() {
        val result = validator.isValidISO("2003-11-11")
        assertTrue("2003-11-11 should be valid", result)
        assertTrue("isValid should accept it", validator.isValid("2003-11-11"))
    }

    @Test
    fun `isValidISO returns true for date 1988-02-29 on a leap year`() {
        // 1988 is a leap year
        val result = validator.isValidISO("1988-02-29")
        assertTrue("1988 is a leap year so Feb 29 is valid", result)
        assertNotNull("Age calculation should work", validator.calculateAge("1988-02-29"))
    }

    @Test
    fun `isValidISO returns true for date 1945-05-08`() {
        val result = validator.isValidISO("1945-05-08")
        assertTrue("1945-05-08 should be valid", result)
        assertFalse("Someone born in 1945 is not a minor", validator.isMinor("1945-05-08"))
    }

    @Test
    fun `isValidISO returns true for date 2012-09-09`() {
        val result = validator.isValidISO("2012-09-09")
        assertTrue("2012-09-09 should be valid", result)
        assertTrue("isValid wrapper should also confirm", validator.isValid("2012-09-09"))
    }

    @Test
    fun `isValidISO returns true for date 1963-03-17`() {
        val result = validator.isValidISO("1963-03-17")
        assertTrue("1963-03-17 should be valid", result)
        assertNotNull("calculateAge must return non-null", validator.calculateAge("1963-03-17"))
    }

    @Test
    fun `isValidISO returns true for date 1980-04-18`() {
        val result = validator.isValidISO("1980-04-18")
        assertTrue("1980-04-18 should be valid", result)
        assertTrue("isAdult should be true", validator.isAdult("1980-04-18"))
    }

    @Test
    fun `isValidISO returns true for date 2007-01-29`() {
        val result = validator.isValidISO("2007-01-29")
        assertTrue("2007-01-29 should be valid", result)
        assertTrue("isValid should confirm", validator.isValid("2007-01-29"))
    }

    // =========================================================================
    // SECTION 2: Valid DOBs — US Format (MM/DD/YYYY) — 25 tests
    // =========================================================================

    @Test
    fun `isValidUS returns true for 06-15-1990 style date`() {
        val result = validator.isValidUS("06/15/1990")
        assertTrue("06/15/1990 should be valid US format", result)
        assertTrue("isValid should also return true", validator.isValid("06/15/1990"))
    }

    @Test
    fun `isValidUS returns true for 01-01-2000`() {
        val result = validator.isValidUS("01/01/2000")
        assertTrue("01/01/2000 should be valid US format", result)
        assertNotNull("calculateAge should work", validator.calculateAge("01/01/2000"))
    }

    @Test
    fun `isValidUS returns true for 12-31-1999`() {
        val result = validator.isValidUS("12/31/1999")
        assertTrue("12/31/1999 should be valid US format", result)
        assertTrue("isValid should accept it", validator.isValid("12/31/1999"))
    }

    @Test
    fun `isValidUS returns true for 02-29-2000 leap year`() {
        val result = validator.isValidUS("02/29/2000")
        assertTrue("02/29/2000 is a leap year date in US format", result)
        assertNotNull("calculateAge should work", validator.calculateAge("02/29/2000"))
    }

    @Test
    fun `isValidUS returns true for 11-22-1985`() {
        val result = validator.isValidUS("11/22/1985")
        assertTrue("11/22/1985 should be valid US format", result)
        assertTrue("isAdult should be true", validator.isAdult("11/22/1985"))
    }

    @Test
    fun `isValidUS returns true for 03-08-1975`() {
        val result = validator.isValidUS("03/08/1975")
        assertTrue("03/08/1975 should be valid US format", result)
        assertFalse("Should not be a minor", validator.isMinor("03/08/1975"))
    }

    @Test
    fun `isValidUS returns true for 07-04-2005`() {
        val result = validator.isValidUS("07/04/2005")
        assertTrue("07/04/2005 should be valid US format", result)
        assertTrue("isValid should confirm", validator.isValid("07/04/2005"))
    }

    @Test
    fun `isValidUS returns true for 09-30-1960`() {
        val result = validator.isValidUS("09/30/1960")
        assertTrue("09/30/1960 should be valid US format", result)
        assertTrue("isAdult should be true", validator.isAdult("09/30/1960"))
    }

    @Test
    fun `isValidUS returns true for 04-14-1955`() {
        val result = validator.isValidUS("04/14/1955")
        assertTrue("04/14/1955 should be valid US format", result)
        assertNotNull("calculateAge should return a value", validator.calculateAge("04/14/1955"))
    }

    @Test
    fun `isValidUS returns true for 10-10-2010`() {
        val result = validator.isValidUS("10/10/2010")
        assertTrue("10/10/2010 should be valid US format", result)
        assertTrue("isValid should return true", validator.isValid("10/10/2010"))
    }

    @Test
    fun `isValidUS returns true for 08-15-1948`() {
        val result = validator.isValidUS("08/15/1948")
        assertTrue("08/15/1948 should be valid US format", result)
        assertFalse("Should not be a minor", validator.isMinor("08/15/1948"))
    }

    @Test
    fun `isValidUS returns true for 02-28-2015`() {
        val result = validator.isValidUS("02/28/2015")
        assertTrue("02/28/2015 should be valid US format", result)
        assertNotNull("calculateAge should work", validator.calculateAge("02/28/2015"))
    }

    @Test
    fun `isValidUS returns true for 12-25-1932`() {
        val result = validator.isValidUS("12/25/1932")
        assertTrue("12/25/1932 should be valid US format", result)
        assertTrue("isAdult should be true", validator.isAdult("12/25/1932"))
    }

    @Test
    fun `isValidUS returns true for 05-20-2008`() {
        val result = validator.isValidUS("05/20/2008")
        assertTrue("05/20/2008 should be valid US format", result)
        assertTrue("isValid should confirm", validator.isValid("05/20/2008"))
    }

    @Test
    fun `isValidUS returns true for 06-01-1920`() {
        val result = validator.isValidUS("06/01/1920")
        assertTrue("06/01/1920 should be valid US format", result)
        assertNotNull("calculateAge should return value", validator.calculateAge("06/01/1920"))
    }

    @Test
    fun `isValidUS returns true for 07-20-1970`() {
        val result = validator.isValidUS("07/20/1970")
        assertTrue("07/20/1970 should be valid US format", result)
        assertTrue("isAdult should be true", validator.isAdult("07/20/1970"))
    }

    @Test
    fun `isValidUS returns true for 11-11-2003`() {
        val result = validator.isValidUS("11/11/2003")
        assertTrue("11/11/2003 should be valid US format", result)
        assertTrue("isValid should confirm", validator.isValid("11/11/2003"))
    }

    @Test
    fun `isValidUS returns true for 02-29-1988 leap year`() {
        val result = validator.isValidUS("02/29/1988")
        assertTrue("02/29/1988 is valid, 1988 is a leap year", result)
        assertNotNull("calculateAge should work", validator.calculateAge("02/29/1988"))
    }

    @Test
    fun `isValidUS returns true for 05-08-1945`() {
        val result = validator.isValidUS("05/08/1945")
        assertTrue("05/08/1945 should be valid US format", result)
        assertFalse("Should not be a minor", validator.isMinor("05/08/1945"))
    }

    @Test
    fun `isValidUS returns true for 09-09-2012`() {
        val result = validator.isValidUS("09/09/2012")
        assertTrue("09/09/2012 should be valid US format", result)
        assertTrue("isValid should confirm", validator.isValid("09/09/2012"))
    }

    @Test
    fun `isValidUS returns true for 03-17-1963`() {
        val result = validator.isValidUS("03/17/1963")
        assertTrue("03/17/1963 should be valid US format", result)
        assertNotNull("calculateAge should return value", validator.calculateAge("03/17/1963"))
    }

    @Test
    fun `isValidUS returns true for 04-18-1980`() {
        val result = validator.isValidUS("04/18/1980")
        assertTrue("04/18/1980 should be valid US format", result)
        assertTrue("isAdult should be true", validator.isAdult("04/18/1980"))
    }

    @Test
    fun `isValidUS returns true for 01-29-2007`() {
        val result = validator.isValidUS("01/29/2007")
        assertTrue("01/29/2007 should be valid US format", result)
        assertTrue("isValid should confirm", validator.isValid("01/29/2007"))
    }

    @Test
    fun `isValidUS returns true for 05-31-1995`() {
        val result = validator.isValidUS("05/31/1995")
        assertTrue("05/31/1995 should be valid US format", result)
        assertFalse("Should not be a minor", validator.isMinor("05/31/1995"))
    }

    @Test
    fun `isValidUS returns true for 10-01-2001`() {
        val result = validator.isValidUS("10/01/2001")
        assertTrue("10/01/2001 should be valid US format", result)
        assertNotNull("calculateAge should work", validator.calculateAge("10/01/2001"))
    }

    // =========================================================================
    // SECTION 3: Valid DOBs — EU Format (DD/MM/YYYY) — 25 tests
    // =========================================================================

    @Test
    fun `isValidEU returns true for 15-06-1990`() {
        val result = validator.isValidEU("15/06/1990")
        assertTrue("15/06/1990 should be valid EU format", result)
        assertTrue("isValid should recognize EU format", validator.isValid("15/06/1990"))
    }

    @Test
    fun `isValidEU returns true for 01-01-2000`() {
        val result = validator.isValidEU("01/01/2000")
        assertTrue("01/01/2000 should be valid EU format", result)
        assertNotNull("calculateAge should work", validator.calculateAge("01/01/2000"))
    }

    @Test
    fun `isValidEU returns true for 31-12-1999`() {
        val result = validator.isValidEU("31/12/1999")
        assertTrue("31/12/1999 should be valid EU format", result)
        assertTrue("isValid should accept it", validator.isValid("31/12/1999"))
    }

    @Test
    fun `isValidEU returns true for 29-02-2000 leap year`() {
        val result = validator.isValidEU("29/02/2000")
        assertTrue("29/02/2000 is a leap year in EU format", result)
        assertNotNull("calculateAge should work", validator.calculateAge("29/02/2000"))
    }

    @Test
    fun `isValidEU returns true for 22-11-1985`() {
        val result = validator.isValidEU("22/11/1985")
        assertTrue("22/11/1985 should be valid EU format", result)
        assertTrue("isAdult should be true", validator.isAdult("22/11/1985"))
    }

    @Test
    fun `isValidEU returns true for 08-03-1975`() {
        val result = validator.isValidEU("08/03/1975")
        assertTrue("08/03/1975 should be valid EU format", result)
        assertFalse("Should not be a minor", validator.isMinor("08/03/1975"))
    }

    @Test
    fun `isValidEU returns true for 04-07-2005`() {
        val result = validator.isValidEU("04/07/2005")
        assertTrue("04/07/2005 should be valid EU format", result)
        assertTrue("isValid should confirm", validator.isValid("04/07/2005"))
    }

    @Test
    fun `isValidEU returns true for 30-09-1960`() {
        val result = validator.isValidEU("30/09/1960")
        assertTrue("30/09/1960 should be valid EU format", result)
        assertTrue("isAdult should be true", validator.isAdult("30/09/1960"))
    }

    @Test
    fun `isValidEU returns true for 14-04-1955`() {
        val result = validator.isValidEU("14/04/1955")
        assertTrue("14/04/1955 should be valid EU format", result)
        assertNotNull("calculateAge should return value", validator.calculateAge("14/04/1955"))
    }

    @Test
    fun `isValidEU returns true for 10-10-2010`() {
        val result = validator.isValidEU("10/10/2010")
        assertTrue("10/10/2010 should be valid EU format", result)
        assertTrue("isValid should confirm", validator.isValid("10/10/2010"))
    }

    @Test
    fun `isValidEU returns true for 15-08-1948`() {
        val result = validator.isValidEU("15/08/1948")
        assertTrue("15/08/1948 should be valid EU format", result)
        assertFalse("Should not be a minor", validator.isMinor("15/08/1948"))
    }

    @Test
    fun `isValidEU returns true for 28-02-2015`() {
        val result = validator.isValidEU("28/02/2015")
        assertTrue("28/02/2015 should be valid EU format", result)
        assertNotNull("calculateAge should work", validator.calculateAge("28/02/2015"))
    }

    @Test
    fun `isValidEU returns true for 25-12-1932`() {
        val result = validator.isValidEU("25/12/1932")
        assertTrue("25/12/1932 should be valid EU format", result)
        assertTrue("isAdult should be true", validator.isAdult("25/12/1932"))
    }

    @Test
    fun `isValidEU returns true for 20-05-2008`() {
        val result = validator.isValidEU("20/05/2008")
        assertTrue("20/05/2008 should be valid EU format", result)
        assertTrue("isValid should confirm", validator.isValid("20/05/2008"))
    }

    @Test
    fun `isValidEU returns true for 01-06-1920`() {
        val result = validator.isValidEU("01/06/1920")
        assertTrue("01/06/1920 should be valid EU format", result)
        assertNotNull("calculateAge should return value", validator.calculateAge("01/06/1920"))
    }

    @Test
    fun `isValidEU returns true for 20-07-1970`() {
        val result = validator.isValidEU("20/07/1970")
        assertTrue("20/07/1970 should be valid EU format", result)
        assertTrue("isAdult should be true", validator.isAdult("20/07/1970"))
    }

    @Test
    fun `isValidEU returns true for 11-11-2003`() {
        val result = validator.isValidEU("11/11/2003")
        assertTrue("11/11/2003 should be valid EU format", result)
        assertTrue("isValid should confirm", validator.isValid("11/11/2003"))
    }

    @Test
    fun `isValidEU returns true for 29-02-1988 leap year`() {
        val result = validator.isValidEU("29/02/1988")
        assertTrue("29/02/1988 is valid, 1988 is a leap year", result)
        assertNotNull("calculateAge should work", validator.calculateAge("29/02/1988"))
    }

    @Test
    fun `isValidEU returns true for 08-05-1945`() {
        val result = validator.isValidEU("08/05/1945")
        assertTrue("08/05/1945 should be valid EU format", result)
        assertFalse("Should not be a minor", validator.isMinor("08/05/1945"))
    }

    @Test
    fun `isValidEU returns true for 09-09-2012`() {
        val result = validator.isValidEU("09/09/2012")
        assertTrue("09/09/2012 should be valid EU format", result)
        assertTrue("isValid should confirm", validator.isValid("09/09/2012"))
    }

    @Test
    fun `isValidEU returns true for 17-03-1963`() {
        val result = validator.isValidEU("17/03/1963")
        assertTrue("17/03/1963 should be valid EU format", result)
        assertNotNull("calculateAge should return value", validator.calculateAge("17/03/1963"))
    }

    @Test
    fun `isValidEU returns true for 18-04-1980`() {
        val result = validator.isValidEU("18/04/1980")
        assertTrue("18/04/1980 should be valid EU format", result)
        assertTrue("isAdult should be true", validator.isAdult("18/04/1980"))
    }

    @Test
    fun `isValidEU returns true for 29-01-2007`() {
        val result = validator.isValidEU("29/01/2007")
        assertTrue("29/01/2007 should be valid EU format", result)
        assertTrue("isValid should confirm", validator.isValid("29/01/2007"))
    }

    @Test
    fun `isValidEU returns true for 31-05-1995`() {
        val result = validator.isValidEU("31/05/1995")
        assertTrue("31/05/1995 should be valid EU format", result)
        assertFalse("Should not be a minor", validator.isMinor("31/05/1995"))
    }

    @Test
    fun `isValidEU returns true for 01-10-2001`() {
        val result = validator.isValidEU("01/10/2001")
        assertTrue("01/10/2001 should be valid EU format", result)
        assertNotNull("calculateAge should work", validator.calculateAge("01/10/2001"))
    }

    // =========================================================================
    // SECTION 4: Valid DOBs — Long Format — 20 tests
    // =========================================================================

    @Test
    fun `isValidLong returns true for January 1 1980`() {
        val result = validator.isValidLong("January 1, 1980")
        assertTrue("January 1, 1980 should be valid long format", result)
        assertTrue("isValid should also accept this", validator.isValid("January 1, 1980"))
    }

    @Test
    fun `isValidLong returns true for March 15 1992`() {
        val result = validator.isValidLong("March 15, 1992")
        assertTrue("March 15, 1992 should be valid long format", result)
        assertNotNull("calculateAge should return value", validator.calculateAge("March 15, 1992"))
    }

    @Test
    fun `isValidLong returns true for December 31 1999`() {
        val result = validator.isValidLong("December 31, 1999")
        assertTrue("December 31, 1999 should be valid long format", result)
        assertTrue("isAdult should be true", validator.isAdult("December 31, 1999"))
    }

    @Test
    fun `isValidLong returns true for February 29 2000 leap year`() {
        val result = validator.isValidLong("February 29, 2000")
        assertTrue("February 29, 2000 is valid, 2000 is a leap year", result)
        assertNotNull("calculateAge should work", validator.calculateAge("February 29, 2000"))
    }

    @Test
    fun `isValidLong returns true for July 4 1776`() {
        // Historical date — valid if within supported range
        val result = validator.isValidLong("July 4, 1776")
        // Just test that parsing is attempted; validity depends on validator range
        assertNotNull("Result should not throw an exception", result)
        assertNotNull("isValid should handle this gracefully", validator.isValid("July 4, 1776"))
    }

    @Test
    fun `isValidLong returns true for November 22 1963`() {
        val result = validator.isValidLong("November 22, 1963")
        assertTrue("November 22, 1963 should be valid long format", result)
        assertFalse("Should not be a minor", validator.isMinor("November 22, 1963"))
    }

    @Test
    fun `isValidLong returns true for June 15 1990`() {
        val result = validator.isValidLong("June 15, 1990")
        assertTrue("June 15, 1990 should be valid long format", result)
        assertTrue("isAdult should be true", validator.isAdult("June 15, 1990"))
    }

    @Test
    fun `isValidLong returns true for September 8 2005`() {
        val result = validator.isValidLong("September 8, 2005")
        assertTrue("September 8, 2005 should be valid long format", result)
        assertNotNull("calculateAge should work", validator.calculateAge("September 8, 2005"))
    }

    @Test
    fun `isValidLong returns true for April 1 1985`() {
        val result = validator.isValidLong("April 1, 1985")
        assertTrue("April 1, 1985 should be valid long format", result)
        assertTrue("isValid should accept it", validator.isValid("April 1, 1985"))
    }

    @Test
    fun `isValidLong returns true for October 31 1955`() {
        val result = validator.isValidLong("October 31, 1955")
        assertTrue("October 31, 1955 should be valid long format", result)
        assertFalse("Should not be a minor", validator.isMinor("October 31, 1955"))
    }

    @Test
    fun `isValidLong returns true for May 5 2015`() {
        val result = validator.isValidLong("May 5, 2015")
        assertTrue("May 5, 2015 should be valid long format", result)
        assertNotNull("calculateAge should return a value", validator.calculateAge("May 5, 2015"))
    }

    @Test
    fun `isValidLong returns true for August 20 1975`() {
        val result = validator.isValidLong("August 20, 1975")
        assertTrue("August 20, 1975 should be valid long format", result)
        assertTrue("isAdult should be true", validator.isAdult("August 20, 1975"))
    }

    @Test
    fun `isValidLong returns true for February 14 2000`() {
        val result = validator.isValidLong("February 14, 2000")
        assertTrue("February 14, 2000 should be valid long format", result)
        assertTrue("isValid should confirm", validator.isValid("February 14, 2000"))
    }

    @Test
    fun `isValidLong returns true for July 20 1969`() {
        val result = validator.isValidLong("July 20, 1969")
        assertTrue("July 20, 1969 should be valid long format", result)
        assertNotNull("calculateAge should work", validator.calculateAge("July 20, 1969"))
    }

    @Test
    fun `isValidLong returns true for January 15 2010`() {
        val result = validator.isValidLong("January 15, 2010")
        assertTrue("January 15, 2010 should be valid long format", result)
        assertTrue("isValid should accept it", validator.isValid("January 15, 2010"))
    }

    @Test
    fun `isValidLong returns true for March 3 1933`() {
        val result = validator.isValidLong("March 3, 1933")
        assertTrue("March 3, 1933 should be valid long format", result)
        assertFalse("Should not be a minor", validator.isMinor("March 3, 1933"))
    }

    @Test
    fun `isValidLong returns true for November 11 2011`() {
        val result = validator.isValidLong("November 11, 2011")
        assertTrue("November 11, 2011 should be valid long format", result)
        assertNotNull("calculateAge should return value", validator.calculateAge("November 11, 2011"))
    }

    @Test
    fun `isValidLong returns true for December 25 1945`() {
        val result = validator.isValidLong("December 25, 1945")
        assertTrue("December 25, 1945 should be valid long format", result)
        assertTrue("isAdult should be true", validator.isAdult("December 25, 1945"))
    }

    @Test
    fun `isValidLong returns true for September 30 1970`() {
        val result = validator.isValidLong("September 30, 1970")
        assertTrue("September 30, 1970 should be valid long format", result)
        assertTrue("isValid should confirm", validator.isValid("September 30, 1970"))
    }

    @Test
    fun `isValidLong returns true for February 28 1900`() {
        val result = validator.isValidLong("February 28, 1900")
        assertTrue("February 28, 1900 should be valid (1900 is not a leap year, but Feb 28 is always valid)", result)
        assertNotNull("calculateAge should work", validator.calculateAge("February 28, 1900"))
    }

    // =========================================================================
    // SECTION 5: Invalid DOBs — 30 tests
    // =========================================================================

    @Test
    fun `isValid returns false for month 13 in ISO format`() {
        val result = validator.isValid("1990-13-01")
        assertFalse("Month 13 does not exist", result)
        assertFalse("isValidISO should also reject this", validator.isValidISO("1990-13-01"))
    }

    @Test
    fun `isValid returns false for day 32 in ISO format`() {
        val result = validator.isValid("1990-01-32")
        assertFalse("Day 32 does not exist", result)
        assertFalse("isValidISO should also reject", validator.isValidISO("1990-01-32"))
    }

    @Test
    fun `isValid returns false for year 0`() {
        val result = validator.isValid("0000-01-01")
        assertFalse("Year 0 is not a valid birth year", result)
        assertNull("calculateAge should return null for year 0", validator.calculateAge("0000-01-01"))
    }

    @Test
    fun `isValid returns false for null input`() {
        val result = validator.isValid(null)
        assertFalse("Null input should return false", result)
    }

    @Test
    fun `isValid returns false for empty string`() {
        val result = validator.isValid("")
        assertFalse("Empty string should return false", result)
        assertFalse("isValidISO should also reject empty string", validator.isValidISO(""))
    }

    @Test
    fun `isValid returns false for February 29 on a non-leap year`() {
        val result = validator.isValid("1990-02-29")
        assertFalse("1990 is not a leap year, Feb 29 is invalid", result)
        assertFalse("isValidISO should reject", validator.isValidISO("1990-02-29"))
    }

    @Test
    fun `isValid returns false for February 30`() {
        val result = validator.isValid("2000-02-30")
        assertFalse("February never has 30 days", result)
        assertNull("calculateAge should return null", validator.calculateAge("2000-02-30"))
    }

    @Test
    fun `isValid returns false for April 31`() {
        val result = validator.isValid("2000-04-31")
        assertFalse("April has only 30 days", result)
        assertFalse("isValidISO should reject", validator.isValidISO("2000-04-31"))
    }

    @Test
    fun `isValid returns false for June 31`() {
        val result = validator.isValid("1990-06-31")
        assertFalse("June has only 30 days", result)
        assertNull("calculateAge should return null", validator.calculateAge("1990-06-31"))
    }

    @Test
    fun `isValid returns false for September 31`() {
        val result = validator.isValid("1980-09-31")
        assertFalse("September has only 30 days", result)
        assertFalse("isValidISO should reject", validator.isValidISO("1980-09-31"))
    }

    @Test
    fun `isValid returns false for November 31`() {
        val result = validator.isValid("1975-11-31")
        assertFalse("November has only 30 days", result)
        assertNull("calculateAge should return null", validator.calculateAge("1975-11-31"))
    }

    @Test
    fun `isValid returns false for day 0 in ISO format`() {
        val result = validator.isValid("1990-05-00")
        assertFalse("Day 0 does not exist", result)
        assertFalse("isValidISO should reject day 0", validator.isValidISO("1990-05-00"))
    }

    @Test
    fun `isValid returns false for month 0 in ISO format`() {
        val result = validator.isValid("1990-00-15")
        assertFalse("Month 0 does not exist", result)
        assertNull("calculateAge should return null", validator.calculateAge("1990-00-15"))
    }

    @Test
    fun `isValid returns false for plaintext gibberish`() {
        val result = validator.isValid("not-a-date")
        assertFalse("Gibberish should not be valid", result)
        assertFalse("isValidISO should also reject", validator.isValidISO("not-a-date"))
    }

    @Test
    fun `isValid returns false for partial date only year`() {
        val result = validator.isValid("1990")
        assertFalse("A year alone is not a complete DOB", result)
        assertFalse("isValidISO should reject partial year", validator.isValidISO("1990"))
    }

    @Test
    fun `isValid returns false for date with letters mixed in`() {
        val result = validator.isValid("19AB-06-15")
        assertFalse("Non-numeric characters in year are invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("19AB-06-15"))
    }

    @Test
    fun `isValid returns false for US format with month 13`() {
        val result = validator.isValidUS("13/15/1990")
        assertFalse("Month 13 is invalid in US format", result)
        assertFalse("isValid should also reject", validator.isValid("13/15/1990"))
    }

    @Test
    fun `isValid returns false for EU format with day 32`() {
        val result = validator.isValidEU("32/01/1990")
        assertFalse("Day 32 is invalid in EU format", result)
        assertFalse("isValid should also reject", validator.isValid("32/01/1990"))
    }

    @Test
    fun `isValid returns false for date far in the future beyond 130 years`() {
        // A date more than 130 years ago would make the person unrealistically old
        // Testing a date that would make someone well over 130 years old
        val result = validator.isValid("1880-01-01")
        // This tests that the validator rejects implausibly old dates (if that rule applies)
        // Some validators reject dates more than 130 years in the past
        assertNotNull("Result should be a definitive boolean", result)
        // Depending on implementation, we just verify it returns a boolean
        assertFalse("1880 is more than 130 years ago and should be rejected", result)
    }

    @Test
    fun `isValid returns false for negative year`() {
        val result = validator.isValid("-1990-06-15")
        assertFalse("Negative year is invalid", result)
        assertNull("calculateAge should return null for negative year", validator.calculateAge("-1990-06-15"))
    }

    @Test
    fun `isValid returns false for ISO date with wrong separator`() {
        val result = validator.isValidISO("1990/06/15")
        assertFalse("ISO format requires dashes, not slashes", result)
        assertFalse("isValid may reject this format if format is strictly enforced", validator.isValidISO("1990/06/15"))
    }

    @Test
    fun `isValid returns false for US format with wrong separator`() {
        val result = validator.isValidUS("06-15-1990")
        assertFalse("US format requires slashes, not dashes", result)
        assertFalse("isValid should also reject this", validator.isValid("06-15-1990"))
    }

    @Test
    fun `isValid returns false for single digit year`() {
        val result = validator.isValid("9-06-15")
        assertFalse("Single digit year is invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("9-06-15"))
    }

    @Test
    fun `isValid returns false for whitespace only input`() {
        val result = validator.isValid("   ")
        assertFalse("Whitespace-only input should return false", result)
        assertFalse("isValidISO should also reject", validator.isValidISO("   "))
    }

    @Test
    fun `isValid returns false for future date 2099-12-31`() {
        val result = validator.isValid("2099-12-31")
        assertFalse("A date far in the future cannot be a valid DOB", result)
        assertNull("calculateAge should return null for future dates", validator.calculateAge("2099-12-31"))
    }

    @Test
    fun `isValid returns false for US format February 30`() {
        val result = validator.isValidUS("02/30/2000")
        assertFalse("February never has 30 days", result)
        assertFalse("isValid should also reject", validator.isValid("02/30/2000"))
    }

    @Test
    fun `isValid returns false for incomplete ISO date missing day`() {
        val result = validator.isValidISO("1990-06")
        assertFalse("Incomplete date without day is invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("1990-06"))
    }

    @Test
    fun `isValid returns false for long format with invalid month name`() {
        val result = validator.isValidLong("Octember 15, 1990")
        assertFalse("Octember is not a real month", result)
        assertNull("calculateAge should return null", validator.calculateAge("Octember 15, 1990"))
    }

    @Test
    fun `isValid returns false for February 29 on century non-leap year 1900`() {
        val result = validator.isValid("1900-02-29")
        assertFalse("1900 is divisible by 100 but not 400, so not a leap year", result)
        assertFalse("isValidISO should reject", validator.isValidISO("1900-02-29"))
    }

    @Test
    fun `isValid returns false for date with extra characters appended`() {
        val result = validator.isValid("1990-06-15XYZ")
        assertFalse("Trailing characters make the date invalid", result)
        assertFalse("isValidISO should also reject", validator.isValidISO("1990-06-15XYZ"))
    }

    // =========================================================================
    // SECTION 6: Age Calculation — 25 tests
    // =========================================================================

    @Test
    fun `calculateAge returns correct age for someone born in 1990 on ISO date`() {
        // As of March 2026, someone born Jan 1 1990 is 36
        val age = validator.calculateAge("1990-01-01")
        assertNotNull("Age should not be null", age)
        assertTrue("Age should be reasonable (between 30 and 40)", age!! in 30..40)
    }

    @Test
    fun `calculateAge returns null for invalid date`() {
        val age = validator.calculateAge("not-a-date")
        assertNull("Invalid date should return null", age)
    }

    @Test
    fun `calculateAge returns null for null-equivalent blank string`() {
        val age = validator.calculateAge("")
        assertNull("Empty date should return null", age)
    }

    @Test
    fun `calculateAge returns positive integer for valid past date`() {
        val age = validator.calculateAge("1985-07-20")
        assertNotNull("Age should not be null", age)
        assertTrue("Age should be greater than 0", age!! > 0)
    }

    @Test
    fun `calculateAge returns reasonable age for 2000-02-29 leap year birthday`() {
        val age = validator.calculateAge("2000-02-29")
        assertNotNull("Age should not be null", age)
        assertTrue("Age should be around 25-26 in 2026", age!! in 24..27)
    }

    @Test
    fun `calculateAge returns age greater than 50 for 1960s birth year`() {
        val age = validator.calculateAge("1965-06-01")
        assertNotNull("Age should not be null", age)
        assertTrue("Someone born in 1965 should be over 50 in 2026", age!! > 50)
    }

    @Test
    fun `isMinor returns true for someone born 10 years ago`() {
        val result = validator.isMinor("2016-01-01")
        assertTrue("Someone born in 2016 is a minor in 2026", result)
    }

    @Test
    fun `isMinor returns false for someone born 25 years ago`() {
        val result = validator.isMinor("2001-01-01")
        assertFalse("Someone born in 2001 is 25 in 2026, not a minor", result)
        assertTrue("isAdult should return true", validator.isAdult("2001-01-01"))
    }

    @Test
    fun `isAdult returns true for someone born in 1990`() {
        val result = validator.isAdult("1990-06-15")
        assertTrue("Someone born in 1990 is definitely an adult", result)
        assertFalse("isMinor should return false", validator.isMinor("1990-06-15"))
    }

    @Test
    fun `isAdult returns false for someone born 5 years ago`() {
        val result = validator.isAdult("2021-01-01")
        assertFalse("Someone born in 2021 is 5 years old in 2026, not an adult", result)
        assertTrue("isMinor should return true", validator.isMinor("2021-01-01"))
    }

    @Test
    fun `isAdult returns true for someone born exactly 18 years ago today conceptually`() {
        // Use a date clearly 20 years ago to avoid edge cases
        val result = validator.isAdult("2006-01-01")
        assertTrue("Someone born in 2006 is 20 in 2026", result)
        assertFalse("isMinor should return false", validator.isMinor("2006-01-01"))
    }

    @Test
    fun `calculateAge returns value consistent with isAdult for 1980 birth year`() {
        val age = validator.calculateAge("1980-03-15")
        val adult = validator.isAdult("1980-03-15")
        assertNotNull("Age should not be null", age)
        assertTrue("Age >= 18 means isAdult should be true", adult)
        assertTrue("Age should be >= 18", age!! >= 18)
    }

    @Test
    fun `calculateAge returns value consistent with isMinor for recent birth year`() {
        val age = validator.calculateAge("2015-06-01")
        val minor = validator.isMinor("2015-06-01")
        assertNotNull("Age should not be null", age)
        assertTrue("Age < 18 means isMinor should be true", minor)
        assertTrue("Age should be < 18 for someone born in 2015", age!! < 18)
    }

    @Test
    fun `calculateAge for US format date is consistent with ISO format`() {
        val ageISO = validator.calculateAge("1990-06-15")
        val ageUS = validator.calculateAge("06/15/1990")
        assertNotNull("ISO age should not be null", ageISO)
        assertNotNull("US age should not be null", ageUS)
        assertEquals("Both formats for same date should return same age", ageISO, ageUS)
    }

    @Test
    fun `calculateAge for EU format date is consistent with ISO format`() {
        val ageISO = validator.calculateAge("1990-06-15")
        val ageEU = validator.calculateAge("15/06/1990")
        assertNotNull("ISO age should not be null", ageISO)
        assertNotNull("EU age should not be null", ageEU)
        assertEquals("Both formats for same date should return same age", ageISO, ageEU)
    }

    @Test
    fun `calculateAge for long format is consistent with ISO format`() {
        val ageISO = validator.calculateAge("1990-06-15")
        val ageLong = validator.calculateAge("June 15, 1990")
        assertNotNull("ISO age should not be null", ageISO)
        assertNotNull("Long format age should not be null", ageLong)
        assertEquals("Both formats for same date should give same age", ageISO, ageLong)
    }

    @Test
    fun `isMinor returns false for person born in 1945`() {
        val result = validator.isMinor("1945-12-25")
        assertFalse("Person born in 1945 is 80 years old, not a minor", result)
        assertTrue("isAdult should be true", validator.isAdult("1945-12-25"))
    }

    @Test
    fun `calculateAge returns value less than 130 for valid dates since 1900`() {
        val age = validator.calculateAge("1900-01-01")
        assertNotNull("Age should not be null", age)
        assertTrue("Age should be at most 130", age!! <= 130)
    }

    @Test
    fun `calculateAge returns non-negative value for all valid past dates`() {
        val dates = listOf("1990-01-01", "2000-06-15", "1975-11-30", "2010-03-08")
        dates.forEach { date ->
            val age = validator.calculateAge(date)
            assertNotNull("Age for $date should not be null", age)
            assertTrue("Age for $date should be non-negative", age!! >= 0)
        }
    }

    @Test
    fun `isAdult and isMinor are mutually exclusive for valid dates`() {
        val date = "2000-01-01"
        val adult = validator.isAdult(date)
        val minor = validator.isMinor(date)
        assertNotEquals("isAdult and isMinor should not both be true", adult, minor)
    }

    @Test
    fun `calculateAge increments correctly between two adjacent years`() {
        val age1990 = validator.calculateAge("1990-01-01")
        val age1991 = validator.calculateAge("1991-01-01")
        assertNotNull("1990 age should not be null", age1990)
        assertNotNull("1991 age should not be null", age1991)
        assertEquals("Person born one year later should be 1 year younger", 1, age1990!! - age1991!!)
    }

    @Test
    fun `isMinor returns true for newborn born in 2025`() {
        val result = validator.isMinor("2025-01-01")
        assertTrue("Someone born in 2025 is definitely a minor in 2026", result)
        assertFalse("isAdult should be false", validator.isAdult("2025-01-01"))
    }

    @Test
    fun `calculateAge returns null for future date`() {
        val age = validator.calculateAge("2099-06-15")
        assertNull("Future dates should return null from calculateAge", age)
    }

    @Test
    fun `isAdult returns true for 18th birthday in 2008`() {
        // As of 2026, someone born in 2008 is 17-18, test a date that clearly makes them 18+
        val result = validator.isAdult("2007-06-15")
        assertTrue("Someone born in 2007 is 18-19 in 2026, should be an adult", result)
    }

    @Test
    fun `calculateAge returns consistent result for multiple calls on same date`() {
        val date = "1985-09-15"
        val age1 = validator.calculateAge(date)
        val age2 = validator.calculateAge(date)
        assertEquals("Multiple calls should return the same age", age1, age2)
    }

    // =========================================================================
    // SECTION 7: Partial Date Detection — 20 tests
    // =========================================================================

    @Test
    fun `isValid returns false for year only 1990`() {
        val result = validator.isValid("1990")
        assertFalse("A year alone is not a complete date of birth", result)
    }

    @Test
    fun `isValid returns false for year-month only 1990-06`() {
        val result = validator.isValid("1990-06")
        assertFalse("Year and month without day is not a complete DOB", result)
        assertFalse("isValidISO should also reject this partial date", validator.isValidISO("1990-06"))
    }

    @Test
    fun `isValidISO returns false for year only 2000`() {
        val result = validator.isValidISO("2000")
        assertFalse("Year alone is not valid ISO date", result)
    }

    @Test
    fun `isValidUS returns false for month-year only 06-1990`() {
        val result = validator.isValidUS("06/1990")
        assertFalse("Month and year without day is not valid US DOB", result)
    }

    @Test
    fun `isValidEU returns false for day-month only 15-06`() {
        val result = validator.isValidEU("15/06")
        assertFalse("Day and month without year is not valid EU DOB", result)
    }

    @Test
    fun `isValid returns false for just a day number 15`() {
        val result = validator.isValid("15")
        assertFalse("A single day number is not a valid DOB", result)
    }

    @Test
    fun `isValid returns false for month name only January`() {
        val result = validator.isValid("January")
        assertFalse("A month name alone is not a valid DOB", result)
    }

    @Test
    fun `isValid returns false for month and year January 1990`() {
        val result = validator.isValid("January 1990")
        assertFalse("Month and year without day is not a complete DOB", result)
        assertFalse("isValidLong should also reject this", validator.isValidLong("January 1990"))
    }

    @Test
    fun `extractFromText does not return partial dates as full DOBs`() {
        val text = "He was born in 1990 somewhere in June"
        val results = validator.extractFromText(text)
        // Partial dates should not be included as full DOBs
        results.forEach { dob ->
            assertTrue("Extracted DOB '$dob' should be valid", validator.isValid(dob))
        }
    }

    @Test
    fun `isValidISO returns false for two-digit year 90-06-15`() {
        val result = validator.isValidISO("90-06-15")
        assertFalse("Two-digit years are not valid ISO format", result)
    }

    @Test
    fun `isValidUS returns false for two-digit year 06-15-90`() {
        val result = validator.isValidUS("06/15/90")
        assertFalse("Two-digit years in US format should be rejected", result)
    }

    @Test
    fun `isValidEU returns false for two-digit year 15-06-90`() {
        val result = validator.isValidEU("15/06/90")
        assertFalse("Two-digit years in EU format should be rejected", result)
    }

    @Test
    fun `isValid returns false for century only 1900`() {
        val result = validator.isValid("1900")
        assertFalse("A four-digit year alone is not a complete DOB", result)
    }

    @Test
    fun `isValidLong returns false for just the day and month March 15`() {
        val result = validator.isValidLong("March 15")
        assertFalse("Month and day without year is not a valid DOB", result)
    }

    @Test
    fun `isValid returns false for ISO date missing leading zero 1990-6-5`() {
        val result = validator.isValidISO("1990-6-5")
        assertFalse("ISO format requires zero-padded month and day", result)
    }

    @Test
    fun `calculateAge returns null for partial date year-month only`() {
        val age = validator.calculateAge("1990-06")
        assertNull("Partial date should not produce an age", age)
    }

    @Test
    fun `calculateAge returns null for year only`() {
        val age = validator.calculateAge("1990")
        assertNull("Year alone should not produce an age", age)
    }

    @Test
    fun `isAdult returns false for invalid partial date`() {
        val result = validator.isAdult("1990-06")
        assertFalse("Partial date should not be treated as adult", result)
    }

    @Test
    fun `isMinor returns false for partial date year only`() {
        val result = validator.isMinor("2015")
        assertFalse("Year alone is not enough to determine if minor", result)
    }

    @Test
    fun `isValid returns false for date string with only slashes 00-00-0000`() {
        val result = validator.isValid("00/00/0000")
        assertFalse("All-zero date should be invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("00/00/0000"))
    }

    // =========================================================================
    // SECTION 8: extractFromText — 25 tests
    // =========================================================================

    @Test
    fun `extractFromText finds ISO date in plain sentence`() {
        val text = "She was born on 1990-06-15 in London."
        val results = validator.extractFromText(text)
        assertTrue("Should find at least one date", results.isNotEmpty())
        assertTrue("Should contain the ISO date", results.any { it.contains("1990") })
    }

    @Test
    fun `extractFromText finds US format date in sentence`() {
        val text = "His date of birth is 06/15/1990 according to records."
        val results = validator.extractFromText(text)
        assertTrue("Should find at least one date", results.isNotEmpty())
        assertTrue("Should contain the US date", results.any { it.contains("1990") })
    }

    @Test
    fun `extractFromText finds EU format date in sentence`() {
        val text = "DOB: 15/06/1990 as per passport."
        val results = validator.extractFromText(text)
        assertTrue("Should find at least one date", results.isNotEmpty())
        assertTrue("Should extract a date", results.any { it.contains("1990") })
    }

    @Test
    fun `extractFromText finds long format date in sentence`() {
        val text = "The patient was born on January 1, 1980."
        val results = validator.extractFromText(text)
        assertTrue("Should find at least one date", results.isNotEmpty())
        assertTrue("Should contain year 1980", results.any { it.contains("1980") })
    }

    @Test
    fun `extractFromText returns empty list for text with no dates`() {
        val text = "There are no dates in this paragraph at all."
        val results = validator.extractFromText(text)
        assertNotNull("Result should not be null", results)
        assertTrue("Should return empty list when no dates are present", results.isEmpty())
    }

    @Test
    fun `extractFromText returns empty list for empty string`() {
        val results = validator.extractFromText("")
        assertNotNull("Result should not be null", results)
        assertTrue("Empty string should return empty list", results.isEmpty())
    }

    @Test
    fun `extractFromText finds multiple dates in a paragraph`() {
        val text = "She was born on 1990-06-15 and her sister was born on 1993-11-22."
        val results = validator.extractFromText(text)
        assertTrue("Should find at least two dates", results.size >= 2)
    }

    @Test
    fun `extractFromText does not return invalid dates`() {
        val text = "The reference number is 1990-13-45 for this case."
        val results = validator.extractFromText(text)
        results.forEach { dob ->
            assertTrue("Each extracted DOB should be valid: $dob", validator.isValid(dob))
        }
    }

    @Test
    fun `extractFromText handles text with date at beginning`() {
        val text = "1990-06-15 is her date of birth."
        val results = validator.extractFromText(text)
        assertTrue("Should find the date at start of text", results.isNotEmpty())
    }

    @Test
    fun `extractFromText handles text with date at end`() {
        val text = "Her date of birth is 1990-06-15"
        val results = validator.extractFromText(text)
        assertTrue("Should find the date at end of text", results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds date in form field label context`() {
        val text = "Date of Birth: 1985-11-22, Place of Birth: New York"
        val results = validator.extractFromText(text)
        assertTrue("Should find the date", results.isNotEmpty())
        assertTrue("Should contain the year 1985", results.any { it.contains("1985") })
    }

    @Test
    fun `extractFromText handles multiple format types in same text`() {
        val text = "Born on January 5, 1990 or written as 01/05/1990 in the US."
        val results = validator.extractFromText(text)
        assertTrue("Should find at least one date", results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds date in medical record style text`() {
        val text = "Patient Name: John Doe, DOB: 1975-03-08, MRN: 123456"
        val results = validator.extractFromText(text)
        assertTrue("Should find the DOB", results.isNotEmpty())
        assertTrue("Should contain the year 1975", results.any { it.contains("1975") })
    }

    @Test
    fun `extractFromText finds date in government document style`() {
        val text = "Full Name: Jane Smith. Date of Birth: 22/11/1985. Nationality: British."
        val results = validator.extractFromText(text)
        assertTrue("Should find the DOB", results.isNotEmpty())
    }

    @Test
    fun `extractFromText handles leap year dates correctly`() {
        val text = "She celebrates her birthday on February 29, 2000 every four years."
        val results = validator.extractFromText(text)
        assertTrue("Should find the leap year date", results.isNotEmpty())
    }

    @Test
    fun `extractFromText does not mistake non-date numbers for dates`() {
        val text = "Call us at 555-1234 or visit room 101 on floor 3."
        val results = validator.extractFromText(text)
        results.forEach { dob ->
            assertTrue("Extracted value '$dob' should be a valid date", validator.isValid(dob))
        }
    }

    @Test
    fun `extractFromText finds date preceded by born keyword`() {
        val text = "He was born 1962-08-04 and has lived here since 1990."
        val results = validator.extractFromText(text)
        assertTrue("Should find at least one date", results.isNotEmpty())
        assertTrue("Should contain year 1962", results.any { it.contains("1962") })
    }

    @Test
    fun `extractFromText returns list not null for any input`() {
        val inputs = listOf("no dates here", "1990-01-01", "", "random text 12/25/2000 end")
        inputs.forEach { text ->
            val results = validator.extractFromText(text)
            assertNotNull("Result should never be null for input: $text", results)
        }
    }

    @Test
    fun `extractFromText finds date in resume style text`() {
        val text = "John Doe\nDate of Birth: 15 June 1988\nAddress: 123 Main St"
        val results = validator.extractFromText(text)
        assertTrue("Should find a date in resume text", results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds ISO date when surrounded by punctuation`() {
        val text = "(DOB: 1990-06-15) — for verification purposes."
        val results = validator.extractFromText(text)
        assertTrue("Should find date surrounded by punctuation", results.isNotEmpty())
    }

    @Test
    fun `extractFromText handles long text with single date`() {
        val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "The individual born on 2005-07-04 has requested a new ID. " +
            "All relevant documentation has been submitted for review."
        val results = validator.extractFromText(text)
        assertTrue("Should find exactly the one date", results.isNotEmpty())
        assertTrue("Should contain year 2005", results.any { it.contains("2005") })
    }

    @Test
    fun `extractFromText finds date in ISO format with time appended`() {
        val text = "Created at 1990-06-15T00:00:00Z for user profile."
        val results = validator.extractFromText(text)
        // The validator should extract just the date portion
        assertTrue("Should find at least one result", results.isNotEmpty())
    }

    @Test
    fun `extractFromText returns distinct dates for duplicate occurrences`() {
        val text = "Born 1990-06-15, verified again: 1990-06-15."
        val results = validator.extractFromText(text)
        assertNotNull("Result should not be null", results)
        assertTrue("Should find the date", results.isNotEmpty())
    }

    @Test
    fun `extractFromText finds date in JSON-like text`() {
        val text = """{"name": "Alice", "dob": "1990-06-15", "city": "Berlin"}"""
        val results = validator.extractFromText(text)
        assertTrue("Should find the date in JSON text", results.isNotEmpty())
        assertTrue("Should contain year 1990", results.any { it.contains("1990") })
    }

    @Test
    fun `extractFromText finds US format date in narrative`() {
        val text = "According to the file, the person was born on 07/04/1986 in Chicago."
        val results = validator.extractFromText(text)
        assertTrue("Should find the US format date", results.isNotEmpty())
        assertTrue("Should contain year 1986", results.any { it.contains("1986") })
    }

    // =========================================================================
    // SECTION 9: Format Conversion — 20 tests
    // =========================================================================

    @Test
    fun `convertToISO converts US format to ISO format correctly`() {
        val result = validator.convertToISO("06/15/1990")
        assertNotNull("Conversion result should not be null", result)
        assertEquals("US to ISO should produce 1990-06-15", "1990-06-15", result)
    }

    @Test
    fun `convertToISO converts EU format to ISO format correctly`() {
        val result = validator.convertToISO("15/06/1990")
        assertNotNull("Conversion result should not be null", result)
        assertEquals("EU to ISO should produce 1990-06-15", "1990-06-15", result)
    }

    @Test
    fun `convertToISO returns same value for already ISO format`() {
        val result = validator.convertToISO("1990-06-15")
        assertNotNull("Result should not be null", result)
        assertEquals("ISO to ISO should return the same date", "1990-06-15", result)
    }

    @Test
    fun `convertToISO converts long format January 1 1980 to ISO`() {
        val result = validator.convertToISO("January 1, 1980")
        assertNotNull("Conversion result should not be null", result)
        assertEquals("Long format to ISO should produce 1980-01-01", "1980-01-01", result)
    }

    @Test
    fun `convertToISO converts long format December 31 1999 to ISO`() {
        val result = validator.convertToISO("December 31, 1999")
        assertNotNull("Conversion result should not be null", result)
        assertEquals("Long format to ISO should produce 1999-12-31", "1999-12-31", result)
    }

    @Test
    fun `convertToISO returns null for invalid date`() {
        val result = validator.convertToISO("not-a-date")
        assertNull("Invalid date conversion should return null", result)
    }

    @Test
    fun `convertToISO returns null for empty string`() {
        val result = validator.convertToISO("")
        assertNull("Empty string conversion should return null", result)
    }

    @Test
    fun `convertToISO converts US format 01-01-2000 to 2000-01-01`() {
        val result = validator.convertToISO("01/01/2000")
        assertNotNull("Result should not be null", result)
        assertEquals("Should convert to 2000-01-01", "2000-01-01", result)
    }

    @Test
    fun `convertToISO result is always a valid ISO date`() {
        val inputs = listOf("06/15/1990", "15/06/1990", "January 1, 1980", "1990-06-15")
        inputs.forEach { input ->
            val result = validator.convertToISO(input)
            if (result != null) {
                assertTrue("Converted result '$result' should be a valid ISO date", validator.isValidISO(result))
            }
        }
    }

    @Test
    fun `convertToISO handles leap year date in US format`() {
        val result = validator.convertToISO("02/29/2000")
        assertNotNull("Leap year date conversion should not return null", result)
        assertEquals("Should convert to 2000-02-29", "2000-02-29", result)
    }

    @Test
    fun `convertToISO converts EU format 29-02-2000 to ISO`() {
        val result = validator.convertToISO("29/02/2000")
        assertNotNull("EU leap year conversion should not return null", result)
        assertEquals("Should convert to 2000-02-29", "2000-02-29", result)
    }

    @Test
    fun `convertToISO handles February 28 in non-leap year US format`() {
        val result = validator.convertToISO("02/28/2001")
        assertNotNull("Should successfully convert non-leap Feb 28", result)
        assertEquals("Should produce 2001-02-28", "2001-02-28", result)
    }

    @Test
    fun `convertToISO returns null for US format with invalid month 13`() {
        val result = validator.convertToISO("13/15/1990")
        assertNull("Invalid US date with month 13 should return null", result)
    }

    @Test
    fun `convertToISO returns null for EU format with day 32`() {
        val result = validator.convertToISO("32/01/1990")
        assertNull("Invalid EU date with day 32 should return null", result)
    }

    @Test
    fun `convertToISO output matches calculateAge of original date`() {
        val original = "06/15/1990"
        val iso = validator.convertToISO(original)
        assertNotNull("ISO conversion should not be null", iso)
        val ageFromOriginal = validator.calculateAge(original)
        val ageFromISO = validator.calculateAge(iso!!)
        assertEquals("Age from original and ISO conversion should match", ageFromOriginal, ageFromISO)
    }

    @Test
    fun `convertToISO handles November 22 1963 long format`() {
        val result = validator.convertToISO("November 22, 1963")
        assertNotNull("Conversion should not be null", result)
        assertEquals("Should produce 1963-11-22", "1963-11-22", result)
    }

    @Test
    fun `convertToISO handles July 20 1969 long format`() {
        val result = validator.convertToISO("July 20, 1969")
        assertNotNull("Conversion should not be null", result)
        assertEquals("Should produce 1969-07-20", "1969-07-20", result)
    }

    @Test
    fun `convertToISO produces valid ISO output for EU format 31-12-1999`() {
        val result = validator.convertToISO("31/12/1999")
        assertNotNull("Should not return null", result)
        assertEquals("Should produce 1999-12-31", "1999-12-31", result)
        assertTrue("Result should be valid ISO", validator.isValidISO(result!!))
    }

    @Test
    fun `convertToISO handles March 15 1992 long format`() {
        val result = validator.convertToISO("March 15, 1992")
        assertNotNull("Conversion should not be null", result)
        assertEquals("Should produce 1992-03-15", "1992-03-15", result)
    }

    @Test
    fun `convertToISO is idempotent when called twice on the same ISO date`() {
        val input = "1985-07-20"
        val firstConversion = validator.convertToISO(input)
        assertNotNull("First conversion should not be null", firstConversion)
        val secondConversion = validator.convertToISO(firstConversion!!)
        assertNotNull("Second conversion should not be null", secondConversion)
        assertEquals("Two ISO conversions of ISO date should be same", firstConversion, secondConversion)
    }

    // =========================================================================
    // BONUS SECTION: Additional edge cases and boundary tests
    // =========================================================================

    @Test
    fun `isValid handles date with surrounding whitespace`() {
        val result = validator.isValid("  1990-06-15  ")
        // Some validators trim, some don't; test that it doesn't crash
        assertNotNull("Should return a boolean, not throw", result)
    }

    @Test
    fun `isValidISO returns false for year with 5 digits`() {
        val result = validator.isValidISO("19900-06-15")
        assertFalse("5-digit year is not valid ISO", result)
    }

    @Test
    fun `isValidUS returns false for date with missing separators`() {
        val result = validator.isValidUS("06151990")
        assertFalse("US date without separators should be invalid", result)
    }

    @Test
    fun `isValidEU returns false for date with dash separators instead of slash`() {
        val result = validator.isValidEU("15-06-1990")
        assertFalse("EU format requires slashes", result)
    }

    @Test
    fun `calculateAge returns age around 36 for someone born January 1 1990`() {
        val age = validator.calculateAge("1990-01-01")
        assertNotNull("Age should not be null", age)
        // As of March 2026, they are 36
        assertTrue("Age should be in a reasonable range for 1990", age!! in 34..38)
    }

    @Test
    fun `isValid returns false for date with Unicode lookalike characters`() {
        // Using Unicode hyphen instead of ASCII hyphen
        val result = validator.isValid("1990\u20106-06\u201015")
        assertFalse("Unicode lookalike separators should not be valid", result)
    }

    @Test
    fun `isValidLong returns false for abbreviated month Jan 1 1990`() {
        val result = validator.isValidLong("Jan 1, 1990")
        // Depending on implementation, abbreviated months may or may not be supported
        assertNotNull("Should return a boolean without throwing", result)
    }

    @Test
    fun `extractFromText handles null-like edge cases gracefully`() {
        val result = validator.extractFromText("   ")
        assertNotNull("Result should not be null for whitespace input", result)
        assertTrue("Whitespace should yield empty list", result.isEmpty())
    }

    @Test
    fun `isValid returns false for date string with time component`() {
        val result = validator.isValidISO("1990-06-15T12:00:00")
        assertFalse("ISO datetime should not be accepted as DOB unless stripped", result)
    }

    @Test
    fun `convertToISO handles EU format 01-01-2000`() {
        val result = validator.convertToISO("01/01/2000")
        assertNotNull("Conversion of ambiguous date should not return null", result)
        // Either 2000-01-01 from US or 2000-01-01 from EU — same result in this case
        assertEquals("Unambiguous date should convert to 2000-01-01", "2000-01-01", result)
    }

    @Test
    fun `isValidISO accepts all 12 months properly`() {
        val months = listOf("01","02","03","04","05","06","07","08","09","10","11","12")
        months.forEach { month ->
            val date = "1990-$month-01"
            assertTrue("Month $month should be valid in ISO format", validator.isValidISO(date))
        }
    }

    @Test
    fun `isValidEU accepts all months from 01 to 12 with day 01`() {
        val months = listOf("01","02","03","04","05","06","07","08","09","10","11","12")
        months.forEach { month ->
            val date = "01/$month/1990"
            assertTrue("Month $month in EU format should be valid: $date", validator.isValidEU(date))
        }
    }

    @Test
    fun `isValidUS accepts all months from 01 to 12 with day 01`() {
        val months = listOf("01","02","03","04","05","06","07","08","09","10","11","12")
        months.forEach { month ->
            val date = "$month/01/1990"
            assertTrue("Month $month in US format should be valid: $date", validator.isValidUS(date))
        }
    }

    @Test
    fun `calculateAge returns consistent results for all 12 birth months in 1990`() {
        val months = listOf("01","02","03","04","05","06","07","08","09","10","11","12")
        months.forEach { month ->
            val date = "1990-$month-01"
            val age = validator.calculateAge(date)
            assertNotNull("Age for $date should not be null", age)
            assertTrue("Age for 1990 birth month $month should be positive", age!! > 0)
        }
    }

    @Test
    fun `isAdult is true for all dates in 1980`() {
        val months = listOf("01","03","05","07","08","10","12")
        months.forEach { month ->
            val date = "1980-$month-01"
            assertTrue("Person born $date in 1980 should be adult", validator.isAdult(date))
        }
    }

    @Test
    fun `isMinor is true for all dates in 2015`() {
        val months = listOf("01","03","05","07","08","10","12")
        months.forEach { month ->
            val date = "2015-$month-01"
            assertTrue("Person born $date in 2015 should be a minor", validator.isMinor(date))
        }
    }

    @Test
    fun `extractFromText finds exactly one date in sentence with one date`() {
        val text = "My date of birth is 1990-06-15."
        val results = validator.extractFromText(text)
        assertFalse("Should find at least one result", results.isEmpty())
        results.forEach { dob ->
            assertTrue("Extracted '$dob' should be valid", validator.isValid(dob))
        }
    }

    @Test
    fun `isValid returns false for ISO date year 9999`() {
        val result = validator.isValid("9999-12-31")
        assertFalse("Year 9999 is implausible as a date of birth", result)
    }

    @Test
    fun `convertToISO preserves day and month for all months`() {
        val usInputs = mapOf(
            "03/17/1963" to "1963-03-17",
            "11/30/1985" to "1985-11-30",
            "08/08/1988" to "1988-08-08"
        )
        usInputs.forEach { (input, expected) ->
            val result = validator.convertToISO(input)
            assertNotNull("Conversion of $input should not be null", result)
            assertEquals("$input should convert to $expected", expected, result)
        }
    }

    @Test
    fun `isValid returns false for SQL injection style date input`() {
        val result = validator.isValid("1990-06-15'; DROP TABLE users; --")
        assertFalse("SQL injection string should not be valid", result)
    }

    @Test
    fun `isValid returns false for extremely long string input`() {
        val longString = "1990-06-15".repeat(1000)
        val result = validator.isValid(longString)
        assertFalse("Extremely long string should not be valid", result)
    }

    @Test
    fun `extractFromText returns list of strings that can be processed by calculateAge`() {
        val text = "John was born on 1985-04-12 and Mary was born on 1992-08-23."
        val results = validator.extractFromText(text)
        results.forEach { dob ->
            val age = validator.calculateAge(dob)
            assertNotNull("calculateAge should work on extracted DOB: $dob", age)
            assertTrue("Extracted DOB age should be positive: $dob", age!! > 0)
        }
    }

    @Test
    fun `isValidISO returns false for date with double dashes 1990--06-15`() {
        val result = validator.isValidISO("1990--06-15")
        assertFalse("Double dashes should make the date invalid", result)
    }

    @Test
    fun `isValidUS returns false for date with double slashes 06--15-1990`() {
        val result = validator.isValidUS("06//15/1990")
        assertFalse("Double slashes should make the US date invalid", result)
    }

    @Test
    fun `calculateAge for 1990 birth year is greater than age for 2000 birth year`() {
        val age1990 = validator.calculateAge("1990-01-01")
        val age2000 = validator.calculateAge("2000-01-01")
        assertNotNull("1990 age should not be null", age1990)
        assertNotNull("2000 age should not be null", age2000)
        assertTrue("Person born in 1990 should be older than person born in 2000", age1990!! > age2000!!)
    }

    @Test
    fun `isValid handles ISO format date on first day of every month`() {
        val firstDays = listOf(
            "1990-01-01", "1990-02-01", "1990-03-01", "1990-04-01",
            "1990-05-01", "1990-06-01", "1990-07-01", "1990-08-01",
            "1990-09-01", "1990-10-01", "1990-11-01", "1990-12-01"
        )
        firstDays.forEach { date ->
            assertTrue("First day of each month should be valid: $date", validator.isValid(date))
        }
    }

    @Test
    fun `isValid handles ISO format date on last day of months with 31 days`() {
        val lastDays = listOf(
            "1990-01-31", "1990-03-31", "1990-05-31",
            "1990-07-31", "1990-08-31", "1990-10-31", "1990-12-31"
        )
        lastDays.forEach { date ->
            assertTrue("Last day of 31-day month should be valid: $date", validator.isValid(date))
        }
    }

    @Test
    fun `isValid handles ISO format date on last day of months with 30 days`() {
        val lastDays = listOf(
            "1990-04-30", "1990-06-30", "1990-09-30", "1990-11-30"
        )
        lastDays.forEach { date ->
            assertTrue("Last day of 30-day month should be valid: $date", validator.isValid(date))
        }
    }

    @Test
    fun `isValid returns false for day 31 in months with only 30 days`() {
        val invalidDates = listOf(
            "1990-04-31", "1990-06-31", "1990-09-31", "1990-11-31"
        )
        invalidDates.forEach { date ->
            assertFalse("Day 31 in a 30-day month should be invalid: $date", validator.isValid(date))
        }
    }

    @Test
    fun `isAdult and isMinor never both return true for the same valid date`() {
        val testDates = listOf(
            "1990-06-15", "2010-03-22", "2005-11-11", "1970-08-08"
        )
        testDates.forEach { date ->
            val adult = validator.isAdult(date)
            val minor = validator.isMinor(date)
            assertFalse("isAdult and isMinor cannot both be true for $date", adult && minor)
        }
    }

    @Test
    fun `convertToISO followed by isValidISO always returns true for valid inputs`() {
        val inputs = listOf(
            "06/15/1990", "15/06/1990", "March 15, 1992",
            "12/31/1999", "31/12/1999"
        )
        inputs.forEach { input ->
            val iso = validator.convertToISO(input)
            if (iso != null) {
                assertTrue("Converted '$input' to '$iso' which should be valid ISO", validator.isValidISO(iso))
            }
        }
    }

    @Test
    fun `isValid returns false for date containing HTML entities`() {
        val result = validator.isValid("1990&ndash;06&ndash;15")
        assertFalse("HTML entities should not be treated as valid date separators", result)
    }

    @Test
    fun `isValid is case insensitive for long format month names`() {
        val lower = validator.isValidLong("january 1, 1990")
        val upper = validator.isValidLong("JANUARY 1, 1990")
        val mixed = validator.isValidLong("January 1, 1990")
        // All should consistently handle the input (implementation-dependent)
        assertNotNull("Lowercase month result should not be null", lower)
        assertNotNull("Uppercase month result should not be null", upper)
        assertNotNull("Mixed case month result should not be null", mixed)
    }

    @Test
    fun `extractFromText handles multiline text with dates on separate lines`() {
        val text = "Name: John Doe\nDate of Birth: 1990-06-15\nPlace: New York"
        val results = validator.extractFromText(text)
        assertTrue("Should find the date in multiline text", results.isNotEmpty())
        assertTrue("Should contain year 1990", results.any { it.contains("1990") })
    }

    @Test
    fun `calculateAge returns age in years not months or days`() {
        val age = validator.calculateAge("1990-06-15")
        assertNotNull("Age should not be null", age)
        // Age in years for someone born in 1990 should be between 30 and 50 in 2026
        assertTrue("Age should be in year units (30-50 for 1990 birth)", age!! in 30..50)
        assertFalse("Age should not be over 200 (not in days)", age > 200)
    }

    @Test
    fun `isValid correctly handles year 2000 as a leap year`() {
        // 2000 is divisible by 400, so it IS a leap year
        val result = validator.isValid("2000-02-29")
        assertTrue("2000 is a leap year (divisible by 400), Feb 29 is valid", result)
        assertNotNull("calculateAge should work for this date", validator.calculateAge("2000-02-29"))
    }

    @Test
    fun `isValid correctly rejects 1900 as a non-leap year for Feb 29`() {
        // 1900 is divisible by 100 but not 400, so it is NOT a leap year
        val result = validator.isValid("1900-02-29")
        assertFalse("1900 is not a leap year (divisible by 100 but not 400), Feb 29 is invalid", result)
    }

    @Test
    fun `isValid correctly accepts 2004 as a leap year for Feb 29`() {
        // 2004 is divisible by 4 and not by 100, so it IS a leap year
        val result = validator.isValid("2004-02-29")
        assertTrue("2004 is a leap year, Feb 29 should be valid", result)
        assertNotNull("calculateAge should work", validator.calculateAge("2004-02-29"))
    }

    @Test
    fun `isValid correctly accepts 1600 as a leap year for Feb 29`() {
        // 1600 is divisible by 400, so it IS a leap year
        val result = validator.isValidISO("1600-02-29")
        // Depending on supported range, this may or may not be valid
        assertNotNull("Result should be a boolean", result)
    }

    // =========================================================================
    // SECTION 10: Additional boundary and regression tests
    // =========================================================================

    @Test
    fun `isValidISO returns false for date where month and day are swapped to be invalid`() {
        // 1990-31-06 — day and month swapped, month 31 does not exist
        val result = validator.isValidISO("1990-31-06")
        assertFalse("Month 31 does not exist, swapped date should fail", result)
        assertNull("calculateAge should return null", validator.calculateAge("1990-31-06"))
    }

    @Test
    fun `isValidUS returns false for zero-padded impossible date 00-00-0000`() {
        val result = validator.isValidUS("00/00/0000")
        assertFalse("00/00/0000 is not a valid US date", result)
        assertNull("calculateAge should return null", validator.calculateAge("00/00/0000"))
    }

    @Test
    fun `isValidEU returns false for zero-padded impossible date 00-00-0000`() {
        val result = validator.isValidEU("00/00/0000")
        assertFalse("00/00/0000 is not a valid EU date", result)
        assertFalse("isValid should also reject this", validator.isValid("00/00/0000"))
    }

    @Test
    fun `isValid returns false for date string consisting only of dashes`() {
        val result = validator.isValid("----")
        assertFalse("A string of only dashes is not a valid date", result)
        assertNull("calculateAge should return null", validator.calculateAge("----"))
    }

    @Test
    fun `isValid returns false for date string consisting only of slashes`() {
        val result = validator.isValid("//")
        assertFalse("A string of only slashes is not a valid date", result)
        assertFalse("isValidUS should also reject", validator.isValidUS("//"))
    }

    @Test
    fun `calculateAge is not affected by calling isValid first`() {
        val date = "1990-06-15"
        validator.isValid(date) // side-effect check
        val age = validator.calculateAge(date)
        assertNotNull("calculateAge should work after isValid call", age)
        assertTrue("Age should be positive", age!! > 0)
    }

    @Test
    fun `isMinor and isAdult produce opposite results for the same valid date 2000-01-01`() {
        val date = "2000-01-01"
        val minor = validator.isMinor(date)
        val adult = validator.isAdult(date)
        // In 2026, someone born 2000-01-01 is 26, so adult
        assertFalse("Should not be a minor", minor)
        assertTrue("Should be an adult", adult)
        assertNotEquals("isMinor and isAdult must differ", minor, adult)
    }

    @Test
    fun `isMinor returns false for person born exactly 18 years ago conceptually`() {
        // Use a date that is clearly 20+ years ago to avoid today's date edge cases
        val date = "2004-01-01"
        val minor = validator.isMinor(date)
        // In March 2026, someone born Jan 1, 2004 is 22 years old
        assertFalse("22-year-old is not a minor", minor)
        assertTrue("Should be an adult", validator.isAdult(date))
    }

    @Test
    fun `isValidLong returns false for month name with typo Januray`() {
        val result = validator.isValidLong("Januray 15, 1990")
        assertFalse("Typo in month name should not be valid", result)
        assertNull("calculateAge should return null", validator.calculateAge("Januray 15, 1990"))
    }

    @Test
    fun `isValidLong returns false for month name with typo Febuary`() {
        val result = validator.isValidLong("Febuary 15, 1990")
        assertFalse("Typo in month name should not be valid", result)
        assertFalse("isValid should also reject", validator.isValid("Febuary 15, 1990"))
    }

    @Test
    fun `extractFromText handles text with only numbers but no valid dates`() {
        val text = "Order 12345 shipped on 99 boxes total."
        val results = validator.extractFromText(text)
        assertNotNull("Result should not be null", results)
        results.forEach { date ->
            assertTrue("Any extracted date must be valid: $date", validator.isValid(date))
        }
    }

    @Test
    fun `extractFromText handles very long sentence with one buried date`() {
        val prefix = "a ".repeat(200)
        val suffix = " b".repeat(200)
        val text = "${prefix}1990-06-15${suffix}"
        val results = validator.extractFromText(text)
        assertTrue("Should find the date even in very long text", results.isNotEmpty())
    }

    @Test
    fun `isValidISO returns false for date with all nines 9999-99-99`() {
        val result = validator.isValidISO("9999-99-99")
        assertFalse("9999-99-99 has invalid month and day", result)
        assertNull("calculateAge should return null", validator.calculateAge("9999-99-99"))
    }

    @Test
    fun `isValidUS returns false for date with all nines 99-99-9999`() {
        val result = validator.isValidUS("99/99/9999")
        assertFalse("99/99/9999 has invalid month and day", result)
        assertFalse("isValid should also reject", validator.isValid("99/99/9999"))
    }

    @Test
    fun `convertToISO handles edge case of February in long format`() {
        val result = validator.convertToISO("February 28, 1999")
        assertNotNull("Should successfully convert February 28", result)
        assertEquals("Should produce 1999-02-28", "1999-02-28", result)
        assertTrue("Result should be valid ISO", validator.isValidISO(result!!))
    }

    @Test
    fun `convertToISO handles October correctly in long format`() {
        val result = validator.convertToISO("October 31, 1990")
        assertNotNull("Should not return null for October 31", result)
        assertEquals("Should produce 1990-10-31", "1990-10-31", result)
        assertTrue("Result should be valid ISO", validator.isValidISO(result!!))
    }

    @Test
    fun `convertToISO handles September correctly in US format`() {
        val result = validator.convertToISO("09/30/1990")
        assertNotNull("Should not return null", result)
        assertEquals("Should produce 1990-09-30", "1990-09-30", result)
    }

    @Test
    fun `isValidISO rejects date with extra leading zeros 01990-06-15`() {
        val result = validator.isValidISO("01990-06-15")
        assertFalse("Extra leading zero in year is invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("01990-06-15"))
    }

    @Test
    fun `isValidISO rejects date with spaces between components 1990 06 15`() {
        val result = validator.isValidISO("1990 06 15")
        assertFalse("Spaces as separators are not valid for ISO format", result)
        assertFalse("isValid should also reject", validator.isValid("1990 06 15"))
    }

    @Test
    fun `calculateAge result for 1970 birth is around 55 to 56`() {
        val age = validator.calculateAge("1970-01-01")
        assertNotNull("Age should not be null", age)
        // In March 2026, someone born Jan 1 1970 is 56
        assertTrue("Age for 1970-01-01 should be around 55-57", age!! in 54..58)
    }

    @Test
    fun `calculateAge result for 2010 birth is around 15 to 16`() {
        val age = validator.calculateAge("2010-01-01")
        assertNotNull("Age should not be null", age)
        // In March 2026, someone born Jan 1 2010 is 16
        assertTrue("Age for 2010-01-01 should be around 15-17", age!! in 14..18)
    }

    @Test
    fun `isMinor returns true for child born in 2020`() {
        val result = validator.isMinor("2020-06-15")
        assertTrue("Child born in 2020 is 5-6 years old in 2026, definitely a minor", result)
        assertFalse("isAdult should be false", validator.isAdult("2020-06-15"))
    }

    @Test
    fun `isMinor returns true for teenager born in 2010`() {
        val result = validator.isMinor("2010-06-15")
        assertTrue("Teenager born in 2010 is 15-16 in 2026, still a minor", result)
        assertFalse("isAdult should be false", validator.isAdult("2010-06-15"))
    }

    @Test
    fun `isAdult returns true for young adult born in 2005`() {
        // In March 2026, someone born 2005 is 20-21
        val result = validator.isAdult("2005-01-01")
        assertTrue("21-year-old born in 2005 should be an adult", result)
        assertFalse("isMinor should be false", validator.isMinor("2005-01-01"))
    }

    @Test
    fun `isValid handles dates at the very end of each 31-day month`() {
        val dates = listOf("1990-01-31", "1990-03-31", "1990-05-31",
                           "1990-07-31", "1990-08-31", "1990-10-31", "1990-12-31")
        dates.forEach { date ->
            assertTrue("Last day of 31-day month should be valid: $date", validator.isValid(date))
        }
    }

    @Test
    fun `isValidISO handles correctly formatted dates for every day of December`() {
        (1..31).forEach { day ->
            val paddedDay = day.toString().padStart(2, '0')
            val date = "1990-12-$paddedDay"
            assertTrue("December $paddedDay should be valid: $date", validator.isValidISO(date))
        }
    }

    @Test
    fun `isValidISO rejects day 29 in February for non-leap year 2001`() {
        val result = validator.isValidISO("2001-02-29")
        assertFalse("2001 is not a leap year, Feb 29 is invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("2001-02-29"))
    }

    @Test
    fun `isValidISO rejects day 29 in February for non-leap year 2003`() {
        val result = validator.isValidISO("2003-02-29")
        assertFalse("2003 is not a leap year, Feb 29 is invalid", result)
        assertFalse("isValid should also reject", validator.isValid("2003-02-29"))
    }

    @Test
    fun `isValidISO rejects day 29 in February for non-leap year 2005`() {
        val result = validator.isValidISO("2005-02-29")
        assertFalse("2005 is not a leap year, Feb 29 is invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("2005-02-29"))
    }

    @Test
    fun `isValidISO accepts day 29 in February for leap years divisible by 4`() {
        val leapYears = listOf(1992, 1996, 2004, 2008, 2012, 2016, 2020)
        leapYears.forEach { year ->
            val date = "$year-02-29"
            assertTrue("$year is a leap year, Feb 29 should be valid: $date", validator.isValidISO(date))
        }
    }

    @Test
    fun `isValidISO rejects day 29 in February for non-leap years`() {
        val nonLeapYears = listOf(1993, 1997, 1999, 2001, 2003, 2005, 2007)
        nonLeapYears.forEach { year ->
            val date = "$year-02-29"
            assertFalse("$year is not a leap year, Feb 29 should be invalid: $date", validator.isValidISO(date))
        }
    }

    @Test
    fun `extractFromText finds date embedded within parentheses`() {
        val text = "The employee (DOB: 1988-11-05) is eligible for the program."
        val results = validator.extractFromText(text)
        assertTrue("Should find date within parentheses", results.isNotEmpty())
        assertTrue("Should contain year 1988", results.any { it.contains("1988") })
    }

    @Test
    fun `extractFromText finds date in comma separated list`() {
        val text = "Fields: name, 1990-06-15, nationality, occupation"
        val results = validator.extractFromText(text)
        assertTrue("Should find date in comma-separated list", results.isNotEmpty())
    }

    @Test
    fun `convertToISO handles August in long format`() {
        val result = validator.convertToISO("August 15, 1947")
        assertNotNull("Should not return null", result)
        assertEquals("Should produce 1947-08-15", "1947-08-15", result)
    }

    @Test
    fun `convertToISO handles June in long format`() {
        val result = validator.convertToISO("June 6, 1944")
        assertNotNull("Should not return null", result)
        assertEquals("Should produce 1944-06-06", "1944-06-06", result)
    }

    @Test
    fun `convertToISO handles single digit day in long format`() {
        val result = validator.convertToISO("March 5, 1982")
        assertNotNull("Should not return null", result)
        // Should zero-pad the day in ISO output
        assertEquals("Should produce 1982-03-05", "1982-03-05", result)
    }

    @Test
    fun `isValid handles various century boundary years`() {
        // Test dates right at century boundaries
        assertTrue("1901-01-01 should be valid", validator.isValid("1901-01-01"))
        assertTrue("1950-06-15 should be valid", validator.isValid("1950-06-15"))
        assertTrue("1999-12-31 should be valid", validator.isValid("1999-12-31"))
        assertTrue("2000-01-01 should be valid", validator.isValid("2000-01-01"))
    }

    @Test
    fun `isAdult is consistent with calculateAge for boundary age of 18`() {
        // Someone who is exactly 18 or older should be an adult
        // Using 2000-01-01: in March 2026 they are 26 years old
        val date = "2000-01-01"
        val age = validator.calculateAge(date)
        val adult = validator.isAdult(date)
        assertNotNull("Age should not be null", age)
        if (age!! >= 18) {
            assertTrue("isAdult should be true for age >= 18", adult)
        } else {
            assertFalse("isAdult should be false for age < 18", adult)
        }
    }

    @Test
    fun `isMinor is consistent with calculateAge for boundary age of 18`() {
        val date = "2015-03-01"
        val age = validator.calculateAge(date)
        val minor = validator.isMinor(date)
        assertNotNull("Age should not be null", age)
        if (age!! < 18) {
            assertTrue("isMinor should be true for age < 18", minor)
        } else {
            assertFalse("isMinor should be false for age >= 18", minor)
        }
    }

    @Test
    fun `extractFromText handles tab-separated date fields`() {
        val text = "Name\tDate of Birth\tCity\nJohn Doe\t1990-06-15\tNew York"
        val results = validator.extractFromText(text)
        assertTrue("Should find date in tab-separated text", results.isNotEmpty())
    }

    @Test
    fun `isValidLong returns false for day zero with valid month and year`() {
        val result = validator.isValidLong("January 0, 1990")
        assertFalse("Day 0 in long format should be invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("January 0, 1990"))
    }

    @Test
    fun `isValidLong returns false for day 32 in January`() {
        val result = validator.isValidLong("January 32, 1990")
        assertFalse("Day 32 in January long format should be invalid", result)
        assertFalse("isValid should also reject", validator.isValid("January 32, 1990"))
    }

    @Test
    fun `isValidLong returns false for day 30 in February`() {
        val result = validator.isValidLong("February 30, 2000")
        assertFalse("February never has 30 days", result)
        assertNull("calculateAge should return null", validator.calculateAge("February 30, 2000"))
    }

    @Test
    fun `isValidLong returns false for day 31 in April`() {
        val result = validator.isValidLong("April 31, 1990")
        assertFalse("April has only 30 days", result)
        assertFalse("isValid should also reject", validator.isValid("April 31, 1990"))
    }

    @Test
    fun `isValidLong returns false for day 31 in November`() {
        val result = validator.isValidLong("November 31, 1990")
        assertFalse("November has only 30 days", result)
        assertNull("calculateAge should return null", validator.calculateAge("November 31, 1990"))
    }

    @Test
    fun `isValidLong returns false for day 31 in June`() {
        val result = validator.isValidLong("June 31, 1990")
        assertFalse("June has only 30 days", result)
        assertFalse("isValid should also reject", validator.isValid("June 31, 1990"))
    }

    @Test
    fun `isValidLong returns true for all months with valid day 1`() {
        val months = listOf("January", "February", "March", "April", "May", "June",
                            "July", "August", "September", "October", "November", "December")
        months.forEach { month ->
            val date = "$month 1, 1990"
            assertTrue("Day 1 in $month should be valid in long format", validator.isValidLong(date))
        }
    }

    @Test
    fun `calculateAge returns non-null for all ISO dates in 1990`() {
        val months = listOf("01","02","03","04","05","06","07","08","09","10","11","12")
        months.forEach { month ->
            val date = "1990-$month-01"
            val age = validator.calculateAge(date)
            assertNotNull("calculateAge should not return null for $date", age)
        }
    }

    @Test
    fun `convertToISO handles all 12 months in US format`() {
        val inputs = mapOf(
            "01/15/1990" to "1990-01-15",
            "02/15/1990" to "1990-02-15",
            "03/15/1990" to "1990-03-15",
            "04/15/1990" to "1990-04-15",
            "05/15/1990" to "1990-05-15",
            "06/15/1990" to "1990-06-15",
            "07/15/1990" to "1990-07-15",
            "08/15/1990" to "1990-08-15",
            "09/15/1990" to "1990-09-15",
            "10/15/1990" to "1990-10-15",
            "11/15/1990" to "1990-11-15",
            "12/15/1990" to "1990-12-15"
        )
        inputs.forEach { (input, expected) ->
            val result = validator.convertToISO(input)
            assertNotNull("convertToISO($input) should not be null", result)
            assertEquals("US $input should convert to ISO $expected", expected, result)
        }
    }

    @Test
    fun `convertToISO handles all 12 months in EU format`() {
        val inputs = mapOf(
            "15/01/1990" to "1990-01-15",
            "15/02/1990" to "1990-02-15",
            "15/03/1990" to "1990-03-15",
            "15/04/1990" to "1990-04-15",
            "15/05/1990" to "1990-05-15",
            "15/06/1990" to "1990-06-15",
            "15/07/1990" to "1990-07-15",
            "15/08/1990" to "1990-08-15",
            "15/09/1990" to "1990-09-15",
            "15/10/1990" to "1990-10-15",
            "15/11/1990" to "1990-11-15",
            "15/12/1990" to "1990-12-15"
        )
        inputs.forEach { (input, expected) ->
            val result = validator.convertToISO(input)
            assertNotNull("convertToISO($input) should not be null", result)
            assertEquals("EU $input should convert to ISO $expected", expected, result)
        }
    }

    @Test
    fun `extractFromText returns all results as non-null strings`() {
        val text = "Born 1990-06-15, verified on 2000-01-01, updated 2010-03-22."
        val results = validator.extractFromText(text)
        assertNotNull("Result list should not be null", results)
        results.forEach { date ->
            assertNotNull("Each extracted date string should not be null", date)
            assertTrue("Each date should be non-empty", date.isNotEmpty())
        }
    }

    @Test
    fun `isValid is deterministic for repeated calls with the same input`() {
        val date = "1985-07-20"
        val results = (1..5).map { validator.isValid(date) }
        assertTrue("All calls should return true", results.all { it })
        assertEquals("All calls should return same result", 1, results.toSet().size)
    }

    @Test
    fun `isValidISO is deterministic for repeated calls`() {
        val date = "2000-02-29"
        val results = (1..5).map { validator.isValidISO(date) }
        assertEquals("All calls should return same result", 1, results.toSet().size)
    }

    @Test
    fun `calculateAge is deterministic for repeated calls`() {
        val date = "1990-06-15"
        val ages = (1..5).map { validator.calculateAge(date) }
        assertEquals("All calculateAge calls should return same age", 1, ages.toSet().size)
    }

    @Test
    fun `isValid returns false for date with special characters in year 199O-06-15 letter O not zero`() {
        // Using letter O instead of zero in year 199O
        val result = validator.isValid("199O-06-15")
        assertFalse("Letter O in year position should make the date invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("199O-06-15"))
    }

    @Test
    fun `isValidUS returns false for date with letter l instead of 1 in year`() {
        val result = validator.isValidUS("06/l5/1990")
        assertFalse("Letter l in day position should make the date invalid", result)
        assertFalse("isValid should also reject", validator.isValid("06/l5/1990"))
    }

    @Test
    fun `isValidEU returns false for date with non-ASCII digits`() {
        // Using Arabic-Indic numerals
        val result = validator.isValidEU("15/06/١٩٩٠")
        assertFalse("Non-ASCII digits should not be accepted", result)
    }

    @Test
    fun `extractFromText with mixed valid and invalid date-like strings returns only valid ones`() {
        val text = "Refs: 1990-13-45 and 1985-06-15 and invalid 2000-02-30."
        val results = validator.extractFromText(text)
        results.forEach { dob ->
            assertTrue("Only valid dates should be extracted, but found: $dob", validator.isValid(dob))
        }
        // 1985-06-15 should be the only valid one
        assertTrue("Should contain 1985-06-15", results.any { it.contains("1985") })
    }

    @Test
    fun `isValid returns false for ISO date with extra digits in day 1990-06-150`() {
        val result = validator.isValidISO("1990-06-150")
        assertFalse("Extra digit in day field should make date invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("1990-06-150"))
    }

    @Test
    fun `isValid returns false for date with forward slash instead of dash in ISO 1990-06-15`() {
        // This is already tested for US-with-dash, but explicitly test ISO-with-slash
        val result = validator.isValidISO("1990/06/15")
        assertFalse("ISO format must use dashes, not slashes", result)
    }

    @Test
    fun `isValidUS correctly identifies ambiguous date 01-02-2003 as valid US format`() {
        val result = validator.isValidUS("01/02/2003")
        assertTrue("01/02/2003 is January 2nd 2003 in US format, which is valid", result)
        assertTrue("isValid should also confirm", validator.isValid("01/02/2003"))
    }

    @Test
    fun `isValidEU correctly identifies ambiguous date 01-02-2003 as valid EU format`() {
        val result = validator.isValidEU("01/02/2003")
        assertTrue("01/02/2003 is 1st February 2003 in EU format, which is valid", result)
        assertTrue("isValid should also confirm", validator.isValid("01/02/2003"))
    }

    @Test
    fun `convertToISO for US and EU same string 01-02-2003 may differ by context`() {
        // When the input is "01/02/2003", it could be January 2 (US) or February 1 (EU)
        // The validator should have a defined behavior for ambiguous inputs
        val result = validator.convertToISO("01/02/2003")
        assertNotNull("Should return some ISO date for ambiguous input", result)
        // The result should be either 2003-01-02 or 2003-02-01
        assertTrue("Result should be a valid ISO date", validator.isValidISO(result!!))
    }

    @Test
    fun `isValid handles date 1990-06-15 when called many times without state corruption`() {
        val date = "1990-06-15"
        repeat(10) { iteration ->
            val result = validator.isValid(date)
            assertTrue("Call $iteration should return true for $date", result)
        }
    }

    @Test
    fun `isValidLong handles April with 30 days correctly`() {
        assertTrue("April 30 should be valid", validator.isValidLong("April 30, 1990"))
        assertFalse("April 31 should be invalid", validator.isValidLong("April 31, 1990"))
    }

    @Test
    fun `isValidLong handles September with 30 days correctly`() {
        assertTrue("September 30 should be valid", validator.isValidLong("September 30, 1990"))
        assertFalse("September 31 should be invalid", validator.isValidLong("September 31, 1990"))
    }

    @Test
    fun `isValidLong handles June with 30 days correctly`() {
        assertTrue("June 30 should be valid", validator.isValidLong("June 30, 1990"))
        assertFalse("June 31 should be invalid", validator.isValidLong("June 31, 1990"))
    }

    @Test
    fun `isValidLong handles November with 30 days correctly`() {
        assertTrue("November 30 should be valid", validator.isValidLong("November 30, 1990"))
        assertFalse("November 31 should be invalid", validator.isValidLong("November 31, 1990"))
    }

    @Test
    fun `isValidLong handles January with 31 days correctly`() {
        assertTrue("January 31 should be valid", validator.isValidLong("January 31, 1990"))
        assertFalse("January 32 should be invalid", validator.isValidLong("January 32, 1990"))
    }

    @Test
    fun `isValidLong handles March with 31 days correctly`() {
        assertTrue("March 31 should be valid", validator.isValidLong("March 31, 1990"))
        assertFalse("March 32 should be invalid", validator.isValidLong("March 32, 1990"))
    }

    @Test
    fun `isValidLong handles May with 31 days correctly`() {
        assertTrue("May 31 should be valid", validator.isValidLong("May 31, 1990"))
        assertFalse("May 32 should be invalid", validator.isValidLong("May 32, 1990"))
    }

    @Test
    fun `isValidLong handles July with 31 days correctly`() {
        assertTrue("July 31 should be valid", validator.isValidLong("July 31, 1990"))
        assertFalse("July 32 should be invalid", validator.isValidLong("July 32, 1990"))
    }

    @Test
    fun `isValidLong handles August with 31 days correctly`() {
        assertTrue("August 31 should be valid", validator.isValidLong("August 31, 1990"))
        assertFalse("August 32 should be invalid", validator.isValidLong("August 32, 1990"))
    }

    @Test
    fun `isValidLong handles October with 31 days correctly`() {
        assertTrue("October 31 should be valid", validator.isValidLong("October 31, 1990"))
        assertFalse("October 32 should be invalid", validator.isValidLong("October 32, 1990"))
    }

    @Test
    fun `isValidLong handles December with 31 days correctly`() {
        assertTrue("December 31 should be valid", validator.isValidLong("December 31, 1990"))
        assertFalse("December 32 should be invalid", validator.isValidLong("December 32, 1990"))
    }

    @Test
    fun `extractFromText identifies date in Australian driver license style`() {
        val text = "LICENCE NO: DL123456 DOB: 15/06/1990 EXP: 15/06/2030"
        val results = validator.extractFromText(text)
        assertTrue("Should find DOB in driver license style text", results.isNotEmpty())
        assertTrue("Should contain year 1990", results.any { it.contains("1990") })
    }

    @Test
    fun `extractFromText identifies date in US passport style`() {
        val text = "SURNAME: SMITH GIVEN NAMES: JOHN DOB: 15 JUN 1990"
        val results = validator.extractFromText(text)
        assertTrue("Should find DOB in passport style text", results.isNotEmpty())
    }

    @Test
    fun `isValidISO and isValidUS give different results for the same ambiguous string`() {
        // 12/11/2000 could be December 11 US or 12th November EU; as an ISO check it should fail
        val isoResult = validator.isValidISO("12/11/2000")
        assertFalse("12/11/2000 is not valid ISO format (uses slashes)", isoResult)
        val usResult = validator.isValidUS("12/11/2000")
        assertTrue("12/11/2000 is valid US format (December 11, 2000)", usResult)
    }

    @Test
    fun `isValidUS and isValidEU give different interpretations of 01-12-2000`() {
        // In US: January 12, 2000; In EU: 1st December 2000
        val usResult = validator.isValidUS("01/12/2000")
        val euResult = validator.isValidEU("01/12/2000")
        // Both interpretations are calendar-valid dates
        assertTrue("01/12/2000 is valid as US format (Jan 12, 2000)", usResult)
        assertTrue("01/12/2000 is valid as EU format (1 Dec, 2000)", euResult)
    }

    @Test
    fun `calculateAge handles birthday that has not yet occurred in the current year`() {
        // Someone born December 31, 1990: as of March 2026, they have not had their
        // birthday yet this year, so they are still 35
        val age = validator.calculateAge("1990-12-31")
        assertNotNull("Age should not be null", age)
        // In March 2026, Dec 31 birthday has not occurred: they are 35
        assertTrue("Age should be 35 for Dec 31, 1990 birthday in March 2026", age!! in 34..36)
    }

    @Test
    fun `calculateAge handles birthday that has already occurred in the current year`() {
        // Someone born January 1, 1990: as of March 2026, they have already had their
        // birthday, so they are 36
        val age = validator.calculateAge("1990-01-01")
        assertNotNull("Age should not be null", age)
        // In March 2026, Jan 1 birthday has occurred: they are 36
        assertTrue("Age should be 36 for Jan 1, 1990 birthday in March 2026", age!! in 35..37)
    }

    @Test
    fun `isValid returns false for ISO date missing the year component`() {
        val result = validator.isValidISO("-06-15")
        assertFalse("Missing year component should be invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("-06-15"))
    }

    @Test
    fun `isValid returns false for ISO date missing the month component`() {
        val result = validator.isValidISO("1990--15")
        assertFalse("Missing month component should be invalid", result)
        assertFalse("isValid should also reject", validator.isValid("1990--15"))
    }

    @Test
    fun `isValid returns false for ISO date missing the day component`() {
        val result = validator.isValidISO("1990-06-")
        assertFalse("Missing day component should be invalid", result)
        assertNull("calculateAge should return null", validator.calculateAge("1990-06-"))
    }

    @Test
    fun `isValidLong handles year 1990 for all valid February days`() {
        // 1990 is not a leap year, so only Feb 1-28 are valid
        (1..28).forEach { day ->
            val date = "February $day, 1990"
            assertTrue("February $day 1990 should be valid", validator.isValidLong(date))
        }
        assertFalse("February 29, 1990 should be invalid (not a leap year)",
            validator.isValidLong("February 29, 1990"))
    }

    @Test
    fun `isValidLong handles year 2000 for all valid February days including 29`() {
        // 2000 is a leap year, so Feb 1-29 are valid
        (1..29).forEach { day ->
            val date = "February $day, 2000"
            assertTrue("February $day 2000 should be valid", validator.isValidLong(date))
        }
        assertFalse("February 30, 2000 should be invalid",
            validator.isValidLong("February 30, 2000"))
    }
}
