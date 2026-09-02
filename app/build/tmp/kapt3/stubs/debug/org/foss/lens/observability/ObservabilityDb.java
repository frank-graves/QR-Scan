package org.foss.lens.observability;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0010\u000e\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0011\u001a\u00020\u0012J\b\u0010\u0013\u001a\u00020\u0012H\u0002J\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\u0015J\u001e\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u00062\u0006\u0010\u0018\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\fJ\u0016\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\rJ\u0010\u0010\u001d\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J \u0010 \u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\u00062\u0006\u0010\"\u001a\u00020\u0006H\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R,\u0010\b\u001a \u0012\u001c\u0012\u001a\u0012\u0004\u0012\u00020\u000b\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\n0\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lorg/foss/lens/observability/ObservabilityDb;", "Landroid/database/sqlite/SQLiteOpenHelper;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "logInsertsSincePrune", "", "logPruneInterval", "metricBuffer", "", "Lkotlin/Pair;", "", "", "", "metricFlushThreshold", "exportLogs", "Ljava/io/File;", "flushMetrics", "", "flushMetricsLocked", "getMetrics", "", "insertLog", "level", "tag", "msg", "insertMetric", "name", "value", "onCreate", "db", "Landroid/database/sqlite/SQLiteDatabase;", "onUpgrade", "oldVersion", "newVersion", "app_debug"})
public final class ObservabilityDb extends android.database.sqlite.SQLiteOpenHelper {
    @org.jetbrains.annotations.NotNull
    private final java.util.List<kotlin.Pair<java.lang.Long, kotlin.Pair<java.lang.String, java.lang.Double>>> metricBuffer = null;
    private final int metricFlushThreshold = 25;
    private int logInsertsSincePrune = 0;
    private final int logPruneInterval = 50;
    
    public ObservabilityDb(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super(null, null, null, 0);
    }
    
    @java.lang.Override
    public void onCreate(@org.jetbrains.annotations.NotNull
    android.database.sqlite.SQLiteDatabase db) {
    }
    
    @java.lang.Override
    public void onUpgrade(@org.jetbrains.annotations.NotNull
    android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    
    public final void insertLog(int level, @org.jetbrains.annotations.NotNull
    java.lang.String tag, @org.jetbrains.annotations.NotNull
    java.lang.String msg) {
    }
    
    public final void insertMetric(@org.jetbrains.annotations.NotNull
    java.lang.String name, double value) {
    }
    
    private final void flushMetricsLocked() {
    }
    
    public final void flushMetrics() {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.Double> getMetrics() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.io.File exportLogs() {
        return null;
    }
}