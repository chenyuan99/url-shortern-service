// Copyright (c) 2021, Rice University

package edu.rice.cs.classes.grpc.shared;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import edu.rice.cs.classes.grpc.okserver.UserInfoClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Executors to use in the user info service.
 *
 * @author aps@rice.edu
 */
public final class SharedExecutors {
  private static final int NUM_RPC_THREADS = 4;
  private static final int NUM_WORKER_THREADS = 4;
  private static final Executor RPC_EXECUTOR = Executors.newFixedThreadPool(
      NUM_RPC_THREADS,
      new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat(SharedExecutors.class.getName() + "-rpc-thread-%d")
          .build());
  private static final Executor WORKER_EXECUTOR = Executors.newFixedThreadPool(
      NUM_WORKER_THREADS,
      new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat(SharedExecutors.class.getName() + "-worker-thread-%d")
          .build());

  public static Executor rpcRExecutor() {
    return RPC_EXECUTOR;
  }

  public static Executor workerRExecutor() {
    return WORKER_EXECUTOR;
  }
}
