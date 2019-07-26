/**
 * Defining the Domain Models
 * In this section, we’ll define the domain models of our application - Post and Comment.
 * <p>
 * Note that both Post and Comment entities contain some common auditing related fields like created_at and updated_at.
 * <p>
 * We’ll abstract out these common fields in a separate class called AuditModel and extend this class in the Post and Comment entities.
 * <p>
 * We’ll also use Spring Boot’s <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.auditing">JPA Auditing</a>
 * feature to automatically populate the created_at and updated_at fields while persisting the entities.
 * <p></p>
 * In #AuditModel we’re using Spring Boot’s AuditingEntityListener to automatically populate the createdAt and updatedAt fields.
 * <p></p>
 * Enabling JPA Auditing
 * To enable JPA Auditing, you’ll need to add @EnableJpaAuditing annotation to one of your configuration classes.
 * Open the main class JpaOneToManyDemoApplication.java and add the @EnableJpaAuditing to the main class like so -
 */
package org.hibernate.tutorial.reference.model;