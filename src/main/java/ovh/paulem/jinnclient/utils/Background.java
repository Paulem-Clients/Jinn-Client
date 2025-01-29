package ovh.paulem.jinnclient.utils;

public enum Background {
    WATER("water");

    final String name;

    Background(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
