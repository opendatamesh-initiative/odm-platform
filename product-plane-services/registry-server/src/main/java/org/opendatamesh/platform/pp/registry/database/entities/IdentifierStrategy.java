package org.opendatamesh.platform.pp.registry.database.entities;

import java.util.UUID;

import org.springframework.util.StringUtils;

public class IdentifierStrategy {

    public static IdentifierStrategy DEFUALT = new IdentifierStrategy();

    private IdentifierStrategy() {}

    public String getId(String fqn) {
        String id = null;
        if(StringUtils.hasText(fqn)) {
            id = UUID.nameUUIDFromBytes(fqn.getBytes()).toString();
        } else {
            throw new RuntimeException("Fully qualified name is empty");
        }
        return id;
    }
}
