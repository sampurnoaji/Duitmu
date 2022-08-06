package id.petersam.catatankeuangan.util

import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilTest {

    @Test
    fun toDate() {
        val datePattern = DatePattern.YMD

        val nullActual = "".toDate(datePattern)
        assertEquals(null, nullActual)
    }

    @Test
    fun addThousandSeparator() {
        val failed = "abc".addThousandSeparator()
        assertEquals("abc", failed)

        val belowThousand = "1".addThousandSeparator()
        assertEquals("1", belowThousand)

        val thousand = "1000".addThousandSeparator()
        assertEquals("1.000", thousand)

        val million =  "10000000".addThousandSeparator()
        assertEquals("10.000.000", million)
    }

    @Test
    fun removeThousandSeparator() {
        val failed = "abc".removeThousandSeparator()
        assertEquals(null, failed)

        val belowThousand = "1".removeThousandSeparator()
        assertEquals(1L, belowThousand)

        val thousand = "1000".removeThousandSeparator()
        assertEquals(1_000L, thousand)

        val million =  "10000000".removeThousandSeparator()
        assertEquals(10_000_000L, million)
    }
}