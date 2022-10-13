// Copyright (c) 2021, Rice University

package edu.rice.cs.classes.grpc.okserver;

import com.google.common.base.Preconditions;
import edu.rice.cs.classes.grpc.proto.UserInfo;

import java.util.Optional;

/**
 * A utility class to validate {@link UserInfo}.
 *
 * @author aps@rice.edu
 */
public final class UserInfoValidator {
  public enum StatusEnum {
    UNKNOWN,
    OK,
    MISSING_REQUIRED_FIELD,
    INVALID_FIELD_VALUE;

    public final boolean isValid() {
      return OK == this;
    }
  }

  public static final class Status {
    private final StatusEnum statusEnum;
    private String detailIfAny;

    private Status(StatusEnum statusEnum, String detailIfAny) {
      this.statusEnum = Preconditions.checkNotNull(statusEnum);
      this.detailIfAny = Preconditions.checkNotNull(detailIfAny);
    }

    @Override
    public final String toString() {
      return statusEnum.name() + " [detail = [" + detailIfAny + "]]";
    }

    public final boolean isValid() {
      return statusEnum.isValid();
    }

    public final StatusEnum getEnum() {
      return statusEnum;
    }
  }

  public static Status validate(UserInfo userInfo) {
    Preconditions.checkNotNull(userInfo);
    if (!userInfo.hasName()) {
      return new Status(StatusEnum.MISSING_REQUIRED_FIELD, "name");
    }
    if (!userInfo.hasEmail()) {
      return new Status(StatusEnum.MISSING_REQUIRED_FIELD, "email");
    }
    return new Status(StatusEnum.OK, "");
  }
}
