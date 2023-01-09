package repository;

import domain.Entity;

import java.util.Optional;

public interface Repository<ID, E extends Entity<ID>> {

    /** Finds all the Entities.
     * @return all entities
     */
    Iterable<E> findAll();

    /** Saves an domain.Entity.
     * @param entity
     *         entity must be not null
     * @return an {@code Optional} - null if the entity was saved,
     *                             - the entity (id already exists)
     */
    Optional<E> save(E entity);

    /** Deletes an domain.Entity.
     *  removes the entity with the specified id
     * @param id
     *      id must be not null
     * @return an {@code Optional}
     *            - null if there is no entity with the given id,
     *            - the removed entity, otherwise
     */
    Optional<E> delete(ID id);
}
