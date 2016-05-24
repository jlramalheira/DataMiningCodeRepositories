package dao;

import com.uaihebert.factory.EasyCriteriaFactory;
import com.uaihebert.model.EasyCriteria;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class Dao<T> {

    protected static EntityManager em = Persistence.createEntityManagerFactory("UP").createEntityManager();
    protected EasyCriteria<T> criteria;
    private Class classe;

    public Dao(Class classe) {
        this.classe = classe;
    }

    /**
     * Persiste um objeto no banco de dados e sincroniza com a memória.
     *
     * @param entidade Objeto a ser persistido
     */
    public void insert(T entidade) {
        if (entidade != null) {
            em.getTransaction().begin();
            em.persist(entidade);
            em.flush();
            em.getTransaction().commit();
        }
    }

    /**
     * Retorna um objeto do banco de dados. Se o objeto não for encontrado,
     * retorna null.
     *
     * @param id Identidade do Objeto
     * @return O objeto encontrado na memória.
     */
    public T get(Long id) {
        return (T) em.find(classe, id);
    }

    /**
     * Atualiza um objeto no banco de dados
     *
     * @param entidade Objeto a ser atualizado
     */
    public void update(T entidade) {
        em.getTransaction().begin();
        em.merge(entidade);
        em.getTransaction().commit();
    }

    /**
     * Remove um objeto no banco de dados
     *
     * @param entidade Objeto a ser removido
     */
    public void remove(Long id) {
        T entidade = get(id);
        if (entidade != null) {
            em.getTransaction().begin();
            em.remove(entidade);
            em.getTransaction().commit();
        }
    }

    /**
     * Lista todos os objetos persistidos no banco de dados.
     *
     * @param entidade Objeto a ser atualizado
     * @return Lista tipada dos objetos
     */
    public List<T> list() {
        criteria = newCriteria();
        return criteria.getResultList();
    }

    protected EasyCriteria<T> newCriteria(){
        return EasyCriteriaFactory.createQueryCriteria(em, this.classe);
    }

    public static EntityManager getEm() {
        return em;
    }
}
