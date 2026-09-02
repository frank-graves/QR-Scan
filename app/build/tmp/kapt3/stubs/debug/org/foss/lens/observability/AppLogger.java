package org.foss.lens.observability;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0003\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bJ\"\u0010\n\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\fJ\b\u0010\r\u001a\u0004\u0018\u00010\u000eJ\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00110\u0010J\u0016\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bJ\u000e\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0014\u001a\u00020\u0015J\'\u0010\u0016\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bH\u0002\u00a2\u0006\u0002\u0010\u0019J\u001d\u0010\u001a\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u001b\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\u0011\u00a2\u0006\u0002\u0010\u001dJ\u000e\u0010\u001e\u001a\u00020\b2\u0006\u0010\u001f\u001a\u00020\bJ\u0016\u0010 \u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bR\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lorg/foss/lens/observability/AppLogger;", "", "()V", "db", "Lorg/foss/lens/observability/ObservabilityDb;", "debug", "", "tag", "", "msg", "error", "t", "", "export", "Ljava/io/File;", "goldenSignals", "", "", "info", "init", "context", "Landroid/content/Context;", "persist", "level", "", "(ILjava/lang/String;Ljava/lang/String;)Lkotlin/Unit;", "recordMetric", "name", "value", "(Ljava/lang/String;D)Lkotlin/Unit;", "redactPii", "raw", "warn", "app_debug"})
public final class AppLogger {
    @kotlin.jvm.Volatile
    @org.jetbrains.annotations.Nullable
    private static volatile org.foss.lens.observability.ObservabilityDb db;
    @org.jetbrains.annotations.NotNull
    public static final org.foss.lens.observability.AppLogger INSTANCE = null;
    
    private AppLogger() {
        super();
    }
    
    public final void init(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    public final void debug(@org.jetbrains.annotations.NotNull
    java.lang.String tag, @org.jetbrains.annotations.NotNull
    java.lang.String msg) {
    }
    
    public final void info(@org.jetbrains.annotations.NotNull
    java.lang.String tag, @org.jetbrains.annotations.NotNull
    java.lang.String msg) {
    }
    
    public final void warn(@org.jetbrains.annotations.NotNull
    java.lang.String tag, @org.jetbrains.annotations.NotNull
    java.lang.String msg) {
    }
    
    public final void error(@org.jetbrains.annotations.NotNull
    java.lang.String tag, @org.jetbrains.annotations.NotNull
    java.lang.String msg, @org.jetbrains.annotations.Nullable
    java.lang.Throwable t) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.Unit recordMetric(@org.jetbrains.annotations.NotNull
    java.lang.String name, double value) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.String, java.lang.Double> goldenSignals() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.io.File export() {
        return null;
    }
    
    private final kotlin.Unit persist(int level, java.lang.String tag, java.lang.String msg) {
        return null;
    }
    
    /**
     * Redacts sensitive PII in three passes, then applies a blunt heuristic.
     *
     * Pass 1 kills classic key=value / key: value pairs (covers logs from our own code).
     * Pass 2 neutralizes URL query strings carrying a secret without destroying the URL.
     * Pass 3 scrubs JSON string values associated with sensitive keys.
     *
     * If after all that the remaining text still looks like a JWT (three dot-separated
     * base64url segments) or a JSON blob with suspicious keys, we don't gamble: we return
     * [REDACTED_PAYLOAD]. A partially redacted token is still a token, and we'd rather
     * lose a log line than leak a fragment.
     */
    @org.jetbrains.annotations.NotNull
    public final java.lang.String redactPii(@org.jetbrains.annotations.NotNull
    java.lang.String raw) {
        return null;
    }
}