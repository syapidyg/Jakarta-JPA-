
package com.bookstore.business.bll.catalogmngmt;

import com.bookstore.business.persistence.catalog.Book;
import java.util.List;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Service local de gestion des livres exposant une vue sans interface<br>
 * Composant basé sur le pattern Session facade.<br>
 * composant fine grained chargé des opérations CRUD relatives à la gestion des livres.<br>
 * accessible qu'en local - qu'au sein de la JVM où ce code s'exécute.<br>
 * Attribut transactionnel MANDATORY spécifié sur la classe : 
 * toutes les méthodes ne spécifiant pas explicitement un autre attribut transactionnel 
 * devront être invoquées dans un contexte transactionnel initié par un appelant.<br>
 */
@Stateless(name="BookManager")
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@LocalBean //facultatif
public class BookManagerServiceBean {

    @PersistenceContext(unitName="bsPU")
    private EntityManager em;

    @PreDestroy
    void prevent(){
        System.out.println("instance va être détruite");
    }

    /**
     *
     * @param book livre à persister
     * sauvegarder en base l'état d'un livre nouvellement créé
     * @return le livre persisté
     */
    public Book saveBook(Book book){
        em.persist(book);
        return book;
    }

    /**
     * Retourner un livre en fonction d'une identité unique
     * @param bookId id livre recherché
     * @return le livre correspondant à l'id passée en argument
     */
    public Book findBookById(Long bookId) {
        Book book = em.find(Book.class, bookId);
        return book;
    }

    /**
     *
     * supprimer un livre
     * @param book le livre a supprimé. Si book est null, l'opération de suppression n'est pas exécutée
     */
    public void deleteBook(Book book) {
        em.remove(book);
    }

    /**
     * retourner une liste de livres dont le titre contient l'argument passé en paramètre.
     * Comportement transactionnel redéfini (SUPPORTS)
     * @param pattern motif permettant de retrouver une liste de livres
     * contenant le motif dans le titre
     * @return la liste des livres possédant le motif dans le titre
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS) //méthode pouvant joindre le contexte transactionnel de l'appelant
    public List<Book> findByCriteria(String pattern) {
        CriteriaBuilder criteria = em.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteria.createQuery(Book.class);
        Root root = criteriaQuery.from(Book.class);
        Predicate predicate = criteria.like(root.get("title"), pattern);
        criteriaQuery.where(predicate);
        return em.createQuery(criteriaQuery).getResultList();

    }

    /**
     * modifier un livre et synchroniser l'état modifié avec la base.
     * Le comportement transactionnel de la méthode est redéfini. l'attribut transactionnel est REQUIRED.
     * Ainsi l'opération de mise à jour nécessitant un contexte transactionnel peut être invoquée depuis un bean CDI ne s'exécutant pas dans une transaction.
     * @param book livre existant (dans la base) à modifier
     * @return le livre modifié
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Book updateBook(Book book) {
        Book bookToUpdate = em.merge(book);
        return bookToUpdate;
    }

}
