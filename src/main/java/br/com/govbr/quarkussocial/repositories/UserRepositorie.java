package br.com.govbr.quarkussocial.repositories;

import javax.enterprise.context.ApplicationScoped;

import br.com.govbr.quarkussocial.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserRepositorie implements PanacheRepository<User> {

}
