import java.time.Instant
import java.time.temporal.ChronoUnit.HOURS

interface EventsStatistics {
    fun incEvent(name: String)
    fun getEventStatisticsByName(name: String): Double
    fun getAllEventStatistic(): Map<String, Double>
    fun printStatistic()
}

data class StatisticsEvent(val name: String, val timestamp: Instant)

class EventsStatisticsImpl(private val clock: Clock) : EventsStatistics {

    private val events = ArrayDeque<StatisticsEvent>()
    private val statisticMap = mutableMapOf<String, Int>()

    override fun incEvent(name: String) {
        clearExpiredEvents()
        statisticMap.compute(name) { _, count -> (count ?: 0) + 1 }
        events.addLast(StatisticsEvent(name, clock.now()))
    }

    override fun getEventStatisticsByName(name: String): Double {
        clearExpiredEvents()
        return statisticMap.getOrDefault(name, 0).toDouble() / MINUTES_IN_HOUR
    }

    override fun getAllEventStatistic(): Map<String, Double> {
        clearExpiredEvents()
        return statisticMap.map { (name, _) -> Pair(name, getEventStatisticsByName(name)) }.toMap()
    }

    override fun printStatistic() = println(getAllEventStatistic())

    private fun clearExpiredEvents() {
        while (events.isNotEmpty() && shouldRemoveEvent(events.first())) {
            val name = events.removeFirst().name
            val count = statisticMap[name] ?: continue
            if (count <= 1) {
                statisticMap.remove(name)
            } else {
                statisticMap[name] = count - 1
            }
        }
    }

    private fun shouldRemoveEvent(event: StatisticsEvent) = HOURS.between(event.timestamp, clock.now()) >= 1

    companion object {
        private val MINUTES_IN_HOUR = HOURS.duration.toMinutes()
    }
}
