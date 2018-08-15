package com.websystique.springmvc.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.websystique.springmvc.model.User;
import com.websystique.springmvc.model.UserProfile;
import com.websystique.springmvc.service.UserProfileService;
import com.websystique.springmvc.service.UserService;

@Controller
@RequestMapping("/")
public class RegistrationController {

	@Autowired
	UserService userService;
	
	@Autowired
	UserProfileService userProfileService;
	
	@Autowired
	MessageSource messageSource;

	@Autowired
	PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;
	
	@Autowired
	AuthenticationTrustResolver authenticationTrustResolver;
	
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register(ModelMap model,  @ModelAttribute User userPost) {
		
		User user = new User();
		
		model.addAttribute("user", user);
		model.addAttribute("edit", false);
		model.addAttribute("loggedinuser", getPrincipal());
		return "register";
	}
	/**
	 * This method will be called on form submission, handling POST request for
	 * saving user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/register" }, method = RequestMethod.POST)
	public String saveUser(@Valid User user, BindingResult result,
			ModelMap model) {
		
		boolean formValid = true;
		Pattern patternFirstName = Pattern.compile("[A-Za-z]{3,}$");
		Pattern patternUserName = Pattern.compile("[A-Za-z0-9_]{3,}$");
		Pattern patternPassword = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{7,}$");
		Pattern patternEmail = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		
		formValid = (user.getFirstName() != null) && patternFirstName.matcher(user.getFirstName()).matches();		
		if(!formValid){
			FieldError firstName =new FieldError("user","firstName",messageSource.getMessage("validation.user.firstName", new String[]{user.getFirstName()}, Locale.getDefault()));
		    result.addError(firstName);
			return "register";
		}
		
		formValid = (user.getLastName() != null) && patternFirstName.matcher(user.getLastName()).matches();
		if(!formValid){
			FieldError lastName =new FieldError("user","lastName",messageSource.getMessage("validation.user.lastName", new String[]{user.getLastName()}, Locale.getDefault()));
		    result.addError(lastName);
			return "register";
		}
		
		formValid = (user.getSsoId() != null) && patternUserName.matcher(user.getSsoId()).matches();
		if(!formValid){
			FieldError ssoError =new FieldError("user","ssoId",messageSource.getMessage("validation.user.ssoId", new String[]{user.getSsoId()}, Locale.getDefault()));
		    result.addError(ssoError);
			return "register";
		}
		
		formValid = (user.getPassword() != null) && patternPassword.matcher(user.getPassword()).matches();
		if(!formValid){
			FieldError passwordError =new FieldError("user","password",messageSource.getMessage("validation.user.password", new String[]{user.getPassword()}, Locale.getDefault()));
		    result.addError(passwordError);
			return "register";
		}
		
		formValid = (user.getEmail() != null) && patternEmail.matcher(user.getEmail()).matches();		
		if(!formValid){
			FieldError firstEmail =new FieldError("user","email",messageSource.getMessage("validation.user.email", new String[]{user.getEmail()}, Locale.getDefault()));
		    result.addError(firstEmail);
			return "register";
		}
		
		List<UserProfile> list = new ArrayList<UserProfile>();		
		UserProfile userprofile = userProfileService.findByType("USER");
		list.add(userprofile);
		Set<UserProfile> userProfileSet = new HashSet<UserProfile>(list);
		
		user.setUserProfiles(userProfileSet);
		

		/*
		 * Preferred way to achieve uniqueness of field [sso] should be implementing custom @Unique annotation 
		 * and applying it on field [sso] of Model class [User].
		 * 
		 * Below mentioned peace of code [if block] is to demonstrate that you can fill custom errors outside the validation
		 * framework as well while still using internationalized messages.
		 * 
		 */
		if(!userService.isUserSSOUnique(user.getId(), user.getSsoId())){
			FieldError ssoError =new FieldError("user","ssoId",messageSource.getMessage("non.unique.ssoId", new String[]{user.getSsoId()}, Locale.getDefault()));
		    result.addError(ssoError);
			return "register";
		}
		
		userService.saveUser(user);

		model.addAttribute("success", "User " + user.getFirstName() + " "+ user.getLastName() + " registered successfully");
		model.addAttribute("loggedinuser", getPrincipal());
		//return "success";
		return "registrationsuccess";
	}
	/**
	 * This method returns the principal[user-name] of logged-in user.
	 */
	private String getPrincipal(){
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails)principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
	}
}
