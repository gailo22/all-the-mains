package com.gailo22;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Main55 {

	interface Meter {
		static Builder builder(String name, Type type) {
			return new Builder(name, type);
		}

		Id getId();

		enum Type { A, B }

		class Id {
			String name;
			Type type;
			Id(String name, Type type) {
				this.name = name;
				this.type = type;
			}
		}

		class Builder {
			String name;
			Type type;
			Builder(String name, Type type) {
				this.name = name;
				this.type = type;
			}
		}

	}

	interface Timer extends Meter {
        void record(Runnable f);
        <T> T record(Supplier<T> f);
        <T> T recordCallable(Callable<T> f) throws Exception;
        void record(long amount, TimeUnit unit);
        default Runnable wrap(Runnable f) {
            return () -> record(f);
        }
        default <T> Callable<T> wrap(Callable<T> f) {
            return () -> recordCallable(f);
        }
    }

    static abstract class AbstractMeter implements Meter {
        private final Meter.Id id;
        AbstractMeter(Id id) {
            this.id = id;
        }
        @Override
        public Id getId() {
            return id;
        }
    }

    static abstract class AbstractTimer extends AbstractMeter implements Timer {

	    final Clock clock;

	    AbstractTimer(Id id, Clock clock) {
            super(id);
            this.clock = clock;
        }

        @Override
        public <T> T record(Supplier<T> f) {
            final long s = clock.monotonicTime();
            try {
                return f.get();
            } finally {
                final long e = clock.monotonicTime();
                record(e - s, TimeUnit.NANOSECONDS);
            }
        }

        @Override
        public void record(Runnable f) {
            final long s = clock.monotonicTime();
            try {
                f.run();
            } finally {
                final long e = clock.monotonicTime();
                record(e - s, TimeUnit.NANOSECONDS);
            }
        }

        @Override
        public <T> T recordCallable(Callable<T> f) throws Exception {
            return null;
        }

        @Override
        public final void record(long amount, TimeUnit unit) {
        }
    }

    interface Clock {
        long monotonicTime();
    }

	interface MeterFilter {
		enum MeterFilterReply { DENY, NEUTRAL, ACCEPT }

		default Meter.Id map(Meter.Id id) {
			return id;
		}

		default MeterFilterReply accept(Meter.Id id) {
			return MeterFilterReply.NEUTRAL;
		}

		static MeterFilter accept(Predicate<Meter.Id> iff) {
            return new MeterFilter() {
				@Override
				public MeterFilterReply accept(Meter.Id id) {
					return iff.test(id) ? MeterFilterReply.ACCEPT : MeterFilterReply.NEUTRAL;
				}
			};
		}
	}

    static class StepTimer extends AbstractTimer {
        public StepTimer(Id id, Clock clock) {
            super(id, clock);
        }
    }

    static class PrometheusTimer extends AbstractTimer implements Timer {
        public PrometheusTimer(Id id, Clock clock) {
            super(id, clock);
        }
    }

    public static void main(String[] args) {
		
	}

}
