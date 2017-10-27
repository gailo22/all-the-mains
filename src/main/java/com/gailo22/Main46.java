package com.gailo22;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class Main46 {
	
	@FunctionalInterface
	public interface ServiceCall<Request, Response> {
		CompletionStage<Response> invoke(Request var1);
	}
	
	static class NotUsed {}
	static class Done {}
	
	static class User {
		private Long id;
		private String name;
		
		static Builder builder() {
			return new Builder();
		}
		
		static class Builder {
			User user = new User();
			
			public Builder id(Long id) {
				user.id = id;
				return this;
			}
			
			public Builder name(String name) {
				user.name = name;
				return this;
			}
			
			public User build() {
				return user;
			}
		}
	}
	
	static interface UserService {
		
		ServiceCall<NotUsed, Optional<User>> getUserById(Long id);
		ServiceCall<NotUsed, List<User>> getUsers();
		ServiceCall<User, Done> addUser();
		ServiceCall<User, Done> updateUsers();
		ServiceCall<NotUsed, User> deleteUser(Long id);
		
	}
	
	
	static class UserServiceImpl implements UserService {
		
		//UserRepository userRepository;
		//@Inject
		//UserServiceImpl(UserRepository userRepository) {
		//	this.userRepository = userRepository;
		//}

		@Override
		public ServiceCall<NotUsed, Optional<User>> getUserById(Long id) {
			return request -> {
				CompletionStage<Optional<User>> userFuture = null;
				// TODO:
				return userFuture;
			};
		}

		@Override
		public ServiceCall<NotUsed, List<User>> getUsers() {
			return request -> {
				CompletionStage<List<User>> userFutureList = null;
				// TODO:
				return userFutureList;
			};
		}

		@Override
		public ServiceCall<User, Done> addUser() {
			return user -> {
				CompletionStage<Done> doneFuture = null;
				// TODO:
				return doneFuture;
			};
		}

		@Override
		public ServiceCall<User, Done> updateUsers() {
			return user -> {
				CompletionStage<Done> doneFuture = null;
				// TODO:
				return doneFuture;
			};
		}

		@Override
		public ServiceCall<NotUsed, User> deleteUser(Long id) {
			return request -> {
				CompletionStage<User> userFuture = null;
	            User user = User.builder().id(id).build();
	            // TODO:
	            return userFuture;
	        };
		}
		
	}
	
	public static void main(String[] args) {
		
		UserService userService = new UserServiceImpl();
		
		ServiceCall<NotUsed, Optional<User>> userById = userService.getUserById(1L);
		CompletionStage<Optional<User>> invoke = userById.invoke(new NotUsed());
		invoke.thenAccept(opt -> {
			System.out.println(opt.orElseGet(null));
		});
		
		ServiceCall<NotUsed, List<User>> users = userService.getUsers();
		users.invoke(new NotUsed());
		
		ServiceCall<User, Done> addUser = userService.addUser();
		addUser.invoke(User.builder().id(1L).name("Hello").build());
		
	}

}
