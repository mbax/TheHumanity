package org.royaldev.thehumanity.util;

public class Pair<L, R> {

    private L left;
    private R right;

    public Pair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return this.left;
    }

    public void setLeft(final L left) {
        this.left = left;
    }

    public R getRight() {
        return this.right;
    }

    public void setRight(final R right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("Pair{left = %s, right = %s}", this.getLeft(), this.getRight());
    }
}
