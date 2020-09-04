package com.yangzhou.frame.vmmapper;

import java.util.Set;
import java.util.stream.Collectors;
import com.yangzhou.frame.domain.Authority;

public class AuthorityToStringConverter {
  public static String AuthorityToString(Authority authority) {
    return authority.getName();
  }

  public static Set<String> authoritiySettoStringSet(Set<Authority> authorities) {
    return authorities.stream().map(a -> a.getName()).collect(Collectors.toSet());
  }

  public static Authority stringToAurhotiry(String string) {
    Authority authority = new Authority();
    authority.setName(string);
    return authority;
  }

  public static Set<Authority> authoritiesFromStrings(Set<String> strings) {
    return strings.stream().map(string -> stringToAurhotiry(string)).collect(Collectors.toSet());
  }
}
