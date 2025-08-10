package com.devives.commons.collection;

import com.devives.commons.collection.store.BufferController;

public class AbstractBufferedListWrapper<E> extends AbstractListWrapper<E> implements BufferedList<E> {


    public AbstractBufferedListWrapper(BufferedList<E> delegate) {
        super(delegate);
    }

    @Override
    public BufferController getBufferController() {
        return ((BufferedList) delegate_).getBufferController();
    }
}
