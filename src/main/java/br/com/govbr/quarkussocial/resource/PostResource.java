package br.com.govbr.quarkussocial.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.govbr.quarkussocial.domain.Post;
import br.com.govbr.quarkussocial.domain.User;
import br.com.govbr.quarkussocial.dto.CreatePostRequest;
import br.com.govbr.quarkussocial.dto.PostResponse;
import br.com.govbr.quarkussocial.repositories.FollowerRepositorie;
import br.com.govbr.quarkussocial.repositories.PostRepositorie;
import br.com.govbr.quarkussocial.repositories.UserRepositorie;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Postagens", description = "Endpoints relacionados a operações referente a entidade Post.")
public class PostResource {

	@Inject
	private PostRepositorie repository;
	@Inject
	private UserRepositorie userRepository;
	@Inject 
	private FollowerRepositorie followerRepositorie;
	
	@POST
	@Transactional
	public Response savePost(@PathParam("userId") Long userId, @RequestBody CreatePostRequest postRequest) {
		User user = userRepository.findById(userId);

		if (user == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		Post post = new Post();
		post.setTexto(postRequest.getTexto());
		post.setUser(user);
		repository.persist(post);
		
		return Response.status(Response.Status.CREATED).build();
	}

	@GET
	public Response listPost(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId) {
		User user = userRepository.findById(userId);

		if (user == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		if(followerId == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Id do seguidor está nulo, verifique o header da requisição.").build();
		}
		
		User follower = userRepository.findById(followerId);
		
		if (follower == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		if(!(followerRepositorie.follows(follower, user))){
			return Response.status(Response.Status.FORBIDDEN).entity("Você não pode ver esses posts").build();
		}
		
//		Querie para puxar as postagens por usuario. e ordenar do maior para o menor
		PanacheQuery<Post> query = repository.find("user",Sort.by("data", Sort.Direction.Descending) , user);
		List<Post> list = query.list();
		
//		Converte do tipo post, para PostResponse.
		List<PostResponse> responseList = list.stream().map(post -> PostResponse.fromEntity(post)).collect(Collectors.toList());
		
		return Response.ok(responseList).build();
	}
}
