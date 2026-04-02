package userservice.dao.impl;

import userservice.config.HibernateUtil;
import userservice.constants.Messages;
import userservice.dao.UserDao;
import userservice.entity.UserEntity;
import userservice.exception.DataAccessException;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.Optional;

/**
 * Реализация DAO для работы с сущностью пользователя через Hibernate.
 */
public class UserDaoImpl implements UserDao {
    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(UserEntity userEntity) {
        try {
            return HibernateUtil.executeInTransaction(session -> {
                session.persist(userEntity);
                return userEntity.getId();
            });
        } catch (ConstraintViolationException e) {
            throw new DataAccessException(Messages.DB_CONSTRAINT_FAILED, e);
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.SAVE_DB_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserEntity> findById(Long id) {
        try {
            return HibernateUtil.executeWithoutTransaction(
                    session -> Optional.ofNullable(session.get(UserEntity.class, id))
            );
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.FIND_BY_ID_DB_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserEntity> findByEmail(String email) {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    session.createQuery(
                                    "select u from UserEntity u where u.email = :email", UserEntity.class)
                            .setParameter("email", email)
                            .uniqueResultOptional()
            );
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.FIND_BY_EMAIL_DB_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEntity> findAll() {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    session.createQuery("select u from UserEntity u order by u.id", UserEntity.class)
                            .getResultList()
            );
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.FIND_ALL_DB_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    session.createSelectionQuery("select count(u.id) from UserEntity u", Long.class)
                            .getSingleResult()
            );
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.COUNT_DB_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Long id) {
        try {
            return HibernateUtil.executeWithoutTransaction(session ->
                    session.createSelectionQuery(
                                    "select count(u.id) from UserEntity u where u.id = :id", Long.class)
                            .setParameter("id", id)
                            .getSingleResult() > 0
            );
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.FIND_BY_ID_DB_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserEntity update(UserEntity userEntity) {
        try {
            return HibernateUtil.executeInTransaction(session -> session.merge(userEntity));
        } catch (ConstraintViolationException e) {
            throw new DataAccessException(Messages.DB_CONSTRAINT_FAILED, e);
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.UPDATE_DB_FAILED, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteById(Long id) {
        try {
            return HibernateUtil.executeInTransaction(session -> {
                UserEntity userEntity = session.get(UserEntity.class, id);
                if (userEntity == null) {
                    return false;
                }
                session.remove(userEntity);
                return true;
            });
        } catch (RuntimeException e) {
            throw new DataAccessException(Messages.DELETE_DB_FAILED, e);
        }
    }
}