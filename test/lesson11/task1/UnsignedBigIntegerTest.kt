package lesson11.task1

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import kotlin.ArithmeticException

internal class UnsignedBigIntegerTest {

    @Test
    @Tag("8")
    fun plus() {
        assertEquals(UnsignedBigInteger(4), UnsignedBigInteger(2) + UnsignedBigInteger(2))
        assertEquals(UnsignedBigInteger("9087654330"), UnsignedBigInteger("9087654329") + UnsignedBigInteger(1))

        assertEquals(UnsignedBigInteger("1000000000"), UnsignedBigInteger("1") + UnsignedBigInteger(999999999))
        assertEquals(UnsignedBigInteger("0"), UnsignedBigInteger("0") + UnsignedBigInteger(0))
        assertEquals(UnsignedBigInteger("9087654330"), UnsignedBigInteger("9087654330") + UnsignedBigInteger(0))
    }

    @Test
    @Tag("8")
    fun minus() {
        assertEquals(UnsignedBigInteger(2), UnsignedBigInteger(4) - UnsignedBigInteger(2))
        assertEquals(UnsignedBigInteger("9087654329"), UnsignedBigInteger("9087654330") - UnsignedBigInteger(1))
        assertEquals(
            UnsignedBigInteger("100000000"),
            UnsignedBigInteger("1000000000") - UnsignedBigInteger(900000000)
        )
        assertThrows(ArithmeticException::class.java) {
            UnsignedBigInteger(2) - UnsignedBigInteger(4)
        }
    }

    @Test
    @Tag("12")
    fun times() {
        assertEquals(UnsignedBigInteger("6"), UnsignedBigInteger("2") * UnsignedBigInteger("3"))
        assertEquals(UnsignedBigInteger("0"), UnsignedBigInteger("17825") * UnsignedBigInteger("0"))
        assertEquals(UnsignedBigInteger("17825"), UnsignedBigInteger("17825") * UnsignedBigInteger("1"))
        assertEquals(
            UnsignedBigInteger("20000000000"),
            UnsignedBigInteger("100000000") * UnsignedBigInteger("200")
        )
        assertEquals(
            UnsignedBigInteger("18446744073709551616"),
            UnsignedBigInteger("4294967296") * UnsignedBigInteger("4294967296")
        )
    }

    @Test
    @Tag("16")
    fun div() {
        assertEquals(UnsignedBigInteger(6), UnsignedBigInteger(30) / UnsignedBigInteger(5))
        assertThrows(ArithmeticException::class.java) {
            UnsignedBigInteger("3406723053202385826820345353623692839754") / UnsignedBigInteger(0)
        }
        assertEquals(
            UnsignedBigInteger("4294967296"),
            UnsignedBigInteger("18446744073709551616") / UnsignedBigInteger("4294967296")
        )
    }

    @Test
    @Tag("16")
    fun rem() {
        assertEquals(UnsignedBigInteger(5), UnsignedBigInteger(19) % UnsignedBigInteger(7))
        assertEquals(UnsignedBigInteger(100), UnsignedBigInteger("10000000000000") % UnsignedBigInteger(300))
        assertEquals(
            UnsignedBigInteger(0),
            UnsignedBigInteger("18446744073709551616") % UnsignedBigInteger("4294967296")
        )
    }

    @Test
    @Tag("8")
    fun equals() {
        assertEquals(UnsignedBigInteger(123456789), UnsignedBigInteger("123456789"))
    }

    @Test
    @Tag("8")
    fun compareTo() {
        assertTrue(UnsignedBigInteger(123456789) < UnsignedBigInteger("9876543210"))
        assertTrue(UnsignedBigInteger("9876543210") > UnsignedBigInteger(123456789))
    }

    @Test
    @Tag("8")
    fun toInt() {
        assertEquals(123456789, UnsignedBigInteger("123456789").toInt())
    }

    @Test
    fun testHashCode() {
        assertEquals(UnsignedBigInteger("12345").hashCode(), UnsignedBigInteger(12345).hashCode())
        assertEquals(UnsignedBigInteger("12345").hashCode(), UnsignedBigInteger("12345").hashCode())
        assertEquals(
            UnsignedBigInteger("12345678900").hashCode(),
            UnsignedBigInteger("12345678900").hashCode()
        )
        assertNotEquals(UnsignedBigInteger("123456").hashCode(), UnsignedBigInteger(920).hashCode())
        assertNotEquals(UnsignedBigInteger("123456").hashCode(), UnsignedBigInteger("892").hashCode())
    }
}