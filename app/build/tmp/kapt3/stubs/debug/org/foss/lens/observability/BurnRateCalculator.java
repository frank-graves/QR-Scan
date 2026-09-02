package org.foss.lens.observability;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001\u0013B#\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\u000e\u0010\f\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\u0003J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\r\u001a\u00020\u0003J\u0016\u0010\u0010\u001a\u00020\u00112\u0006\u0010\r\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u000fR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lorg/foss/lens/observability/BurnRateCalculator;", "", "shortWindowMs", "", "longWindowMs", "criticalFactor", "", "(JJD)V", "lock", "samples", "Lkotlin/collections/ArrayDeque;", "Lorg/foss/lens/observability/BurnRateCalculator$Sample;", "burnRatio", "nowMs", "isCritical", "", "record", "", "error", "Sample", "app_debug"})
public final class BurnRateCalculator {
    private final long shortWindowMs = 0L;
    private final long longWindowMs = 0L;
    private final double criticalFactor = 0.0;
    @org.jetbrains.annotations.NotNull
    private final kotlin.collections.ArrayDeque<org.foss.lens.observability.BurnRateCalculator.Sample> samples = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.Object lock = null;
    
    public BurnRateCalculator(long shortWindowMs, long longWindowMs, double criticalFactor) {
        super();
    }
    
    public final void record(long nowMs, boolean error) {
    }
    
    public final double burnRatio(long nowMs) {
        return 0.0;
    }
    
    public final boolean isCritical(long nowMs) {
        return false;
    }
    
    public BurnRateCalculator() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u00052\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0014"}, d2 = {"Lorg/foss/lens/observability/BurnRateCalculator$Sample;", "", "ts", "", "error", "", "(JZ)V", "getError", "()Z", "getTs", "()J", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "", "app_debug"})
    static final class Sample {
        private final long ts = 0L;
        private final boolean error = false;
        
        public Sample(long ts, boolean error) {
            super();
        }
        
        public final long getTs() {
            return 0L;
        }
        
        public final boolean getError() {
            return false;
        }
        
        public final long component1() {
            return 0L;
        }
        
        public final boolean component2() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final org.foss.lens.observability.BurnRateCalculator.Sample copy(long ts, boolean error) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
    }
}