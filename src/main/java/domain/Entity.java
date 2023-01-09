package domain;

import java.io.Serializable;
import java.util.Objects;
public class Entity<ID> implements Serializable {

    private ID id;

    /** Gets the od of the entity
     *
     * @return the of the function.
     */
    public ID getId() {
        return id;
    }

    /** Sets the id of the funcit
     *
     * @param id the new id.
     */
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity<?> entity = (Entity<?>) o;
        return getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
