package cz.muni.acon.convertor;

import java.util.Objects;

/**
 *
 * @author Jan Koscak
 */
public class Property {
    
    private final String key;
    
    private final Object object;
    
    public Property(String key, Object object) {
        this.key = key;
        this.object = object;
    }

    public String getPropertyKey() {
        return this.key;
    }
    
    public Object getPropertyValue() {
        return this.object;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Property other = (Property) obj;
        if (!(this.key.equals(other.getPropertyKey())) || !(this.object.equals(other.getPropertyValue()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.key);
        hash = 13 * hash + Objects.hashCode(this.object);
        return hash;
    }
    
    @Override
    public String toString() {
        return this.getPropertyKey() + " " + this.getPropertyValue();
    }
}
