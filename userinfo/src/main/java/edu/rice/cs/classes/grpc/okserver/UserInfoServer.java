// Copyright (c) 2021, Rice University

package edu.rice.cs.classes.grpc.okserver;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import edu.rice.cs.classes.grpc.proto.UserInfo;
import edu.rice.cs.classes.grpc.proto.UserInfoServiceGetRequest;
import edu.rice.cs.classes.grpc.proto.UserInfoServiceGetResponse;
import edu.rice.cs.classes.grpc.proto.UserInfoServiceGrpc;
import edu.rice.cs.classes.grpc.proto.UserInfoServiceSaveRequest;
import edu.rice.cs.classes.grpc.proto.UserInfoServiceSaveResponse;
import edu.rice.cs.classes.grpc.shared.SharedExecutors;
import edu.rice.cs.classes.grpc.shared.UserInfoDb;
import io.grpc.Context;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
  * An asynchronous, non-blocking, implementation of UserInfo service.
  *
  * @author aps@rice.edu
  */
class UserInfoServer extends UserInfoServiceGrpc.UserInfoServiceImplBase {
  private final int port;
  private final UserInfoDb db = UserInfoDb.newInMemoryDb();

  @Override
  public void save(UserInfoServiceSaveRequest request,
                   StreamObserver<UserInfoServiceSaveResponse> responseObserver) {
    System.out.println(("Receiver request [" + request + "] with deadline "
        + Context.current().getDeadline()));
    try {
      Preconditions.checkArgument(request.hasUserInfo());
      UserInfo userInfo = request.getUserInfo();
      Preconditions.checkArgument(!userInfo.hasId());
      final String id = "ID-" + ordinal.getAndIncrement();
      UserInfo userInfoWithId =
          UserInfo.newBuilder(userInfo).setId(id).build();
      ListenableFuture<Exception> future = db.saveUserInfo(userInfoWithId);
      future.addListener(
          new Runnable() {
            @Override
            public void run() {
              try {
                UserInfoServiceSaveResponse.Builder builder =
                    UserInfoServiceSaveResponse.newBuilder();
                Exception result = future.get();
                if (null == result) {
                  builder
                      .setUserIdIfAny(id)
                      .setCompletionCode(UserInfoServiceSaveResponse.CompletionCode.OK);
                } else {
                  builder
                      .setCompletionCode(UserInfoServiceSaveResponse.CompletionCode.OOPS)
                      .setComment(Throwables.getStackTraceAsString(result));
                }
                setResponseAndLog(builder.build(), responseObserver);
              } catch (Exception e) {
                setErrorAndLog(e, responseObserver);
              }
            }
          },
          SharedExecutors.workerRExecutor());
    } catch (Exception e) {
      setErrorAndLog(e, responseObserver);
    }
  }

  private static <X> void setResponseAndLog(
      X response,
      StreamObserver<X> responseObserver) {
    Preconditions.checkNotNull(response);
    Preconditions.checkNotNull(responseObserver);
    System.out.println("Sending response [" + response + ']');
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private static <X> void setErrorAndLog(
      Exception e,
      StreamObserver<X> responseObserver) {
    Preconditions.checkNotNull(e);
    Preconditions.checkNotNull(responseObserver);
    System.out.println("Sending error [" + Throwables.getStackTraceAsString(e) + ']');
    responseObserver.onError(e);
    responseObserver.onCompleted();
  }

  @Override
  public void get(UserInfoServiceGetRequest request,
                  StreamObserver<UserInfoServiceGetResponse> responseObserver) {
    Preconditions.checkArgument(request.hasUserId());
    ListenableFuture<Optional<UserInfo>> future = db.getUserInfoById(request.getUserId());
    future.addListener(
        new Runnable() {
          @Override
          public void run() {
            try {
              Optional<UserInfo> optionalUserInfo = future.get();
              UserInfoServiceGetResponse.Builder builder = UserInfoServiceGetResponse.newBuilder();
              if (!optionalUserInfo.isPresent()) {
                builder.setCompletionCode(UserInfoServiceGetResponse.CompletionCode.NOT_FOUND);
              } else {
                builder.setUserInfo(optionalUserInfo.get())
                    .setCompletionCode(UserInfoServiceGetResponse.CompletionCode.OK);
              }
              setResponseAndLog(builder.build(), responseObserver);
            } catch (Exception e) {
              setErrorAndLog(e, responseObserver);
            }
          }
        },
        SharedExecutors.workerRExecutor());
  }

  private final AtomicInteger ordinal = new AtomicInteger();

  private final ListenableFuture<UserInfo> create() {
    return Futures.immediateFuture(UserInfo.newBuilder()
        .setAgeYears(99)
        .setMemberSinceMillisFromEpoch(System.currentTimeMillis())
        .setEmail("aps@rice.edu")
        .setId("ID-" + ordinal.getAndIncrement())
        .build());
  }

  private final SettableFuture<Boolean> shutdownFuture = SettableFuture.create();

  protected UserInfoServer(int port) {
    Preconditions.checkArgument(port > 0);
    this.port = port;
  }

  protected void startServerAndBlock() {
    try {
      ServerBuilder
          .forPort(port)
          .executor(SharedExecutors.rpcRExecutor())
          .addService(this).build().start();
      System.out.println("Started server on port " + port);
      // now block
      shutdownFuture.get();
    } catch (Exception e) {
      killServerWithDiagnostics("Could not start server on port " + port + ". Exception: "
          + Throwables.getStackTraceAsString(e));
    }
    // shutting down
    System.out.println("Server is shutting down");
  }

  public void shutdown() {
    shutdownFuture.set(true);
  }

  private static void killServerWithDiagnostics(String diagnostics) {
    Preconditions.checkNotNull(diagnostics);
    System.err.println(diagnostics);
    System.exit(1);
  }

  private final static String PORT_FLAG_NAME = "port";

  public static void main(String[] args) {
    ParsedFlags.parse(args);
    /*
    Optional<String> optionalPortFlag =
        ParsedFlags.optionallyGetValueOfFlagByName(PORT_FLAG_NAME);
    if (!optionalPortFlag.isPresent()) {
      killServerWithDiagnostics("--port=port flag is required");
    }
     */
    int fixedPort = 3333;
    try {
      //int port = Integer.parseInt(optionalPortFlag.get());
      int port = fixedPort;
      new UserInfoServer(port).startServerAndBlock();
    } catch (NumberFormatException nfe) {
      //killServerWithDiagnostics("Invalid port flag value: [" + optionalPortFlag.get() + ']');
      killServerWithDiagnostics("Invalid port flag value: [" + fixedPort + ']');
    }
  }
}
