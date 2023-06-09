package fr.boul2gom.cerberus.api.utils.types;

public record Pair<F, S>(F first, S second) {

    public boolean equals(Pair<F, S> pair) {
        return this.first.equals(pair.first) && this.second.equals(pair.second);
    }

    public boolean equals(F first, S second) {
        return this.first.equals(first) && this.second.equals(second);

    }

    public record Double<T>(T first, T second) {

        public boolean equals(Pair.Double<T> pair) {
            return this.first.equals(pair.first) && this.second.equals(pair.second);
        }

        public boolean equals(T first, T second) {
            return this.first.equals(first) && this.second.equals(second);
        }
    }
}
