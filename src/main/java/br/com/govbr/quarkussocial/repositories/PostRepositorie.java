package br.com.govbr.quarkussocial.repositories;

import javax.enterprise.context.ApplicationScoped;

import br.com.govbr.quarkussocial.domain.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PostRepositorie implements PanacheRepository<Post> {

}
