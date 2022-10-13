// Copyright (c) 2021, Rice University

package edu.rice.cs.classes.grpc.okserver;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import edu.rice.cs.classes.grpc.proto.Address;
import edu.rice.cs.classes.grpc.proto.Name;
import edu.rice.cs.classes.grpc.proto.UserInfo;
import edu.rice.cs.classes.grpc.proto.UserInfoServiceGetRequest;
import edu.rice.cs.classes.grpc.proto.UserInfoServiceGetResponse;
import edu.rice.cs.classes.grpc.proto.UserInfoServiceGrpc;
import edu.rice.cs.classes.grpc.proto.UserInfoServiceSaveRequest;
import edu.rice.cs.classes.grpc.proto.UserInfoServiceSaveResponse;
import edu.rice.cs.classes.grpc.shared.SharedExecutors;
import io.grpc.Deadline;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A fully asynchronous client to the UserInfo service.</p>
 *
 * A typical usage will be:
 * <code>
 *   <verbatim>
 *     ListenableFuture<UserInfoSaveMessageResponse> responseFuture =
 *        UserInforClient.forSpecs("127.0.0.1:555").sendSaveMessage(userInfoMessage);
 *        response.addListener(new Runnable() {
 *           ...
 *         });
 *   </verbatim>
 * </code>
 *
 * @author aps@rice.edu
 */
public class UserInfoClient {
  private final UserInfoServiceGrpc.UserInfoServiceStub stub;

  private UserInfoClient(String target) {
    Preconditions.checkNotNull(target);
    stub = UserInfoServiceGrpc.newStub(ManagedChannelBuilder
        .forTarget(target)
        .disableRetry()
        .usePlaintext()
        .executor(SharedExecutors.rpcRExecutor())
        .build());
  }

  public ListenableFuture<UserInfoServiceSaveResponse> sendSaveMessage(UserInfo userInfo) {
    // UserInfoValidator.Status validationStatus = UserInfoValidator.Statu
    //    Preconditions.checkNotNull(UserInfoValidator.validate(userInfo));
    final SettableFuture<UserInfoServiceSaveResponse> responseFuture = SettableFuture.create();
    if (false) {
      responseFuture.set(UserInfoServiceSaveResponse.newBuilder()
          .setCompletionCode(UserInfoServiceSaveResponse.CompletionCode.OOPS)
//          .setComment(validationStatus.toString())
          .build());
      return responseFuture;
    }
    StreamObserver<UserInfoServiceSaveResponse> observer =
        new StreamObserver<UserInfoServiceSaveResponse>() {
          private final AtomicBoolean receivedResponse = new AtomicBoolean();

          @Override
          public void onNext(UserInfoServiceSaveResponse userInfoServiceSaveResponse) {
            Preconditions.checkState(!receivedResponse.getAndSet(true));
            Preconditions.checkNotNull(userInfoServiceSaveResponse);
            responseFuture.set(userInfoServiceSaveResponse);
          }

          @Override
          public void onError(Throwable throwable) {
            Preconditions.checkState(!receivedResponse.getAndSet(true));
            Preconditions.checkNotNull(throwable);
            responseFuture.set(UserInfoServiceSaveResponse.newBuilder()
                .setCompletionCode(UserInfoServiceSaveResponse.CompletionCode.OOPS)
                .setComment(Throwables.getStackTraceAsString(throwable))
                .build());
          }

          @Override
          public void onCompleted() {
            // this is redundant for singular calls
            Preconditions.checkState(receivedResponse.get());
          }};
    stub
        .withDeadline(Deadline.after(30000, TimeUnit.MILLISECONDS))
        .save(
            UserInfoServiceSaveRequest.newBuilder().setUserInfo(userInfo).build(),
            observer);
    return responseFuture;
  }

