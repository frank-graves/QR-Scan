package org.foss.lens.presentation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0010\u001a\u00020\u0011H\u0014J\u0006\u0010\u0012\u001a\u00020\u0011J\u0006\u0010\u0013\u001a\u00020\u0011R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0014"}, d2 = {"Lorg/foss/lens/presentation/ScanVM;", "Landroidx/lifecycle/ViewModel;", "lens", "Lorg/foss/lens/infrastructure/Lens;", "archive", "Lorg/foss/lens/domain/Archive;", "(Lorg/foss/lens/infrastructure/Lens;Lorg/foss/lens/domain/Archive;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lorg/foss/lens/domain/ScanState;", "scanningJob", "Lkotlinx/coroutines/Job;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "onCleared", "", "startScanning", "stopScanning", "app_debug"})
public final class ScanVM extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final org.foss.lens.infrastructure.Lens lens = null;
    @org.jetbrains.annotations.NotNull
    private final org.foss.lens.domain.Archive archive = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<org.foss.lens.domain.ScanState> _state = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<org.foss.lens.domain.ScanState> state = null;
    @org.jetbrains.annotations.Nullable
    private kotlinx.coroutines.Job scanningJob;
    
    public ScanVM(@org.jetbrains.annotations.NotNull
    org.foss.lens.infrastructure.Lens lens, @org.jetbrains.annotations.NotNull
    org.foss.lens.domain.Archive archive) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<org.foss.lens.domain.ScanState> getState() {
        return null;
    }
    
    public final void startScanning() {
    }
    
    public final void stopScanning() {
    }
    
    @java.lang.Override
    protected void onCleared() {
    }
}