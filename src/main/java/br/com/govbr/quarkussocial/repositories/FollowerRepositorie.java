package br.com.govbr.quarkussocial.repositories;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import br.com.govbr.quarkussocial.domain.Follower;
import br.com.govbr.quarkussocial.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

@ApplicationScoped
public class FollowerRepositorie implements PanacheRepository<Follower> {

	public boolean follows(User follower, User user) {
//		Map<String, Object> params = new HashMap<>();
//		params.put("followers", follower);
//		params.put("user", user);
//		Para passar mais de um parametro na querie tem que ser atravez do objeto MAP
		var params = Parameters.with("follower", follower).and("user", user).map();
		
//		Faz a querie passando os parametros
		PanacheQuery<Follower> query = find("follower =:follower and user = :user", params);
//		Busca o primeiro resultado da querie se houver.
		Optional<Follower> result = query.firstResultOptional();
//		Retorna true se houver algum retorno do banco
		return result.isPresent();
	}
	
	public List<Follower> findByUser(Long userId){
		PanacheQuery<Follower> query = find("user.id", userId);
		return query.list();
	}

	public void deleteByFollowerAndUser(Long followerId, Long userId) {
		var params = Parameters.with("followerId", followerId).and("userId", userId).map();
		
		delete("follower.id =:followerId and user.id =:userId", params);
	}
}