  public ListenableFuture<UserInfoServiceGetResponse> sendGetMessage(String userId) {
    Preconditions.checkNotNull(userId);
    final SettableFuture<UserInfoServiceGetResponse> responseFuture = SettableFuture.create();
    StreamObserver<UserInfoServiceGetResponse> observer =
        new StreamObserver<UserInfoServiceGetResponse>() {
          private final AtomicBoolean receivedResponse = new AtomicBoolean();

          @Override
          public void onNext(UserInfoServiceGetResponse userInfoServiceGetResponse) {
            Preconditions.checkState(!receivedResponse.getAndSet(true));
            Preconditions.checkNotNull(userInfoServiceGetResponse);
            responseFuture.set(userInfoServiceGetResponse);
          }

          @Override
          public void onError(Throwable throwable) {
            Preconditions.checkState(!receivedResponse.getAndSet(true));
            Preconditions.checkNotNull(throwable);
            responseFuture.set(UserInfoServiceGetResponse.newBuilder()
                .setCompletionCode(UserInfoServiceGetResponse.CompletionCode.OOPS)
                .setComment(Throwables.getStackTraceAsString(throwable))
                .build());
          }

          @Override
          public void onCompleted() {
            // this is redundant for singular calls
            Preconditions.checkState(receivedResponse.get());
          }};
    stub
        .withDeadline(Deadline.after(3000, TimeUnit.MILLISECONDS))
        .get(
            UserInfoServiceGetRequest.newBuilder().setUserId(userId).build(),
            observer);
    return responseFuture;
  }

  public static UserInfoClient forSpecs(String specs) {
    return new UserInfoClient(specs);
  }

  private final static String PORT_FLAG_NAME = "port";
  private final static String HOST_FLAG_NAME = "host";

  public static void main(String[] args) throws Exception {
    ParsedFlags.parse(args);
    /*
    Optional<String> optionalPortFlag =
        ParsedFlags.optionallyGetValueOfFlagByName(PORT_FLAG_NAME);
    Optional<String> optionalHostFlag =
        ParsedFlags.optionallyGetValueOfFlagByName(HOST_FLAG_NAME);
    if (!optionalHostFlag.isPresent()) {
      System.err.println("--host=host flag is required");
      System.exit(1);
    }
    if (!optionalPortFlag.isPresent()) {
      System.err.println("--port=port flag is required");
      System.exit(1);
    }

     */
    int fixedPort = 3333;
    String fixedHost = "localhost";
    try {
      //int port = Integer.parseInt(optionalPortFlag.get());
      int port = fixedPort;
      //UserInfoClient userInfoClient = forSpecs(optionalHostFlag.get() + ':' + port);
      UserInfoClient userInfoClient = forSpecs(fixedHost + ':' + port);
      UserInfo userInfo = UserInfo.newBuilder()
          .setName(Name.newBuilder()
              .setFirstName("Alexei")
              .setLastName("Stolboushkin")
              .build())
          .setEmail("aps@rice.edu")
          .addAddresses(Address.newBuilder()
              .setAddressType(Address.Type.HOME)
              .setStreetNumber(5)
              .setStreetName("First Street")
              .setCity("Some City")
              .setState("TX")
              .setZip(77777)
              .build())
          .build();
      int userInfoWireFormatLength = userInfo.toByteArray().length;
      int userInfoStringFormatLength = userInfo.toString().length();
      System.out.println("Sending request of length = " + userInfoStringFormatLength
          + " (wire length = " + userInfoWireFormatLength + "), message = [" + userInfo + ']');
      ListenableFuture<UserInfoServiceSaveResponse> responseFuture =
          userInfoClient.sendSaveMessage(userInfo);
      UserInfoServiceSaveResponse response = responseFuture.get();
      System.out.println("Response [" + response + ']');
      System.out.println("Sending request [" + response.getUserIdIfAny() + ']');
      UserInfoServiceGetResponse getResponse =
          userInfoClient.sendGetMessage(response.getUserIdIfAny()).get();
      System.out.println("Response [" + getResponse + ']');
    } catch (NumberFormatException nfe) {
      //System.err.println("Invalid port flag value: [" + optionalPortFlag.get() + ']');
    }
  }
}
