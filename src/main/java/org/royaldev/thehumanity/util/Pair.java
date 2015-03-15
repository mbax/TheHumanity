package org.royaldev.thehumanity.util;

/**
 * A pair of objects.
 *
 * @param <L> Left type
 * @param <R> Right type
 */
public class Pair<L, R> {

    /**
     * The left object.
     */
    private L left;
    /**
     * The right object.
     */
    private R right;

    /**
     * Constructs a pair of objects.
     *
     * @param left  The left object
     * @param right The right object
     */
    public Pair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Gets the left object.
     *
     * @return Left object
     */
    public L getLeft() {
        return this.left;
    }

    /**
     * Sets the left object.
     *
     * @param left New left object
     */
    public void setLeft(final L left) {
        this.left = left;
    }

    /**
     * Gets the right object.
     *
     * @return Right object
     */
    public R getRight() {
        return this.right;
    }

    /**
     * Sets the right object.
     *
     * @param right New right object
     */
    public void setRight(final R right) {
        this.right = right;
    }

    /**
     * Returns a textual representation of this pair, in which each object has its toString() method called.
     *
     * @return Textual representation
     */
    @Override
    public String toString() {
        return String.format("Pair{left = %s, right = %s}", this.getLeft(), this.getRight());
    }
}
