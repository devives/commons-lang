package com.devives.commons.collection;

import com.devives.commons.collection.store.BufferController;
import com.devives.commons.lang.ExceptionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class CloseableBufferedListWrapper<E> extends AbstractListWrapper<E> implements CloseableBufferedList<E> {

    /**
     * Массив, объектов, которые необходимо закрыть при закрытии текущего экземпляра.
     */
    private final AutoCloseable[] autoCloseableArray_;
    /**
     * Флаг, указывающий, что экземпляр открыт.
     */
    private boolean closed_ = false;

    CloseableBufferedListWrapper(BufferedList<E> delegate, AutoCloseable... autoCloseable) {
        super(delegate);
        autoCloseableArray_ = autoCloseable;
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

    @Override
    public BufferController getBufferController() {
        return ExceptionUtils.passChecked(() -> unwrap(BufferedList.class).getBufferController());
    }
}
