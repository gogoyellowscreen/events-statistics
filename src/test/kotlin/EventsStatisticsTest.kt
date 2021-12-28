import org.junit.jupiter.api.BeforeEach
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import java.time.temporal.ChronoUnit.*

private const val EPS = 0.000001

class EventsStatisticsTest {
    private val clock = SetableClock(Instant.now())

    private var underTest = EventsStatisticsImpl(clock)

    @BeforeEach
    fun setUp() {
        clock.setNow(Instant.now())
        underTest = EventsStatisticsImpl(clock)
    }

    @Test
    fun `simple test`() {
        underTest.incEvent("a")
        underTest.incEvent("b")
        underTest.incEvent("a")

        val expected = mapOf(Pair("a", 2.0 / 60.0), Pair("b", 1.0 / 60.0))
        assertEquals(expected, underTest.getAllEventStatistic())
    }

    @Test
    fun `not presented event test`() {
        assertEquals(0.0, underTest.getEventStatisticsByName("a"), EPS)
    }

    @Test
    fun `all events expired test`() {
        underTest.incEvent("a")
        underTest.incEvent("b")
        clock.setNow(clock.now().plus(1, HOURS))
        clock.setNow(clock.now().plus(3, MINUTES))
        assertEquals(emptyMap(), underTest.getAllEventStatistic())
    }

    @Test
    fun `some event expired test`() {
        underTest.incEvent("a")
        clock.setNow(clock.now().plus(30, MINUTES))
        underTest.incEvent("b")
        clock.setNow(clock.now().plus(40, MINUTES))
        val expected = mapOf(Pair("b", 1.0 / 60))
        assertEquals(expected, underTest.getAllEventStatistic())
    }

    @Test
    fun `forget some entries of event test`() {
        underTest.incEvent("a")
        underTest.incEvent("a")
        clock.setNow(clock.now().plus(30, MINUTES))
        underTest.incEvent("a")
        underTest.incEvent("a")
        underTest.incEvent("b")
        underTest.incEvent("b")
        clock.setNow(clock.now().plus(40, MINUTES))
        assertEquals(2.0 / 60, underTest.getEventStatisticsByName("a"), EPS)
        assertEquals(2.0 / 60, underTest.getEventStatisticsByName("b"), EPS)
    }
}