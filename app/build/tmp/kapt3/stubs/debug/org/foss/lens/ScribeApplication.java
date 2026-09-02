package org.foss.lens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u0000 \u00192\u00020\u0001:\u0001\u0019B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016J\b\u0010\u0017\u001a\u00020\u0018H\u0016R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\b\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000e\u001a\u00020\u000f8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0012\u0010\b\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u001a"}, d2 = {"Lorg/foss/lens/ScribeApplication;", "Landroid/app/Application;", "()V", "archive", "Lorg/foss/lens/domain/Archive;", "getArchive", "()Lorg/foss/lens/domain/Archive;", "archive$delegate", "Lkotlin/Lazy;", "db", "Lorg/foss/lens/data/local/ArchiveDatabase;", "getDb", "()Lorg/foss/lens/data/local/ArchiveDatabase;", "db$delegate", "decoder", "Lorg/foss/lens/infrastructure/CodexDecoder;", "getDecoder", "()Lorg/foss/lens/infrastructure/CodexDecoder;", "decoder$delegate", "createLens", "Lorg/foss/lens/infrastructure/Lens;", "lifecycleOwner", "Landroidx/lifecycle/LifecycleOwner;", "onCreate", "", "Companion", "app_debug"})
public final class ScribeApplication extends android.app.Application {
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy db$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy archive$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy decoder$delegate = null;
    private static org.foss.lens.ScribeApplication instance;
    private static final long processStartMs = 0L;
    @org.jetbrains.annotations.NotNull
    public static final org.foss.lens.ScribeApplication.Companion Companion = null;
    
    public ScribeApplication() {
        super();
    }
    
    private final org.foss.lens.data.local.ArchiveDatabase getDb() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final org.foss.lens.domain.Archive getArchive() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final org.foss.lens.infrastructure.CodexDecoder getDecoder() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final org.foss.lens.infrastructure.Lens createLens(@org.jetbrains.annotations.NotNull
    androidx.lifecycle.LifecycleOwner lifecycleOwner) {
        return null;
    }
    
    @java.lang.Override
    public void onCreate() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u001e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0004@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2 = {"Lorg/foss/lens/ScribeApplication$Companion;", "", "()V", "<set-?>", "Lorg/foss/lens/ScribeApplication;", "instance", "getInstance", "()Lorg/foss/lens/ScribeApplication;", "processStartMs", "", "getProcessStartMs", "()J", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final org.foss.lens.ScribeApplication getInstance() {
            return null;
        }
        
        public final long getProcessStartMs() {
            return 0L;
        }
    }
}