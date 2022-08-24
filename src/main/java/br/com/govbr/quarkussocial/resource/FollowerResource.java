package br.com.govbr.quarkussocial.resource;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.govbr.quarkussocial.domain.Follower;
import br.com.govbr.quarkussocial.domain.User;
import br.com.govbr.quarkussocial.dto.CreateFollowerRequest;
import br.com.govbr.quarkussocial.dto.FollowerResponse;
import br.com.govbr.quarkussocial.dto.FollowersPerUserResponse;
import br.com.govbr.quarkussocial.repositories.FollowerRepositorie;
import br.com.govbr.quarkussocial.repositories.UserRepositorie;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Seguidores", description = "Endpoints relacionados a operações referente a entidade Followers.")
public class FollowerResource {

	@Inject
	private UserRepositorie userRepository;
	
	@Inject
	private FollowerRepositorie repository;
	
	@PUT
	@Transactional
	@Operation(summary = "Cria uma relação de seguir outro usuário.")
	public Response followerUser(@PathParam("userId") Long userId, CreateFollowerRequest request ) {
		
//		Verifica se o usuario e o seguidor são o mesmo
		if(userId.equals(request.getFollowerId())) {
			return Response.status(Response.Status.CONFLICT).entity("Não é possivel seguir você mesmo!").build();
		}
		
		User user = userRepository.findById(userId);

		if (user == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		User userFollower = userRepository.findById(request.getFollowerId());
		
//		Verifica se o seguidor, já segue o usuário
		boolean follows = repository.follows(userFollower, user);
		
		if(!follows) {
			Follower fol = new Follower();
			
			fol.setUser(user);
			fol.setFollower(userFollower);
			
			repository.persist(fol);
		}
		return Response.status(Status.NO_CONTENT).build();
	}
	
	@GET
	public Response listFollower(@PathParam("userId") Long userId) {
		
		User user = userRepository.findById(userId);
		if (user == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
        var list = repository.findByUser(userId);
        
        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowersCount(list.size());

        var followerList = list.stream().map( FollowerResponse::new ).collect(Collectors.toList());

        responseObject.setResponse(followerList);
        return Response.ok(responseObject).build();
	}
	
	@DELETE
	@Transactional
	public Response unfollowUser(@PathParam("userId") Long userId,@QueryParam("followerId") Long followerId) {
		User user = userRepository.findById(userId);
		
		if(user == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		repository.deleteByFollowerAndUser(followerId, userId);
		
		return Response.status(Response.Status.NO_CONTENT).build();
	}
	

	
}
