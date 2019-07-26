package org.hibernate.tutorial.reference.repository;

import org.hibernate.tutorial.reference.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
