/*
 * Copyright 2022-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendatamesh.platform.core.dpds.model;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of DPDS entity types.
 *
 * @author Andrea Gioia
 * @see <a href="https://dpds.opendatamesh.org/resources/specifications/1.0.0-DRAFT/#object-types">DPDS Object Types</a>
 */
public enum EntityTypeDPDS {

    DATAPRODUCT("dataproduct", "dataproducts","dataProducts"),

    INPUTPORT("inputport", "inputports","inputPorts"),
    OUTPUTPORT("outputport", "outputports","outputPorts"),
    DISCOVERYPORT("discoveryport", "discoveryports","discoveryPorts"),
    CONTROLPORT("controlport", "controlports","controlPorts"),
    OBSERVABILITYPORT("observabilityport", "observabilityports","observabilityPorts"),

    APPLICATION("application", "applications","applicationComponents"),
    INFRASTRUCTURE("infrastructure", "infrastructures","infrastructuralComponents"),

	API("api", "apis","apis"),
    TEMPLATE("template", "templates","templates");

    private static final EntityTypeDPDS[] VALUES;
	public static final List<EntityTypeDPDS> PORTS;
	public static final List<EntityTypeDPDS> COMPONENTS;
	public static final List<EntityTypeDPDS> EXTERNAL_COMPONENTS;

	static {
		VALUES = values();
		PORTS = Arrays.asList(INPUTPORT, OUTPUTPORT, DISCOVERYPORT, CONTROLPORT, OBSERVABILITYPORT);
		COMPONENTS = Arrays.asList(INPUTPORT, OUTPUTPORT, DISCOVERYPORT, CONTROLPORT, OBSERVABILITYPORT, APPLICATION, INFRASTRUCTURE, API, TEMPLATE);
		EXTERNAL_COMPONENTS = Arrays.asList(API, TEMPLATE);
	}

    private final String propertyValue;
    private final String collectionName;
    private final String groupingPropertyName;


    private EntityTypeDPDS(String propertyValue, String collectionName, String groupingPropertyName) {
        this.propertyValue = propertyValue;
        this.collectionName = collectionName;
        this.groupingPropertyName = groupingPropertyName;
    }

    /**
	 * Return the value of this entity type to be used as entityType property value in json/yaml documents.
	 */
	@JsonValue
    public String propertyValue() {
        return propertyValue;
    }

    /**
	 * Return the collection name of this entity type to be used in rest api uri.
	 */
    public String collectionName() {
        return collectionName;
    }

    /**
	 * Return the property name of this entity type to be used for the array property that group entities of the this type in in json/yaml documents (ex "inputPorts": [...]).
	 */
    public String groupingPropertyName() {
        return groupingPropertyName;
    }

	/**
	 * Return a string representation of this entity type.
	 */
	@Override
	public String toString() {
		return propertyValue;
	}

    /**
	 * Whether this entity type is a port
	 * {@link org.opendatamesh.platform.core.dpds.model.PortDPDS}.
	 */
    public boolean isPort(){
		return PORTS.contains(this);
    }

    /**
	 * Whether this entity type is a component
	 * {@link org.opendatamesh.platform.core.dpds.model.ComponentDPDS}.
	 */
    public boolean isComponent(){
        return COMPONENTS.contains(this);
    }

	/**
	 * Whether this entity type is an external component
	 * {@link org.opendatamesh.platform.core.dpds.model.ComponentDPDS}.
	 */
    public boolean isExternalComponent(){
        return EXTERNAL_COMPONENTS.contains(this);
    }

    /**
	 * Return the {@code EntityTypeDPDS} enum constant with the specified property value.
	 * @param label the property value of the enum to be returned
	 * @return the enum constant with the specified property value
	 * @throws IllegalArgumentException if this enum has no constant for the specified property value
	 */
	public static EntityTypeDPDS valueOfPropertyValue(String propertyValue) {

		EntityTypeDPDS type = resolvePropertyValue(propertyValue);
		if (type == null) {
			throw new IllegalArgumentException("No matching constant for [" + propertyValue + "]");
		}
		return type;
	}

    /**
	 * Resolve the given type property value to an {@code EntityTypeDPDS}, if possible.
	 * @param statusCode the entity type property value
	 * @return the corresponding {@code EntityTypeDPDS}, or {@code null} if not found
	 */
	public static EntityTypeDPDS resolvePropertyValue(String propertyValue) {
		// Use cached VALUES instead of values() to prevent array allocation.
		for (EntityTypeDPDS type : VALUES) {
			if (type.propertyValue.equals(propertyValue)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Resolve the given type property grouping name to an {@code EntityTypeDPDS}, if possible.
	 * @param statusCode the entity type property grouping name
	 * @return the corresponding {@code EntityTypeDPDS}, or {@code null} if not found
	 */
	public static EntityTypeDPDS resolveGroupingPropertyName(String groupingPropertyName) {
		// Use cached VALUES instead of values() to prevent array allocation.
		for (EntityTypeDPDS type : VALUES) {
			if (type.groupingPropertyName.equals(groupingPropertyName)) {
				return type;
			}
		}
		return null;
	}
}
