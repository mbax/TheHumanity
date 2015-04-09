package org.royaldev.thehumanity.util;

import org.jetbrains.annotations.NotNull;

public interface Snapshottable<R> {

    @NotNull
    R takeSnapshot();

}
