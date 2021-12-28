import java.time.Instant

interface Clock {
    fun now(): Instant
}

class NormalClock : Clock {
    override fun now(): Instant {
        return Instant.now()
    }
}

class SetableClock(private var now: Instant) : Clock {
    override fun now(): Instant {
        return now
    }

    fun setNow(now: Instant) {
        this.now = now
    }
}
