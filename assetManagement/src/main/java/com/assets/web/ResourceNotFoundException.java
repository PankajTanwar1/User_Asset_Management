package com.assets.web;
public class ResourceNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8578587572996574395L;

	public ResourceNotFoundException(String message) {
        super(message);
    }
}
