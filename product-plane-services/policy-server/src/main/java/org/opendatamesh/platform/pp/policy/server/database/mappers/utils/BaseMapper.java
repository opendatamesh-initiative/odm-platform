package org.opendatamesh.platform.pp.policy.server.database.mappers.utils;

public interface BaseMapper<R, T> {
    T toEntity(R resource);

    R toRes(T entity);
}
