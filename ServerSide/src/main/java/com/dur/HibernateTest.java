package com.dur;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dur.dao.TestDao;
import com.dur.database.entity.TableName;

public class HibernateTest {
	private SessionFactory sessionFactory = null;
	
	TestDao testDao;
	
	@Autowired
    private ApplicationContext context;
	
	
	
	@Autowired
	public void setTestDao(TestDao testDao){
		this.testDao = testDao;
	}
	
	public TestDao getTestDao(){
		return testDao;
	}
	
	private void initSessionFactory(){
		Configuration config = new Configuration().configure("hibernate/hibernate.cfg.xml");
		// Build a Registry with our configuration properties
		StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
		// If you miss the below line then it will compiling about a missing dialect setting
		serviceRegistryBuilder.applySettings(config.getProperties());
		ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
		// create the session factory
		sessionFactory = config.buildSessionFactory(serviceRegistry);
	}
	
	private void persistTestData(TableName testObject) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.save(testObject);
		session.getTransaction().commit();
	}
	
	private void findTestData(int objectId) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		TableName object = (TableName)session.load(TableName.class, objectId);
		System.out.println(object.getText());
		session.getTransaction().commit();
	}
	
	private void removetData(int objectId) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		TableName object = (TableName)session.load(TableName.class, objectId);
		System.out.println(object.getText());
		session.delete(object);
		session.getTransaction().commit();
	}
	
	private void findAllTestData(int objectId) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		List<TableName> objects = session.createQuery("from TableName").list();
		session.getTransaction().commit();
		System.out.println("All objects:" + objects);
	}
	
	public static void main(String args[]){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring/application-context.xml");
		HibernateTest test = applicationContext.getBean(HibernateTest.class);
//		TestDao testDao = applicationContext.getBean(TestDao.class);
		test.getTestDao().printHello();
//		test.initSessionFactory();
//		TableName table = new TableName();
//		table.setId(1);
//		table.setNumber(23);
//		table.setText("Darek");
//		test.persistTestData(table);
//		test.removetData(1);
	}
}
