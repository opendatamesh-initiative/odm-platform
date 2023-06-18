package org.opendatamesh.platform.core.dpds;

import java.net.URI;
import java.util.Objects;
import java.util.function.Predicate;

public class AuthorizationValue {
    private String value, type, keyName;
    private Predicate<URI> urlMatcher;

    public AuthorizationValue(String keyName, String value, String type) {
        this(keyName, value, type, url -> true);
    }

    public AuthorizationValue(String keyName, String value, String type, Predicate<URI> urlMatcher) {
        this.setKeyName(keyName);
        this.setValue(value);
        this.setType(type);
        this.setUrlMatcher(urlMatcher);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = Objects.requireNonNull(type, "type cannot be null").trim().toLowerCase();
        if(!(type.equals("query") || type.equals("header"))) {
            throw new RuntimeException("type cannot be equal to [" + type + "]. Admissible value for type are [query, header]");
        }
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public Predicate<URI> getUrlMatcher() {
        return urlMatcher;
    }

    public void setUrlMatcher(Predicate<URI> urlMatcher) {
        this.urlMatcher = Objects.requireNonNull(urlMatcher);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + urlMatcher.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AuthorizationValue other = (AuthorizationValue) obj;
        if (keyName == null) {
            if (other.keyName != null) {
                return false;
            }
        } else if (!keyName.equals(other.keyName)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        if (!urlMatcher.equals(other.urlMatcher)) {
            return false;
        }
        return true;
    }    
}
