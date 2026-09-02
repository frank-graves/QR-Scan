package org.foss.lens.infrastructure;

/**
 * Camera abstraction.
 *
 * The Activity talks to this seam, never to CameraX directly, so the
 * scanning pipeline stays testable without hardware.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H&J\b\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\u0007"}, d2 = {"Lorg/foss/lens/infrastructure/Lens;", "", "start", "Lkotlinx/coroutines/flow/Flow;", "Lorg/foss/lens/domain/ScanState;", "stop", "", "app_debug"})
public abstract interface Lens {
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<org.foss.lens.domain.ScanState> start();
    
    public abstract void stop();
}