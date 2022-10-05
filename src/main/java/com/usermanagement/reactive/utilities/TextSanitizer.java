package com.usermanagement.reactive.utilities;

import java.util.Optional;
import java.util.regex.Pattern;

public final class TextSanitizer {
  private static Pattern spacePattern = Pattern.compile("\\s+");
  private static Pattern apostrophePattern = Pattern.compile("['’’‘`]+");
  private static Pattern punctuationPattern =
      Pattern.compile("[!\"“”#$%()*�+,\\\\/:±;<【】=>?\\[\\]\\^™{|}~®（）一°]+");

  public static String sanitize(String text) {
    return stripBadCharacters(text);
  }

  private static String stripBadCharacters(String text) {
    return spacePattern
        .matcher(
            punctuationPattern
                .matcher(apostrophePattern.matcher(nullCheck(text)).replaceAll(""))
                .replaceAll(" "))
        .replaceAll(" ")
        .trim();
  }

  private static String nullCheck(String query) {
    return Optional.ofNullable(query).orElse("");
  }
}
