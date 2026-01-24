package com.chatapp.chat.user.exception;

public class UsernameAlreadyUsedException extends RuntimeException {
  public UsernameAlreadyUsedException(String message) {
    super(message);
  }
}
