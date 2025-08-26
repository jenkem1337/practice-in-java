package Shared;

import org.HibernateExamples.Event.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;

public class HibernateTestBase {
    public static SessionFactory sessionFactory;
    public Transaction tx;
    public Session session;
    @BeforeAll
    static void setUpSessionFactory(){
        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .build();

        try {
            sessionFactory =
                    new MetadataSources(registry)
                            .addAnnotatedClass(Event.class)
                            .buildMetadata()
                            .buildSessionFactory();

        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
    @BeforeEach
    void setUpSessionAndTransaction(){
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
    }

    @AfterAll
    static void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @AfterEach
    void afterEach(TestInfo testInfo) {
        boolean rollbackEnabled = testInfo.getTestMethod()
                .map(m -> m.isAnnotationPresent(Rollback.class))
                .orElse(false);

        if (rollbackEnabled && tx != null && tx.isActive()) {
            tx.rollback();
        } else if (tx != null && tx.isActive()) {
            tx.commit();
        }

        if (session != null) {
            session.close();
        }
    }

}
