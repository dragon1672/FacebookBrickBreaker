package utils;

/**
 * Holds onto multiple objects
 */
public class Tuple {

    /**
     * Holds onto 2 objects
     */
    public static class Tuple2<T1, T2> {
        private final T1 first;
        private final T2 second;

        private Tuple2(T1 first, T2 second) {
            this.first = first;
            this.second = second;
        }

        public static <T1, T2> Tuple2<T1, T2> of(T1 first, T2 second) {
            return new Tuple2<>(first, second);
        }

        public T2 getSecond() {
            return second;
        }

        public T1 getFirst() {
            return first;
        }
    }

    public static <T1, T2> Tuple2<T1, T2> of(T1 first, T2 second) {
        return Tuple2.of(first, second);
    }

    /**
     * Holds onto 2 objects
     */
    public static class Tuple3<T1, T2, T3> {
        private final T1 first;
        private final T2 second;

        private final T3 third;

        private Tuple3(T1 first, T2 second, T3 third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 first, T2 second, T3 third) {
            return new Tuple3<>(first, second, third);
        }

        public T2 getSecond() {
            return second;
        }

        public T1 getFirst() {
            return first;
        }

        public T3 getThird() {
            return third;
        }

    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 first, T2 second, T3 third) {
        return Tuple3.of(first, second, third);
    }

}
