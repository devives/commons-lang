package com.devives.commons.lang;

import com.devives.commons.lang.function.FailableProcedure;
import com.devives.commons.lang.function.Procedure;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility methods for working with objects that may implement {@link AutoCloseable}.
 */
public final class AutoCloseableUtils {

    private AutoCloseableUtils() {
    }

    /**
     * Closes the specified object when it implements {@link AutoCloseable}.
     *
     * <p>If the object does not implement {@code AutoCloseable}, this method does nothing.
     * Exceptions thrown by {@link AutoCloseable#close()} are rethrown using
     * {@link ExceptionUtils#passChecked}.</p>
     *
     * @param object object that may need to be closed
     */
    public static void closeIfAutoCloseable(Object object) {
        if (object instanceof AutoCloseable) {
            ExceptionUtils.passChecked(((AutoCloseable) object)::close);
        }
    }


    /**
     * Closes the specified objects when they implements {@link AutoCloseable}.
     *
     * <p>If the object does not implement {@code AutoCloseable}, this method does nothing.
     * Exceptions thrown by {@link AutoCloseable#close()} are rethrown using
     * {@link ExceptionUtils#passChecked}.</p>
     *
     * @param object object that may need to be closed
     */
    public static void closeIfAutoCloseable(Object... object) {
        ExceptionUtils.collectAndThrow(Stream.of(object)
                .filter(AutoCloseable.class::isInstance)
                .map(AutoCloseable.class::cast)
                .<FailableProcedure>map(it -> () -> ExceptionUtils.passChecked(it::close))
                .toArray(FailableProcedure[]::new)
        );
    }


}
