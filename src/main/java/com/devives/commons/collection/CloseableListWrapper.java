package com.devives.commons.collection;

import com.devives.commons.lang.ExceptionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class CloseableListWrapper<E> extends AbstractListWrapper<E> implements CloseableList<E> {

    /**
     * Массив, объектов, которые необходимо закрыть при закрытии текущего экземпляра.
     */
    private final AutoCloseable[] autoCloseableArray_;
    /**
     * Флаг, указывающий, что экземпляр открыт.
     */
    private boolean closed_ = false;

    /**
     * @param list
     * @param autoCloseable Массив, объектов, которые необходимо закрыть при закрытии текущего экземпляра.
     */
    CloseableListWrapper(List<E> list, AutoCloseable... autoCloseable) {
        super(list);
        autoCloseableArray_ = Objects.requireNonNull(autoCloseable);
    }

    /**
     * Возвращает флаг открытости списка.
     *
     * @return true, если список открыт, иначе false.
     */
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        List<Exception> exceptions = new ArrayList<>();
        for (AutoCloseable closeable : autoCloseableArray_) {
            try {
                closeable.close();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        ExceptionUtils.throwCollected(exceptions);
    }

}
