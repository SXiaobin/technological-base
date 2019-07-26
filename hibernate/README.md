# How Tos

## How to use One to Many mappings

To decide between bidirectional and unidirectional mappings, you should think whether the entities have a tight coupling or not

Code Example:

- [Unidirectional mappings](https://github.com/SXiaobin/technological-base/blob/master/hibernate/hibernate-tutorials/basic/src/main/java/org/hibernate/tutorial/reference/package-info.java) 

**References:**

<!--The blog explains very clear that why he thinks the best way to model a one-to-many relationship is to use just @ManyToOne annotation on the child entity. -->

- [JPA / Hibernate One to Many Mapping Example with Spring Boot](https://www.callicoder.com/hibernate-spring-boot-jpa-one-to-many-mapping-example/)

<!--This blog explains what is the different between two ways to define the Owning Side in relation ship-->

- [Hibernate One to Many Annotation Tutorial](https://www.baeldung.com/hibernate-one-to-many)

## How to map a @ManyToOne association using a non-Primary Key colum

In a One-to-Many/Many-to-One relationship, **the owning side is usually defined on the ‘many’ side of the relationship.** It’s usually the side which owns the foreign key. Many side column default referring to the primary attribute of One side. If we want let Many side references to non-Primary Key column on One side, then it comes to : 

> when using a non-Primary Key association, the `referencedColumnName` should be used to instruct Hibernate which column should be used on the parent side to establish the many-to-one database relationship.

```java
@Entity(name = "Publication")
@Table(name = "publication")
public class Publication {
 
    @Id
    @GeneratedValue
    private Long id;
 
    private String publisher;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "isbn",
        referencedColumnName = "isbn"
    )
    private Book book;
 
    @Column(
        name = "price_in_cents",
        nullable = false
    )
    private Integer priceCents;
 
    private String currency;
 
    //Getters and setters omitted for brevity
}
```

**References:**

- [How to map a @ManyToOne association using a non-Primary Key column with JPA and Hibernate](https://vladmihalcea.com/how-to-map-a-manytoone-association-using-a-non-primary-key-column/)

- [JPA: default column name mapping for @ManyToOne relations](https://stackoverflow.com/questions/3964059/jpa-default-column-name-mapping-for-manytoone-relations)