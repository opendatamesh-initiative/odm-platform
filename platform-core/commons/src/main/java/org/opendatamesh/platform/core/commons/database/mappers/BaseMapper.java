package org.opendatamesh.platform.core.commons.database.mappers;

public interface BaseMapper<R, T> {
    T toEntity(R resource);

    R toRes(T entity);
}
