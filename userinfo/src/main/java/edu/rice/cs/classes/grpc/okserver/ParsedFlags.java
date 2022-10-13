// Copyright (c) 2021, Rice University

package edu.rice.cs.classes.grpc.okserver;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A "poor's man" implementation of command-line flags.
 *
 * @author aps@rice.edu
 */
public final class ParsedFlags {
  private static final AtomicReference<ParsedFlags> PARSED_FLAGS = new AtomicReference<>();

  private final Map<String, String> flags;

  private ParsedFlags(String[] args) {
    Preconditions.checkNotNull(args);
    flags = ImmutableMap.<String, String>builder().putAll(
          Iterables.filter(
            Iterables.transform(
                Arrays.asList(args),
                (String fullFlagForm) -> {
                  Preconditions.checkNotNull(fullFlagForm);
                  if (!fullFlagForm.startsWith(("--"))) {
                    return null;
                  }
                  int firstEqSignIndex = fullFlagForm.indexOf('=');
                  if (firstEqSignIndex <= 2) {
                    return null;
                  }
                  String flagName = fullFlagForm.substring(2, firstEqSignIndex);
                  String flagValue = fullFlagForm.substring(firstEqSignIndex + 1);
                  return Maps.immutableEntry(flagName, flagValue);
                }
              ),
              Predicates.notNull()))
        .build();
  }

  public static final Optional<String> optionallyGetValueOfFlagByName(String flagName) {
    ParsedFlags parsedFlags = PARSED_FLAGS.get();
    Preconditions.checkNotNull(parsedFlags);
    Preconditions.checkNotNull(flagName);
    return Optional.ofNullable(parsedFlags.flags.get(flagName));
  }

  public static void parse(String[] args) {
    Preconditions.checkState(PARSED_FLAGS.compareAndSet(null, new ParsedFlags(args)));
  }
}
