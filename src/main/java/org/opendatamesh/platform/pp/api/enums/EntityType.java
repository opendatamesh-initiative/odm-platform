package org.opendatamesh.platform.pp.api.enums;

public enum EntityType {
    dataproduct, 
    inputport, 
    outputport, 
    discoveryport, 
    controlport, 
    observabilityport, 
    application, 
    infrastructure;


    public static EntityType get(String entityTypeName) {
        EntityType result = null;
        try {
            result = EntityType.valueOf(entityTypeName);
        } catch(Exception e) {}
        return result;
    }

    public boolean isPort(){
        return this.equals(inputport) || this.equals(outputport) || this.equals(discoveryport) 
            || this.equals(controlport) || this.equals(observabilityport);
    }

    public boolean isComponent(EntityType e){
        return isPort() || this.equals(application) || this.equals(infrastructure);
    }

    public String getComponentContainerPropertyName(){
        String componentContainerPropertyName = null;
        
        switch(this){
            case inputport:
                componentContainerPropertyName = "inputPorts";
                break;
            case outputport:
                componentContainerPropertyName = "outputPorts";
                break;
            case discoveryport:
                componentContainerPropertyName = "discoveryPorts";
                break;
            case observabilityport:
                componentContainerPropertyName = "observabilityPorts";
                break;
            case controlport:
                componentContainerPropertyName = "controlPorts";
                break;
            case application:
                componentContainerPropertyName = "applicationComponents";
                break;
            case infrastructure:
                componentContainerPropertyName = "infrastructuralComponents";
                break;
        }

        return componentContainerPropertyName;
    }

}
