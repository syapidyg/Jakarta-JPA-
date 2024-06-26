

package com.bookstore.business.bll.catalogmngmt;

import com.bookstore.business.persistence.catalog.Publisher;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 *
 * service local de gestion des éditeurs (vue sans interface)
 */
@Stateless(name="PublisherManager")//nom EJB du session bean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@LocalBean
public class PublisherManagerServiceBean{

    @PersistenceContext(unitName = "bsPU")
    private EntityManager em;

    /**
     *
     * Sauvegarder un éditeur
     * @param publisher éditeur nouvellement créé
     * @return éditeur managé par le contexte de persistance
     */
    public Publisher savePublisher(Publisher publisher) {
        em.persist(publisher);
        return publisher;
    }

    /**
     *
     * Trouver un éditeur en fonction de son id
     * @param publisherId identité de l'éditeur à retrouver en base
     * @return l'éditeur recherché
     */
    public Publisher findPublisherById(Long publisherId) {
        CriteriaBuilder criteria = em.getCriteriaBuilder();
        CriteriaQuery<Publisher> criteriaQuery = criteria.createQuery(Publisher.class);
        Root root = criteriaQuery.from(Publisher.class);
        Predicate predicate = criteria.equal(root.get("id"), publisherId);
        criteriaQuery.where(predicate);
        return em.createQuery(criteriaQuery).getSingleResult();
    }


}
