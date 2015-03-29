package org.royaldev.thehumanity.util;

import java.util.Comparator;
import java.util.Map;

public class DescendingValueComparator<T> implements Comparator<T> {

    private final Map<T, ? extends Number> base;

    public DescendingValueComparator(final Map<T, ? extends Number> base) {
        this.base = base;
    }

    @Override
    public int compare(final T o1, final T o2) {
        return this.base.get(o1).longValue() >= this.base.get(o2).longValue() ? -1 : 1;
    }
}
