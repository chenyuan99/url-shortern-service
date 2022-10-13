// Copyright (c) 2021, Rice University

package edu.rice.cs.classes.grpc.shared;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import edu.rice.cs.classes.grpc.proto.UserInfo;

import java.util.Map;
import java.util.Optional;

/**
 * The interface abstracts user info database. At present, it only implements the in-memory DB.
 *
 * @author aps@rice.edu
 */
public interface UserInfoDb {

  public ListenableFuture<Exception> saveUserInfo(UserInfo userInfo);

  public ListenableFuture<Optional<UserInfo>> getUserInfoById(String id);

  /**
   * Creates and returns a new in-memory database.
   *
   * @return
   */
  public static UserInfoDb newInMemoryDb() {
    return new Implementations.InMemoryUserInfoDb();
  }

  public final class Implementations {
    private static final class InMemoryUserInfoDb implements UserInfoDb {

      private final Map<String, UserInfo> db = Maps.newConcurrentMap();

      @Override
      public final ListenableFuture<Exception> saveUserInfo(UserInfo userInfo) {
        Preconditions.checkNotNull(userInfo);
        Preconditions.checkArgument(userInfo.hasId());
        final SettableFuture<Exception> future = SettableFuture.create();
        SharedExecutors.workerRExecutor().execute(() -> {
          try {
            String id = userInfo.getId();
            Preconditions.checkState(
                null == db.putIfAbsent(id, userInfo),
                "User ID [" + id + "] is not unique");
          } catch (Exception e) {
            future.set(e);
            return;
          }
          future.set(null);
        });
        return future;
      }

      @Override
      public final ListenableFuture<Optional<UserInfo>> getUserInfoById(String id) {
        Preconditions.checkNotNull(id);
        final SettableFuture<Optional<UserInfo>> future = SettableFuture.create();
        SharedExecutors.workerRExecutor().execute(() -> {
          future.set(Optional.ofNullable(db.get(id)));
        });
        return future;
      }
    }
  }
}
