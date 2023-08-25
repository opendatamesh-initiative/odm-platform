package org.opendatamesh.platform.core.dpds.model;

public enum EntityTypeDPDS {
    dataproduct, 
    inputport, 
    outputport, 
    discoveryport, 
    controlport, 
    observabilityport, 
    application, 
    infrastructure,
    domain,
    template;


    public static EntityTypeDPDS get(String entityTypeName) {
        EntityTypeDPDS result = null;
        try {
            result = EntityTypeDPDS.valueOf(entityTypeName);
        } catch(Exception e) {}
        return result;
    }

    public boolean isPort(){
        return this.equals(inputport) || this.equals(outputport) || this.equals(discoveryport) 
            || this.equals(controlport) || this.equals(observabilityport);
    }

    public boolean isComponent(EntityTypeDPDS e){
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
            case template:
                componentContainerPropertyName = "infrastructuralComponents";
                break;
            default:
                throw new RuntimeException("[" + this + "] is not a valid component type");
        }

        return componentContainerPropertyName;
    }

}
