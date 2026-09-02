package org.foss.lens.observability;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\r\u0010\u0003\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0002\u0010\u0005J\r\u0010\u0006\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0002\u0010\u0005J\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u00042\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0015\u0010\u000b\u001a\u0004\u0018\u00010\u00042\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e\u00a8\u0006\u0010"}, d2 = {"Lorg/foss/lens/observability/GoldenSignals;", "", "()V", "analyzerError", "", "()Lkotlin/Unit;", "analyzerOk", "coldStart", "ms", "", "(J)Lkotlin/Unit;", "frameRender", "saturation", "memKb", "", "batteryPct", "app_debug"})
public final class GoldenSignals {
    @org.jetbrains.annotations.NotNull
    public static final org.foss.lens.observability.GoldenSignals INSTANCE = null;
    
    private GoldenSignals() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.Unit coldStart(long ms) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.Unit frameRender(long ms) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.Unit analyzerOk() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.Unit analyzerError() {
        return null;
    }
    
    public final void saturation(int memKb, int batteryPct) {
    }
}