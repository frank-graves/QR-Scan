package org.foss.lens.infrastructure;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u0000 !2\u00020\u0001:\u0001!B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0013\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\r0\u0015H\u0010\u00a2\u0006\u0002\b\u0016J\u0011\u0010\u0017\u001a\u00020\u000fH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0018J\u000e\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001b0\u001aH\u0016J\b\u0010\u001c\u001a\u00020\u001dH\u0016J!\u0010\u001e\u001a\u0002H\u001f\"\u0004\b\u0000\u0010\u001f*\b\u0012\u0004\u0012\u0002H\u001f0\u0015H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010 R\u0016\u0010\t\u001a\n \u000b*\u0004\u0018\u00010\n0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\""}, d2 = {"Lorg/foss/lens/infrastructure/CameraLens;", "Lorg/foss/lens/infrastructure/Lens;", "context", "Landroid/content/Context;", "lifecycleOwner", "Landroidx/lifecycle/LifecycleOwner;", "decoder", "Lorg/foss/lens/infrastructure/CodexDecoder;", "(Landroid/content/Context;Landroidx/lifecycle/LifecycleOwner;Lorg/foss/lens/infrastructure/CodexDecoder;)V", "analyzerExecutor", "Ljava/util/concurrent/ExecutorService;", "kotlin.jvm.PlatformType", "cameraProvider", "Landroidx/camera/lifecycle/ProcessCameraProvider;", "chaosEnabled", "", "chaosPrefs", "Landroid/content/SharedPreferences;", "frameCounter", "Ljava/util/concurrent/atomic/AtomicInteger;", "cameraProviderFuture", "Lcom/google/common/util/concurrent/ListenableFuture;", "cameraProviderFuture$app_debug", "requestPermissions", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "start", "Lkotlinx/coroutines/flow/Flow;", "Lorg/foss/lens/domain/ScanState;", "stop", "", "await", "T", "(Lcom/google/common/util/concurrent/ListenableFuture;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class CameraLens implements org.foss.lens.infrastructure.Lens {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LifecycleOwner lifecycleOwner = null;
    @org.jetbrains.annotations.NotNull
    private final org.foss.lens.infrastructure.CodexDecoder decoder = null;
    @org.jetbrains.annotations.Nullable
    private androidx.camera.lifecycle.ProcessCameraProvider cameraProvider;
    private final java.util.concurrent.ExecutorService analyzerExecutor = null;
    @org.jetbrains.annotations.NotNull
    private final android.content.SharedPreferences chaosPrefs = null;
    private final boolean chaosEnabled = false;
    @org.jetbrains.annotations.NotNull
    private final java.util.concurrent.atomic.AtomicInteger frameCounter = null;
    @org.jetbrains.annotations.NotNull
    public static final org.foss.lens.infrastructure.CameraLens.Companion Companion = null;
    
    public CameraLens(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    androidx.lifecycle.LifecycleOwner lifecycleOwner, @org.jetbrains.annotations.NotNull
    org.foss.lens.infrastructure.CodexDecoder decoder) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public java.lang.Object requestPermissions(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public com.google.common.util.concurrent.ListenableFuture<androidx.camera.lifecycle.ProcessCameraProvider> cameraProviderFuture$app_debug() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.flow.Flow<org.foss.lens.domain.ScanState> start() {
        return null;
    }
    
    @java.lang.Override
    public void stop() {
    }
    
    private final <T extends java.lang.Object>java.lang.Object await(com.google.common.util.concurrent.ListenableFuture<T> $this$await, kotlin.coroutines.Continuation<? super T> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006\u00a8\u0006\t"}, d2 = {"Lorg/foss/lens/infrastructure/CameraLens$Companion;", "", "()V", "isWithinCooldown", "", "now", "", "last", "cooldown", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final boolean isWithinCooldown(long now, long last, long cooldown) {
            return false;
        }
    }
}